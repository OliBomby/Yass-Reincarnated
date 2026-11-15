# Editor Insert Note

## Status

Implemented/current. Covers the Add Syllable dialog opened from the editor
lyrics menu, insert-note toolbar button, `Shift+Enter`, and `Ctrl+Enter`.

## Purpose

Yass Reincarnated lets chart authors add missing syllables or placeholder notes
without losing existing notes or leaving the current lyric page.

## Functional Requirements

- The action stops playback and opens a lyrics input dialog. Accepting the
  dialog adds notes. Cancel, Escape, and closing the dialog leave the song unchanged.
- With a selection, additions go immediately after the last selected row,
  starting at the selected note's end. They stay on that page, before its page break,
  regardless of the playback cursor position.
- Without a selection, additions use the editor cursor position.
- Adding notes preserves existing notes, including notes whose timing overlaps
  the additions. Existing notes and page breaks are not removed or shifted.
- Blank or whitespace-only input behaves exactly like entering `~`, including
  lyric spacing, timing, and pitch. It does not connect to the following note.
  Lack of free space before the following note does not silently cancel the addition.
- Ordinary text uses the editor's lyric syllable splitting and word spacing.
- New lyrics have a word space separating them from the preceding syllable
  unless the input starts with `-`. Blank placeholders also preserve this word boundary.
- Syllables generated together have no time gaps: each starts exactly when the
  preceding generated syllable ends. Supplied note rows retain their explicit timing.
- In text containing hyphens, each hyphen separates explicitly supplied syllables
  without a word space. A leading hyphen connects to the previous syllable; a
  trailing hyphen connects to the next syllable. The connector itself is not
  stored as lyric text. Spaces still separate words. Connections to existing
  notes do not cross page breaks.
- Successful additions select only the newly inserted notes and refresh the
  editor view. Preserved earlier/later notes, page breaks, and the end marker
  are not included in the selection.
- Cancelling the dialog preserves the selection as well as the song.
- Selection behavior is the same for the lyrics menu, toolbar, `Shift+Enter`,
  and `Ctrl+Enter`, including its ordinary-insertion fallback.
- `Ctrl+Enter` uses loaded vocal pitch when the selected audio track is
  `#VOCALS` and pitch data is available. Other cases use ordinary insertion.
- Vocal-aware insertion keeps the generated group within its local page and
  the available range before the following existing note. Too many syllables
  or no available range produces an explanatory message without changing the song.
  Unusable pitch or an alignment result outside the available range falls back
  to ordinary insertion, preserving existing notes.
- Plain `Enter` retains its separate page-break action.

## Acceptance Scenarios

- Select a note ending at beat 104 while the cursor is elsewhere, then add
  `one two`. The additions start at beat 104 immediately after that note in
  the lyric page. The selected note remains present, with a word space before
  `one`. The `two` note starts exactly when the `one` note ends.
- Add several syllables whose timing overlaps a following note. Both the
  additions and the following note remain present with their original timing.
- Select the last note before a page break and add lyrics or a blank placeholder.
  The new notes appear before that page break on the selected note's page.
- Select a note ending at beat 4 with the next note starting at beat 4, then
  accept blank input. A `~` placeholder is added and both original notes remain.
- Between `one ` and `two `, accept blank input. The result is `one ~ two`,
  exactly as when entering `~` explicitly, with a word boundary after the tilde.
- Add `hel-lo world`. The additions contain `hel`, `lo`, and `world`, with no
  word space between `hel` and `lo` and a word space before `world`.
- Between `pre ` and `next ` on one page, add `-fix-`. The three syllables read
  as one connected word, with no literal hyphens or separating word spaces.
- After a syllable `pre` with no trailing space, add `fix`. The lyrics read
  `pre fix`. Adding `-fix` instead reads `prefix`.
- Insert one or several syllables in the middle of a longer song. Only the
  inserted notes are selected; preserved notes on either side remain unselected.
- Insert at the end of a song. New notes are selected without the end marker.
- Use `Ctrl+Enter` when vocal-aware insertion falls back to ordinary insertion.
  Only the resulting new notes are selected.
- Cancel the dialog. Song content and selection remain unchanged.

## Scope And Constraints

These requirements cover the dialog-based Add Syllable action. Clipboard paste
and the separate page-break action retain their own behavior. No new persistent
settings or song tags are introduced.
