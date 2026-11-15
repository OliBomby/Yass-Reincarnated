# Lyric Syllable Editing

## Status

Implemented in Yass Reincarnated.

## Purpose

Let authors split and join lyric text at the caret while retaining the existing
notes and their timing.

## Functional Requirements

- In lyrics editing mode (`F4`), `-` inside a syllable splits its text at the
  caret and shifts subsequent lyrics one note right. It does not insert `~`
  before the whole syllable.
- Space inside a syllable also splits at the caret and shifts following lyrics
  right, adding a word boundary rather than a hyphen before the second part.
- Text splitting preserves note count, timing, duration, and pitch.
- At a syllable start immediately after a hyphen or word space, `-` and Space
  act on that preceding separator. The caret remains at the syllable start.
  Pressing the key matching the existing separator leaves text and caret unchanged.
- Delete before a hyphen or Backspace after it joins the syllables on the earlier
  note and rolls later lyrics one note left, preserving timed notes.
- Delete before a word space or Backspace after it joins the neighboring text
  without introducing a hyphen.
- These actions support spaces after words and legacy spaces before words,
  including word boundaries, line boundaries, and the start of a song.
- A required [spacing confirmation](lyrics-edit-spacing.md) appears only once
  for the action. The same keystroke must not also split a timed note.
- One Undo reverses a split, join, or separator change; Redo reapplies it,
  without an empty lyric state or an extra step with no visible change.
  Editing context follows [Undo And Redo Context](editor-undo-redo-context.md).

## Acceptance Scenarios

- With `sou` followed by a `~` continuation, press `-` at `so|u`.
  The existing notes contain `so` and `u`, with later lyrics shifted right.
  Repeat with both spacing conventions, including after a word boundary.
- Press Space at `so|u`. The editor displays `so u`, without a hyphen
  before `u`.
- Press Space at `ru-|les`. The text becomes `ru |les`. Press `-` there
  to restore `ru-|les`. The caret stays at the start of `les`.
- Press Delete at `ru|-les` or Backspace at `ru-|les`. The earlier note
  contains `rules` and subsequent lyrics roll left. One Undo restores the
  division and Redo removes it again.
- Press Delete at `ru| les` or Backspace at `ru |les`. The text becomes
  `rules` without a replacement hyphen, under either spacing convention.
- Press `-` at `r|u` with a following `~`. If spacing conversion is
  required, accept its single prompt. Existing notes now contain `r` and `u`;
  no timed note or extra `~` is added. One Undo restores the original state.

## Scope And Constraints

These actions edit lyrics across existing notes.
[Note Splitting And Merging](splitting.md) covers changes to timed note structure.
