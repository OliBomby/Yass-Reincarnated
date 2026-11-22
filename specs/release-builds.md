# Release Builds

## Status

Implemented for Yass Reincarnated versioned builds and GitHub Releases.

## Purpose

Build downloadable releases on demand or for a version tag, without nightly
builds or rebuilding installers on ordinary branch pushes.

## Functional Requirements

- Automatic builds run when a version tag beginning with `v` is pushed.
- Ordinary branch pushes and nightly schedules do not start release builds.
- The Build action can also be started manually with an existing version tag.
  All packages are built from that tag, regardless of the branch selected to run
  the action.
- Builds produce Windows, Linux, macOS Apple Silicon, macOS Intel, and Linux Nix
  downloads, including the versioned Linux JAR.
- The app's About window and the package versions use the version from the
  release tag on every platform.
- Linux and macOS builds compile successfully with their required JavaFX
  components and package the native libraries for the selected platform.
- After all platform builds succeed, their downloads are attached to the GitHub
  Release for that tag. Tag-triggered builds publish the release; manual runs
  can choose whether to publish it or keep it as a draft.
- A failed platform build prevents release publication for that run.

## Acceptance Scenarios

- A commit is pushed to the main development branch. No release build starts.
- A `v2026.12` tag is pushed. All platform packages build from that tag and are
  published together in its GitHub Release after successful builds. The About
  window in each download shows version `2026.12`.
- The Build action is run manually for `v2026.7` with the draft option selected.
  Its packages are attached to a draft GitHub Release for `v2026.7`.
- The Linux or macOS build runs with the selected architecture. JavaFX imports
  compile and the resulting installer includes the matching JavaFX libraries.
- A platform build fails. The run does not publish a release with partial assets.
