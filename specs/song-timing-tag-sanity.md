# Song Timing Tag Sanity

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded. Validation runs
when opening songs and when returning to the Library after a touched session.

## Purpose

Keep malformed timing metadata from placing audio, video, preview markers, or
the editor view far outside a song's useful range.

## Functional Requirements

- Unsafe timing tags are cleaned automatically. Ordinary editor opening may
  report the removed or corrected tags; wizard-created songs can be cleaned
  silently.
- Cleanup leaves the song marked modified for a normal user save.
- Returning to the Library revalidates a song only after a touched editor
  session. Opening starts a fresh session; writing the song during the session
  makes it eligible for this revalidation.
- `#GAP` cannot be negative; an invalid value is corrected to 0.
- `#START`, `#END`, and `#PREVIEWSTART` cannot be negative or exceed known
  audio duration. Invalid optional values are removed.
- A nonzero `#END` must be after a nonzero `#START`. `#START` must precede
  the first singable note in audio time.
- Negative `#VIDEOGAP` is allowed, but implausibly large offsets are removed.
- `#MEDLEYENDBEAT` must follow `#MEDLEYSTARTBEAT`. If either is invalid,
  both medley tags are removed.
- Negative note beats remain valid when plausible relative to `#GAP`.
  Every negative note position is considered, not just the first note.
- The song file stores `#END` in milliseconds while the editor displays it
  in seconds.

## Acceptance Scenarios

- Open a song with negative GAP and a preview beyond its known audio duration.
  GAP becomes 0, the invalid preview tag is removed, and the song is modified.
- Open a song whose end precedes its start. The invalid optional timing is
  removed rather than leaving an unusable playback range.
- Open a song with a reversed medley range. Both medley tags are removed.
- Open a song with a plausible negative note beat and enough GAP to accommodate
  it. Its valid timing is preserved.
- Include an implausible negative note later in the song. Validation considers
  that note even when the first note is valid.
- Open a song with no optional timing tags. Cleanup does not invent those tags.

## Scope And Constraints

Validation prevents unusable timing without rejecting unusual but valid
authoring choices.
