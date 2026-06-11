package org.privacyguides.verifiedapps.github

import android.content.pm.PackageManager
import android.net.Uri
import org.privacyguides.verifiedapps.data.Hashes

/**
 * Builds a GitHub "new issue" URL that opens [app-submission.yml] with prefilled form fields.
 *
 * Field IDs match https://github.com/privacyguides/verified-apps/blob/main/.github/ISSUE_TEMPLATE/app-submission.yml
 */
object GitHubAppSubmission {

    private const val NEW_ISSUE_URI =
        "https://github.com/privacyguides/verified-apps/issues/new"

    private const val TEMPLATE = "app-submission.yml"

    /** Dropdown option text must match the issue form exactly. */
    const val VERIFIER_SOURCE_VERIFIED_APPS = "N/A - I'm using Verified Apps from PrivacyGuides"

    fun newIssueUri(
        packageManager: PackageManager,
        packageName: String,
        appLabel: String,
        hashes: Hashes,
    ): Uri {
        val verificationInfo = buildVerificationInfo(packageName, hashes)
        val title = buildTitle(appLabel, packageName)
        val appSource = AppInstallSource.detectAppSource(packageManager, packageName)

        return Uri.parse(NEW_ISSUE_URI).buildUpon()
            .appendQueryParameter("template", TEMPLATE)
            .appendQueryParameter("title", title)
            .appendQueryParameter("verificationInfo", verificationInfo)
            .appendQueryParameter("appSource", appSource)
            .appendQueryParameter("verifierSource", VERIFIER_SOURCE_VERIFIED_APPS)
            .build()
    }

    fun buildVerificationInfo(packageName: String, hashes: Hashes): String =
        "$packageName\n${hashes.hashes.joinToString("\n")}"

    fun buildTitle(appLabel: String, packageName: String): String {
        val name = appLabel.trim().ifEmpty { packageName.trim() }
        return "[New]: $name"
    }
}
