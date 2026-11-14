# Disable Alt+Space

## Status

Implemented for the main Windows window of Yass Reincarnated.

## Purpose

Prevent accidental presses of Alt+Space from opening the Windows system menu
and interrupting the user's editing flow.

## Functional Requirements

- Alt+Space does not open the system menu while the editor or library has focus.
- The shortcut remains disabled after switching into or out of fullscreen.

## Acceptance Scenarios

- While editing a song, pressing Alt+Space leaves the system menu closed and
  allows the user to continue editing.
- In the library, pressing Alt+Space leaves the system menu closed.
- After entering and leaving fullscreen, Alt+Space still leaves the system menu
  closed.

## Scope And Constraints

The change applies within Yass Reincarnated and does not change shortcuts in
other applications.
