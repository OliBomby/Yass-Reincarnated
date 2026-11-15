# Transcription Artifacts

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Reuse lyrics and timing across wizard and editor workflows, regardless of
whether they came from LRCLib, local LRC, subtitles, OpenAI, or WhisperX.

## Functional Requirements

- `yass-transcript.json` is the reusable song-local transcript file.
  The wizard saves it into the final song folder when transcript data exists.
- The editor looks for this file before offering fresh transcription and
  prefers it to provider-specific caches. Post-wizard separation prompts mention
  reusable transcript data when it is present.
- Text and timing origins are kept distinct: selected LRCLib lyrics can supply
  clean text while OpenAI/WhisperX provide word timing. Subtitles supply fallback
  text and timing when no better transcript exists.
- The wizard's LRCLib action offers online search or importing a local
  `.lrc` file. Both provide reusable synced lyrics.
- Curated lyrics can replace transcript text while retaining timing through
  the [lyrics comparison](wizard-clipboard-diff.md).
- YouTube wizard downloads can select a downloaded subtitle file for transcript
  fallback. Manual subtitles take precedence over automatically generated ones.
- Subtitle cues provide timed lyric segments and words. Rolling captions are
  collapsed; effectively all-caps subtitles become sentence-style lyric casing.
- Wizard completion also attempts fallback from a selected subtitle file when
  no transcript was already created in the lyrics step.
- Fallback never replaces an existing wizard transcript. Blank paths, missing
  subtitle files, or an empty conversion leave the current state unchanged.
- Transcript provenance stays in the artifact. Obsolete `transcriptText`,
  `transcriptTiming`, and `transcriptSource` entries are removed from
  `#COMMENT` without removing unrelated comment values.
- Provider caches can still be retained for reuse or diagnostics; their default
  folder is `.yass-cache`.

## Acceptance Scenarios

- Finish a wizard run with transcript data. The final song folder contains
  `yass-transcript.json`; editor alignment can offer to reuse it.
- Have both a canonical artifact and provider cache. Editor reuse prefers the
  song-local artifact.
- Import a local LRC file through the LRCLib choice. Its text and timing are
  available for later note generation and saving.
- Download manual and auto subtitles for a wizard song. Manual subtitles are
  selected for fallback.
- Finish with a subtitle file but no existing transcript. A reusable transcript
  is produced even if the lyrics step did not create one earlier.
- Finish with an existing transcript and a subtitle file, or with a missing
  subtitle path. Existing transcript data remains unchanged.
- Save provenance cleanup in a song with other comment values. Only the three
  obsolete transcript entries disappear.

## Scope And Constraints

This spec covers transcript sources, persistence, and reuse.
[Vocal Timing Refinement](transcript-timing-refinement.md) and
[Transcription Alignment](transcription-alignment.md) define timing adjustments
and their application to notes.
