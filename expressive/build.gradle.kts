plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
}

/*
 * Shoal's Material 3 Expressive pieces: shapes and settings tiles. Kept apart from shoal-ui
 * because they need Material 3's pre-release Expressive API, which an app on stable Material 3
 * (Squill) shouldn't be moved onto.
 */
android {
    namespace = "io.github.shoalapps.common.expressive"
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
    api(project(":ui"))
    api(platform("androidx.compose:compose-bom:2026.09.00"))
    // Exposed, so an app compiles and runs against the same Material 3 as these tiles.
    api("androidx.compose.material3:material3:1.5.0-rc01")
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.graphics:graphics-shapes:1.1.0")
    implementation("io.coil-kt.coil3:coil-compose:3.6.3")
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
            artifactId = "shoal-expressive"
            afterEvaluate {
                from(components["release"])
            }
            pom {
                name = "Shoal Expressive"
                description = "Material 3 Expressive shapes and settings tiles shared by Shoal apps"
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
