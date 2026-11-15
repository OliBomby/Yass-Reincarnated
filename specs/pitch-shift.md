# Pitch Shift

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded. Covers song-wide
tuning analysis, manual correction, playback preview, and rendering to files.

## Purpose

Correct recordings that are uniformly sharp or flat so playback, detected pitch,
and melody alignment agree with the chromatic grid.

## Functional Requirements

- **Extras > Pitch Shift…** is available for any open song. The dialog shows
  stored correction and analysis results in cents and semitones
  (`100 cents = 1 semitone`).
- Analyze requires Aubio and loaded audio. Analysis follows the selected audio
  track, with the declared instrumental track preferred as a tuning reference.
  Loading audio does not start analysis automatically.
- Analysis reports whether the source is sharp or flat and offers a recommended
  correction. A marginal estimate shows a low-confidence explanation; an
  unreliable estimate reports that no reliable offset is available.
- A source offset below 5 cents reports no correction needed. Users can still
  store a manual value.
- Manual view, edit, apply, and remove remain available without Aubio. Disabled
  analysis or rendering controls explain the missing prerequisite.
- Applying a value stores it as `#COMMENT:pitchShiftCents=...`, preserving
  unrelated comment values such as `key=` and `v=`.
- Live correction preserves source files and audio duration. Playback, detected
  pitch lines when enabled, and melody alignment reflect the correction exactly
  once, including any separate whole-semitone display transpose.
- Changing the value prepares corrected playback before it can be heard;
  continuous real-time adjustment is not required.
- **Render to Files…** requires a stored nonzero correction and FFmpeg. It
  creates collision-safe `<name> (Pitch Shifted).<ext>` files without
  overwriting originals.
- Successful rendering updates `#AUDIO`, `#VOCALS`, and `#INSTRUMENTAL`
  assignments to the new files and removes only the pitch-shift comment value.
  Shared source audio is rendered once.
- Cancellation or failure leaves original assignments and correction metadata
  intact, without committing a partially rendered song.

## Acceptance Scenarios

- Analyze a uniformly sharp recording. The dialog shows the positive source
  offset and offers the compensating correction.
- Analyze an inconsistent recording. The dialog reports no reliable global
  offset instead of offering a misleading confident correction.
- Enter a manual correction without Aubio. It can be stored and removed even
  though Analyze is unavailable.
- Apply a correction with pitch display enabled. Playback, visible pitch, and
  melody alignment agree; no second correction is added to the displayed data.
- Render successfully. New files are assigned, originals remain, and the
  pitch-shift comment is removed while other comment values remain.
- Cancel or fail partway through rendering. The song keeps its original
  assignments and stored correction.
- Render when a proposed output name already exists. A distinct filename is
  used rather than overwriting that file.

## Scope And Constraints

This is a uniform song-wide correction. It does not correct individual notes
or drifting tuning. The legacy `#MP3` tag is excluded from permanent rendering.
The proposed [vocal auto-tune helper](../docs/specs/vocal-auto-tune-filter.md)
has separate scope.
