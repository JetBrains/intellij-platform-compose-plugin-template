package org.jetbrains.plugins.template.chatApp.viewmodel

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jetbrains.plugins.template.chatApp.model.ChatMessage
import org.jetbrains.plugins.template.chatApp.repository.ChatRepository
import org.jetbrains.plugins.template.chatApp.repository.ChatRepositoryApi

interface ChatViewModelApi : Disposable {
    val chatMessagesFlow: StateFlow<List<ChatMessage>>

    fun onPromptInputChanged(input: String)

    fun onSendMessage()

    fun onAbortSendingMessage()

    fun searchChatMessagesHandler(): SearchChatMessagesHandler

    val promptInputState: StateFlow<MessageInputState>
}

class ChatViewModel(
    private val coroutineScope: CoroutineScope,
    private val repository: ChatRepositoryApi
) : ChatViewModelApi {

    private val _chatMessagesFlow = MutableStateFlow(emptyList<ChatMessage>())

    override val chatMessagesFlow: StateFlow<List<ChatMessage>> = _chatMessagesFlow.asStateFlow()

    private val _promptInputState = MutableStateFlow<MessageInputState>(MessageInputState.Disabled)
    override val promptInputState: StateFlow<MessageInputState> = _promptInputState.asStateFlow()

    private val searchChatMessagesHandler: SearchChatMessagesHandler = SearchChatMessagesHandlerImpl(
        coroutineScope = coroutineScope,
        messagesFlow = repository.messagesFlow
    )

    /**
     * A nullable [Job] instance used to manage the coroutine responsible for sending a message.
     * This property holds a reference to the currently active job related to the `onSendMessage`
     * operation in the [ChatViewModel]. It enables tracking, cancellation, and lifecycle management
     * of the message sending process.
     */
    private var currentSendMessageJob: Job? = null

    init {
        // Emit all messages from the repository to the UI
        repository
            .messagesFlow
            .onEach { messages -> _chatMessagesFlow.value = messages }
            .launchIn(coroutineScope)
    }

    override fun onPromptInputChanged(input: String) {
        val currentPromptInputState = _promptInputState.value
        _promptInputState.value = when {
            currentPromptInputState is MessageInputState.Sending -> MessageInputState.Sending(input)
            input.isEmpty() -> MessageInputState.Disabled
            else -> MessageInputState.Enabled(input)
        }
    }

    override fun onSendMessage() {
        currentSendMessageJob = coroutineScope.launch {
            try {
                val currentUserMessage = getCurrentInputTextIfNotEmpty() ?: return@launch
                emitPromptInputState(MessageInputState.Sending(""))

                repository.sendMessage(currentUserMessage)

                emitPromptInputState(
                    when (val currentInputState = getCurrentInputTextIfNotEmpty()) {
                        null -> MessageInputState.Disabled
                        else -> MessageInputState.Enabled(currentInputState)
                    }
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                emitPromptInputState(MessageInputState.SendFailed(e.message ?: "Unknown error", e))
            }
        }
    }

    override fun onAbortSendingMessage() {
        currentSendMessageJob?.cancel()

        emitPromptInputState(
            when (val currentPromptInput = getCurrentInputTextIfNotEmpty()) {
                null -> MessageInputState.Disabled
                else -> MessageInputState.Enabled(currentPromptInput)
            }
        )
    }

    override fun searchChatMessagesHandler(): SearchChatMessagesHandler = searchChatMessagesHandler

    override fun dispose() {
        coroutineScope.cancel()
    }

    private fun emitPromptInputState(state: MessageInputState) {
        _promptInputState.value = state
    }

    private fun getCurrentInputTextIfNotEmpty(): String? = _promptInputState.value.inputText.takeIf { it.isNotBlank() }
}

@Service(Service.Level.PROJECT)
class ChatViewModelFactory(
    @Suppress("unused") private val project: Project,
    private val coroutineScope: CoroutineScope
) {
    fun create(): ChatViewModel = ChatViewModel(coroutineScope, service<ChatRepository>())
}