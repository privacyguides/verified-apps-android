@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.privacyguides.verifiedapps.ui

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch
import org.privacyguides.verifiedapps.R
import org.privacyguides.verifiedapps.Source
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus
import org.privacyguides.verifiedapps.codeberg.CodebergAppSubmission
import org.privacyguides.verifiedapps.github.GitHubAppSubmission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyAppScreen(
    onNavigateUp: () -> Unit,
    icon: Drawable?,
    name: String,
    packageName: String,
    hashes: Hashes,
    onLaunchedEffectHashEmpty: () -> Unit,
    internalDatabaseInfo: InternalDatabaseInfo,
    apkFailedToParse: Boolean,
    showHasMultipleSigners: Boolean,
    showSharingTools: Boolean,
    alwaysShowGitHubSubmit: Boolean,
    showCodebergSubmit: Boolean,
    isSystemApp: Boolean,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val verticalScroll = rememberScrollState()
    LaunchedEffect(Unit) {
        if (hashes.hashes.isEmpty()) {
            onLaunchedEffectHashEmpty()
        }
    }

    val databaseStatus = internalDatabaseInfo.internalDatabaseStatus

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.verify_app)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(verticalScroll),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        if (apkFailedToParse) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.apk_failed_to_parse_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    Text(
                        stringResource(R.string.apk_failed_to_parse_message),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (icon != null) {
                        Image(
                            rememberDrawablePainter(drawable = icon),
                            contentDescription = null,
                            modifier = Modifier.size(96.dp),
                        )
                    }
                    Text(text = name, style = MaterialTheme.typography.headlineSmallEmphasized)
                    Text(
                        text = packageName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = hashes.hashes.joinToString("\n"),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                    )
                    if (showHasMultipleSigners) {
                        Text(
                            text = stringResource(
                                R.string.has_multiple_signers_debug,
                                hashes.hasMultipleSigners,
                            ),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = databaseStatus.statusIcon(),
                            contentDescription = stringResource(databaseStatus.labelRes()),
                            tint = databaseStatus.contentColor(),
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.verify_internal_database_status),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = stringResource(databaseStatus.labelRes()),
                                style = MaterialTheme.typography.titleLargeEmphasized,
                                color = databaseStatus.contentColor(),
                            )
                        }
                    }
                }
            }

            if (databaseStatus == InternalDatabaseStatus.MATCH) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(databaseStatus.infoRes()),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = stringResource(R.string.internal_database_sources_label),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Column {
                            internalDatabaseInfo.sources.forEach { source ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = source.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    if (source == Source.VERIFIED_DOMAIN) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = stringResource(R.string.internal_database_sources_explanation),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            val showGitHubSubmit = if (isSystemApp) {
                alwaysShowGitHubSubmit
            } else {
                databaseStatus == InternalDatabaseStatus.NOT_FOUND ||
                    databaseStatus == InternalDatabaseStatus.NOMATCH ||
                    alwaysShowGitHubSubmit
            }
            if (showGitHubSubmit) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        val verificationData =
                            GitHubAppSubmission.buildVerificationInfo(packageName, hashes)
                        when (databaseStatus) {
                            InternalDatabaseStatus.NOT_FOUND -> {
                                Text(
                                    text = stringResource(R.string.not_found_submit_message),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            InternalDatabaseStatus.NOMATCH -> {
                                Text(
                                    text = stringResource(R.string.nomatch_github_submit_message),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            InternalDatabaseStatus.MATCH -> Unit
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val issueUri = GitHubAppSubmission.newIssueUri(
                                    packageManager = context.packageManager,
                                    packageName = packageName,
                                    appLabel = name,
                                    hashes = hashes,
                                )
                                openSubmissionUri(context, issueUri, R.string.github_submission_no_browser)
                            },
                        ) {
                            Text(stringResource(R.string.submit_on_github))
                        }
                        if (showCodebergSubmit) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    coroutineScope.launch {
                                        copyVerificationInfoToClipboard(
                                            context = context,
                                            clipboard = clipboard,
                                            verificationData = verificationData,
                                        )
                                    }
                                    val issueUri = CodebergAppSubmission.newIssueUri(
                                        packageName = packageName,
                                        appLabel = name,
                                        hashes = hashes,
                                    )
                                    openSubmissionUri(context, issueUri, R.string.codeberg_submission_no_browser)
                                },
                            ) {
                                Text(stringResource(R.string.submit_to_codeberg))
                            }
                        }
                    }
                }
            }

            if (showSharingTools) {
                val verificationData = GitHubAppSubmission.buildVerificationInfo(packageName, hashes)
                val mimeType = "text/plain"
                val clipLabel = stringResource(R.string.verification_info_clip_label)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, verificationData)
                                type = mimeType
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                    ) {
                        Text(stringResource(R.string.share_verification_info))
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                val clip = ClipData.newPlainText(clipLabel, verificationData)
                                clipboard.setClipEntry(ClipEntry(clip))
                            }
                        },
                    ) {
                        Text(stringResource(R.string.copy_verification_info))
                    }
                }
            }
        }

            Spacer(Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
        }
    }
}

private suspend fun copyVerificationInfoToClipboard(
    context: android.content.Context,
    clipboard: Clipboard,
    verificationData: String,
) {
    val clip = ClipData.newPlainText(
        context.getString(R.string.verification_info_clip_label),
        verificationData,
    )
    clipboard.setClipEntry(ClipEntry(clip))
    Toast.makeText(
        context,
        context.getString(R.string.verification_info_copied_toast),
        Toast.LENGTH_SHORT,
    ).show()
}

private fun openSubmissionUri(
    context: Context,
    issueUri: Uri,
    @StringRes noBrowserMessageRes: Int,
) {
    val intent = browserOnlyIntent(context, issueUri)
    if (intent == null) {
        Toast.makeText(
            context,
            context.getString(noBrowserMessageRes),
            Toast.LENGTH_LONG,
        ).show()
        return
    }

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(noBrowserMessageRes),
            Toast.LENGTH_LONG,
        ).show()
    }
}

private fun browserOnlyIntent(context: Context, uri: Uri): Intent? {
    val packageManager = context.packageManager
    val browserProbeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com")).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
    }
    val browserPackages = packageManager.queryIntentActivities(
        browserProbeIntent,
        PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
    ).map { it.activityInfo.packageName }
        .distinct()

    val viewIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
    }

    val defaultBrowserPackage = packageManager.resolveActivity(
        browserProbeIntent,
        PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
    )?.activityInfo?.packageName
        ?.takeIf { it in browserPackages }

    if (defaultBrowserPackage != null) {
        return Intent(viewIntent).setPackage(defaultBrowserPackage)
    }

    val browserIntents = browserPackages.map { packageName ->
        Intent(viewIntent).setPackage(packageName)
    }
    return when (browserIntents.size) {
        0 -> null
        1 -> browserIntents.single()
        else -> Intent.createChooser(browserIntents.first(), null).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, browserIntents.drop(1).toTypedArray<Parcelable>())
        }
    }
}
