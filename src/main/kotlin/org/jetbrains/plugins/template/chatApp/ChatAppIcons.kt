package org.jetbrains.plugins.template.chatApp

import org.jetbrains.jewel.ui.icons.AllIconsKeys

/**
 * Centralized icon keys used by the Chat sample UI.
 * Grouped by feature area to keep call-sites tidy and consistent.
 */
object ChatAppIcons {
    object Header {
        val search = AllIconsKeys.Actions.Find
        val close = AllIconsKeys.Actions.Cancel
    }

    object Prompt {
        val send = AllIconsKeys.RunConfigurations.TestState.Run
        val stop = AllIconsKeys.Run.Stop
    }
}