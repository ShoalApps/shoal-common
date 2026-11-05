# Shoal common

Shared Android components for Squill, Angelfish, Butterflyfish and Shoal, licensed
under GPL-3.0-or-later (see LICENSE). The first component is `SupportCard`:
it preserves the host theme and accepts app-localised strings, accessibility
labels, a heart ImageVector and the donation callback. It has no app dependencies
and uses stable Compose Material3 APIs from BOM 2026.09.00.

## Using it in an app

In `dependencyResolutionManagement.repositories` in `settings.gradle.kts`:

```kotlin
maven("https://shoalapps.github.io/shoal-common/") {
    content { includeGroup("io.github.shoalapps.common") }
}
```

Pin a published version in the root `shoal-common.version` file, then in the
Android app module:

```kotlin
implementation("io.github.shoalapps.common:shoal-ui:" + rootProject.file("shoal-common.version").readText().trim())
```

Import `io.github.shoalapps.common.ui.SupportCard`. Supply `title`, `description`,
`donateLabel`, `onDonateLabel`, `heartIcon`, optional `heartContentDescription`
(default null for a decorative icon), and `onDonate`. Localise visible strings in
the app before calling; keep the English phrases at the call site for catalogue
extraction. Squill supplies plain English and its filled heart; the other apps
use `AppLanguage.text(...)` and their rounded heart. The existing row click label
remains English, preserving its prior accessibility behaviour.

## How changes reach everyone

Pull requests build and test the library. Each main push publishes
`io.github.shoalapps.common:shoal-ui:1.0.<GITHUB_RUN_NUMBER>` plus sources.
Local builds use `1.0.0-SNAPSHOT`. Manual publication is restricted to main;
rerunning a release fails if it would change an existing version's bytes.

The publish workflow seeds `build/repo` from the `maven` branch before Gradle
publishes, so Maven metadata includes accumulated versions. It checks that all
existing version directories are unchanged, overlays the result, and writes
`latest.txt` only once the artifact is in place. Configure GitHub Pages to serve
the root of `maven`; no separate Pages deployment workflow is needed.

Artifacts: https://shoalapps.github.io/shoal-common/io/github/shoalapps/common/shoal-ui/

Latest marker: https://shoalapps.github.io/shoal-common/latest.txt

Each app checks hourly, tests and compiles a changed pin, commits it only on
success, then explicitly dispatches its release workflow. The initial `1.0.1`
app pins are placeholders to replace with the first published version.
Installed apps receive changes through an app rebuild and an update via Shoal.

## Working locally

Keep this repository beside an app checkout. Add this temporary composite build
to the app's `settings.gradle.kts` (outside `dependencyResolutionManagement`):

```kotlin
includeBuild("../shoal-common") {
    dependencySubstitution {
        substitute(module("io.github.shoalapps.common:shoal-ui")).using(project(":ui"))
    }
}
```

Explicit substitution is needed because the module is named `ui` but its Maven
artifact is `shoal-ui`. Leave the pinned dependency in place; Gradle substitutes
the local project. Remove this local settings change before sending app changes.
The toolchain matches the apps: wrapper 9.8.2, AGP 9.5.0 with built-in Kotlin,
Compose plugin 2.4.21, JDK 17, compile SDK 37 and min SDK 26. No signing setup is
needed. CI runs `./gradlew :ui:test :ui:assembleRelease`; the card has no pure JVM
logic requiring a separate unit test.
