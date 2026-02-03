package org.jetbrains.plugins.template.chatApp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.plugins.template.chatApp.model.ChatMessage
import org.jetbrains.plugins.template.chatApp.ui.*
import org.jetbrains.plugins.template.chatApp.viewmodel.ChatViewModel
import org.jetbrains.plugins.template.chatApp.viewmodel.MessageInputState
import org.jetbrains.plugins.template.components.CloseIcon

@Composable
fun ChatAppSample(viewModel: ChatViewModel) {
    val chatMessages by viewModel.chatMessagesFlow.collectAsState(emptyList<ChatMessage>())
    val lastMessage by derivedStateOf { chatMessages.lastOrNull() }
    val lastMessageIndex by derivedStateOf { chatMessages.lastIndex }
    val searchState by viewModel.searchChatMessagesHandler().searchStateFlow.collectAsState(SearchState.Idle)
    val messageInputState by viewModel.promptInputState.collectAsState(MessageInputState.Disabled)

    val listState = rememberLazyListState()
    val textFieldState = rememberTextFieldState()

    // Auto-scroll to the bottom when new messages arrive (only when not searching)
    LaunchedEffect(lastMessage?.id, lastMessageIndex) {
        if (chatMessages.isNotEmpty() && !searchState.isSearching) {
            listState.animateScrollToItem(lastMessageIndex)
        }
    }

    // Auto-scroll to the current search result
    LaunchedEffect(searchState.currentSelectedSearchMatch) {
        val selectedSearchMatch = searchState.currentSelectedSearchMatch
        if (selectedSearchMatch != null) {
            val messageIndexInList = chatMessages.indexOfFirst { it.id == selectedSearchMatch.messageId }
            if (messageIndexInList >= 0) {
                listState.animateScrollToItem(messageIndexInList)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatAppColors.Panel.background)
    ) {
        // Chat header with search button
        ChatHeaderWithSearchBar(
            searchState,
            onStartSearch = { viewModel.searchChatMessagesHandler().onStartSearch() },
            onStopSearch = { viewModel.searchChatMessagesHandler().onStopSearch() },
            onSearchQueryChange = { query -> viewModel.searchChatMessagesHandler().onSearchQuery(query) },
            onNextResult = { viewModel.searchChatMessagesHandler().onNavigateToNextSearchResult() },
            onPreviousResult = { viewModel.searchChatMessagesHandler().onNavigateToPreviousSearchResult() })

        // Message area
        ChatList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            chatMessages = chatMessages,
            listState = listState,
            searchState = searchState
        )

        HorizontalDivider()

        PromptInput(
            modifier = Modifier.fillMaxWidth().heightIn(max = 136.dp),
            textFieldState = textFieldState,
            promptInputState = messageInputState,
            onInputChanged = { viewModel.onPromptInputChanged(it) },
            onSend = { viewModel.onSendMessage() },
            onStop = { viewModel.onAbortSendingMessage() }
        )
    }
}

@Composable
private fun HorizontalDivider() {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        orientation = Orientation.Horizontal
    )
}

@Composable
private fun ChatList(
    modifier: Modifier = Modifier,
    chatMessages: List<ChatMessage>,
    listState: LazyListState,
    searchState: SearchState
) {
    Box(modifier = modifier) {
        if (chatMessages.isEmpty()) {
            // Empty state
            EmptyChatListPlaceholder()
        } else {
            VerticallyScrollableContainer(
                modifier = Modifier.fillMaxWidth().safeContentPadding(),
                scrollState = listState,
            ) {
                SelectionContainer {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(chatMessages, key = { it.id }) { message ->
                            if (message.isMyMessage) {
                                SentMessageBubble(
                                    message = message,
                                    modifier = Modifier.fillMaxWidth(),
                                    searchState = searchState
                                )
                            } else {
                                ReceivedMessageBubble(
                                    message = message,
                                    modifier = Modifier.fillMaxWidth(),
                                    searchState = searchState
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyChatListPlaceholder(
    placeholderText: String = "Start a conversation with your AI Assistant!",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = placeholderText,
            style = JewelTheme.defaultTextStyle.copy(
                color = ChatAppColors.Text.disabled,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
private fun ChatHeaderWithSearchBar(
    searchState: SearchState,
    onStartSearch: () -> Unit,
    onStopSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onNextResult: () -> Unit,
    onPreviousResult: () -> Unit
) {
    val showSearchBar = searchState.isSearching

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 24.dp, end = 24.dp, top= 16.dp, bottom = 12.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        ChatHeaderTitle(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { if (showSearchBar) onStopSearch() else onStartSearch() },
        ) {
            Icon(
                ChatAppIcons.Search,
                contentDescription = if (showSearchBar) "Close search" else "Search messages"
            )
        }
    }

    HorizontalDivider()

    // Search bar (shown when search is active)
    if (showSearchBar) {
        ChatSearchBar(
            searchState = searchState,
            onSearchQueryChange = { query -> onSearchQueryChange(query) },
            onNextResult = { onNextResult() },
            onPreviousResult = { onPreviousResult() },
            onCloseSearch = { onStopSearch() }
        )

        HorizontalDivider()
    }

}

@Composable
private fun ChatSearchBar(
    searchState: SearchState,
    modifier: Modifier = Modifier,
    searchFieldPlaceholderText: String = "Search messages...",
    onSearchQueryChange: (String) -> Unit = {},
    onNextResult: () -> Unit = {},
    onPreviousResult: () -> Unit = {},
    onCloseSearch: () -> Unit = {}
) {
    val searchQuery = searchState.searchQuery.orEmpty()
    val hasResults = searchState.hasResults
    val totalResults = searchState.searchMatchesCount
    val currentResultIndex = searchState.selectedSearchMatchId

    val searchFieldState = rememberTextFieldState(searchQuery)
    val isInputFieldEmpty by remember { derivedStateOf { searchFieldState.text.isBlank() } }

    val focusRequester = remember { FocusRequester() }

    // Handle text changes
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()

        snapshotFlow { searchFieldState.text.toString() }
            .distinctUntilChanged()
            .collect { query -> onSearchQueryChange(query) }
    }

    // Handle focus request
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search input field
        TextField(
            state = searchFieldState,
            placeholder = { Text(searchFieldPlaceholderText) },
            modifier = Modifier
                .weight(0.8f)
                .focusRequester(focusRequester = focusRequester)
                .onPreviewKeyEvent { keyEvent ->
                    when {
                        keyEvent.key == Key.Escape && keyEvent.type == KeyEventType.KeyDown -> {
                            onCloseSearch()
                            true
                        }

                        keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown -> {
                            onNextResult()
                            true
                        }

                        keyEvent.key == Key.F3 && keyEvent.type == KeyEventType.KeyDown -> {
                            if (keyEvent.isShiftPressed) {
                                onPreviousResult()
                            } else {
                                onNextResult()
                            }
                            true
                        }

                        else -> false
                    }
                },
            leadingIcon = {
                Icon(ChatAppIcons.Search, contentDescription = null, Modifier.padding(end = 8.dp))
            },
            trailingIcon = {
                if (!isInputFieldEmpty) {
                    CloseIcon { searchFieldState.setTextAndPlaceCursorAtEnd("") }
                }
            },
        )

        // Results counter
        Text(
            modifier = Modifier
                .weight(0.1f),
            textAlign = TextAlign.Center,
            text = when {
                hasResults -> "${currentResultIndex + 1}/$totalResults"
                searchQuery.isNotBlank() -> "No results"
                else -> ""
            },
            color = ChatAppColors.Text.disabled
        )

        // Navigation buttons
        Row(Modifier.weight(0.1f)) {
            IconButton(
                onClick = onPreviousResult,
                enabled = hasResults && totalResults > 1,
            ) {
                Icon(ChatAppIcons.Up, contentDescription = null)
            }

            IconButton(
                onClick = onNextResult,
                enabled = hasResults && totalResults > 1,
            ) {
                Icon(ChatAppIcons.Down, contentDescription = null)
            }

            // Close button
            IconButton(onClick = onCloseSearch) {
                Icon(ChatAppIcons.Close, contentDescription = "Close search")
            }
        }
    }
}