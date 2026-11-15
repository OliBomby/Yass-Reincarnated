# Project Identity

## Status

Implemented/current. This fork is named **Yass Reincarnated**. Its upstream is
Yass Reloaded by DoubleDee, which is based on Saruta's Yass 2.4.3.

## Purpose

Give this fork a distinct identity in the application, downloads, documentation,
and contributor tooling while preserving attribution and existing user data.

## User-Facing Behavior

- Window titles, options, About, generated playlist comments, and bundled help
  identify the application as Yass Reincarnated in all six supported languages.
- About credits OliBomby's fork, DoubleDee's Yass Reloaded, and Saruta's Yass.
- About shows the Reincarnated logo from `src/yass/resources/img/about.jpg` and
  copyright notices for OliBomby (2026), DoubleDee (2023-2026), and Saruta
  (2009-2023). Keep this JPEG and the launcher BMP consistent.
- Maven produces `target/Yass-Reincarnated.jar`.
- Windows installs into `Yass Reincarnated` and creates matching Start Menu and
  desktop shortcuts. The downloadable installer is
  `yass-reincarnated-installer.exe`; the installed launcher remains `yass.exe`.
- macOS/Linux packages and Nix commands use the reincarnated name. Release
  asset names match their workflow upload paths and README examples.
- VS Code's Run/Debug entry is **Yass Reincarnated** and its build task is
  **Yass Reincarnated: compile**.

## Core Rules

- Use Yass Reincarnated for current product branding and new specs.
- Retain copyright holders, original author names, and third-party licenses.
- Inherited implemented specs credit Yass Reloaded in their status. The Maven
  Wrapper and VS Code Development specs belong to this fork and have no such
  inherited-feature note.
- Historical upstream issue links, tutorial titles, donations, and legacy wiki
  pages retain their attribution. Describe donations as support for upstream.
- Keep Java package/class names, resource keys, configuration paths, and song
  artifact formats compatible; a branding change does not migrate user data.

## Data And Configuration

- Project/release links target `https://github.com/OliBomby/Yass-Reincarnated`.
- GitHub Pages uses `https://olibomby.github.io/Yass-Reincarnated/`.
- Both `_config.yml` and `docs/_config.yml` carry these site settings; the Pages
  workflow builds from `docs/`.
- Nix uses package/command/desktop/icon name `yass-reincarnated`.
- Nix and legacy Launch4j/Ant version metadata match Maven's `2026.7` release;
  Launch4j requires Java 21 and uses Windows version `2026.7.0.0`.
- CI's native packages read their application version from Maven's filtered
  `target/classes/version.properties` and use OliBomby as vendor. Windows keeps
  the launcher filename `yass.exe` but describes it as Yass Reincarnated.
- Windows installer registration and shell verb names identify this fork.
- `.yass`, existing `yass-*` song artifacts, `yass.exe`, and the internal keyring
  service `Yass Reloaded / USDB` remain compatible with saved settings/passwords.
- Renaming the hosted GitHub repository and local checkout directory is separate
  from changing tracked project files. Git remotes are not edited by the build.

## Code Entry Points

- `pom.xml`, `launch4j.xml`, `nsis-installer.nsi`, `flake.nix`: build/distribution.
- `.github/workflows/build.yml`: release assets.
- `src/yass/I18.java`, `YassMain.java`, `YassActions.java`: About/startup/help.
- `src/yass/resources/i18/`: language bundles, help, translation review tables.
- `src/yass/usdb/UsdbCredentialStore.java`: saved-password compatibility key.
- `src/yass/resources/icons/yass-logo.bmp`: renamed legacy launcher splash logo.
- `src/yass/resources/img/about.jpg`: vertical logo used by the About dialog.
- `src/yass/resources/img/about.gif`: legacy copy of the same vertical logo.
- `src/yass/resources/icons/yass-icon.png`, `yass-multi-icon.ico`,
  `src/yass/resources/img/yass-icon-*.png`, and
  `docs/assets/images/favicon.png`: matching Reincarnated icons.
- `.vscode/`, `.idea/`, `Yass-Reincarnated.iml`: contributor configuration.
- `README.md`, `docs/`, `AGENTS.md`, `CLAUDE.md`: project documentation.

## Regression Coverage

- `ShadedJarPackagingSpec`: release Linux JAR names and upload patterns.
- `NsisInstallerEncodingSpec`: UTF-8 BOM, translated installer messages, renamed
  registration, safe app-image upgrades, and preservation of user settings.
- `UiLanguageSupportSpec`: supported language resource loading.
- Packaging and inspection of the built JAR verify renamed bundled resources.
- Manual verification through VS Code's Run Without Debugging confirms startup
  and the actual About dialog's new logo and three copyright notices.
- A JDK 21 Windows `jpackage --type app-image` build verifies the updated ICO,
  `Yass-Reincarnated.jar` launcher input, OliBomby vendor, Yass Reincarnated file
  description, and `2026.7` product version. NSIS and macOS/Linux native package
  builds are not covered by this local smoke check.
- Source scans check remaining Reloaded mentions for historical attribution or
  persisted compatibility keys. No external publishing is part of verification.

## Extension Notes

Keep artifact names, upload paths, launcher inputs, and README commands in sync.
Avoid changing persisted compatibility identifiers as part of future branding.
The legacy launcher bitmap was updated with the built-in imagegen tool using
this prompt: replace the purple word `reloaded` with `reincarnated`, preserving
the blue `yass`, white background, outlined purple lettering, and reflection.
The generated image is converted back to a 400 by 273 BMP for the launcher.
The About JPEG was separately edited with the built-in imagegen tool: replace
the rotated purple `reloaded` with `reincarnated`, preserving the vertical
layout, blue `yass`, pale background, and reflection. It remains 139 by 256.
The icon was edited with built-in imagegen to replace the purple `rld` suffix
with `ri`, keeping the blue `y`, musical staff, border, and palette. All PNG
sizes, the eight ICO frames (16 through 256 pixels), and the favicon are
converted from that one edited source.
