# Android ProGuard Rules for Grace's Diary

# Keep Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-dontwarn androidx.compose.**

# Keep Room entities
-keep class com.grace.sdiary.data.local.db.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.**

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.hilt.**
-dontwarn javax.inject.**

# Keep Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.grace.sdiary.data.backup.** { *; }
-dontwarn com.google.gson.**

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Keep DataStore
-dontwarn androidx.datastore.**

# Keep WorkManager
-keep class * extends androidx.work.Worker { *; }
-dontwarn androidx.work.**

# Keep Navigation
-dontwarn androidx.navigation.**

# Keep Activity Result API
-dontwarn androidx.activity.result.**

# General Android
-keepattributes SourceFile,LineNumberTable
-keep class * extends android.app.Activity { *; }
-keep class * extends android.app.Application { *; }
-keep class * extends android.app.Service { *; }
-keep class * extends android.content.BroadcastReceiver { *; }
-keep class * extends android.appwidget.AppWidgetProvider { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
