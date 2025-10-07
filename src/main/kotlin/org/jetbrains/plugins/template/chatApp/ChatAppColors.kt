package org.jetbrains.plugins.template.chatApp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.theme.defaultBannerStyle

object ChatAppColors {
    object Panel {
        val background: Color
            @Composable get() = JewelTheme.globalColors.panelBackground
    }

    object Text {
        val disabled: Color
            @Composable get() = JewelTheme.globalColors.text.disabled

        val normal: Color
            @Composable get() = JewelTheme.globalColors.text.normal

        // Misc labels
        val timestamp: Color = Color.LightGray.copy(alpha = 0.8f)

        val authorName: Color = Color(0xDBE0EBFF)
    }

    object MessageBubble {
        // Backgrounds
        val myBackground: Color
            @Composable get() = JewelTheme.defaultBannerStyle.information.colors.background.copy(alpha = 0.75f)

        val othersBackground: Color
            @Composable get() = JewelTheme.defaultBannerStyle.success.colors.background.copy(alpha = 0.75f)

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

    object Prompt {
        val border: Color = Color.White
    }

    object Icon {
        val enabledIconTint: Color = Color.White
        val disabledIconTint: Color = Color.Gray
        val stopIconTint: Color = Color.White
    }
}