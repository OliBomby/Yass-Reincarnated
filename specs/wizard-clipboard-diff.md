# Wizard Clipboard Lyrics Diff

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded.

## Purpose

Let authors use curated clipboard or LRCLib lyrics in the Create Song Wizard
while retaining reliable timing from an existing transcript.

## Functional Requirements

- The lyrics step offers **Paste Lyrics**. Without a transcript, it pastes
  normalized clipboard text directly into the lyrics field.
- Normalization makes line endings consistent, trims leading/trailing blank
  lines, and collapses repeated trailing whitespace.
- With a transcript, a comparison dialog shows transcript text on the left and
  clipboard text on the right, highlights differences, and offers Apply or Cancel.
  LRCLib lyrics can use the same comparison.
- Applying chooses the right-hand lyric text. It updates the reusable transcript
  text while preserving timing where safely possible.
- Safe word joins or splits can retain timing. Ambiguous structure is rejected
  or uses a fallback rather than inventing unsupported timing.
- Empty or unavailable clipboard text produces an informational message.
- Applying corrected text keeps it available for later note generation and
  [transcript saving](transcription-artifacts.md). It changes no preferences.

## Acceptance Scenarios

- Paste curated lyrics without an existing transcript. Normalized text appears
  directly in the lyrics field.
- Paste different lyrics with a transcript present. The comparison highlights
  the changes; Apply uses the curated text and retains reliable timing.
- Cancel the comparison. Existing lyrics and transcript remain in place.
- Choose LRCLib lyrics with a transcript present. The same comparison is available.
- Paste from an empty clipboard. An informational message appears.

## Scope And Constraints

The comparison is not a general merge editor. It applies the chosen right-hand
text rather than combining arbitrary fragments from both sides.
