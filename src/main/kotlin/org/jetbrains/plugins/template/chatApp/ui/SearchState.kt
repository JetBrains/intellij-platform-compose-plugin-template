package org.jetbrains.plugins.template.chatApp.ui

import org.jetbrains.plugins.template.chatApp.model.ChatMessage
import org.jetbrains.plugins.template.chatApp.ui.SearchState.*


/**
 * Represents a search match result within a message.
 *
 * @property searchMatchId Unique identifier for the search match. Typically used to differentiate between multiple matches.
 * @property messageId Identifier of the message containing the search match.
 * @property startInclusive The inclusive start index of the match in the message text.
 * @property endExclusive The exclusive end index of the match in the message text.
 */
data class SearchMatches(
    val searchMatchId: Int,
    val messageId: String,
    val startInclusive: Int,
    val endExclusive: Int
)

/**
 * Represents the current state of a search process, providing different states for idle, active searching,
 * and search results.
 */
sealed class SearchState {
    /**
     * Represents the idle state of the search process where no search operation is currently active.
     * This state is used to indicate that the search is neither in progress nor displaying results.
     */
    object Idle : SearchState()

    /**
     * Represents the active searching state within the search process, holding the search query being processed.
     *
     * @property query The search query entered by the user.
     */
    data class Searching(val query: String) : SearchState()

    /**
     * Represents the result of a search operation. This sealed class provides different possible outcomes for a search,
     * including no results or successfully found matches.
     */
    sealed class SearchResult : SearchState() {
        /**
         * Represents a search result when no matches are found.
         *
         * @property query The search query that resulted in no matches.
         */
        data class None(val query: String) : SearchResult()
        /**
         * Represents a search result that contains one or more matches for a given query.
         *
         * @property query The search query string that was performed.
         * @property selectedSearchMatchId The unique identifier for the currently selected search match, if any.
         * Defaults to -1, indicating no match is currently selected.
         * @property searchMatches A list of all search matches found for the query. Each match provides details
         * including its position within the associated message.
         */
        data class Found(
            val query: String,
            val selectedSearchMatchId: Int = -1,
            val searchMatches: List<SearchMatches> = emptyList()
        ) : SearchResult()
    }
}

/**
 * Indicates whether the current state of the search process is either actively searching or displaying search results.
 *
 * This property returns `true` if the `SearchState` is of type `Searching` or `SearchResult`, denoting that the search
 * process is in an active or result-displaying state. Conversely, it returns `false` when the `SearchState` is idle or
 * in any other non-search-related state.
 */
val SearchState.isSearching: Boolean get() = this is Searching || this is SearchResult

/**
 * Indicates whether the current `SearchState` contains search results.
 * This is `true` when the state is `SearchResult.Found` and the `searchMatches` list is not empty.
 *
 * Use this property to check if a search operation has successfully returned results.
 */
val SearchState.hasResults: Boolean get() = this is SearchResult.Found && searchMatches.isNotEmpty()

/**
 * Retrieves a list of search matches for a specific message identified by its message ID.
 *
 * This method evaluates the current search state and returns the matches if the state contains
 * valid search results. When the search state is either idle, actively searching, or contains
 * no results, an empty list is returned.
 *
 * @param message The message for which search matches are requested.
 * @return A list of [SearchMatches] corresponding to the specified message ID. If no matches
 *         are found, or the current state does not contain valid matches, an empty list is returned.
 */
fun SearchState.searchMatchesIn(message: ChatMessage): List<SearchMatches> = when (this) {
    is Idle, is Searching -> emptyList()
    is SearchResult.None -> emptyList()
    is SearchResult.Found -> searchMatches
}.filter { it.messageId == message.id }

/**
 * Provides the number of matches for the current search state.
 *
 * - When the search state is `Idle` or `Searching`, returns `-1` to indicate that the match count is not available.
 * - When the search state is `SearchResult.None`, returns `0` to indicate no matches were found.
 * - When the search state is `SearchResult.Found`, returns the count of matches in the `searchMatches` list.
 */
val SearchState.searchMatchesCount: Int
    get() = when (this) {
        is Idle, is Searching -> -1
        is SearchResult.None -> 0
        is SearchResult.Found -> searchMatches.size
    }

/**
 * Retrieves the identifier of the currently selected search match, if applicable, based on the current search state.
 *
 * In the following cases, the value is always `-1`:
 * - When the search state is idle and no search operation is ongoing.
 * - When a search operation is currently in progress.
 * - When the search results indicate no matches were found.
 *
 * If the search state represents a successful result (`SearchResult.Found`), this property returns the unique
 * identifier of the currently selected match from the list of found search matches.
 */
val SearchState.selectedSearchMatchId: Int
    get() = when (this) {
        is Idle, is Searching, is SearchResult.None -> -1
        is SearchResult.Found -> selectedSearchMatchId
    }

/**
 * Retrieves the current search query associated with the search state.
 *
 * - For the `Idle` state, this will return `null` as no search is being performed.
 * - For the `Searching` state, it returns the query that is actively being searched.
 * - For the `SearchResult.Found` state, it provides the query that resulted in matches.
 * - For the `SearchResult.None` state, it contains the query that yielded no results.
 *
 * @return The search query string if applicable, or `null` if the state is idle.
 */
val SearchState.searchQuery: String?
    get() = when (this) {
        is Idle -> null
        is Searching -> query
        is SearchResult.Found -> query
        is SearchResult.None -> query
    }

/**
 * Retrieves the currently selected search match, if any, based on the current search state.
 *
 * This property evaluates the current `SearchState` and determines the selected `SearchMatches` item.
 * If the state is `Idle`, `Searching`, or `SearchResult.None`, the value is `null` as there are no valid results.
 * If the state is `SearchResult.Found`, it attempts to return the selected item from the `searchMatches` list
 * using `selectedSearchMatchId` as the index. Returns `null` if the index is invalid or not set.
 *
 * @return The currently selected `SearchMatches` if available, or `null` otherwise.
 */
val SearchState.currentSelectedSearchMatch: SearchMatches?
    get() = when (this) {
        is Idle, is Searching, is SearchResult.None -> null
        is SearchResult.Found -> searchMatches.getOrNull(selectedSearchMatchId)
    }
