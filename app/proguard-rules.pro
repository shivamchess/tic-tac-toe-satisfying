# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Keep the application class
-keep class com.satisfying.tictactoe.** { *; }

# Compose
-keep class androidx.compose.** { *; }
