package org.privacyguides.verifiedapps.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.privacyguides.verifiedapps.R
import org.privacyguides.verifiedapps.preferences.PreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewPrivacyPolicyAndLicense(
    preferencesViewModel: PreferencesViewModel,
) {
    val preferencesUiState by preferencesViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var checked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(R.string.review_privacy_policy_and_license))
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(innerPadding),
        ) {
            Text(
                text = stringResource(R.string.full_privacy_policy) + "\n\n\n\n" +
                    stringResource(R.string.full_license) + "\n\n\n\n",
                style = MaterialTheme.typography.bodyMedium,
            )
            ListItem(
                modifier = Modifier.toggleable(
                    value = checked,
                    role = Role.Checkbox,
                    onValueChange = { checked = it },
                ),
                headlineContent = {
                    Text(
                        text = stringResource(R.string.privacy_policy_and_license_checkbox_text),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                leadingContent = {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = null,
                    )
                },
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        preferencesViewModel.setPreference(
                            preferencesUiState.acceptedPrivacyPolicyAndLicense.first,
                            checked,
                        )
                    }
                },
                enabled = checked,
            ) {
                Text(text = stringResource(R.string.continue_to_app))
            }
        }
    }
}
