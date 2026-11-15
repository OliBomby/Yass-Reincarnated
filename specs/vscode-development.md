# VS Code Development

## Status

Implemented in Yass Reincarnated.

## Purpose

Let contributors build, run, and debug Yass Reincarnated with VS Code's Run
button using the project's supported JDK and dependencies.

## Functional Requirements

- Contributors open the repository folder, install the recommended Java
  language/debugger extensions, and allow Maven project import to complete.
- VS Code uses JDK 21 from `JAVA_HOME`. After changing it, contributors restart
  VS Code so the new environment is available.
- Run and Debug offers **Yass Reincarnated**. F5 starts debugging; Ctrl+F5
  runs without debugging.
- The **Yass Reincarnated: compile** task builds through the
  [Maven Wrapper](maven-wrapper.md) before launch. A compilation failure stops
  the launch.
- Launch starts from the repository root with all production dependencies,
  including legacy media libraries.
- Launch continues to work after a project rename without a machine-specific
  Java project name or JDK installation path in shared configuration.
- The launch workflow accommodates long dependency paths on Windows.

## Acceptance Scenarios

- Import the repository with JDK 21 and the recommended extensions, select
  Yass Reincarnated, and press F5. Compilation completes and debugging starts.
- Use Ctrl+F5. Compilation completes and the application runs without debugging.
- Introduce a compilation error and launch. The build failure prevents startup.
- Rename the imported project and complete import again. The shared launch
  configuration still starts the application.

## Scope And Constraints

The configuration requires the Java extensions and a completed Maven import.
Contributor setup commands remain in the repository README.
