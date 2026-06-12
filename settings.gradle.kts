pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
// The foojay-resolver-convention plugin is deliberately absent: it can pull
// non-free JDK binaries at build time, which breaks F-Droid/IzzyOnDroid
// reproducible builds. No project requests an auto-provisioned toolchain, so it
// isn't needed. Do not re-add it (Android Studio may offer to). Likewise, there
// is intentionally no gradle/gradle-daemon-jvm.properties — the daemon runs on
// the build environment's JDK (17, per AGP 9.x), with no pinned vendor and no
// foojay download.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VerifiedApps"
include(":app")
