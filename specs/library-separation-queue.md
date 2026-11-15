# Library Separation Queue

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Let users separate audio for multiple local songs without opening each song in
the editor, with visible progress, cancellation, and per-song failures.

## Functional Requirements

- The Library context menu's **Separate Audio** queues one separation job per
  selected song. Songs remain closed in the editor.
- **Open Queue** is available from the Library context menu and Extras menu,
  independently of USDB login or the USDB Search dialog.
- The **Song Queue** shows each job's mode: Import, Import + Separate, or
  Separation, along with status and detail logs.
- Selecting a song already waiting for or undergoing separation does not create
  another active separation job for that same song file.
- Queued separation runs sequentially. Failure affects that job and does not
  stop later queued songs.
- Songs with valid existing `#VOCALS` or `#INSTRUMENTAL` files remain queueable.
  A detail-log hint explains that these assignments will not be overwritten
  automatically, and quiet separation preserves them.
- Cancelling a waiting separation-only job does not offer to delete the local
  song folder.
- Provider choice, output formats, and new stem assignments follow
  [Audio Separation](separation-feature.md).
- Separation-only jobs do not appear as active USDB imports in USDB Search.

## Acceptance Scenarios

- Select three local songs and choose Separate Audio. Three separation jobs
  appear in the Song Queue without opening editor tabs.
- Queue one of those songs again before its job completes. No duplicate active
  separation job is added.
- The first job fails. It shows a failure and the next song still runs.
- Queue a song with valid assigned stems. The hint is visible and those
  assignments remain unchanged.
- Cancel a waiting separation-only job. The job is cancelled and the local song
  folder remains.
- Open Queue from Library Extras while logged out of USDB. The queue opens.
