# Autosave

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Protect unsaved editing work with a recoverable backup next to the song file,
without interrupting the editing session.

## Functional Requirements

- Autosave runs silently for open songs. The Sketch options set its interval in
  seconds: the default is 300, the maximum is 600, and 0 disables autosave.
  With autosave enabled, the first opportunity is 30 seconds after opening.
- A changed song is backed up to `<song>.txt.bak`. Unchanged content is not
  repeatedly rewritten. Later edits make the song eligible for another backup.
- Backups contain a consistent song, including the open tracks of a duet.
  Writing a backup preserves the live notes, lyrics, tags, and manual save state.
- Autosave preserves focus, selection, caret, scrolling, and active editing.
  A background backup failure does not open a blocking dialog.
- Opening a song with a newer `.bak` offers restoration. Accepting copies the
  current song file to `<song>.txt.old`, then restores the backup to the song file.
  Declining deletes that backup.
- An older backup, or one made unnecessary by a normal save, is cleaned up when
  the song closes. Closing also stops that song's autosave.

## Acceptance Scenarios

- Edit lyrics and wait for autosave. A recoverable `.bak` appears while focus,
  caret, selection, and the visible timeline stay in place.
- Leave the song unchanged after a successful backup. Autosave does not keep
  rewriting the same content. Edit again and a later backup contains that edit.
- Open a song with a newer backup and accept restoration. The restored song
  contains the backup's changes, and `.old` retains the previous song file.
- Decline restoration. The song file remains unchanged and the backup is removed.
- Autosave an edited duet. Reopening the backup recovers both tracks together.
- Set the interval to 0. No periodic backup is written.

## Scope And Constraints

Autosave writes recovery files; explicit Save retains its own validation and
error reporting.
