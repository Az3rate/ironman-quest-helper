# Ironman Questing Helper

Ironman Questing Helper is a third-party RuneLite plugin designed to assist with optimal Ironman progression through step-by-step guidance. The long-term goal is to provide a curated, trackable quest route based on popular Ironman guides from across the community, with dynamic tracking and in-game integration.

## Current State

This plugin is under active development. The current version is focused on establishing a stable and testable base.

### Implemented Features

- Loads and parses `quest_steps.json` on startup from plugin resources
- Displays a step list overlay on screen
- Uses `ConfigManager` to persist per-step completion state
- Includes a developer test toggle (`enableDevTest`) to simulate state updates
- Supports pre-login mode rendering (for PR/test purposes)

### Testing Notes

The included step data is **placeholder only** and intended for visual and configuration testing. It does not reflect the final Ironman quest route.

To simplify validation:

- Steps load immediately from JSON
- Step 1 is marked as completed automatically when `enableDevTest` is true
- Overlay renders regardless of login state, with messaging adapted accordingly

## Planned Roadmap

- Integration with game state to auto-mark quest steps
- Full curated route based on multiple Ironman sources
- In-game context tracking (inventory, skills, quests, etc.)
- Config options for filters, difficulty modes, and priorities

## Contribution and Status

This PR is submitted to allow proper testing of the base structure inside RuneLite. Any guidance or feedback is appreciated.
