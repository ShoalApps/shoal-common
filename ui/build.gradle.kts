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
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
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
