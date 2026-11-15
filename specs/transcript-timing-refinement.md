# Vocal Timing Refinement

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Make coarse transcript timing follow the actual sung phrases, without pulling
words into neighboring phrases or truncating held syllables.

## Functional Requirements

- Vocal-guided refinement can improve line-based timing from subtitles, LRCLib,
  and local LRC, as well as OpenAI cache timing, when nearby vocal evidence is
  available.
- With a known GAP, the expected first lyric start guides the opening vocal
  anchor. With an unknown GAP, opening dialogue or instrumental bleed must not
  replace the first real sung entry.
- If usable vocals are not near the expected first lyric start, the workflow
  can search from the beginning for the first relevant vocal entry.
- Correcting an early opening subtitle cue must not pull later phrases away
  from their own nearby sung starts.
- Each phrase keeps its own vocal material. Vocal activity belonging to the
  next phrase must not lengthen the current phrase or consume its first word.
- Weak or spurious pitch detections must not displace supported word starts.
  Genuinely wide vocal ranges and unpitched onset evidence remain usable.
- Dense or fragmented vocal activity must not drag a word past its sung onset.
  A later, separately sung word should not be stranded inside the previous
  word's sustained sound.
- A held word continues through its own sung body, stopping before the next
  word. Internal onsets in a melismatic word must not consume later words.
- Extra sung segments should accommodate multi-syllable words without letting
  a short leading word absorb the following vocal phrase.
- Wizard note generation uses refinement before rebuilding when separated
  vocals are available, then can use [Align To Melody](align-to-melody.md) for
  pitch and duration.

## Acceptance Scenarios

- Create a song from coarse subtitles where the first cue starts early.
  The opening aligns to vocals while later lines remain near their own sung starts.
- Use unknown GAP with dialogue before singing. The opening anchor follows the
  real vocal entry rather than the introductory speech.
- Place two lyric phrases close together. The current phrase does not consume
  the next phrase's first vocal block.
- Refine a sustained word followed by a separately sung word. The first retains
  its sung duration and the next starts on its own vocal entry.
- Refine a multi-syllable held word with internal onsets. Later words retain
  their own sung segments.
- Include a wide-range phrase and spurious octave detections. Useful vocal
  evidence remains available without shifting words onto those outliers.

## Scope And Constraints

Refinement conservatively adjusts timing. It does not infer new lyrics or
reorder phrases. Source selection and persistence belong to
[Transcription Artifacts](transcription-artifacts.md); applying the resulting
timing to notes belongs to [Transcription Alignment](transcription-alignment.md).
