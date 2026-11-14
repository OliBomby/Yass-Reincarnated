# Prevent Accidental Alt Menu Focus

## Status

Implemented for the main Windows window of Yass Reincarnated.

## Purpose

Prevent accidental presses of Alt or Alt+Space from focusing window menus
and interrupting the user's editing flow.

## Functional Requirements

- Alt+Space does not open the system menu while the editor or library has focus.
- Pressing and releasing Alt, including repeatedly, keeps focus in the editor or
  library instead of activating the menu bar or window chrome.
- Alt combinations used for application shortcuts and menu mnemonics remain available.
- The shortcut remains disabled after switching into or out of fullscreen.

## Acceptance Scenarios

- While editing a song, pressing Alt+Space leaves the system menu closed and
  allows the user to continue editing.
- In the library, pressing Alt+Space leaves the system menu closed.
- While editing a song, pressing and releasing Alt several times allows the user
  to continue editing without dismissing a menu or restoring focus.
- Alt+F still opens the File menu.
- After entering and leaving fullscreen, Alt+Space still leaves the system menu
  closed.

## Scope And Constraints

The change applies within Yass Reincarnated and does not change shortcuts in
other applications.
