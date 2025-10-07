package org.jetbrains.plugins.template.chatApp.viewmodel

import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.plugins.template.chatApp.ui.SearchState

/**
 * Interface that handles the process of searching for chat messages.
 * Provides functionality for initiating, stopping, and performing searches,
 * as well as navigating between search results.
 */
interface SearchChatMessagesHandler {

    /**
     * A [StateFlow] that represents the current state of the chat message search functionality.
     * It emits instances of [SearchState] to reflect the ongoing state of search operations.
     *
     * This flow can emit the following states:
     * - [SearchState.Idle]: Indicates no active search operation is ongoing.
     * - [SearchState.Searching]: Represents an active search operation with the associated query.
     * - [SearchState.SearchResults]: Contains the results of the search operation, including the matching
     *   message IDs, the query used, and the index of the currently selected search result.
     *
     * This property is intended to be observed by consumers to react to search state changes
     * and provide appropriate updates to the UI or other components.
     */
    val searchStateFlow: StateFlow<SearchState>

    fun onStartSearch()

    fun onStopSearch()

    // Search functionality
    fun onSearchQuery(query: String)

    fun onNavigateToNextSearchResult()

    fun onNavigateToPreviousSearchResult()
}