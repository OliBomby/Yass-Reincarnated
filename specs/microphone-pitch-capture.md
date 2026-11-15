# Microphone Pitch Capture

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded. Covers setting
the pitch of selected notes by singing or humming.

## Purpose

Let authors enter a known melody by ear when they do not know its note names,
without recording audio or creating new notes.

## Functional Requirements

- **Set Pitch From Microphone** in the Edit menu and `Ctrl+Shift+M` toggle
  listening. The info bar's **Mic** indicator starts or commits and stops the
  same capture mode, and is lit while listening.
- A song and selected notes are required. Starting without selected notes shows
  a selection hint without changing the song.
- The microphone is chosen under **Extras > Preferences... > Editor > Control**
  in the **Mic:** dropdown. Available devices are refreshed at application
  startup.
- Listening shows a banner in the visible note grid, followed by the detected
  note name when a stable pitch is available. It remains visible during playback.
- All selected notes follow the same detected pitch live. Note timing, duration,
  and lyrics remain unchanged.
- Detection uses a one-octave range based on the current page's pitches. For
  example, a G4–D5 page uses F4–E5. Up and Down move that range by an octave
  while listening; playback and other editor shortcuts remain available.
- Toggling off or pressing Escape accepts the current pitch and removes the
  banner. Undo can discard the accepted change.
- Selecting another group of notes accepts the current group and continues
  listening on the new group. Selecting a non-note or clearing selection accepts
  the current group and stops listening.
- Each accepted changed group forms one Undo step; live tracking does not fill
  the Undo history. A group with no stable detected pitch retains its original
  pitches without a lingering message.
- An unavailable or unconfigured microphone produces a setup hint without
  changing notes. Escape dismisses that hint.

## Acceptance Scenarios

- Select one note, start listening, and hum a steady pitch. The banner identifies
  it and the note follows. Toggle off and Undo once; the original pitch returns.
- Select several notes and sing a stable pitch. Every selected note receives
  that pitch while timing and lyrics stay unchanged.
- Move to the next note while listening. The previous group's change is
  accepted and capture continues on the new selection.
- Press Escape after detecting a pitch. Listening stops with the pitch accepted.
  Undo restores the original.
- Listen without a stable tone. Stopping leaves the selected notes unchanged.
- Start with no available microphone. A dismissible setup hint appears and the
  song stays unchanged.

## Scope And Constraints

Captured audio is not stored. This feature edits note pitch;
[Tapping Recording](tapping-recording.md) edits note timing.
