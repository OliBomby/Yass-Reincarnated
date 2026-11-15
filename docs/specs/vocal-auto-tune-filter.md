# Vocal Auto-Tune Filter

## Status

Exploratory proposal, not implemented. The workflow below is a candidate;
backend, correction modes, and output choices remain open.

## Purpose

Explore an optional pitch-corrected vocal helper that makes chart authoring
easier while preserving original song audio.

## Functional Requirements

- A candidate **Create Pitch-Corrected Vocals** action uses the configured
  `#VOCALS` track and explains missing vocals or processing prerequisites.
- The workflow analyzes vocals and lets the user choose correction settings,
  create a helper file only, assign the result as vocals, or cancel.
- Key/scale information may come from `#COMMENT:key=...` or user selection.
  Chromatic behavior without a key remains an open decision.
- Processing creates a deliberate offline render next to the song, with a
  collision-safe name such as
  `<Artist> - <Title> (Vocals Tuned).ogg`.
- The helper preserves duration as closely as possible, avoids clipping, uses a
  reliably playable format, and records the applied correction settings.
- Original audio and stems remain untouched. Assigning the helper requires
  user confirmation and changes only `#VOCALS`.
- Cancellation creates no output or tag changes. Failure leaves the song unchanged.
- The interface distinguishes note-level auto-tuning from uniform global pitch
  correction.

## Acceptance Scenarios

These examples describe the proposed behavior rather than implemented support.

- Start without a vocal track or usable processing backend. A clear message
  identifies the missing prerequisite.
- Create a helper without assigning it. A new song-local file is available,
  while the original vocal file and all song tags remain unchanged.
- Confirm assignment of a successful helper. Only `#VOCALS` changes;
  the original file remains available.
- Repeat rendering with an existing proposed filename. The existing file is
  preserved and the helper gets a distinct name.
- Cancel or fail processing. Song assignments and original files remain unchanged.

## Scope And Constraints

This proposal is an offline helper workflow, not live pitch correction.
[Pitch Shift](../../specs/pitch-shift.md) already covers uniform source tuning;
that implemented feature does not establish note-level auto-tune support.

## Open Questions

- Which backend provides sufficient quality and distribution compatibility
  across Windows, macOS, and Linux?
- Which correction modes and key/scale controls should be offered? Is
  chromatic correction allowed without a key?
- Should input remain limited to `#VOCALS` or also allow `#AUDIO`?
- Which output formats should be supported first, such as Ogg/Opus or WAV?
- Should rendered helpers be tracked in comment metadata?
- Where should the action appear, and when is its quality sufficient for a
  prominent place in the editing workflow?
