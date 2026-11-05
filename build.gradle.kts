plugins {
    id("com.android.library") version "9.5.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.21" apply false
}

allprojects {
    group = "io.github.shoalapps.common"
    version = "1.0." + (System.getenv("GITHUB_RUN_NUMBER") ?: "0-SNAPSHOT")
}
