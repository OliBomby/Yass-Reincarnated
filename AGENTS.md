# Yass Reincarnated Agent Notes

## Project Identity

This repository is **Yass Reincarnated**, a fork of Yass Reloaded (itself a fork
of Yass). User-facing documentation,
release notes, UI copy, and new specs should use the name "Yass Reincarnated" when
the project identity matters.

## Documentation Layout

- `specs/` contains current-state specs for implemented or accepted features.
- `docs/specs/` contains functional specs for open or planned features that are
  not done yet.
- When a feature from `docs/specs/` is implemented, move its spec into `specs/`
  and rewrite it as a current-state spec.
- Completed feature specs must follow the template in `specs/README.md`.
- Public GitHub Pages documentation lives under `docs/`; do not treat
  `specs/` as end-user documentation.

## Spec Content

- Specs describe functional requirements: the user's problem, intended behavior,
  observable outcomes, acceptance examples, and explicitly requested constraints.
- Stay close to the prompt or bug report that caused the spec. Preserve its scope
  and intent; do not turn implementation discoveries into additional requirements.
- Do not include implementation details, code entry points, class or method names,
  internal algorithms, test-file inventories, debugging notes, or implementation
  advice. Keep those in code, tests, or separate engineering documentation.
- User-visible settings, shortcuts, file formats, and domain rules belong in a
  spec when they are part of the requested behavior. Describe what they mean to
  the user rather than how the code implements them.
- For bugfixes, describe the expected behavior and a concise acceptance example;
  leave the root-cause analysis and patch mechanics out of the spec.

## Feature And Bugfix Workflow

- When working on an existing feature or bugfix, first check whether a matching
  current-state spec already exists in `specs/`.
- If no matching spec exists, point that out before making broad changes and
  suggest a Deep Explore pass to document the feature or bug area first.
- A Deep Explore pass should investigate current behavior, code entry points,
  relevant tests, edge cases, and missing regression coverage. Use those findings
  to create or update the functional requirements spec, keeping implementation
  findings and coverage details in separate engineering notes when needed.
- Small, obvious fixes may still proceed without a full Deep Explore, but note
  the missing documentation in the final response so it can be backfilled.

## Development Notes

- This is a Java/Swing application with Groovy/Spock tests.
- Prefer focused changes over broad rewrites, especially in large classes such
  as `YassActions`, `YassTable`, `YassSheet`, and `SongHeader`.
- Keep existing user work intact. Do not revert unrelated dirty files.
- Use `rg` for repository search.
- Use `apply_patch` for manual file edits.

## Verification

Use the Maven Wrapper with JDK 21 (`JAVA_HOME` must point to the JDK).
On Windows, replace `sh ./mvnw` below with `.\mvnw.cmd`.

- Compile without tests: `sh ./mvnw -q -DskipTests compile`
- Package without tests: `sh ./mvnw -q -DskipTests package`
- Run all tests: `sh ./mvnw -q test`
- Run one Spock spec: `sh ./mvnw -q "-Dtest=SpecName" test`

For docs-only changes, a directory/link sanity check is usually enough. Mention
explicitly when no code tests were run.
