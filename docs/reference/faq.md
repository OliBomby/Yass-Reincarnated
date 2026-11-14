---
title: FAQ
---

# FAQ

## What is Yass Reincarnated for?

Yass Reincarnated is an editor for UltraStar songs. It helps with lyrics, notes, timing, metadata, page breaks, playback, and media management.

## Should I use relative or absolute pitch view?

Use the relative view for fast page-focused editing. Use the absolute view when octave consistency, pitch inspection, vertical scrolling, or recording workflows matter more.

## Do I need a `#VOCALS` track?

Not always, but many pitch-aware workflows become much more useful with a clean vocals track. This includes melody inspection and some alignment scenarios.

## Can I time notes by ear?

Yes. The recording and tapping workflow is designed exactly for that, and you can refine the result afterward.

## Where do I configure fanart.tv?

The fanart.tv API key is configured in:

- `Settings -> Library -> Setup`

## Where are WhisperX and audio-separator configured?

These are configured in:

- `Settings -> External Tools -> Transcription`
- `Settings -> External Tools -> audio-separator`

They can also fall back to:

- `Settings -> External Tools -> Locations -> Default Python executable`

If the tool-specific Python field is empty and you use `Update package`, Yass Reincarnated can create a dedicated virtual environment for WhisperX or audio-separator automatically.

## Where is USDB configured?

USDB-related settings are split across two places:

- `Settings -> Library -> Setup` for the USDB user name and account-oriented behavior
- `Settings -> External Tools -> Locations` for the optional USDB Syncer path

The Syncer path improves search and refresh workflows, but the direct live USDB integration can still work without it.

## Does Yass Reincarnated support LrcLib?

Yes.

LrcLib is available as an online lyrics source in the song creation wizard. It does not require a separate API key or its own settings page. When timed lyrics are available, Yass Reincarnated can use them as structured lyric input before or instead of a full transcription workflow.

## Is the GitHub Wiki still relevant?

Yes. The upstream Yass Reloaded wiki is still useful for inherited workflows and
older notes. The Yass Reincarnated GitHub Pages site is the main documentation
for this fork.

## Is Yass Reincarnated a fork of the original Yass?

Yes. Yass Reincarnated is a fork of Yass Reloaded by DoubleDee, which in turn
builds on the original Yass project by Saruta. It inherits modern Java support
and improved editor workflows from those projects.

## Why does this documentation keep saying "Yass Reincarnated" instead of just "Yass"?

To avoid confusion. Some older tutorials, screenshots, and help texts still refer to the original Yass project, but this documentation is specifically about Yass Reincarnated unless stated otherwise.

## Which older Yass topics are still relevant for Yass Reincarnated?

A number of classic concepts still carry over well:

- library maintenance and error-driven editing
- page trimming and phrase layout
- note, lyrics, and page-break editing basics
- copied melody and repeating phrase workflows
- keyboard-driven editing and virtual piano usage

## Does it support UltraStar Deluxe songs?

Yes. Yass Reincarnated is designed for UltraStar-style song files and typical UltraStar Deluxe workflows.

## Which operating systems are supported?

Windows, macOS, and Linux.

## Do I need FFmpeg?

Usually yes, especially if you work with a broad set of audio formats or use advanced wizard and analysis features.

## Does Yass Reincarnated support transcription and stem separation?

Yes, through external integrations such as:

- WhisperX
- OpenAI-based transcription
- MVSEP
- audio-separator

## Where should I report bugs or request features?

Use the project repository:

- [Issues](https://github.com/OliBomby/Yass-Reincarnated/issues)
