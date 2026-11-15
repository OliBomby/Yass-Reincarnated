# WhisperX Alignment

## Status

Implemented in Yass Reincarnated, inherited from Yass Reloaded. WhisperX is the
local provider for the shared transcription workflow.

## Purpose

Generate timestamped transcripts locally for note alignment and wizard creation,
without OpenAI API cost. A configured local environment can run offline.

## Functional Requirements

- External Tools / Transcription exposes Python executable, module invocation,
  command override, model, device, compute type, cache folder, health check,
  and package update controls.
- Model, device, and compute type each support Auto. Hardware-aware choices
  favor a stable runtime; manual selections are not overwritten by auto-apply.
- Health check reports Python, WhisperX, FFmpeg, GPU, VRAM, CUDA/MPS status,
  recommended and applied values, and the reason for the recommendation.
- Automatic choices use CUDA when suitable, MPS where available, and a
  conservative `small / cpu / int8` fallback for weak or unknown hardware.
- Transcription output streams into progress UI. If float16 fails on the
  backend, transcription retries with CPU/int8.
- Package updates show progress and can be cancelled.
- A reusable cached transcript is checked before a fresh local run. The default
  cache folder is `.yass-cache`.
- Opus source audio remains usable even when conversion is needed for WhisperX.
- Local results support the same [alignment](transcription-alignment.md) and
  [artifact reuse](transcription-artifacts.md) behavior as other transcript sources.

## Acceptance Scenarios

- Run health check on a configured machine. The report shows available tools,
  hardware, recommended runtime, and why it was selected.
- Set model to a manual value and device/compute to Auto. Auto-apply changes
  only the Auto fields and retains the manual model.
- Run without usable GPU telemetry. Auto uses the conservative CPU fallback.
- Encounter a float16 backend failure. The workflow retries with CPU/int8
  instead of ending at that failure alone.
- Start a package update. Progress is visible and cancellation is available.
- Align using a reusable local transcript. It is reused without starting a fresh
  WhisperX process.

## Scope And Constraints

This spec covers the local provider and its setup. It does not introduce
additional advanced transcription controls.
