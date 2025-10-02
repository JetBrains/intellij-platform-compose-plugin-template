package org.jetbrains.plugins.template.chatApp.viewmodel

import org.jetbrains.plugins.template.chatApp.model.ChatMessage

data class ChatListUiState(
    val messages: List<ChatMessage> = emptyList(),
) {
    companion object Companion {
        val EMPTY = ChatListUiState()
    }
}