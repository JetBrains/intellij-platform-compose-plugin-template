package org.jetbrains.plugins.template.chatApp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.modifier.thenIf
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.CircularProgressIndicator
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography
import org.jetbrains.plugins.template.chatApp.ChatAppColors
import org.jetbrains.plugins.template.chatApp.model.ChatMessage

@Composable
fun SentMessageBubble(message: ChatMessage, modifier: Modifier) {
    MessageBubbleImpl(
        message = message,
        modifier = modifier,
        maxWidthFraction = 0.8f,
        padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        alignment = Alignment.CenterEnd,
        bubbleBackgroundColor = ChatAppColors.MessageBubble.myBackground,
        bubbleShape = RoundedCornerShape(6.dp),
        isMatchingSearch = false,
        isHighlightedInSearch = false
    )
}

@Composable
fun ReceivedMessageBubble(message: ChatMessage, modifier: Modifier) {
    MessageBubbleImpl(
        message = message,
        modifier = modifier,
        maxWidthFraction = 1f,
        padding = PaddingValues(vertical = 12.dp),
        alignment = Alignment.CenterStart,
        isMatchingSearch = false,
        isHighlightedInSearch = false
    )
}

@Composable
private fun MessageHeader(message: ChatMessage) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (message.isMyMessage) "Me" else message.author,
            style = JewelTheme.typography.medium,
            color = ChatAppColors.Text.authorName
        )
        Text(
            text = message.formattedTime(), style = JewelTheme.typography.medium, color = ChatAppColors.Text.timestamp
        )
    }
}

@Composable
private fun MessageBubbleImpl(
    message: ChatMessage,
    modifier: Modifier,
    maxWidthFraction: Float = 1f,
    padding: PaddingValues = PaddingValues(0.dp),
    alignment: Alignment = Alignment.Center,
    bubbleBackgroundColor: Color = Color.Transparent,
    bubbleShape: Shape? = null,
    isMatchingSearch: Boolean = false,
    isHighlightedInSearch: Boolean = false
) {
    BoxWithConstraints(modifier = modifier) {
        val maxContentWidth = maxWidth * maxWidthFraction

        Column(
            modifier = Modifier
                .align(alignment)
                .widthIn(max = maxContentWidth)
                .thenIf(bubbleShape != null) { background(bubbleBackgroundColor, bubbleShape!!) }
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MessageHeader(message)

            if (message.isTextMessage()) {
                MessageContent(
                    message = message,
                    isMatchingSearch = isMatchingSearch,
                    isHighlightedInSearch = isHighlightedInSearch,
                )
            } else if (message.isAIThinkingMessage()) {
                LoadingIndicator("Working on it...")
            }
        }
    }
}

@Composable
private fun MessageContent(
    message: ChatMessage,
    isMatchingSearch: Boolean,
    isHighlightedInSearch: Boolean,
) {
    Text(
        text = message.content,
        style = JewelTheme.typography.regular,
        color = ChatAppColors.Text.normal,
    )
}

@Composable
private fun LoadingIndicator(loadingText: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()

        Text(text = loadingText, style = JewelTheme.typography.regular, color = ChatAppColors.Text.loadingMessage)
    }
}
