# Maven Wrapper

## Status

Implemented in Yass Reincarnated.

## Purpose

Let contributors build with a consistent Maven version without installing
Maven separately.

## Functional Requirements

- Windows contributors use `.\mvnw.cmd`; macOS/Linux contributors use
  `sh ./mvnw` from the repository root.
- A JDK is required. The supported development setup uses JDK 21 with
  `JAVA_HOME` pointing to its installation.
- The wrapper downloads the repository's pinned Maven distribution on first
  use and reuses it afterward. The distribution is verified against its pinned
  SHA-256 checksum.
- First use requires internet access; dependencies also require downloading
  unless already cached.
- The default wrapper cache is `~/.m2/wrapper/dists`; `MAVEN_USER_HOME`
  can override the cache location.
- `compile`, `test`, and `package` are available through the wrapper.
  `compile exec:exec` builds and launches the application with its production
  dependencies, including legacy media libraries.
- The same wrapper workflow supports Windows, macOS, and Linux release builds.

## Acceptance Scenarios

- On a machine with JDK 21 and no separate Maven installation, run the wrapper
  version command. Maven downloads, verifies, and reports the pinned version.
- Run the wrapper again. The downloaded distribution is reused.
- Run `.\mvnw.cmd compile exec:exec` on Windows or
  `sh ./mvnw compile exec:exec` on macOS/Linux. The application launches
  with its required media and JavaFX components available.
- Compile, test, and package through the wrapper. Each command uses the same
  pinned Maven distribution.

## Scope And Constraints

The wrapper supplies Maven, not a JDK or the external media tools. Nix packaging
uses Maven from its Nix environment. VS Code launch behavior is specified in
[VS Code Development](vscode-development.md).
