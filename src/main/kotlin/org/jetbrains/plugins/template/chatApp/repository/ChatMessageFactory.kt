package org.jetbrains.plugins.template.chatApp.repository

import org.jetbrains.plugins.template.chatApp.model.ChatMessage
import java.time.LocalDateTime


/**
 * Factory class responsible for creating instances of `ChatMessage`.
 *
 * @param aiCompanionName The name of the AI companion, used as the author for AI-generated messages.
 * @param myUserName The name of the user, used as the author for user-generated messages.
 */
class ChatMessageFactory(
    private val aiCompanionName: String,
    private val myUserName: String
) {

    /**
     * Creates a new instance of `ChatMessage` representing an AI-generated message emitted
     * while AI is processing the request.
     *
     * @param content The content of the message.
     * @param timestamp The timestamp of the message. Defaults to the current time.
     */
    fun createAIThinkingMessage(
        content: String,
        timestamp: LocalDateTime = LocalDateTime.now(),
    ): ChatMessage {
        return ChatMessage(
            id = AI_THINKING_MESSAGE_ID,
            content = content,
            author = aiCompanionName,
            timestamp = timestamp,
            isMyMessage = false,
            type = ChatMessage.ChatMessageType.AI_THINKING
        )
    }

    /**
     * Creates a new instance of `ChatMessage` representing an AI-generated message response.
     *
     * @param content The content of the message.
     * @param timestamp The timestamp of the message. Defaults to the current time.
     */
    fun createAIMessage(
        content: String,
        timestamp: LocalDateTime = LocalDateTime.now(),
    ): ChatMessage {
        return ChatMessage(
            content = content,
            author = aiCompanionName,
            timestamp = timestamp,
            isMyMessage = false,
            type = ChatMessage.ChatMessageType.TEXT
        )
    }

    /**
     * Creates a new instance of `ChatMessage` representing a user message.
     *
     * @param content The content of the message.
     * @param timestamp The timestamp of the message. Defaults to the current time.
     */
    fun createUserMessage(content: String, timestamp: LocalDateTime = LocalDateTime.now()): ChatMessage {
        return ChatMessage(
            content = content,
            author = myUserName,
            timestamp = timestamp,
            isMyMessage = true,
            type = ChatMessage.ChatMessageType.TEXT
        )
    }

    companion object {
        private const val AI_THINKING_MESSAGE_ID = "ai-thinking-message-id"
    }
}