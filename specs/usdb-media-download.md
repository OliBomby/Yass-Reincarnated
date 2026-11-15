# USDB Media Download

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded. Covers media
download for single and queued USDB imports.

## Purpose

Turn media references in USDB metadata into a playable song folder containing
dedicated audio, optional video, and available cover/background images.

## Functional Requirements

- A video source (`v`) supplies both video and extracted audio. Without a video
  source, an audio source (`a`) supplies only audio. Without either source,
  media download is skipped.
- Bare YouTube IDs and `v=<id>` references are accepted alongside source URLs.
- Video imports produce two distinct files: audio in the chosen format and a
  video honoring the chosen codec and resolution. Audio and video downloads
  for the same source must not compete through simultaneous media pulls.
- Audio choices include M4A/AAC, Ogg/Vorbis, and Opus with the configured quality
  or bitrate. Video choices honor the configured codec and maximum resolution.
- Successful import assigns the audio filename to `#MP3` and `#AUDIO`,
  video to `#VIDEO`, and downloaded images to `#COVER` and `#BACKGROUND`.
  The optional `.usdb` metadata records resolved resource URLs.
- Import shows general and per-song progress, with detailed downloader output
  available in the job log.
- Cover or background failure is nonfatal: import continues without that image.
  Audio/video failure fails the song import and is visible in its queue status.
- Failed imports remove their staging directory rather than leave partial media
  as a completed song.
- Media handling uses the configured yt-dlp and FFmpeg executables, with the
  normal executable lookup fallback for yt-dlp.

## Acceptance Scenarios

- Import a song with a video source. Its folder contains separate audio and
  video files in the selected formats, and their tags identify the correct files.
- Import an audio-only song. An audio file is assigned without requiring video.
- Import with a bare YouTube ID. The source is resolved to the corresponding
  video URL.
- Fail a cover download while audio/video succeed. The song import completes
  without the cover and records the image failure.
- Fail an audio or video download. The job reports failure and no partial staging
  folder is retained as a successful import.

## Scope And Constraints

Optional post-import stem processing follows
[Audio Separation](separation-feature.md).
