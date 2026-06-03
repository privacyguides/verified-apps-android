package org.privacyguides.verifiedapps.github

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
    private const val VERIFIER_SOURCE_OTHER = "Other"

    fun newIssueUri(
        packageName: String,
        appLabel: String,
        hashes: Hashes,
    ): Uri {
        val verificationInfo = buildVerificationInfo(packageName, hashes)
        val title = buildTitle(appLabel, packageName)

        return Uri.parse(NEW_ISSUE_URI).buildUpon()
            .appendQueryParameter("template", TEMPLATE)
            .appendQueryParameter("title", title)
            .appendQueryParameter("verificationInfo", verificationInfo)
            .appendQueryParameter("verifierSource", VERIFIER_SOURCE_OTHER)
            .build()
    }

    fun buildVerificationInfo(packageName: String, hashes: Hashes): String =
        "$packageName\n${hashes.hashes.joinToString("\n")}"

    fun buildTitle(appLabel: String, packageName: String): String {
        val name = appLabel.trim().ifEmpty { packageName.trim() }
        return "[New]: $name"
    }
}
