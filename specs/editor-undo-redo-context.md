# Undo And Redo Context

## Status

Implemented in Yass Reincarnated. Covers preserving the working position while
undoing and redoing editor changes, including active lyrics editing.

## Purpose

Let authors reverse changes without losing their current page, selected
syllable, or text-editing position.

## Functional Requirements

- Undo and Redo preserve the current timeline position, horizontal zoom, and
  playback cursor in both relative and absolute pitch views.
- During lyrics editing, `Ctrl+Z`, `Ctrl+Y`, and the menu actions keep the
  lyrics editor focused and editable so typing can continue immediately.
- The current syllable, caret position within it, and text selection remain at
  the current editing location rather than returning to an earlier edit's selection.
- Selection direction is preserved. Changes to words or page breaks before
  the selection do not move it to unrelated syllables.
- If a syllable becomes shorter, the caret and selection endpoints stay as close
  as possible within that syllable.
- Undoing replacement of a selected syllable does not select all lyrics.
- Undo/Redo of a lyric page-break edit keeps the selected syllable and caret at
  the edited boundary, including after navigation away from earlier edits.

## Acceptance Scenarios

- Make an earlier edit at a different page zoom, then split lyrics on a later
  page. Undo and Redo preserve the later timeline section, zoom, and playback
  cursor in either pitch-view mode.
- Navigate away from an earlier lyric edit, then Undo or Redo while editing
  the current syllable. Focus stays in the lyrics editor and typing can continue.
- Select text backward across syllables and undo a change before it. The same
  syllables and selection direction remain selected despite changed text offsets.
- Replace a selected syllable and Undo or Redo. Focus remains and all lyrics
  are not highlighted, including for the first syllable of the song.
- Add or remove a page break on a later page after navigating from an earlier
  edit. Undo and Redo retain the syllable at that later boundary.

## Scope And Constraints

Each action's content changes and Undo grouping belong to its own feature spec.
This spec defines preservation of the user's working context.
