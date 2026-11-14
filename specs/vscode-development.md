# VS Code Development

## Status

Implemented/current. The repository includes a Java Run/Debug configuration.

## Purpose

Let contributors build and launch Yass Reincarnated with VS Code's Run button while
preserving the JDK and classpath requirements of the application.

## User-Facing Behavior

- Open the repository folder and install the recommended Java language support
  and debugger extensions.
- Set `JAVA_HOME` to JDK 21 and restart VS Code after changing it.
- Wait for the Maven project import, select **Yass Reincarnated** in **Run and Debug**,
  then press the green Run button or F5. Ctrl+F5 runs without debugging.
- The required **Yass Reincarnated: compile** task builds through the Maven Wrapper
  before the application starts. Compilation failures must stop launch.

## Core Rules

- Launch `yass.YassMain` from the repository root with the JDK in `JAVA_HOME`.
- Omit `projectName` from the launch configuration. The debugger discovers the
  project containing the main class, so a cached Maven project name cannot
  invalidate launch after a rename. This repository has one application project.
- Include `target/classes`, Maven runtime dependencies, and `lib/*.jar` on the
  classpath so legacy JMF classes are available.
- Keep module paths empty for this non-modular application and use a JVM
  argument file to avoid Windows command-line length limits.
- Maven owns pre-launch compilation. Disable the Java debugger's additional
  Eclipse build: it rejects the existing package/folder mismatch in the resource
  utility `CoverGenerator.java`, which Maven accepts.

## Data And Configuration

- `.vscode/launch.json`: Java executable, main class, classpath,
  integrated terminal, and pre-launch task.
- `.vscode/tasks.json`: wrapper compile task with Windows and Unix commands.
- `.vscode/extensions.json`: `redhat.java` and `vscjava.vscode-java-debug`.
- `.vscode/settings.json`: `java.debug.settings.forceBuildBeforeLaunch=false`.
- Wrapper bootstrap and cache behavior are documented in
  [Maven Wrapper](maven-wrapper.md).

## Code Entry Points

- `.vscode/launch.json` and `.vscode/tasks.json`: launch/build integration.
- `src/yass/YassMain.java`: application entry point.
- `pom.xml`: Maven project layout, dependencies, and compilation.
- `README.md`, Developers / VS Code: contributor setup instructions.

## Regression Coverage

Manual verification with JDK 21:

- VS Code configuration JSON validates.
- The wrapper compile task passes.
- F5 launches the actual application with the existing song library.
- Run Without Debugging (Ctrl+F5) launches after the project rename without a
  hardcoded Java project name; verified in the existing VS Code window.

No automated VS Code UI regression test is included.

## Extension Notes

Keep machine-specific JDK paths out of shared configuration. VS Code inherits
`JAVA_HOME` when it starts; existing windows may retain an outdated environment.
This configuration requires the Java extensions and completed project import.
