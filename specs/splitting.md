# Note Splitting And Merging

## Status

Implemented in Yass Reincarnated.

## Purpose

Let authors split held syllables across detected pitches with less manual
cleanup and merge the resulting notes without leaving continuation artifacts.

## Functional Requirements

- The editor's split action can split one selected note according to clear
  changes in detected pitch. Insufficient or ambiguous pitch/text uses the
  normal split fallback.
- When the normal split has a clear changed pitch for the right-hand note,
  that note receives the changed pitch.
- Explicit syllable divisions are preferred when available. Equal syllable
  and pitch-segment counts map one-to-one.
- With two pitch segments and more than two syllables, the first syllable
  maps to the first segment and the remaining syllables to the second.
- For an unhyphenated word spanning several pitch segments, Final Consonant
  Split keeps the main word body first and places trailing consonants in the
  last segment as `~cluster`.
- Text from a contraction apostrophe onward moves to the final segment.
  Two-letter words remain intact on the first segment.
- Punctuation and trailing spacing stay on the final segment. Existing
  continuation text such as `~n` stays on the right if split again.
- Splitting respects the touching-syllable preference.
- Merging split notes at a line end removes the continuation marker introduced
  by the split while preserving text, punctuation, and trailing spacing.
  This applies to merging left, right, or selected notes.

## Acceptance Scenarios

- Split a held syllable with clear detected pitch changes. New notes follow
  those pitches and retain lyric text in order.
- Split without usable pitch information. Normal splitting remains available.
- Split a note with equal explicit syllables and pitch segments. Each syllable
  receives its corresponding segment.
- Split a two-letter word over several pitch segments. The word stays intact
  on the first segment.
- Split an existing `~n` continuation again. Its continuation text stays on
  the right.
- Split a line-ending `is ` note, then merge the parts. The merged note reads
  `is ` without an added trailing `~`. Repeat with `wind, `; its consonants,
  comma, and trailing space are restored.

## Scope And Constraints

Pitch-guided splitting acts on one selected note, not a whole phrase.
[Lyric Syllable Editing](lyrics-syllable-editing.md) covers caret-based text
splits and joins that preserve existing timed notes.
