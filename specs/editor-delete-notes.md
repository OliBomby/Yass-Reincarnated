# Editor Delete Notes

## Status

Implemented/current. Covers deleting selected notes in the song editor.

## Purpose

Yass Reincarnated lets chart authors delete unwanted notes and their syllable
text together, without unexpectedly moving that text onto another note.

## Functional Requirements

- `Delete`, the remove-note toolbar button, and **Delete Notes and Lyrics**
  erase the selected notes together with their lyric text.
- Unselected notes keep their lyric text, timing, pitch, and note style.
- **Remove Notes (Keep Lyrics)** remains available through `Ctrl+Delete` and
  the Lyrics menu. It transfers removed syllables to adjacent notes.
- Both menu labels distinguish whether lyrics are deleted or retained.
- Deletion supports multiple selected notes and the last remaining note.
  Song headers and the end marker remain intact. Page breaks left after the
  final note are removed.
- After deletion, selection stays on a remaining note near the deleted notes,
  or is cleared when no notes remain. Undo restores deleted notes and lyrics.
- In the lyrics area, ordinary Delete while editing text retains its text-editing
  behavior. Outside text editing, Delete erases the selected notes and lyrics.

## Acceptance Scenarios

- Select `lo` between notes containing `hel` and `world`, then press Delete.
  The `lo` note and text disappear; the other two notes retain their own text.
- Select several notes, then use the remove-note toolbar button. Their notes
  and syllables disappear together, including when the selection spans pages.
- Delete the only remaining note. Its text disappears, song headers and the end
  marker remain, and selection clears. Undo restores the note and its text.
- Select a note containing `hel` followed by `lo`, then press Ctrl+Delete.
  The first note is removed and its text is retained on the adjacent note.

## Scope And Constraints

Original Yass used Delete to retain lyrics and Ctrl+Delete to erase them. Yass
Reincarnated makes complete deletion the default and keeps lyric-preserving
removal as the explicit alternative.
