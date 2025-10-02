package org.jetbrains.plugins.template.chatApp.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.theme.iconButtonStyle
import org.jetbrains.plugins.template.chatApp.ChatAppColors
import org.jetbrains.plugins.template.chatApp.ChatAppIcons
import org.jetbrains.plugins.template.chatApp.viewmodel.MessageInputState
import org.jetbrains.plugins.template.chatApp.viewmodel.isSending

@Composable
fun ChatSearchBarPreview() {
    val state = remember { mutableStateOf<MessageInputState>(MessageInputState.Enabled("")) }
    val textFieldState = rememberTextFieldState()

    PromptInput(
        Modifier
            .fillMaxWidth()
            .heightIn(max = 120.dp),
        promptInputState = state.value,
        textFieldState = textFieldState,
        onInputChanged = {
            state.value = if (it.isNotBlank()) MessageInputState.Enabled(it) else MessageInputState.Disabled
        },
        onSend = { text ->
            if (state.value is MessageInputState.Sending) {
                state.value = MessageInputState.Disabled
            } else {
                state.value = MessageInputState.Sending(text)
            }
        },
    )
}

@Composable
fun PromptInput(
    modifier: Modifier = Modifier,
    promptInputState: MessageInputState = MessageInputState.Disabled,
    textFieldState: TextFieldState = rememberTextFieldState(),
    hint: String = "Whats on your mind...",
    onInputChanged: (String) -> Unit = {},
    onSend: (String) -> Unit = {},
    onStop: (String) -> Unit = {}
) {
    val isSending = promptInputState.isSending
    var skipInputChangeUpdate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow { textFieldState.text }
            .distinctUntilChanged()
            .collect { inputText ->
                if (skipInputChangeUpdate) {
                    skipInputChangeUpdate = false
                    return@collect
                }

                onInputChanged(inputText.toString())
            }
    }

    Column(
        modifier
            .border(1.dp, ChatAppColors.Prompt.border, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .padding(8.dp),
    ) {
        TextArea(
            state = textFieldState,
            modifier = Modifier
                .weight(0.75f)
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown) {
                        if (keyEvent.isShiftPressed) {
                            // Shift+Enter for new line - let default behavior handle it
                            skipInputChangeUpdate = true
                            textFieldState.setTextAndPlaceCursorAtEnd("${textFieldState.text}\n")
                            false
                        } else {
                            // Enter to send/update message
                            val message = textFieldState.text
                            if (message.isNotBlank()) {
                                if (isSending) {
                                    onStop(message.toString())
                                } else {
                                    onSend(message.toString())
                                    skipInputChangeUpdate = true
                                    textFieldState.setTextAndPlaceCursorAtEnd("")
                                }
                            }
                            true
                        }
                    } else {
                        false
                    }
                },
            placeholder = { Text(hint) },
        )

        Row(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            when (promptInputState) {
                MessageInputState.Disabled,
                is MessageInputState.Enabled,
                is MessageInputState.SendFailed,
                is MessageInputState.Sent -> {
                    DefaultButton(
                        modifier = Modifier.wrapContentSize(),
                        enabled = promptInputState != MessageInputState.Disabled,
                        onClick = {
                            onSend(textFieldState.text.toString())
                            skipInputChangeUpdate = true
                            textFieldState.setTextAndPlaceCursorAtEnd("")
                        },
                        content = {
                            Row(
                                Modifier.padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                Text("Send")

                                Icon(
                                    modifier = Modifier.size(JewelTheme.iconButtonStyle.metrics.minSize.height),
                                    key = ChatAppIcons.Prompt.send,
                                    contentDescription = "Send",
                                    tint = if (promptInputState != MessageInputState.Disabled) ChatAppColors.Icon.enabledIconTint else ChatAppColors.Icon.disabledIconTint
                                )
                            }
                        }
                    )
                }

                is MessageInputState.Sending -> {
                    OutlinedButton(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            onStop(textFieldState.text.toString())
                        },
                        content = {
                            Row(
                                Modifier.padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                Text("Stop")

                                Icon(
                                    modifier = Modifier.size(JewelTheme.iconButtonStyle.metrics.minSize.height),
                                    key = ChatAppIcons.Prompt.stop,
                                    contentDescription = "Stop sending",
                                    tint = ChatAppColors.Icon.stopIconTint
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
