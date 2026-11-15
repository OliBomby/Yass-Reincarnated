# Transcription Alignment

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded. Covers the shared
editor alignment workflow and OpenAI provider settings. Local provider setup is
specified in [WhisperX Alignment](whisperx-alignment.md).

## Purpose

Use timestamped song transcription to improve existing note timing or generate
replacement notes, while respecting the author's chosen lyrics.

## Functional Requirements

- The editor offers **Align Notes with Transcription**, using OpenAI or WhisperX
  according to transcription settings and availability.
- OpenAI settings under External Tools / Transcription include API key,
  transcription model, and optional language.
- Editor transcription prefers `#VOCALS`, then `#AUDIO` or the selected/source
  audio fallback.
- A reusable `yass-transcript.json` or cached transcription can be offered for
  reuse before starting a new run.
- Progress and failures are visible, with useful messages rather than raw
  exceptions.
- Existing-note alignment adjusts timing to supported transcript anchors while
  preserving lyric text and pitch.
- When available, the user can instead choose to rebuild notes from transcript
  timing. This replaces note rows and may subsequently align them to melody
  when pitch data exists.
- Filler words and non-lyric sounds are handled conservatively; alignment does
  not invent lyric text that the chart did not request.
- Choosing a rewrite/rebuild path is explicit; ordinary timing alignment does
  not silently replace curated lyrics.

## Acceptance Scenarios

- Align existing notes against a timestamped transcript. Their timing improves
  while note pitches and curated lyric text remain intact.
- Open a song with a reusable transcript. The user can reuse it instead of paying
  for or running another transcription.
- Choose rebuild where offered. Notes are replaced using transcript timing,
  with optional melody adjustment when vocal pitch is available.
- Include filler speech in the transcript. It is not casually inserted into
  the chart as new lyric notes.
- Fail a provider request. The workflow reports a useful failure message.

## Scope And Constraints

The normal input already has lyrics or receives curated lyrics through the
wizard/LRCLib. This is not a general lyrics-generation feature. No broader
duet/multi-singer support is claimed here.
[Transcription Artifacts](transcription-artifacts.md) defines reusable files;
[Vocal Timing Refinement](transcript-timing-refinement.md) defines vocal-guided
adjustments before applying timing.
