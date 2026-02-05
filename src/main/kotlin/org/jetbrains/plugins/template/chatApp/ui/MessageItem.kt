package org.jetbrains.plugins.template.chatApp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.modifier.thenIf
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.CircularProgressIndicator
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography
import org.jetbrains.plugins.template.chatApp.ChatAppColors
import org.jetbrains.plugins.template.chatApp.model.ChatMessage

@Composable
fun SentMessageBubble(
    message: ChatMessage,
    searchState: SearchState?,
    modifier: Modifier = Modifier
) {
    MessageBubbleImpl(
        message = message,
        searchState = searchState,
        modifier = modifier,
        maxWidthFraction = 0.8f,
        padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        alignment = Alignment.CenterEnd,
        bubbleBackgroundColor = ChatAppColors.MessageBubble.myBackground,
        bubbleShape = RoundedCornerShape(6.dp)
    )
}

@Composable
fun ReceivedMessageBubble(
    message: ChatMessage,
    searchState: SearchState?,
    modifier: Modifier = Modifier
) {
    MessageBubbleImpl(
        message = message,
        searchState = searchState,
        modifier = modifier,
        padding = PaddingValues(vertical = 12.dp),
        alignment = Alignment.CenterStart,
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
    searchState: SearchState?,
    modifier: Modifier = Modifier,
    maxWidthFraction: Float = 1f,
    padding: PaddingValues = PaddingValues(0.dp),
    alignment: Alignment = Alignment.Center,
    bubbleBackgroundColor: Color = Color.Transparent,
    bubbleShape: Shape? = null
) {
    BoxWithConstraints(modifier = modifier) {
        val maxContentWidth = maxWidth * maxWidthFraction

        Column(
            modifier = Modifier
                .align(alignment)
                .widthIn(max = maxContentWidth)
                .thenIf(bubbleShape != null) {
                    background(bubbleBackgroundColor, bubbleShape!!)
                }.padding(padding),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MessageHeader(message)

            if (message.isTextMessage()) {
                val selectedSearchMatchId = searchState?.selectedSearchMatchId ?: -1
                val searchMatchesInMessage = searchState?.searchMatchesIn(message) ?: emptyList()

                val styledTextSegments = remember(message.content, searchMatchesInMessage, selectedSearchMatchId) {
                    message.content.computeStyledTextSegments(
                        selectedSearchMatchId = selectedSearchMatchId,
                        searchMatches = searchMatchesInMessage
                    )
                }

                MessageContent(styledTextSegments)
            } else if (message.isAIThinkingMessage()) {
                LoadingIndicator("Working on it...")
            }
        }
    }
}

private fun String.computeStyledTextSegments(
    selectedSearchMatchId: Int,
    searchMatches: List<SearchMatches>
): List<TextSegment> {
    val message = this

    if (searchMatches.isEmpty()) {
        return listOf(TextSegment.PlainTextSegment(textToStyle = message))
    }

    val segments = mutableListOf<TextSegment>()

    var cursor = 0

    for (match in searchMatches) {
        val start = match.startInclusive
        val end = match.endExclusive

        if (cursor < start) {
            segments += TextSegment.PlainTextSegment(message.substring(cursor, start))
        }

        segments += if (match.searchMatchId == selectedSearchMatchId) {
            TextSegment.SearchMatchTextSegmentSelected(
                stylePlaceholderId = "match_${match.searchMatchId}",
                textToStyle = message.substring(start, end),
            )
        } else {
            TextSegment.SearchMatchTextSegmentHighlighted(
                stylePlaceholderId = "match_${match.searchMatchId}",
                textToStyle = message.substring(start, end),
            )
        }
        cursor = end
    }

    if (cursor < message.length) {
        segments += TextSegment.PlainTextSegment(textToStyle = message.substring(cursor))
    }

    return segments
}

@Composable
private fun MessageContent(textSegments: List<TextSegment>) {
    val measurer: TextMeasurer = rememberTextMeasurer()
    val inlineContent = mutableMapOf<String, InlineTextContent>()
    val builder = AnnotatedString.Builder()

    textSegments.forEach { textSegment ->
        when (textSegment) {
            is TextSegment.PlainTextSegment -> builder.append(textSegment.textToStyle)
            is TextSegment.SearchMatchTextSegmentSelected -> {
                builder.appendInlineContent(textSegment.stylePlaceholderId)

                inlineContent[textSegment.stylePlaceholderId] = InlineTextContent(
                    placeholder = createPlaceholderForText(
                        textSegment.textToStyle,
                        JewelTheme.typography.regular,
                        measurer,
                    )
                ) {
                    SelectedHighlightedText(textSegment)
                }
            }

            is TextSegment.SearchMatchTextSegmentHighlighted -> {
                builder.appendInlineContent(textSegment.stylePlaceholderId)

                inlineContent[textSegment.stylePlaceholderId] = InlineTextContent(
                    placeholder = createPlaceholderForText(
                        textSegment.textToStyle,
                        JewelTheme.typography.regular,
                        measurer,
                    )
                ) {
                    HighlightedText(textSegment)
                }
            }
        }
    }

    Text(
        text = builder.toAnnotatedString(),
        inlineContent = inlineContent
    )
}

@Composable
private fun HighlightedText(textSegment: TextSegment) {
    Text(
        modifier = Modifier.drawWithContent {
            drawContent()
            drawRoundRect(
                color = ChatAppColors.Search.highlightedWordBackground,
                size = size,
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
        },
        text = textSegment.textToStyle,
        style = JewelTheme.typography.regular,
        color = ChatAppColors.Search.highlightedWordText
    )
}

@Composable
private fun SelectedHighlightedText(textSegment: TextSegment) {
    val backgroundColor = ChatAppColors.Search.selectedWordBackground
    Text(
        modifier = Modifier.drawWithContent {
            drawRoundRect(
                color = backgroundColor,
                size = size,
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Fill
            )
            drawContent()
        },
        text = textSegment.textToStyle,
        style = JewelTheme.typography.regular,
        color = ChatAppColors.Search.selectedHighlightedWordText)
}

@Composable
fun createPlaceholderForText(
    text: String,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer,
): Placeholder {

    val density = LocalDensity.current

    val layoutResult = textMeasurer.measure(
        text = AnnotatedString(text),
        style = textStyle,
        softWrap = true
    )

    val sizePx = layoutResult.size

    return with(density) {
        Placeholder(
            width = sizePx.width.toSp(),
            height = sizePx.height.toSp(),
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    }
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
