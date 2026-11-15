# Error Checks And Correction

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded. Covers correction
preferences, the editor Errors dialog, and Library batch correction.

## Purpose

Help authors find and correct song-file problems while controlling lyric
spacing, page boundaries, apostrophes, and capitalization.

## Functional Requirements

### Correction Preferences

- Options under Error Checks / Page breaks let users choose:
  - whether touching syllables should be separated;
  - whether uncommon page breaks should be corrected, left alone, or confirmed;
  - a fixed beat distance for page breaks, with 0 selecting automatic placement;
  - spaces after words or the legacy spaces-before convention;
  - typographic apostrophe correction;
  - capitalization of the first lyric after a page break.
- Defaults leave touching-syllable, apostrophe, and capitalization correction off,
  use automatic page-break placement, and ask about uncommon page-break trimming.
- OK saves preferences, Cancel discards temporary changes, and Reset restores
  panel defaults. Changing preferences does not itself rewrite song rows.
- When no spacing convention has been chosen, startup asks once. OK selects
  spaces after words; Cancel selects spaces before words.

### Editor And Library Correction

- The editor Errors dialog lists detected song problems. Selecting a finding
  selects its row, moves playback position there, and fits its page. Absolute
  pitch view recenters when enabled.
- Supported findings offer Correct for the selected row and Correct All for the
  same finding throughout the song. The song is rechecked afterward, retaining
  the selected finding where possible.
- Direct editor actions correct safe errors, spacing, transposition, or page
  breaks. The Library can correct selected or all songs and selected or all
  visible error classes, save changed song files, and refresh their entries.
- Choosing not to correct uncommon page breaks still allows early, late, and
  overlapping page breaks to be detected and corrected. The ask option can
  remember the user's choice.
- A positive fixed page-break distance places the break that many beats after
  the previous note, within the available pause. Automatic placement follows
  the pause length and song tempo.
- Spacing correction moves word separators to the configured side of words.
- With touching-syllable checks enabled, adjacent notes without a free beat are
  reported when the previous note is longer than one beat; correction shortens
  that note by one beat.
- Apostrophe correction covers lyrics and title/artist metadata. Capitalization
  correction covers lyric starts after page breaks and also applies when page
  boundaries are added or removed.
- Golden-note imbalance follows the [Golden Notes](golden-notes.md) requirements.
  Safe or Library batch correction does not distribute golden notes automatically.

## Acceptance Scenarios

- Change correction preferences and cancel Options. The previous choices remain.
  Repeat and choose OK; the new preferences persist without rewriting lyrics.
- Choose an error in the editor dialog. Its note and page become visible.
  Correct All fixes that error class and refreshes the findings.
- Disable uncommon page-break correction. An overlapping break is still eligible
  for correction, while an uncommon but otherwise valid break is left alone.
- Correct a song using spaces before words while spaces after words is selected.
  Word boundaries are retained with separators moved to the chosen convention.
- Run Library correction on selected songs and error classes. Changed files are
  saved and the Library reflects their updated status.

## Scope And Constraints

This spec covers choosing and applying correction policies. Direct lyric edits
have separate [spacing-confirmation requirements](lyrics-edit-spacing.md).
