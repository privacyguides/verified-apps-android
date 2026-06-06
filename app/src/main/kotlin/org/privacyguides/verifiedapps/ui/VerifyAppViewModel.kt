package org.privacyguides.verifiedapps.ui

import android.app.Application
import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import org.privacyguides.verifiedapps.Source
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus
import org.privacyguides.verifiedapps.data.VerificationInfo
import org.privacyguides.verifiedapps.data.VerifyAppUiState
import org.privacyguides.verifiedapps.internalVerificationInfoDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.util.UUID

class VerifyAppViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VerifyAppUiState())
    val uiState: StateFlow<VerifyAppUiState> = _uiState.asStateFlow()

    fun setAppVerificationInfo(
        name: String,
        packageName: String,
        hashes: Hashes,
        internalDatabaseInfo: InternalDatabaseInfo,
        isSystemApp: Boolean = false,
    ) {
        _uiState.update {
            it.copy(
                name = name,
                packageName = packageName,
                hashes = hashes,
                internalDatabaseInfo = internalDatabaseInfo,
                isSystemApp = isSystemApp,
            )
        }
    }

    fun setAppIcon(icon: Drawable) {
        _uiState.update { it.copy(icon = icon) }
    }

    fun getHashesFromPackageInfo(packageInfo: PackageInfo): Hashes {
        val signingInfo = packageInfo.signingInfo
            ?: throw IllegalStateException(
                "PackageInfo.signingInfo is null for package ${packageInfo.packageName}"
            )
        val hasMultipleSigners = signingInfo.hasMultipleSigners()

        val signatures = if (hasMultipleSigners) {
            signingInfo.apkContentsSigners
                .map { signature ->
                    MessageDigest
                        .getInstance("SHA-256")
                        .digest(signature.toByteArray())
                        .joinToString(":") {
                            "%02x".format(it)
                        }
                        .uppercase()
                }
        } else {
            signingInfo.signingCertificateHistory
                .map { signature ->
                    MessageDigest
                        .getInstance("SHA-256")
                        .digest(signature.toByteArray())
                        .joinToString(":") {
                            "%02x".format(it)
                        }
                        .uppercase()
                }
        }

        return Hashes(listOf(Source.NONE), signatures, hasMultipleSigners)
    }

    fun setApkFailedToParse(b: Boolean) {
        _uiState.update { it.copy(apkFailedToParse = b) }
    }

    fun getInternalDatabaseInfoFromVerificationInfo(verificationInfo: VerificationInfo): InternalDatabaseInfo {
        return internalVerificationInfoDatabase.run {
            val packageNameMatchedInternalDatabaseVerificationInfo = this.firstOrNull {
                it.packageName == verificationInfo.packageName
            } ?: return@run InternalDatabaseInfo(InternalDatabaseStatus.NOT_FOUND, listOf(Source.NONE))

            val maybeMatchedHashes = packageNameMatchedInternalDatabaseVerificationInfo.hashesList.find {
                it.matchesSigningFingerprints(verificationInfo.hashes)
            }
            if (maybeMatchedHashes != null) {
                InternalDatabaseInfo(InternalDatabaseStatus.MATCH, maybeMatchedHashes.sources)
            } else {
                InternalDatabaseInfo(InternalDatabaseStatus.NOMATCH, listOf(Source.NONE))
            }
        }
    }

    fun setApkVerificationInfoAndInternalDatabaseStatusFromUri(
        contentResolver: ContentResolver,
        uri: Uri,
        packageManager: PackageManager,
    ) {
        contentResolver.openInputStream(uri).use { inputStream ->
            // Use a unique cache file per verification to avoid concurrent overwrite/delete races.
            val tempFile = File(
                getApplication<Application>().cacheDir,
                "pending-verification-${UUID.randomUUID()}.apk"
            )

            tempFile.outputStream().use { fileOut ->
                val nonNullInputStream = inputStream
                    ?: throw IOException("Unable to open input stream for URI: $uri")
                nonNullInputStream.use { it.copyTo(fileOut) }
            }

            val packageInfo = packageManager.getPackageArchiveInfo(
                tempFile.path,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            val applicationInfo = packageInfo?.applicationInfo ?: ApplicationInfo()

            if (packageInfo == null) {
                setApkFailedToParse(true)
                deleteTempFile(tempFile)
                return
            }

            applicationInfo.sourceDir = tempFile.path
            applicationInfo.publicSourceDir = tempFile.path

            val packageName = packageInfo.packageName

            val hashes = getHashesFromPackageInfo(packageInfo)

            setAppVerificationInfo(
                packageManager.getApplicationLabel(applicationInfo).toString(),
                packageName,
                hashes,
                getInternalDatabaseInfoFromVerificationInfo(VerificationInfo(packageName, hashes)),
                isSystemApp = packageManager.isInstalledSystemPackage(packageName),
            )
            setAppIcon(packageManager.getApplicationIcon(applicationInfo))

            deleteTempFile(tempFile)
        }
    }

    private fun deleteTempFile(tempFile: File) {
        tempFile.delete()
    }
}

private fun PackageManager.isInstalledSystemPackage(packageName: String): Boolean =
    getInstalledPackages(PackageManager.MATCH_SYSTEM_ONLY)
        .any { it.packageName == packageName }
