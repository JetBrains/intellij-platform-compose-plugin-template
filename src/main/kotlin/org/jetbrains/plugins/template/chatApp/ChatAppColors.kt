package org.jetbrains.plugins.template.chatApp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.intellij.ui.JBColor
import org.jetbrains.jewel.bridge.toComposeColor
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.ui.theme.defaultBannerStyle

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

        // Misc labels
        val timestamp: Color = Color.LightGray.copy(alpha = 0.8f)

        val authorName: Color = Color(0xDBE0EBFF)
    }

    object MessageBubble {
        // Backgrounds
        val myBackground: Color
            @Composable get() = JBColor.namedColor("HelpBrowser.UserMessage.background", 0xDFE1E5)
                .toComposeColor()

        val othersBackground: Color
            @Composable get() = Color.Transparent

        // Borders
        val myBackgroundBorder: Color
            @Composable get() = JewelTheme.defaultBannerStyle.information.colors.border

        val othersBackgroundBorder: Color
            @Composable get() = JewelTheme.defaultBannerStyle.success.colors.border

        // Search highlight state
        val mySearchHighlightedBackground: Color
            @Composable get() = JewelTheme.defaultBannerStyle.information.colors.background

        // Search highlight state
        val othersSearchHighlightedBackground: Color
            @Composable get() = JewelTheme.defaultBannerStyle.success.colors.background

        val searchHighlightedBackgroundBorder: Color = Color(0xFFDF9303)

        val matchingMyBorder: Color
            @Composable get() = JewelTheme.defaultBannerStyle.information.colors.border.copy(alpha = 0.75f)

        val matchingOthersBorder: Color
            @Composable get() = JewelTheme.defaultBannerStyle.success.colors.border.copy(alpha = 0.75f)

    }
}