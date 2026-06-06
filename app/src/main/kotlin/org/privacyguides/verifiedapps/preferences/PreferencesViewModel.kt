package org.privacyguides.verifiedapps.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.privacyguides.verifiedapps.preferences.PreferencesUiState.Keys
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PreferencesViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = _uiState.asStateFlow()

    private val _preferencesLoaded = MutableStateFlow(false)
    val preferencesLoaded: StateFlow<Boolean> = _preferencesLoaded.asStateFlow()

    init {
        viewModelScope.launch {
            populateSettingsFromDatastore()
        }
    }

    private suspend fun populateSettingsFromDatastore() {
        dataStore.data.collect { settings ->
            _uiState.update {
                PreferencesUiState(
                    showHasMultipleSigners = settings[Keys.SHOW_HAS_MULTIPLE_SIGNERS] ?: false,
                    showSharingTools = settings[Keys.SHOW_SHARING_TOOLS] ?: false,
                    alwaysShowGitHubSubmit = settings[Keys.ALWAYS_SHOW_GITHUB_SUBMIT] ?: false,
                    showCodebergSubmit = settings[Keys.SHOW_CODEBERG_SUBMIT] ?: false,
                    showSystemApps = settings[Keys.SHOW_SYSTEM_APPS] ?: false,
                    dynamicColor = settings[Keys.DYNAMIC_COLOR] ?: false,
                    pitchBlackBackground = settings[Keys.PITCH_BLACK_BACKGROUND] ?: false,
                )
            }
            _preferencesLoaded.value = true
        }
    }

    suspend fun setPreference(key: Preferences.Key<Boolean>, value: Boolean) {
        updateLocalPreference(key, value)
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    private fun updateLocalPreference(key: Preferences.Key<Boolean>, value: Boolean) {
        _uiState.update { state ->
            when (key) {
                Keys.SHOW_HAS_MULTIPLE_SIGNERS -> state.copy(showHasMultipleSigners = value)
                Keys.SHOW_SHARING_TOOLS -> state.copy(showSharingTools = value)
                Keys.ALWAYS_SHOW_GITHUB_SUBMIT -> state.copy(alwaysShowGitHubSubmit = value)
                Keys.SHOW_CODEBERG_SUBMIT -> state.copy(showCodebergSubmit = value)
                Keys.SHOW_SYSTEM_APPS -> state.copy(showSystemApps = value)
                Keys.DYNAMIC_COLOR -> state.copy(dynamicColor = value)
                Keys.PITCH_BLACK_BACKGROUND -> state.copy(pitchBlackBackground = value)
                else -> state
            }
        }
    }

    class PreferencesViewModelFactory(
        private val dataStore: DataStore<Preferences>,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PreferencesViewModel(dataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }
    }
}
