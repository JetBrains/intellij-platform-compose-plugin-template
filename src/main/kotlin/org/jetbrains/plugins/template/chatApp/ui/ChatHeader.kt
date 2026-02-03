package org.jetbrains.plugins.template.chatApp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.annotations.Nls
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.ui.typography


@Composable
fun ChatHeaderTitle(
    modifier: Modifier = Modifier,
    title: String = "AI Assistant Chat",
    subtitle: String = "Chat with your AI Assistant! Ask questions, get help, \u2028or just have a conversation"
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ChatHeaderTitle(text = title)
        ChatHeaderSubtitle(text = subtitle)
    }
}

@Composable
private fun ChatHeaderTitle(@Nls text: String) {
    Text(
        text = text,
        style = JewelTheme.typography.h2TextStyle,
        color = JewelTheme.colorPalette.grayOrNull(12) ?: JewelTheme.defaultTextStyle.color
    )
}

@Composable
private fun ChatHeaderSubtitle(
    @Nls text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = JewelTheme.typography.medium,
        color = JewelTheme.colorPalette.grayOrNull(7) ?: JewelTheme.consoleTextStyle.color,
        modifier = modifier.padding(top = 4.dp)
    )
}