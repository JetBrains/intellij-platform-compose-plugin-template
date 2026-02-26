package org.jetbrains.plugins.template.weatherApp.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.icon.IconKey

/**
 * An icon button designed for use on colored card surfaces.
 *
 * Unlike the standard Jewel [ActionButton], this button derives its hover and focus
 * overlay colors from [tint], ensuring the interaction states blend naturally with
 * any card background color rather than using theme-level opaque backgrounds.
 *
 * @param iconKey The icon to display.
 * @param contentDescription Accessibility description of the button action.
 * @param onClick Called when the button is clicked.
 * @param modifier Modifier applied to the button.
 * @param tint Icon tint and base color for hover/focus overlays. Defaults to [Color.White].
 * @param tooltip Tooltip content shown on hover/focus.
 */
@Composable
fun CardIconButton(
    iconKey: IconKey,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    tooltip: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val shape = RoundedCornerShape(8.dp)

    Tooltip(tooltip = tooltip) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(if (isFocused || isHovered) tint.copy(alpha = 0.20f) else Color.Transparent)
                .then(if (isFocused) Modifier.border(1.dp, tint.copy(alpha = 0.60f), shape) else Modifier)
                .hoverable(interactionSource)
                .focusable(interactionSource = interactionSource)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(key = iconKey, contentDescription = contentDescription, tint = tint)
        }
    }
}
