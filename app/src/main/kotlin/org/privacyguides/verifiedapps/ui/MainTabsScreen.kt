package org.privacyguides.verifiedapps.ui

import android.graphics.drawable.Drawable
import android.content.pm.PackageInfo
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.privacyguides.verifiedapps.R
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.VerificationInfo
import org.privacyguides.verifiedapps.preferences.PreferencesViewModel

enum class BottomNavPage(@StringRes val labelRes: Int) {
    About(R.string.about),
    AppList(R.string.app_list),
    OpenApk(R.string.nav_open_apk),
    Settings(R.string.settings),
}

@Composable
fun MainTabsScreen(
    pagerState: PagerState,
    searchQuery: String,
    onAppListItemClick: (
        name: String,
        packageName: String,
        hashes: Hashes,
        icon: Drawable,
        internalDatabaseInfo: InternalDatabaseInfo,
    ) -> Unit,
    onAppListLaunchedEffect: () -> Unit,
    onQueryChange: (query: String) -> Unit,
    onSearch: (query: String) -> Unit,
    onSearchActiveChange: (active: Boolean) -> Unit,
    getHashesFromPackageInfo: (packageInfo: PackageInfo) -> Hashes,
    getInternalDatabaseInfoFromVerificationInfo: (verification: VerificationInfo) -> InternalDatabaseInfo,
    showSystemApps: Boolean,
    onOpenApkFile: () -> Unit,
    preferencesViewModel: PreferencesViewModel,
    onLicenseIconButtonClicked: () -> Unit,
    onPrivacyPolicyIconButtonClicked: () -> Unit,
    onCreditsIconButtonClicked: () -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
    ) { page ->
        when (BottomNavPage.entries[page]) {
            BottomNavPage.About -> AboutScreen(
                onLicenseIconButtonClicked = onLicenseIconButtonClicked,
                onPrivacyPolicyIconButtonClicked = onPrivacyPolicyIconButtonClicked,
                onCreditsIconButtonClicked = onCreditsIconButtonClicked,
            )
            BottomNavPage.AppList -> AppListScreen(
                searchQuery = searchQuery,
                onClickAppItem = onAppListItemClick,
                onLaunchedEffect = onAppListLaunchedEffect,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                onSearchActiveChange = onSearchActiveChange,
                getHashesFromPackageInfo = getHashesFromPackageInfo,
                getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
                showSystemApps = showSystemApps,
            )
            BottomNavPage.OpenApk -> OpenApkScreen(onOpenApkFile = onOpenApkFile)
            BottomNavPage.Settings -> SettingsScreen(preferencesViewModel = preferencesViewModel)
        }
    }
}
