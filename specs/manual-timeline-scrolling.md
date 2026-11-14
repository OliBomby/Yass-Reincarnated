# Manual Timeline Scrolling

## Status

Implemented for the left and right scroll buttons in Yass Reincarnated.

## Purpose

Let users inspect earlier or later notes without the view snapping back to a
page boundary when they release a scroll button.

## Functional Requirements

- Clicking the left or right scroll button moves the timeline in that direction.
- Holding a scroll button continues scrolling while the pointer is over it.
- Releasing the button stops scrolling and preserves the resulting horizontal
  view and zoom, including when scrolling starts from a fitted page.
- The behavior applies in both relative and absolute pitch views.

## Acceptance Scenarios

- Fit a page, click the right scroll button, and release it. The view stays at
  the scrolled position instead of returning to the page boundary.
- Hold the left scroll button, then release it. Scrolling stops at the reached
  position without snapping back or changing the horizontal zoom.
- Move the pointer away from a held scroll button and release it. Scrolling
  stops and does not resume after release.
