# Lyrics Cursor Page Navigation

## Status

Implemented for navigation from the lyrics editor in Yass Reincarnated.

## Purpose

Keep the note view stable while editing lyrics so that moving the lyrics cursor
does not move the current syllable to the left edge and lose page boundaries.

## Functional Requirements

- Moving the lyrics cursor continues to select the corresponding note.
- When the corresponding note is already visible, cursor movement preserves the
  current horizontal view.
- When the cursor moves to an off-screen page, the note view follows the page
  containing the syllable, preserving page boundaries.
- The same behavior applies when navigating backwards through the lyrics.

## Acceptance Scenarios

- With a page visible, move the lyrics cursor between syllables on that page.
  The corresponding notes are selected and the horizontal view stays in place.
- Move the cursor to a syllable in the middle of the next off-screen page. The
  view shows that page from its beginning, rather than starting at the syllable.
- Move between syllables on the newly displayed page. The view remains stable.
- Move the cursor back to a syllable on the previous off-screen page. The view
  returns to that page's boundaries.
