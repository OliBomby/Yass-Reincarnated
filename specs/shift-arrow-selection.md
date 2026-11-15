# Shift-Arrow Note Selection

## Status

Implemented/current in Yass Reincarnated. Covers keyboard note selection and
the optional acceleration to word and page boundaries.

## Purpose

Let users extend a contiguous note selection from the keyboard, with both
note-by-note control and quick selection of words or pages. Explain the
abbreviated "Shift Acc" indicator so users can understand and control this
behavior.

## Functional Requirements

- Selection shortcuts apply when the sheet or lyrics view has focus and the
  song header is not being edited. Selection remains a contiguous range.
- A single `Shift+Down` press adds the next note after the selection. A single
  `Shift+Up` press adds the previous note before the selection. Repeated presses
  continue extending in that direction, preserving notes already selected.
- With selection acceleration enabled, two quick, separate taps of the same
  `Shift+Up` or `Shift+Down` shortcut extend to the word boundary in that
  direction. Three quick taps extend to the page boundary.
- Word extension works from any syllable within a word and respects both
  leading-space and trailing-space lyric conventions, even when the song's
  spacing differs from the user's spacing-correction preference. It follows
  the spaces already present in the lyrics without modifying them. When already
  at the word boundary in the chosen direction, it extends through the adjacent
  word.
- A pause between taps resets selection to note-by-note behavior. Holding the
  shortcut also extends note by note, without escalating to word or page jumps.
- Word and page selection land on notes. When already at a page boundary,
  extending to a page boundary continues to the following boundary in the
  chosen direction.
- With acceleration disabled, each press extends by one note. Word/page
  acceleration is enabled by default and the user's choice persists.
- `F8`, the Edit menu's "Extend Selection by Word/Page" checkbox, and clicking
  "Shift Acc" in the info bar all toggle the same setting and reflect its state.
- The active track's "Shift Acc" indicator shows a green dot when enabled and a
  dim dot when disabled, with a hand cursor and a bordered highlight on hover.
- The info row containing "Shift Acc" is separate from the bottom timeline's
  click area. Clicking empty space in this row or dragging from it does not
  change the timeline position, view, or note selection.
- Hovering over the visible "Shift Acc" indicator displays a tooltip in the
  selected UI language. It explains the green enabled state, the one-note,
  word-boundary, and page-boundary tap behavior, note-by-note behavior when
  disabled, and the click/F8 toggle. The tooltip is available in either state
  and is absent outside the indicator or when the indicator is hidden.
- Zoom accommodates selections spanning pages, and playback and scrolling
  positions follow the moving end of the selection.

## Acceptance Scenarios

- With acceleration enabled, quickly tap `Shift+Down` twice: selection extends
  to a word boundary. A third quick tap extends to a page boundary.
- Start inside a word containing several note syllables, then quickly tap
  `Shift+Down` twice: the selection reaches the last syllable of that word.
  Starting near the word's end and tapping `Shift+Up` twice reaches its first
  syllable. Both behaviors work with leading or trailing lyric spaces.
- Open a song whose lyric spacing differs from the spacing-correction
  preference, then double-tap `Shift+Up` or `Shift+Down`: selection stops at the
  word boundary indicated by the song's spaces. It does not skip intervening
  words to reach the page edge. A third quick tap selects to a page boundary.
- Select a note and press `Shift+Up` repeatedly with acceleration disabled:
  each press adds the previous note, until the first note is reached. Pressing
  `Shift+Down` then adds a note below the selection without deselecting notes.
- Disable acceleration using `F8`, then quickly tap `Shift+Down` repeatedly:
  selection advances note by note, and both the info bar and Edit menu show
  acceleration disabled. Restarting retains this choice.
- Hold `Shift+Down` with acceleration enabled: selection advances note by note.
- Hover over "Shift Acc" with its dot green: the tooltip explains what the
  setting does, what green means, and how to toggle it. Disable the setting and
  hover again: the explanation remains available.
- Move the pointer off "Shift Acc", or view a track where the indicator is not
  shown: its tooltip is absent.
- Click beside "Shift Acc", then drag from that row into the bottom timeline:
  the current position, view, and note selection stay unchanged. Clicking the
  timeline itself still navigates normally.
