package org.jetbrains.plugins.template.chatApp.ui

import org.jetbrains.plugins.template.chatApp.ui.SearchState.*

sealed class SearchState {
    object Idle : SearchState()

    data class Searching(val query: String) : SearchState()

    data class SearchResults(
        val query: String = "",
        // List of message IDs that match search
        val searchResultIds: List<String> = emptyList(),
        val currentSelectedSearchResultIndex: Int = -1
    ) : SearchState()
}

val SearchState.isSearching: Boolean get() = this is Searching || this is SearchResults

val SearchState.hasResults: Boolean get() = this is SearchResults && searchResultIds.isNotEmpty()

val SearchState.totalResults: Int
    get() = when (this) {
        is Idle, is Searching -> -1
        is SearchResults -> searchResultIds.size
    }

val SearchState.currentSearchResultIndex: Int
    get() = when (this) {
        is Idle, is Searching -> -1
        is SearchResults -> currentSelectedSearchResultIndex
    }

val SearchState.searchQuery: String?
    get() = when (this) {
        is Idle -> null
        is Searching -> query
        is SearchResults -> query
    }

val SearchState.searchResultIds: List<String>
    get() = when (this) {
        is Idle -> emptyList()
        is Searching -> emptyList()
        is SearchResults -> searchResultIds
    }

// Corresponds to chat message id
val SearchState.currentSelectedSearchResultId: String? get() = searchResultIds.getOrNull(currentSearchResultIndex)
