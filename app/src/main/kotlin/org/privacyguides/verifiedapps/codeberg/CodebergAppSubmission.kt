package org.privacyguides.verifiedapps.codeberg

import android.net.Uri
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.github.GitHubAppSubmission

/**
 * Builds a Codeberg "new issue" URL for [app-submission.yml].
 *
 * Codeberg currently only applies the `title` query parameter; the other fields are
 * included for forward compatibility if form prefills are added later.
 */
object CodebergAppSubmission {

    private const val NEW_ISSUE_URI =
        "https://codeberg.org/privacyguides/verified-apps/issues/new"

    private const val TEMPLATE = ".github/ISSUE_TEMPLATE/app-submission.yml"

    fun newIssueUri(
        packageName: String,
        appLabel: String,
        hashes: Hashes,
    ): Uri {
        val verificationInfo = GitHubAppSubmission.buildVerificationInfo(packageName, hashes)
        val title = GitHubAppSubmission.buildTitle(appLabel, packageName)

        return Uri.parse(NEW_ISSUE_URI).buildUpon()
            .appendQueryParameter("template", TEMPLATE)
            .appendQueryParameter("title", title)
            .appendQueryParameter("verificationInfo", verificationInfo)
            .appendQueryParameter("verifierSource", GitHubAppSubmission.VERIFIER_SOURCE_VERIFIED_APPS)
            .build()
    }
}
