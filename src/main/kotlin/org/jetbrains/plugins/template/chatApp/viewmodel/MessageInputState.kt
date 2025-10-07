package org.jetbrains.plugins.template.chatApp.viewmodel

sealed class MessageInputState(val inputText: String) {
    object Disabled : MessageInputState("")

    data class Enabled(val text: String) : MessageInputState(text)

    data class Sending(val messageText: String) : MessageInputState(messageText)

    data class Sent(val messageText: String) : MessageInputState(messageText)

    data class SendFailed(val messageText: String, val throwable: Throwable) : MessageInputState(messageText)
}

val MessageInputState.isSending: Boolean get() = this is MessageInputState.Sending