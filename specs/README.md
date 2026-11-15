# Feature Spec Format

`specs/` contains functional requirements for implemented or accepted features in
Yass Reincarnated. Specs capture the user's intent and the behavior the product
must provide.

Open or planned requirements belong in `docs/specs/`. Once implemented, move the
spec here and describe the accepted behavior in the present tense.

## Required Structure

Each completed feature spec should use these sections in this order:

```markdown
# Feature Name

## Status

State whether the feature is implemented or accepted, including relevant scope.

## Purpose

Describe the user's problem and desired outcome from the originating request.

## Functional Requirements

Describe what the user can do and the observable behavior the product must
provide, including relevant settings, shortcuts, and fallback or error states.

## Acceptance Scenarios

Give concise examples of user actions and expected results. Include edge cases
that clarify the requested behavior.

## Scope And Constraints

Record explicit limits, exclusions, and compatibility requirements from the
request. Omit this section if there are none.
```

## Style

- Stay close to the originating prompt or bug report in scope and level of detail.
- Describe requirements and observable outcomes, not how they are implemented.
- Do not derive new requirements merely from the current code or tests.
- Include user-visible settings, file formats, and domain rules only when they
  help define the requested behavior.
- Exclude code paths, class and method names, internal algorithms and thresholds,
  test inventories, root-cause analysis, debugging details, and implementation
  advice. Keep engineering documentation separate.
- Acceptance scenarios describe expected behavior; they are not reports of which
  automated tests currently pass.
- Keep future proposals in `docs/specs/` until accepted.
- Apply this format when creating or revising specs; older engineering-oriented
  sections should be removed or moved to separate documentation as they are revised.

## Choosing Spec Boundaries

- Give each spec a coherent user task or observable behavior. Do not split a
  bugfix into a separate spec when it only refines an existing feature.
- Merge requirements that describe the same action and inputs, keeping the
  acceptance examples that distinguish its cases.
- Split independent user tasks even when they share an implementation. Lyric
  text splitting and timed-note splitting, for example, have different outcomes.
- Keep shared behavior in one owning spec and link to it from related workflows.
  Provider setup, transcript persistence, timing refinement, and note alignment
  can evolve independently.
- A short spec is acceptable when its behavior is independently useful. Related
  filenames alone are not a reason to merge.
- After a merge, split, or rename, update repository references.
- Planned specs should state Planned or Exploratory in Status and use the same
  functional sections where practical. They may add Open Questions for unsettled
  decisions; do not present proposals as implemented behavior.
