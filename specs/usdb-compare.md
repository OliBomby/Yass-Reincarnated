# USDB Compare

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Let maintainers compare a local song TXT with USDB before saving or submitting
changes, without accidentally comparing solo and duet variants.

## Functional Requirements

- **Compare with USDB** resolves the remote song using local `.usdb` metadata
  or an exact artist/title search and opens a diff of local and editable USDB TXT.
- If no suitable identity is found, the USDB Search dialog opens with the local
  artist and title filled in.
- An automatically resolved solo/duet mismatch also falls back to that search.
- Explicitly choosing a mismatching search result is rejected with a clear
  solo/duet mismatch message instead of opening a misleading diff.
- Solo songs compare only with solo songs, and duets only with duets.
  The remote song's actual TXT determines its duet status.
- Duet markers include `#DUETSINGERP...`, `#P1:`-style singer headers,
  and `P1` or `P 1` track-switch lines.
- Local TXT is read as UTF-8; line-ending differences are normalized for comparison.

## Acceptance Scenarios

- Compare a local song with matching USDB metadata and the same duet structure.
  The diff opens for the correct remote TXT.
- Compare without a resolvable ID. Search opens with the song's artist and title.
- Automatically resolve a solo USDB variant for a local duet. Search opens
  instead of the diff.
- Explicitly choose that solo variant. A mismatch message appears and the diff
  stays closed.
- Use remote TXT with singer headers or track-switch duet markers. It is
  treated as a duet regardless of incomplete search metadata.
