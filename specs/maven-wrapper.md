# Maven Wrapper

## Status

Implemented/current. Apache Maven Wrapper 3.3.4 pins Maven 3.9.16.

## Purpose

Let Yass Reloaded contributors build with a consistent Maven version without
installing Maven separately.

## User-Facing Behavior

- Windows contributors use `.\mvnw.cmd` from PowerShell.
- macOS/Linux contributors use `sh ./mvnw`.
- The first invocation downloads Maven; later invocations reuse the cached copy.
- A JDK is still required. Use JDK 21 and set `JAVA_HOME` to its installation.

## Core Rules

- Keep the upstream wrapper scripts unmodified.
- Use the `only-script` distribution; no wrapper JAR is checked in.
- Pin the Maven version and its SHA-256 checksum together.
- Preserve LF line endings for `mvnw` and CRLF for `mvnw.cmd`.

## Data And Configuration

- `.mvn/wrapper/maven-wrapper.properties` selects the Maven Central distribution
  and its SHA-256 checksum.
- Maven is cached under `~/.m2/wrapper/dists` by default. `MAVEN_USER_HOME` can
  override the wrapper cache location.
- First use requires internet access. Project dependencies also require a
  download unless already cached in the Maven local repository.

## Code Entry Points

- `mvnw` and `mvnw.cmd`: official Apache Maven Wrapper 3.3.4 scripts.
- `.mvn/wrapper/maven-wrapper.properties`: pinned distribution configuration.
- `pom.xml`: Java 21 compilation, dependencies, tests, and packaging.
- `README.md`: contributor setup and build commands.

## Regression Coverage

Manual verification with JDK 21:

- Fresh Windows wrapper bootstrap with `mvnw.cmd -version`.
- Fresh Unix wrapper bootstrap under Git Bash with `bash ./mvnw -version`.
- `sh -n ./mvnw` shell syntax check.
- Windows wrapper compilation and `YassRowSpec` execution.

The wrapper uses upstream scripts; application tests remain under `test/groovy/`.

## Extension Notes

CI currently uses the runners' installed Maven. Changing CI to use the wrapper
is a separate build-pipeline change. When updating Maven, update the distribution
URL and checksum and verify a fresh wrapper bootstrap.
