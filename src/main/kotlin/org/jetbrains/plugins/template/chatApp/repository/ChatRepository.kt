package org.jetbrains.plugins.template.chatApp.repository

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.plugins.template.chatApp.model.ChatMessage
import java.time.LocalDateTime

/**
 * Interface defining the contract for managing chat messages and interactions within a chat system.
 * Provides access to the flow of messages and supports operations for sending and editing chat messages.
 */
interface ChatRepositoryApi {
    /**
     * Flow that emits a list of chat messages.
     * Updates with new messages as they are received or edited.
     */
    val messagesFlow: StateFlow<List<ChatMessage>>

    /**
     * Sends a message with the provided content.
     *
     * @param messageContent The content of the message to be sent.
     */
    suspend fun sendMessage(messageContent: String)
}

@Service
class ChatRepository : ChatRepositoryApi {

    private val chatMessageFactory = ChatMessageFactory("AI Buddy", "Me")
    private val aiResponseGenerator = AIResponseGenerator()
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            chatMessageFactory.createAIMessage(
                content = "Hello! I'm your AI Buddy. I'm here to help and chat with you about anything you'd like to discuss. How are you doing today?",
                timestamp = LocalDateTime.now().minusMinutes(30),
            ),
            chatMessageFactory.createAIMessage(
                content = "Feel free to ask me questions, share your thoughts, or just have a casual conversation. I'm designed to provide helpful and engaging responses!",
                timestamp = LocalDateTime.now().minusMinutes(25),
            ),
            chatMessageFactory.createAIMessage(
                content = "I can help with a wide variety of topics - from coding and technical questions to creative writing, analysis, math problems, or just friendly chat. What interests you?",
                timestamp = LocalDateTime.now().minusMinutes(20),
            )
        )
    )

    override val messagesFlow: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    override suspend fun sendMessage(messageContent: String) {
        withContext(Dispatchers.IO) {
            try {
                // Emits the user message to a chat list
                _messages.value += chatMessageFactory.createUserMessage(messageContent)

                // Simulate AI responding
                simulateAIResponse(messageContent)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // In case the message sending is canceled before a response is generated,
                    // we remove a loading placeholder message
                    _messages.value = _messages.value.filter { !it.isAIThinkingMessage() }

                    throw e

                }

                e.printStackTrace()
            }
        }
    }

    private suspend fun simulateAIResponse(userMessage: String) {
        val aiThinkingMessage = chatMessageFactory
            .createAIThinkingMessage("Hm, let me think about that...")
        _messages.value += aiThinkingMessage

        // Simulate delay for the AI response
        delay(2000 + (500..2000).random().toLong()) // Random delay between 2.5-4 seconds

        val responseMessage =
            chatMessageFactory.createAIMessage(content = aiResponseGenerator.generateAIResponse(userMessage))

        _messages.value = _messages.value
            .map { message -> if (message.id == aiThinkingMessage.id) responseMessage else message }
    }
}
