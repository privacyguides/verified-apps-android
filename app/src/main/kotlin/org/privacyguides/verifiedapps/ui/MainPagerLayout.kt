package org.privacyguides.verifiedapps.ui

enum class MainPagerPage {
    About,
    AppList,
    OpenApk,
    Settings,
}

object MainPagerLayout {
    val pages: List<MainPagerPage> = MainPagerPage.entries

    fun pagerIndexFor(bottomNav: BottomNavPage): Int = when (bottomNav) {
        BottomNavPage.About -> 0
        BottomNavPage.AppList -> 1
        BottomNavPage.OpenApk -> 2
        BottomNavPage.Settings -> 3
    }
}
