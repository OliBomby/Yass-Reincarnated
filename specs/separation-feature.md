# Audio Separation

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded. Covers MVSEP
and local audio-separator, including MVSEP output formats and reverb removal.

## Purpose

Create song-local vocal and instrumental stems for editing, transcription, and
playback while preserving valid existing audio assignments.

## Functional Requirements

### Providers And Assignment

- External Tools settings configure MVSEP and local audio-separator. Wizard
  settings choose local first, online first, MVSEP only, or audio-separator only.
- Separation follows the chosen preference and available providers. MVSEP
  requires an API token and uploads audio to the online service; audio-separator
  requires a working local Python environment and health check.
- Users can choose the provider's model and output format. MVSEP also supports
  model type and a preferred instrumental assignment; local settings include
  the Python executable and model directory.
- Generated stems have predictable song-based names in the song folder.
  Successful assignment updates `#VOCALS` and `#INSTRUMENTAL`, following the
  preferred instrumental setting.
- Valid existing stem assignments are not silently overwritten.
- Background separation keeps the interface responsive and reports progress
  and failures.
- The [wizard](wizard-separate-transcribe.md) can separate before transcription.
  USDB imports can queue separation afterward, and existing Library songs use
  the [Library Separation Queue](library-separation-queue.md).

### MVSEP Output Formats

- Output choices include WAV, FLAC, MP3, Ogg/Vorbis, and Ogg/Opus, both in
  preferences and per-job choices.
- Ogg/Vorbis produces `.ogg`; Ogg/Opus produces `.opus`. These choices use
  local FFmpeg conversion after receiving WAV stems from MVSEP.
- Encoding quality follows the existing yt-dlp audio-quality settings.
- Successful Ogg separation assigns the final Ogg/Opus files to song tags.
  Intermediate WAV audio remains usable by the editor's audio cache.
- Missing FFmpeg or failed conversion makes separation fail; song tags must not
  be updated to nonexistent Ogg/Opus files. A failure to cache an intermediate
  WAV does not invalidate successfully produced final files.
- Existing saved format choices remain compatible, including `ogg-vorbis` and
  `ogg-opus`.

### Vocal Reverb Removal

- **Remove Reverb (MVSEP)** uses the existing `#VOCALS` file as input, uploads
  it to MVSEP, and downloads a de-reverbed vocal file into the song folder.
- It is a separate post-processing action, rather than a normal separation
  model choice.
- The user is asked whether to replace the current `#VOCALS` assignment with
  the result.

## Acceptance Scenarios

- Configure local-first separation with a healthy local provider. Separation
  uses that preference and produces assignable song-local stems.
- Separate a song with valid existing stem assignments in a background flow.
  Those assignments are preserved without an implicit overwrite.
- Choose Ogg/Vorbis or Ogg/Opus. Successful output has the corresponding
  extension and song tags refer to the final files.
- Choose Ogg output with FFmpeg missing or conversion failing. The job fails
  and tags are not changed to missing files.
- Successfully generate Ogg output but fail to cache an intermediate WAV.
  The final Ogg result remains usable.
- Run Remove Reverb on assigned vocals. A new result is downloaded and the
  user decides whether it becomes the vocal assignment.
