plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
    id("com.google.dagger.hilt.android") version "2.53.1" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

val versionMajor = 1
val versionMinor = 0
val versionPatch = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0
val appVersionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
val appVersionName = "$versionMajor.$versionMinor.$versionPatch"

extra["appVersionCode"] = appVersionCode
extra["appVersionName"] = appVersionName
