# Splitting

## Status

Implemented/current in Yass Reincarnated.

## Purpose

Let users split notes and syllables with less manual cleanup, including held
syllables sung across multiple pitches and text split at the lyrics caret.

## Functional Requirements

- The editor's split action can split a selected note according to clear changes
  in its detected pitch. When pitch or text information is insufficient or
  ambiguous, the normal split behavior is used as the fallback.
- Existing continuation text such as `~n` stays on the right when split again.
- In lyrics editing mode (`F4`), pressing `-` inside a syllable splits its text
  at the caret and shifts subsequent lyrics one note to the right. It must not
  insert `~` before the whole syllable instead.
- Pressing space inside a syllable also splits at the caret and shifts subsequent
  lyrics right, adding a word boundary between the resulting parts.
- Lyrics splitting works with both spaces after words and legacy spaces before
  words, including after word boundaries, between syllables, after line breaks,
  and at the start of a song.
- Splitting respects the editor's touching-syllable setting.
- Explicit syllable divisions are preferred when available.
- Matching syllable and segment counts map one-to-one.
- With two pitch segments and more than two syllables, the first syllable maps
  to the first segment and the remaining syllables map to the second segment.
- For one unhyphenated word across multiple pitch segments, Final Consonant
  Split keeps the main word body first and moves trailing consonant clusters to
  the last segment as `~cluster`.
- Contraction apostrophes move from the apostrophe onward to the final segment.
- Two-letter words remain intact on the first segment.
- Punctuation and trailing spacing stay on the final segment.
- When a normal split is used but a clear changed pitch is available for the
  right-hand note, that note receives the changed pitch.

## Acceptance Scenarios

- With spaces after words enabled, a note containing `sou` follows a word
  boundary and is followed by a `~` continuation note. Enter lyrics editing
  mode, place the caret at `so|u`, and press `-`. The notes contain `so` and `u`,
  and subsequent lyrics shift right. The whole `sou` does not move to the next
  note with a new `~` before it.
- Repeat the same split with spaces before words enabled. The syllable still
  splits at the caret, preserving the existing word boundary.
- Press space at `so|u`. The result is two words, `so` and `u`, rather than two
  connected syllables.
- Split a held syllable with clear detected pitch changes. The resulting notes
  follow those pitches and retain the lyric text in order.
- Split a note without usable pitch information. The normal split behavior is
  used.
- Split an existing `~n` continuation again. The continuation text remains on
  the right-hand note.

## Scope And Constraints

- Pitch-guided splitting acts on a single selected note rather than
  restructuring an entire phrase.
