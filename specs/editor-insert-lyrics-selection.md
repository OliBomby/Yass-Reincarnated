# Selection After Inserting Lyrics

## Status

Implemented/current in Yass Reincarnated.

## Purpose

Adding lyrics should leave the author ready to work on the new notes without
unexpectedly selecting the rest of the song.

## Functional Requirements

- After the author confirms non-blank lyrics in the Add Syllable dialog, the
  selection covers the newly inserted notes.
- Existing notes preserved before or after the insertion are not selected.
- This behavior applies to `Ctrl+Enter`, including its standard insertion
  fallback, and to the same dialog opened with `Shift+Enter`, the lyrics menu,
  or the toolbar.
- Cancelling the dialog leaves the song and selection unchanged.

## Acceptance Scenarios

- With one note selected near the beginning of a longer song, insert a single
  syllable. Only the inserted note is selected; later notes remain unselected.
- Insert several syllables in the middle of a song. The inserted notes are
  selected together, while the preserved notes on either side are unselected.
- Insert lyrics at the end of a song. The final end marker is not selected.
- Use `Ctrl+Enter` when vocal-aware insertion falls back to standard insertion.
  Only the resulting inserted notes are selected.
