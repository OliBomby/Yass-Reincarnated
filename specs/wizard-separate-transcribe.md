# Wizard Separation And Transcription

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Produce an editable first draft by separating prepared audio, transcribing vocals,
populating lyrics, and generating notes from transcript timing.

## Functional Requirements

- The lyrics step offers **Separate Vocals + Transcribe** when prerequisites
  allow it. A disabled or unavailable state explains missing audio, separation
  configuration, or a required WhisperX health check.
- The flow follows the [separation preference](separation-feature.md), then uses
  OpenAI or WhisperX according to configuration and availability.
- A modal progress dialog shows progress and supports cancellation.
- Completed work can be reused only for the same source and base-song identity,
  including the YouTube ID where relevant.
- A matching separation-only run can reuse stems but still needs transcription
  before lyrics are ready.
- Completion populates lyrics and shows a ready transcript state.
- Finishing the wizard copies separated stems, the canonical transcript, and
  available transcript cache/debug files into the final song folder, and assigns
  the applicable assets.
- Outputs are applied once; existing user-selected stems are not silently
  overwritten.
- When separated vocals are available, generated notes can use
  [vocal timing refinement](transcript-timing-refinement.md) and
  [melody alignment](align-to-melody.md).

## Acceptance Scenarios

- Start with audio and configured providers. Run Separate Vocals + Transcribe.
  Progress is visible and completion populates the lyrics.
- Attempt the flow without a healthy required WhisperX setup. The unavailable
  state explains the health-check prerequisite.
- Reopen a completed run for the same song source. It can be reused.
  Change the source identity; the stale run is not reused.
- Reuse a matching run containing only stems. Separation is skipped, but
  transcription still runs before the lyrics are ready.
- Finish after a successful run. The final folder contains the stems and
  `yass-transcript.json`, and the song uses those assets.

## Scope And Constraints

Transcript file reuse is specified in
[Transcription Artifacts](transcription-artifacts.md). Provider configuration
and editor alignment follow [Transcription Alignment](transcription-alignment.md)
and [WhisperX Alignment](whisperx-alignment.md).
