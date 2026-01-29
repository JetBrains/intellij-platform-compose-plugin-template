package org.jetbrains.plugins.template.chatApp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.intellij.ui.JBColor
import org.jetbrains.jewel.bridge.toComposeColor
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.theme.colorPalette

object ChatAppColors {
    object Panel {
        val background: Color
            @Composable get() = JewelTheme.globalColors.panelBackground
    }

    object Text {
        val disabled: Color
            @Composable get() = JewelTheme.colorPalette.grayOrNull(7) ?: JewelTheme.globalColors.text.disabled

        val normal: Color
            @Composable get() = JewelTheme.globalColors.text.normal

        val labelForeground: Color
            @Composable get() = JBColor.namedColor("Label.foreground", 0xDFE1E5).toComposeColor()

        val labelInfoForeground: Color
            @Composable get() = JBColor.namedColor("Label.infoForeground", 0x6F737A).toComposeColor()

        val timestamp: Color
            @Composable get() = labelInfoForeground

        val authorName: Color
            @Composable get() = labelForeground
    }

    object MessageBubble {
        val myBackground: Color
            @Composable get() = JBColor.namedColor("HelpBrowser.UserMessage.background", 0xDFE1E5)
                .toComposeColor()
    }
}