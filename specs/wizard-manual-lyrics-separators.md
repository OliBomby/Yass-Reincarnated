# Wizard Manual Lyrics Separators

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Respect explicit syllable or note boundaries in manually entered wizard lyrics
instead of replacing them with dictionary-inferred splits.

## Functional Requirements

- Manual lyrics in the Create Song Wizard can use `•` or `+` as explicit
  note boundaries when no transcript, subtitle timing map, or MIDI lyric source
  is active.
- A line containing either separator is split only at those explicit separators.
  A line without them retains dictionary-backed word and syllable splitting.
- Separators do not appear in generated lyric text. Spaces around a separator
  are ignored; spaces within a fragment remain.
- Leading, trailing, or repeated separators do not create accidental empty
  fragments. A literal `~` fragment remains an explicit empty-syllable marker.
- Punctuation stays with its fragment. Song-part marker lines such as
  `[chorus]` are skipped.
- Generated word spacing follows the configured spaces-before or spaces-after
  convention. Generated notes retain the wizard's default manual timing and pitch.

## Acceptance Scenarios

- Enter `hel•lo+world`. Three notes are generated in that order without
  dictionary re-splitting or literal separator characters.
- Enter `one two + three`. The first fragment keeps its internal space;
  separator-adjacent spaces do not become extra notes.
- Enter `+hello++~+world!+`. Empty fragments are ignored, while `~` and
  the final punctuation are retained.
- Enter an ordinary line without explicit separators. Dictionary-backed
  syllable splitting still applies.
- Include `[chorus]` before a manually separated line. The marker does not
  become a lyric note.

## Scope And Constraints

This syntax applies only to manual wizard lyrics. It does not change editor
insertion, rehyphenation, or transcript/subtitle-backed note generation.
No persistent settings or song tags are added.
