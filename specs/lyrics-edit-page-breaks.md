# Page Breaks While Editing Lyrics

## Status

Implemented in the lyrics editor in Yass Reincarnated.

## Purpose

Authors can add page breaks with Enter or the page break button while editing
lyrics without leaving lyrics editing mode.

## Functional Requirements

- In lyrics editing mode (`F4`), Enter toggles a page break before the note
  corresponding to the lyrics cursor.
- The page break button toggles the same boundary while editing lyrics.
- The lyrics display reflects the changed page boundary and remains editable.
- Page-break edits preserve note timing, duration, and pitch.
- One Undo reverses the page-break edit; Redo reapplies it.

## Acceptance Scenarios

- Place the lyrics cursor in the second note on a page and press Enter. A page
  break appears before that note, and lyrics editing remains active.
- Place the cursor in the first note after an existing page break and press
  Enter. The page break is removed and the lyrics display joins the pages.
- While editing lyrics, use the page break button to add or remove a break.
  The lyrics immediately split onto separate lines or join onto one line,
  and lyrics editing remains active.
- Undo and Redo either change. The page boundary is restored and reapplied
  without changing note timing, duration, or pitch.
