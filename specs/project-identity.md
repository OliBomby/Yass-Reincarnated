# Project Identity

## Status

Implemented in Yass Reincarnated. This fork descends from DoubleDee's
Yass Reloaded, based on Saruta's Yass 2.4.3.

## Purpose

Give the fork a distinct identity in the application, downloads, documentation,
and contributor tools while preserving attribution and existing user data.

## Functional Requirements

- Current product branding uses **Yass Reincarnated**, including window titles,
  Options, About, generated playlist comments, bundled help, and new specs.
  Application naming is consistent across all six supported languages.
- About credits OliBomby's fork, DoubleDee's Yass Reloaded, and Saruta's Yass,
  with copyright notices for OliBomby (2026), DoubleDee (2023–2026), and
  Saruta (2009–2023).
- About, launcher artwork, application icons, and documentation favicon use
  consistent Reincarnated branding.
- The packaged application JAR is `Yass-Reincarnated.jar`. Windows installs
  into `Yass Reincarnated` with matching Start Menu and desktop shortcuts.
  The installer download is `yass-reincarnated-installer.exe`; the installed
  launcher remains `yass.exe`.
- macOS/Linux packages use Reincarnated naming. Nix package, command, desktop,
  and icon names use `yass-reincarnated`. Download names agree with release
  documentation.
- VS Code uses **Yass Reincarnated** for Run/Debug and
  **Yass Reincarnated: compile** for the build task.
- Project and release links point to
  `https://github.com/OliBomby/Yass-Reincarnated`; public documentation uses
  `https://olibomby.github.io/Yass-Reincarnated/`.
- Distributed packages consistently report the application version and OliBomby
  as vendor.
- Existing settings, saved passwords, and song artifacts remain compatible.
  In particular, `.yass`, `yass-*` artifacts, `yass.exe`, and the existing
  `Yass Reloaded / USDB` credential identity remain usable.
- Copyright holders, author names, and third-party licenses are retained.
  Historical upstream links and tutorial titles retain their attribution;
  donations to upstream are described as upstream support.
- Inherited feature specs acknowledge Yass Reloaded. Features introduced by
  this fork do not acquire an inherited-feature claim.

## Acceptance Scenarios

- Open About in a supported language. Reincarnated branding and all three
  project attributions are visible.
- Install the Windows release. The installation and shortcuts identify
  Yass Reincarnated while the launcher remains `yass.exe`.
- Open the application using an existing user profile and song library.
  Settings, USDB credentials, and song artifacts remain usable.
- Follow current project, release, and documentation links. They identify this
  fork; historical upstream links still credit the original project.

## Scope And Constraints

Branding does not rename internal compatibility identifiers, migrate user data,
rename the hosted repository or local checkout, or edit Git remotes.
