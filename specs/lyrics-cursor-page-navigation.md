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
- Switching to the lyrics editor after arrow-key navigation places the lyrics
  cursor at the currently selected syllable. Further cursor movement does not
  jump back to an old position, including the end of the song.
- Pressing an arrow key and F4 together keeps navigation near the selected
  syllable instead of jumping to the start of the song.
- Undo and Redo preserve the current timeline position, horizontal zoom, and
  playback cursor, so reversing a lyric edit does not jump to an earlier page.

## Acceptance Scenarios

- With a page visible, move the lyrics cursor between syllables on that page.
  The corresponding notes are selected and the horizontal view stays in place.
- Move the cursor to a syllable in the middle of the next off-screen page. The
  view shows that page from its beginning, rather than starting at the syllable.
- Move between syllables on the newly displayed page. The view remains stable.
- Move the cursor back to a syllable on the previous off-screen page. The view
  returns to that page's boundaries.
- While working on an earlier part of a song, update a note, use the arrow keys
  to select a nearby note, and immediately switch to the lyrics editor. Move the
  lyrics cursor with an arrow key. The cursor and selected note stay at that
  part of the song, even without previously visiting the end.
- Select a note in the middle of the song and press Left or Right and F4 nearly
  simultaneously. The cursor and note selection remain near that note, and
  subsequent arrow-key navigation in the lyrics editor works normally.
- Split a lyric on a later page after earlier edits used a different page zoom.
  Undo and Redo the split. The lyrics change while the same timeline section,
  zoom, and playback cursor remain in place, in both pitch-view modes.
