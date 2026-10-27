---
name: play-store-release
description: |
  End-to-end release procedure for publishing the HobbyMatchMaker Android app to the Google Play Store — first-release setup (upload keystore, signingConfig, Play App Signing, privacy policy and account-deletion pages on Firebase Hosting, Firebase/Facebook SHA registration), pre-flight checks, version bump, signed .aab build, and a guided Play Console checklist (store listing, App content, Data safety, track, release notes). Run this skill every time the user wants to ship the Android app. Trigger on phrases like "push the app online", "release the app", "publish to the Play Store", "upload to Google Play", "ship a new version", "new release", "bump the version", "build the aab", or "Play Console".
---

# Google Play Release — HobbyMatchMaker (Android)

Ship `androidApp` (applicationId `com.msoula.hobbymatchmaker`) to Google Play.
The `.aab` is built locally and **uploaded manually** by the user in Play Console.

Account context: personal Google Play developer account created **before Nov 2023** → no
"12 testers for 14 days" closed-test gate; Production is reachable directly.

---

## 0. Rules — read first

- **No secrets anywhere tracked or printed.** Never write passwords, keystore paths, aliases,
  API keys, SHA fingerprints, reviewer credentials or `google-services.json` contents into this
  skill, a commit, or chat output. Check files for *existence* only (`test -f`), never `cat` them.
- **Ask before every outward or irreversible action**: committing, pushing, tagging,
  `firebase deploy`, generating a new keystore, editing `.gitignore`.
- **Always ask which track** (Internal / Closed / Open / Production) for *every* release.
  Never reuse the previous answer. (First release, v1.0: the user chose Production.)
- **Never run the app** on a device or emulator (project rule).
- Commits follow the project format, **no co-author line**:
  `feat (release): <add|fix|clean|migrate> ... --in-progress` / `--finished`.
- Use `AskUserQuestion` for every decision; batch related questions together.
- Work through the sections in order. Each setup step is idempotent: **check first, act only if
  missing**, and tell the user what was skipped.

---

## 1. First release or update?

Detect, then confirm with the user:

```bash
grep -n "signingConfigs\|versionCode\|versionName\|targetSdk" androidApp/build.gradle.kts
git tag --list "v*" --sort=-v:refname | head -5
```

- No `signingConfigs` block **or** no `v*` tag → **first release**: run sections 2 → 7.
- Otherwise → **update**: quickly re-verify section 2 items still hold, then run 3 → 7
  (section 6 only needs the "Every release" part).

---

## 2. One-time setup (first release)

### 2.1 Find the upload keystore

The user has an existing upload keystore but does not know where it is. Search for it:

```bash
# Candidate keystore files (exclude the debug keystore)
find ~ /Volumes -type f \( -name "*.jks" -o -name "*.keystore" \) \
  -not -path "*/.android/debug.keystore" -not -path "*/build/*" 2>/dev/null

# Path remembered by Android Studio's "Generate Signed Bundle" wizard
grep -rhoE "KEY_STORE_PATH[^/]*[^\"]*|value=\"[^\"]*\.(jks|keystore)\"" \
  ~/Library/Application\ Support/Google/AndroidStudio*/options/ \
  .idea/ 2>/dev/null | sort -u
```

- Present the candidates to the user and let them pick. Do not echo the chosen path later.
- Let the user inspect it themselves (the command prompts for the password interactively):
  suggest `! keytool -list -v -keystore "<path>"` to confirm the alias and validity.
- **None found / password lost** → ask before generating a new upload key:
  `keytool -genkeypair -v -keystore <outside-the-repo>/upload-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload`
  (user runs it with `!` so passwords stay out of the transcript). Remind them to back up the
  keystore + passwords in a password manager. Since no app exists on Play yet, a new upload key
  is fine; after the first upload, a lost upload key can be reset via Play Console support.
- The keystore must live **outside the repo**.

### 2.2 `keystore.properties` (gitignored)

1. Ensure `.gitignore` contains (add if missing, after asking):
   ```
   keystore.properties
   *.jks
   *.keystore
   ```
2. If `keystore.properties` does not exist at the project root, create it with **empty values**
   and ask the user to fill it themselves:
   ```properties
   storeFile=
   storePassword=
   keyAlias=
   keyPassword=
   ```
3. Verify with `git check-ignore keystore.properties` (must print the file name).

### 2.3 `signingConfigs` in `androidApp/build.gradle.kts`

Reuse the existing `Properties()` pattern used for `secrets.properties` in the same file:

```kotlin
android {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties().apply {
        if (keystorePropertiesFile.exists()) load(FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        create("release") {
            // Guard on a non-blank path: an empty template would fail configuration ("Cannot convert '' to File")
            val storeFilePath = keystoreProperties.getProperty("storeFile").orEmpty()
            if (storeFilePath.isNotBlank()) {
                storeFile = file(storeFilePath)
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

Keep `isMinifyEnabled = false` (user decision: no R8 for now). Commit after asking:
`feat (release): add release signing config --in-progress`.

### 2.4 Privacy policy + account-deletion pages (Firebase Hosting)

Both URLs are **mandatory** in Play Console.

1. Check `firebase.json` for a `hosting` block; if missing add:
   ```json
   "hosting": { "public": "hosting", "ignore": ["firebase.json", "**/.*"] }
   ```
2. Draft `hosting/privacy.html` and `hosting/delete-account.html` (plain, mobile-friendly HTML,
   English). Build the data inventory from the code, not from memory — at minimum check:
   - Firebase Auth identity (email, display name, provider uid) — Google & Facebook sign-in
   - Firestore user profile, social circle / invites, favorites, matches
     (see `/kmp-database` for collections and fields)
   - Firebase Storage avatar images
   - Local SQLDelight cache on device
   - Kotzilla (Koin monitoring/diagnostics) and any crash/analytics SDK in `libs.versions.toml`
   - TMDB as a third-party content source (no user data sent unless verified otherwise)
   The privacy policy must state: data collected, purpose, third parties/processors
   (Google Firebase, Meta/Facebook Login, Kotzilla), retention, deletion process, contact email.
   **Ask the user for the contact email** — do not guess it.
   The deletion page must name the app/developer, explain steps to request deletion, list data
   deleted vs retained (and how long), and give the in-app path.
3. Show the drafts to the user, then ask before running `firebase deploy --only hosting`.
4. Record the resulting URLs (they are public, not secret) in the release summary for section 6.

### 2.5 Play Console — create the app

Guide the user through **Create app**: app name, default language (English – United States),
App (not Game), Free, accept declarations. Then **Play App Signing** is enabled by default for
new apps — confirm it is on (Google holds the app signing key; the user's keystore is only the
upload key).

### 2.6 After the first upload — register the *app signing* key

Google re-signs the app, so sign-in breaks in production unless the Play **app signing** key is
registered (not only the upload key):

1. Play Console → *Test and release → App integrity → App signing*: copy SHA-1 and SHA-256.
2. Firebase console → Project settings → Android app → *Add fingerprint* (both).
   Then re-download `google-services.json` into `androidApp/` (gitignored) and update the
   `GOOGLE_SERVICES_JSON` GitHub secret if the content changed.
3. Meta for Developers → app → Android → add the key hash
   (`echo <SHA-1 hex without colons> | xxd -r -p | openssl base64`), package name
   `com.msoula.hobbymatchmaker`, set the app Live.
4. Google Sign-In uses the Firebase-managed OAuth client; verify the `web_client_id` in
   `secrets.properties` still matches (existence check only).

The user pastes fingerprints into the consoles themselves; never store them in the repo.

---

## 3. Pre-flight checks (every release)

Run all checks, then report a pass/fail table. **Blocking** failures stop the release.

| # | Check | How | Blocking |
|---|-------|-----|----------|
| 1 | Working tree clean, correct branch | `git status --porcelain`, `git branch --show-current` (ask if not `main`) | yes |
| 2 | CI green on the release commit | `gh run list --branch <branch> --limit 3` | ask |
| 3 | Local config present | `test -f` on `secrets.properties`, `androidApp/google-services.json`, `keystore.properties` | yes |
| 4 | `targetSdk` meets Play's current requirement | WebSearch "Google Play target API level requirement" for today's date; compare with `androidApp/build.gradle.kts`; bump if lower (compileSdk is 37). Commit `feat (release): migrate targetSdk to N --in-progress` | yes |
| 5 | **In-app account deletion exists** | grep `core/` `features/` for a delete-account use case/UI (e.g. `DeleteAccount`, `deleteUser`, `currentUser.delete`) that deletes the Auth user + Firestore docs + Storage avatar. Local cache cleanup alone does **not** count | **yes** — if missing, stop and offer to build it via `/kmp-new-feature` |
| 6 | Privacy policy + deletion URLs reachable | `curl -sI <url> \| head -1` → 200 | yes |
| 7 | Static analysis | `./gradlew detektAll` | yes |
| 8 | Unit tests | `./gradlew testDebugUnitTest` (or `allTests` when iOS mode is on) | yes |
| 9 | Release build type sane | `isMinifyEnabled = false`, `signingConfig` set, no `isDebuggable = true` | yes |

Do not continue past a blocking failure; explain the fix and ask how to proceed.

---

## 4. Version bump

1. Read the current values from `androidApp/build.gradle.kts`.
2. Ask the user for the new `versionName`, suggesting the semver options
   (patch / minor / major) based on `git log <last-tag>..HEAD --oneline`.
   - First release: keep `versionCode = 1`, `versionName = "1.0.0"` (or the user's choice).
3. Set `versionCode = previous + 1` (Play rejects any code ≤ an already uploaded one, even on
   other tracks — if an upload was rejected for this reason, increment again).
4. Edit the gradle file, ask, then commit:
   `feat (release): bump version to X.Y.Z (code N) --in-progress`.

---

## 5. Build the signed bundle

```bash
./switch-build-mode.sh status                # Android-only mode is fine for this build
./gradlew clean :androidApp:bundleRelease
ls -lh androidApp/build/outputs/bundle/release/*.aab
jarsigner -verify androidApp/build/outputs/bundle/release/*.aab   # expect "jar verified."
```

- If signing fails, check `keystore.properties` values with the user (never print them).
- Report the absolute `.aab` path and size. `*.aab` is gitignored — never commit it.
- Optional, if `bundletool` is installed: `bundletool validate --bundle=<aab>`.

---

## 6. Play Console — guided checklist

Walk the user through each item; draft every text for them to paste.

### First release only

**Store presence → Main store listing**
- App name ≤ 30 chars, short description ≤ 80, full description ≤ 4000 — draft in English from
  `README.md` and `features/*/README.md` (social hobby matching; movies first; more hobbies
  coming). No keyword stuffing, no "best/#1", no competitor names.
- Graphics (user produces them; give these specs):
  | Asset | Spec |
  |-------|------|
  | App icon | 512×512 PNG, 32-bit with alpha, ≤ 1 MB, full-bleed square (Play applies the mask) |
  | Feature graphic | 1024×500 JPG or 24-bit PNG (no alpha) |
  | Phone screenshots | 2–8, PNG/JPG, 16:9 or 9:16, each side 320–3840 px (1080×1920 recommended); ≥ 4 recommended for discovery |
  | 7"/10" tablet screenshots | optional unless tablets are targeted |
- Contact email (ask the user), optional website = Firebase Hosting root.
- App category: *Social* (confirm with the user); tags.

**Policy → App content** (all must be complete before review)
- Privacy policy → URL from 2.4.
- App access → "All or some functionality is restricted": the reviewer needs a working login.
  The user creates a dedicated test account and enters its credentials **only in Play Console**.
- Ads → No (unless an ads SDK was added — check `libs.versions.toml`).
- Content rating → IARC questionnaire; flag that users can interact/share content (social
  features) — answer honestly.
- Target audience → 13+ (or 18+); do **not** include under-13 because of social features and
  third-party login.
- Data safety → pre-fill an answer table from the 2.4 inventory: per data type (name, email,
  user IDs, photos, app interactions, crash logs/diagnostics…), collected? shared? optional?
  purpose (account management, app functionality, analytics), encrypted in transit (yes —
  HTTPS/Firebase), deletion request supported (yes → deletion URL).
- Account deletion → URL from 2.4.
- Government apps / Financial features / Health / News → No.
- Advertising ID → No (unless an SDK uses it — check the merged manifest
  `androidApp/build/intermediates/merged_manifest/release/**/AndroidManifest.xml` for
  `com.google.android.gms.permission.AD_ID`; the Facebook SDK may add it).

**Monetize / Countries** → select countries/regions for the release.

### Every release

1. **Ask the track** (Internal testing / Closed testing / Open testing / Production).
2. Create a new release on that track → upload the `.aab` from section 5.
3. Release name: `X.Y.Z (N)`.
4. Release notes (`<en-US>`, ≤ 500 chars): draft from `git log <last-tag>..HEAD --oneline`,
   user-facing wording only (no internal refactors). First release: short launch blurb.
5. Production only: suggest a staged rollout (e.g. 20 %), not for the very first release
   (100 % is fine when there are no existing users).
6. Review the release → fix any errors/warnings shown → *Send for review* (in *Publishing
   overview*, if managed publishing is on, the user must also click *Publish*).
7. First release: complete 2.6 (app signing fingerprints) right after the upload.

First reviews can take several days; updates are usually faster.

---

## 7. Post-release

1. Ask, then tag and push: `git tag vX.Y.Z && git push origin vX.Y.Z` (tags are the release log).
2. Final commit if anything is pending: `feat (release): release vX.Y.Z --finished`.
3. Summarize for the user: version/code, track, `.aab` path, what is pending on their side
   (review, fingerprints, graphics), and anything skipped.
4. After approval: suggest a quick sanity check of Google/Facebook sign-in on the
   Play-installed build (done by the user — never run the app yourself).
