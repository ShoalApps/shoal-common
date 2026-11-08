plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
}

android {
    namespace = "io.github.shoalapps.common.ui"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(platform("androidx.compose:compose-bom:2026.09.00"))
    api("androidx.compose.runtime:runtime")
    api("androidx.compose.ui:ui")
    api("androidx.compose.foundation:foundation") // InlineTextContent is part of the public API.
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-saveable")
    implementation("androidx.core:core:1.19.1")
    // Error reports and self-updates use the app's own Sentry and OkHttp: built against here, not
    // shipped, so an app that doesn't use them (Squill) doesn't carry them.
    compileOnly("io.sentry:sentry-android:8.62.0")
    compileOnly("com.squareup.okhttp3:okhttp:4.12.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20240303")
}

publishing {
    repositories {
        maven {
            name = "Pages"
            url = uri(rootProject.layout.buildDirectory.dir("repo"))
        }
    }
    publications {
        register<MavenPublication>("release") {
            artifactId = "shoal-ui"
            afterEvaluate {
                from(components["release"])
            }
            pom {
                name = "Shoal UI"
                description = "Shared Android Compose components for Shoal apps"
                url = "https://github.com/ShoalApps/shoal-common"
                licenses {
                    license {
                        name = "GNU General Public License v3.0 or later"
                        url = "https://www.gnu.org/licenses/gpl-3.0.html"
                        distribution = "repo"
                    }
                }
            }
        }
    }
}
