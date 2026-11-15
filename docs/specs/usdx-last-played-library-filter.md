# USDX Last Played Library Filter

## Status

Planned, not implemented. Initial scope is the last UltraStar Deluxe (USDX)
session described by its `Error.log`.

## Purpose

Let users find songs actually started during a singing session without reading
USDX logs or mistaking song-browser previews for played songs.

## Functional Requirements

### Availability And Refresh

- Automatically find and read a usable USDX `Error.log`. Show **Last Played**
  in the Library group dropdown only when at least one played event matches
  a song in the current Library.
- Missing, unreadable, incompatible, or unmatched logs leave the normal group
  dropdown unchanged.
- Rebuild session information when the Library loads or is manually refreshed.
  Remove the group on the next rebuild if usable matched events are gone.
- Selecting Last Played immediately applies its single **All Last Played**
  rule, with no second click required.
- Localize the group label, including English **Last Played** and German
  **Zuletzt gespielt**.

### Session Events And Matching

- Treat the log as one session, using its header to reject clearly incompatible
  files where possible.
- Count song starts rather than completed performances or saved scores.
  A song followed quickly by another still counts.
- Distinguish actual starts from browser previews. The supported log signature
  is `STATUS: Begin [OnShow]`, then `STATUS: End [OnShow]`, followed shortly
  by decoder lines for the selected song, such as
  `Using decoder FFmpeg_Decoder for "...\\songs\\..."`.
  Preview decoder lines without that transition do not count.
- Prefer a logged song TXT path for matching. When only media paths are
  available, use the song folder under `songs/`.
- Match conservatively. Skip an event if its folder is outside the Library,
  contains multiple candidate songs that cannot be distinguished, or otherwise
  cannot identify one song. Display-title normalization alone is insufficient.

### Display And Sorting

- Each Library song appears once. Repeated starts use the latest start time
  and retain the play count.
- Tile extra information shows `HH:mm` for one play or `HH:mm (Nx)` for
  repeated plays, for example `23:55` or `00:05 (2x)`.
- Default sorting is oldest to newest by each song's full latest-start date
  and time, even though the display shows only the clock time.
- Sessions crossing midnight sort correctly. A play at
  `2026-05-23 23:55` precedes one at `2026-05-24 00:05`.
- Existing Artist and Title sort controls remain available.
- Session information does not add song tags or modify song files.

## Acceptance Scenarios

- Load a log with a browser preview and two actual starts. Only the started
  songs appear under Last Played.
- Start a song and immediately switch to another. Both count as played.
- Play one song twice. It appears once with its latest time and `(2x)`.
- Play songs across midnight. The default list follows full chronological
  order rather than sorting clock strings.
- Select Last Played. Its songs appear immediately without selecting the rule
  separately. Switch to Artist or Title sorting; those controls still work.
- Load a missing, unreadable, incompatible, or unmatched log. Last Played is absent.
- Log a media folder containing multiple indistinguishable song entries.
  The event is skipped rather than attached to the wrong chart.
- Refresh after the log disappears. Last Played is removed, and song files
  remain unchanged.

## Scope And Constraints

The first version supports USDX only, adds no separate dialog or Extras action,
and requires no `Ultrastar.db`. Scores, completion state, player information,
persistent play history, and other UltraStar-family log formats are outside
this version.
