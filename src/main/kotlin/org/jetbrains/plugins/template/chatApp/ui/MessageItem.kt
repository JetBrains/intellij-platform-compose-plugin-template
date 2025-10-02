package org.jetbrains.plugins.template.chatApp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.plugins.template.chatApp.ChatAppColors
import org.jetbrains.plugins.template.chatApp.model.ChatMessage
import org.jetbrains.plugins.template.components.PulsingText

@Composable
fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    isMatchingSearch: Boolean = false,
    isHighlightedInSearch: Boolean = false
) {
    val isMyMessage = message.isMyMessage
    val messageShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isMyMessage) 16.dp else 6.dp,
        bottomEnd = if (isMyMessage) 6.dp else 16.dp
    )
    val messageBackgroundColor = when {
        isHighlightedInSearch && isMyMessage -> ChatAppColors.MessageBubble.mySearchHighlightedBackground
        isHighlightedInSearch && !isMyMessage -> ChatAppColors.MessageBubble.othersSearchHighlightedBackground
        isMyMessage -> ChatAppColors.MessageBubble.myBackground
        else -> ChatAppColors.MessageBubble.othersBackground
    }

    Row(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 120.dp, max = 420.dp)
                .wrapContentSize()
                .background(messageBackgroundColor, messageShape)
                .messageBorder(messageShape, isMyMessage, isHighlightedInSearch, isMatchingSearch)
                .padding(16.dp)
        ) {
            AuthorName(message)

            if (message.isTextMessage()) {
                MessageContent(message)

                TimeStampLabel(message)
            } else if (message.isAIThinkingMessage()) {
                PulsingText(message.content, isLoading = true)
            } else {
                Unit
            }
        }
    }
}

@Composable
private fun TimeStampLabel(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message.formattedTime(),
            style = JewelTheme.editorTextStyle.copy(fontSize = 12.sp),
            color = ChatAppColors.Text.timestamp
        )
    }
}

@Composable
private fun MessageContent(message: ChatMessage) {
    Text(
        text = message.content,
        style = JewelTheme.defaultTextStyle.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = ChatAppColors.Text.normal,
            lineHeight = 20.sp
        ),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun AuthorName(message: ChatMessage) {
    Text(
        text = if (message.isMyMessage) "Me" else message.author,
        style = JewelTheme.defaultTextStyle.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = ChatAppColors.Text.authorName
        ),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun Modifier.messageBorder(
    shape: Shape,
    isMyMessage: Boolean,
    isHighlightedInSearch: Boolean,
    isMatchingSearch: Boolean
) = border(
    width = if (isMyMessage) 0.dp else 1.dp,
    color = when {
        isHighlightedInSearch -> ChatAppColors.MessageBubble.searchHighlightedBackgroundBorder
        isMatchingSearch && isMyMessage -> ChatAppColors.MessageBubble.matchingMyBorder
        isMatchingSearch && !isMyMessage -> ChatAppColors.MessageBubble.matchingOthersBorder
        isMyMessage -> ChatAppColors.MessageBubble.myBackgroundBorder
        else -> ChatAppColors.MessageBubble.othersBackgroundBorder
    },
    shape = shape
)