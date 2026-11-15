# Tapping Recording

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Let authors retime existing lyrics by pressing and releasing a key or mouse
button in time with playback.

## Functional Requirements

- Recording starts at the selected note or the first note in the selected
  range, skipping non-note rows.
- Each completed press/release pair sets a note's start and duration in the
  song's BPM, GAP, and playback-rate timebase. An unmatched final press is ignored.
- Tap timing is used without reaction-time compensation.
- When recording begins at the song's first note, the first completed tap sets
  `#GAP` and that note starts at beat 0.
- Recording from a later note preserves absolute timing relative to the existing
  GAP.
- If recording is interrupted after complete taps, the user can apply those
  taps and place remaining untapped notes after the final tap.
- Playback can use the vocal track when available and restores the prior audio
  selection afterward. Tap overlays appear only once recording playback starts.
- Applying taps repositions page breaks into the gaps between neighboring notes.
  Optional [Align To Melody](align-to-melody.md) can then follow the vocals,
  using its generated-note octave behavior.
- After acceptance, the view follows and fits the first processed tapped note.

## Acceptance Scenarios

- Start at the first song note and complete a tap pair. GAP records that audio
  start and the first tapped note lands at beat 0.
- Start later in the song. Tapped notes retain their absolute audio timing
  relative to the existing GAP.
- Complete two tap pairs and interrupt during a third. Applying the recording
  uses the two completed pairs and ignores the unmatched timestamp.
- Record using vocals instead of the previous playback track. After recording,
  the previous track is selected again.
- Apply retimed notes around a page break. The break lies within the new pause,
  and the editor shows the first processed note.

## Scope And Constraints

Recording captures tap timing for note rows. It does not record or write audio.
[Microphone Pitch Capture](microphone-pitch-capture.md) is a separate input method.
