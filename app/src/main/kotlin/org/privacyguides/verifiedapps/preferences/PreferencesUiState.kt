package org.privacyguides.verifiedapps.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey

/**
 * Immutable snapshot of the boolean user preferences. Each value defaults to false until the
 * DataStore is read. The DataStore keys are stateless constants and live in [Keys].
 */
data class PreferencesUiState(
    /** Whether to show hasMultipleSigners. */
    val showHasMultipleSigners: Boolean = false,

    /** Whether to show share and copy verification info on the verify app screen. */
    val showSharingTools: Boolean = false,

    /** Whether to always show the GitHub submission button on the verify app screen. */
    val alwaysShowGitHubSubmit: Boolean = false,

    /** Whether to show the Codeberg submission button on the verify app screen. */
    val showCodebergSubmit: Boolean = false,

    /** Whether to include system apps in the app list. */
    val showSystemApps: Boolean = false,

    /** Use Material You dynamic color from the system wallpaper (Android 12+). */
    val dynamicColor: Boolean = false,

    /** Pitch black background. */
    val pitchBlackBackground: Boolean = false,
) {
    object Keys {
        val SHOW_HAS_MULTIPLE_SIGNERS = booleanPreferencesKey("SHOW_HAS_MULTIPLE_SIGNERS")
        val SHOW_SHARING_TOOLS = booleanPreferencesKey("SHOW_SHARING_TOOLS")
        val ALWAYS_SHOW_GITHUB_SUBMIT = booleanPreferencesKey("ALWAYS_SHOW_GITHUB_SUBMIT")
        val SHOW_CODEBERG_SUBMIT = booleanPreferencesKey("SHOW_CODEBERG_SUBMIT")
        val SHOW_SYSTEM_APPS = booleanPreferencesKey("SHOW_SYSTEM_APPS")
        val DYNAMIC_COLOR = booleanPreferencesKey("DYNAMIC_COLOR")
        val PITCH_BLACK_BACKGROUND = booleanPreferencesKey("PITCH_BLACK_BACKGROUND")
    }
}
