# Lyrics Cursor Page Navigation

## Status

Implemented in Yass Reincarnated.

## Purpose

Keep the note view stable while navigating lyrics and preserve the current
syllable when switching from note navigation to lyrics editing.

## Functional Requirements

- Moving the lyrics cursor selects the corresponding note.
- In lyrics editing mode (`F4`), selecting with the mouse or Shift+Arrow highlights
  exactly the selected characters. The corresponding notes stay selected.
- When browsing notes or editing with a caret and no text selected, the lyrics
  highlight shows the whole syllable.
- If that note is already visible, moving the cursor preserves the horizontal
  view.
- Moving to an off-screen page follows that syllable's page from its boundary,
  rather than placing the syllable itself at the left edge.
- Forward and backward navigation behave consistently.
- Entering lyrics editing after arrow-key note navigation places the caret at
  the selected syllable. Further cursor movement does not jump to a stale
  position, including the song's end.
- Pressing an arrow key and F4 together keeps navigation near the selected
  syllable rather than jumping to the start.
- Splitting or merging notes on the timeline keeps the lyrics selection at
  the edit. Pressing F4 starts editing there.

## Acceptance Scenarios

- Move between syllables on a visible page. Corresponding notes are selected
  without changing the horizontal view.
- In `sou-ve-nir`, select `ou` while editing. The lyrics highlight covers `ou`, and
  the first note is selected. Extend the selection through `ve` to highlight
  `ou-ve` and select both notes. Collapse the selection inside `sou` to see the
  whole-syllable highlight again.
- Move to a syllable in the middle of the next off-screen page. The view shows
  that page from its beginning and remains stable for further movement on it.
- Move backward to the previous off-screen page. Its page boundaries are shown.
- Edit a note earlier in a song, navigate to a nearby note with arrows, switch
  to lyrics editing, and move the caret. Selection stays near that syllable
  without first requiring a visit to the song's end.
- Select a middle-song note and press Left or Right almost simultaneously with
  F4. Lyrics navigation continues near that note.
- Split a note or merge two notes in the middle of a song. The lyrics at the
  edit are highlighted. Press F4 and move the cursor with Left or Right.
  Navigation continues near the edit.

## Scope And Constraints

[Undo And Redo Context](editor-undo-redo-context.md) specifies how editing
position, focus, and view are preserved while reversing changes.
