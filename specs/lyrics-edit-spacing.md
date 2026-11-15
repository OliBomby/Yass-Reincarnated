# Spacing Confirmation While Editing Lyrics

## Status

Implemented in Yass Reincarnated.

## Purpose

Editing lyrics must not silently convert the whole song to a different spacing
convention.

## Functional Requirements

- When a lyric edit requires converting the song to the configured spaces-before
  or spaces-after convention, the editor shows the same OK/Cancel confirmation
  used for rolling lyrics.
- The confirmation explains that the song's spacing differs from the settings
  and that continuing normalizes spacing throughout the song. Its title describes
  spacing normalization rather than a failed rolling or splitting action.
- OK applies the edit and normalizes spacing throughout the song.
- Cancel leaves the lyrics and spacing unchanged and adds no Undo step.
- Songs already using the configured spacing convention can be edited without
  this prompt. After conversion, subsequent edits do not repeat the prompt unless
  the song's spacing differs again, for example after Undo.
- The confirmation preserves lyrics editing mode. Undo and Redo restore and
  reapply the text edit together with the spacing conversion.

## Acceptance Scenarios

- With spaces after words configured, edit one letter in a song using spaces
  before words. Accept the confirmation. The letter changes and spacing is
  converted throughout the song, including other lines. Undo restores the
  original letter and spacing; Redo reapplies both.
- Repeat with spaces before words configured and a song using spaces after
  words.
- Cancel the confirmation for typing, deleting, or replacing lyric text. The
  song remains unchanged and the editor remains in lyrics editing mode.
- Accept a conversion and continue typing. No additional spacing prompt appears.
- Open a song, enter lyrics editing mode, and move the cursor without changing
  text. No spacing conversion or prompt occurs.
