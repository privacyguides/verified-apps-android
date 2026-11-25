# SPDX-FileCopyrightText: 2024-2025 soupslurpr
# SPDX-FileCopyrightText: 2026 Jonah Aragon
# SPDX-License-Identifier: ISC AND MIT

# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Strip all android.util.Log calls from the release build (app + bundled
# libraries). proguard-android-optimize.txt enables the optimization pass that
# acts on -assumenosideeffects, so R8 removes these calls entirely. Keeps
# information out of logcat (defense in depth for a privacy app) and clears
# MobSF's android_logging finding.
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
    public static *** wtf(...);
}