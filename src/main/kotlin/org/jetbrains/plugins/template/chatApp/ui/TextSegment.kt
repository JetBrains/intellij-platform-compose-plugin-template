package org.jetbrains.plugins.template.chatApp.ui

import androidx.compose.runtime.Immutable

/**
 * Represents a piece of text with an associated styling and optional style placeholder identifier.
 * This sealed interface is designed to define various types of styled text that can be used in the UI.
 */
@Immutable
sealed interface TextSegment {
    /**
     * A unique identifier associated with a style placeholder.
     *
     * This property is used to define distinct style placeholders for various styled text elements.
     **/
    val stylePlaceholderId: String

    /**
     * The text content that needs to be styled.
     *
     * This property represents the raw text associated with a style definition.
     * It is used in the context of the `TextSegment` sealed interface to provide
     * text that can be rendered with specific styling attributes in the UI.
     */
    val textToStyle : String

    /**
     * Represents a segment of plain text with an associated style placeholder identifier.
     *
     * This implementation of the `TextSegment` sealed interface is designed for cases
     * where the text content does not require special styling beyond the default or regular styling.
     * It provides a default style placeholder identifier to represent regular plain text by default.
     *
     * @property textToStyle The plain text content associated with the style definition.
     * @property stylePlaceholderId A unique identifier for the style placeholder, defaulting to "[regular-text-id]".
     */
    data class PlainTextSegment(
        override val textToStyle : String,
        override val stylePlaceholderId: String = "[regular-text-id]"
    ) : TextSegment
    
    /**
     * A data class representing a text segment that should be highlighted as part of a search match result.
     *
     * This class is intended for use in scenarios where specific portions of text need to be visually
     * distinguished to indicate their relevance in a search operation. It associates the text content to be
     * styled with a placeholder identifier, which can be used to apply consistent styling.
     *
     * @property textToStyle The specific portion of text that needs to be highlighted or styled.
     * @property stylePlaceholderId A unique identifier used to apply the desired styling to the text segment.
     * Defaults to "[search-match-text-highlighted-id]".
     */
    data class SearchMatchTextSegmentHighlighted(
        override val textToStyle : String,
        override val stylePlaceholderId: String = "[search-match-text-highlighted-id]"
    ): TextSegment
    
    /**
     * Represents a specific type of text segment that matches a search query
     * and is selected for styling in a user interface.
     *
     * This class extends the [TextSegment] interface and provides functionality
     * to define a text segment that matches a search result. It includes styling
     * information through a placeholder ID.
     *
     * @property textToStyle The text content of the segment that needs styling.
     * @property stylePlaceholderId An identifier for the style placeholder associated with this text segment.
     */
    data class SearchMatchTextSegmentSelected(
        override val textToStyle : String,
        override val stylePlaceholderId: String = "[search-match-text-id]"
    ): TextSegment
}