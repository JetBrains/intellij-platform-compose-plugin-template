package org.jetbrains.plugins.template.chatApp.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.jetbrains.plugins.template.chatApp.model.ChatMessage
import org.jetbrains.plugins.template.chatApp.ui.*

/**
 * Implementation of the SearchChatMessagesHandler interface for handling
 * chat message search functionality and state management.
 *
 * This class provides the ability to search through a list of chat messages,
 * navigate through search results, and manage the state of the search process.
 *
 * @property messagesFlow A StateFlow of a List containing ChatMessage objects.
 *                        It represents the stream of chat messages to be searched.
 * @property searchStateFlow A StateFlow representing the current search state,
 *                           which can be idle, searching, or showing search results.
 */
class SearchChatMessagesHandlerImpl(
    private val messagesFlow: StateFlow<List<ChatMessage>> = MutableStateFlow(emptyList()),
    coroutineScope: CoroutineScope
) : SearchChatMessagesHandler {
    private val _searchStateFlow: MutableStateFlow<SearchState> = MutableStateFlow(SearchState.Idle)

    override val searchStateFlow: StateFlow<SearchState> = _searchStateFlow.asStateFlow()

    init {
        messagesFlow
            .onEach { _ ->
                // When new messages are received, refresh search results using the latest query.
                // If no search query is available(search is not open), skip the operation.
                val searchState = _searchStateFlow.value
                if (searchState.isSearching) {
                    searchState.searchQuery?.let { query -> onSearchQuery(query) }
                }
            }
            .launchIn(coroutineScope)
    }

    override fun onStartSearch() {
        _searchStateFlow.value = SearchState.Searching("")
    }

    override fun onStopSearch() {
        _searchStateFlow.value = SearchState.Idle
    }

    override fun onSearchQuery(query: String) {
        val messages = messagesFlow.value

        performSearch(query, messages)
    }

    override fun onNavigateToNextSearchResult() {
        val searchState = _searchStateFlow.value
        if (searchState !is SearchState.SearchResult.Found) return
        if (!(searchState.hasResults)) return

        moveSearchResultSelectionToIndex(searchState, searchState.selectedSearchMatchId + 1)
    }

    override fun onNavigateToPreviousSearchResult() {
        val searchState = _searchStateFlow.value
        if (searchState !is SearchState.SearchResult.Found) return
        if (!(searchState.hasResults)) return

        moveSearchResultSelectionToIndex(searchState, searchState.selectedSearchMatchId - 1)
    }

    private fun moveSearchResultSelectionToIndex(searchState: SearchState.SearchResult.Found, newIndex: Int) {
        val nextIndex = when {
            newIndex < 0 -> searchState.searchMatches.lastIndex
            newIndex > searchState.searchMatches.lastIndex -> 0
            else -> newIndex
        }

        _searchStateFlow.value = searchState.copy(selectedSearchMatchId = nextIndex)
    }

    private fun performSearch(
        query: String,
        messages: List<ChatMessage>
    ) {
        if (query.isBlank()) {
            _searchStateFlow.value = SearchState.SearchResult.None(query)
            return
        }

        var currentSearchMatchId = 0
        val searchMatches = mutableListOf<SearchMatches>()
        for (message in messages) {
            val matches = findSearchMatches(message, query, currentSearchMatchId)
            searchMatches += matches
            currentSearchMatchId += matches.size
        }

        _searchStateFlow.value = when {
            searchMatches.isEmpty() -> SearchState.SearchResult.None(query)
            else -> SearchState.SearchResult.Found(
                query = query,
                selectedSearchMatchId = if (searchMatches.isNotEmpty()) 0 else -1,
                searchMatches = searchMatches
            )
        }
    }

    private fun findSearchMatches(message: ChatMessage, query: String, currentSearchMatchId: Int): List<SearchMatches> {
        val lowerText = message.content.lowercase()
        val lowerQuery = query.lowercase()

        var nextSearchMatchId = currentSearchMatchId
        val searchMatches = mutableListOf<SearchMatches>()
        var startIndex = 0

        while (true) {
            val index = lowerText.indexOf(lowerQuery, startIndex)
            if (index == -1) break

            searchMatches += SearchMatches(
                searchMatchId = nextSearchMatchId,
                messageId = message.id,
                startInclusive = index,
                endExclusive = index + query.length
            )

            nextSearchMatchId++
            startIndex = index + query.length
        }

        return searchMatches
    }
}
