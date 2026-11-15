# Golden Notes

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Let authors mark golden notes manually or obtain suggestions that approach the
configured golden-bonus target without replacing existing authoring choices.

## Functional Requirements

- **Suggest Golden Notes** stops playback and marks suitable regular or rap
  notes as golden. It does not run during lyric, Library-cell, filter, or
  song-header text editing.
- `G` toggles selected plain/golden notes. `Shift+G` toggles rap/rap-golden notes.
- Suggestions favor pitch leaps and long words that fit the remaining target
  duration. Word boundaries follow the configured spacing convention, and
  suggested word groups do not cross page breaks.
- Suggestions can convert plain notes (`:`) to golden (`*`) and rap (`R`) to
  rap-golden (`G`). Freestyle and already-golden notes remain unchanged.
- Scoring preferences determine the target and allowed variance: base points
  default to 7500, golden points to 1250, line bonus to 1000, and golden variance
  to 250. Freestyle notes do not count by default.
- The Errors dialog reports uncommon golden balance. Correct and Correct All
  are available only when golden duration is below the target.
- Correction only adds golden notes. At or above the target, it does not demote
  or rebalance existing golden notes.
- Safe-error correction and Library batch correction do not distribute golden
  notes automatically. Suggestions do not create new song tags.

## Acceptance Scenarios

- Run Suggest Golden Notes on a song below its target. Suitable regular and rap
  notes become golden without exceeding the remaining target duration.
- Include freestyle and manually marked golden notes in the song. Suggestions
  leave those notes unchanged.
- Use a song with legacy leading-space word boundaries. Suggestions treat its
  words as complete groups and do not join words across a page break.
- Open a golden-balance finding below the target. Correction is available.
  Open one at or above the target; no automatic removal is offered.
- Run safe Library correction. The song's golden-note distribution stays intact.
