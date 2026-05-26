# Fix beam / pan / tilt flickering — unified DMX sync + fixture config UI + configuration card

## Summary

Introduce `ExtraLightsLightBlockEntity` and `ExtraLightsRenderer`, migrating numerous block entities and renderers to extend the new base. Replace ad-hoc `storePrev()` / `level.sendBlockUpdated()` / `setChanged()` calls with a unified DMX update flow: `beginDmxUpdate()`, snapshot previous values, compute a `changed` flag (including extended channels), and call `finishDmxUpdate(changed, prevAdvanced)`. Update `consumeExtendedChannels` to return a `boolean` so extended channel handling can contribute to change detection. This consolidates client sync logic and simplifies DMX value processing across many fixtures.

**Fixture configuration screen refactor** — Replace the inherited Theatrical config screens (`GenericManualPanTiltScreen` / `LabeledEditBox` at 10px height) with a dedicated `ExtraLightsConfigScreen`: clean panel layout with labels above fields, numeric-only slider values, separate Save / Cancel actions, and distinct modes for moving heads (`CHANNEL_MENU`, DMX only) vs PARs (`CHANNEL_PANTILT`, includes manual pan/tilt).

**Configuration card — automatic universe wrap** — When patching fixtures with the Theatrical Configuration Card, if the fixture's channel footprint no longer fits in the remaining 512 channels of the current universe, the card automatically moves to the next universe at address 1. Auto-increment also wraps across universes (e.g. Universe 1 @ 500 + Atomic Strobe 34ch → patches at Universe 2 @ 1, next address Universe 2 @ 35).

## Demo

### Beam / pan / tilt fix

In-game video: stable beams at full intensity, no flickering.

https://github.com/user-attachments/assets/4c154028-f0a4-4c10-9693-ad1a0738939f

### Fixture config UI

New configuration screen — no overlapping labels or text on buttons:

<img width="999" height="785" alt="Capture d'écran 2026-05-26 021833" src="https://github.com/user-attachments/assets/c7ccbe95-5645-4f96-b8a6-257d6faa0687" />

## Why

Extra Lights fixtures flickered at full intensity, and pan/tilt stayed frozen or unstable until clicking the fixture. Three main causes:

1. **Double beam rendering** — Theatrical drew a beam on top of Extra Lights' (`beforeRenderBeam`), with different alpha values.
2. **Client-side `prev*` reset** — `BaseLightBlockEntity.read()` sets `prevPan = pan` on every network packet, breaking renderer interpolation.
3. **Incomplete or mistimed client sync** — scattered `sendBlockUpdated` calls, sometimes unconditional (flickering) or sometimes missing when only server-side `prev*` needed to catch up (tilt stuck until click).

**Config screen** — The old Theatrical UI stacked labels on top of 10px-tall edit boxes and buttons, causing unreadable overlapping text. Closing the screen also auto-committed changes with no way to cancel.

**Configuration card** — Patching a wide fixture near the end of a universe (e.g. address 500 with 34 channels) would overflow past channel 512. The card kept incrementing the address without switching universes, making large fixtures impossible to patch cleanly in sequence.

## What changed

### Block entities — `ExtraLightsLightBlockEntity`

| Mechanism | Purpose |
|-----------|---------|
| `beginDmxUpdate()` | Calls `storePrev()` before reading DMX; returns whether server `prev*` values were behind |
| Snapshot `_pi`, `_pp`, … | Compares old and new values to compute `changed` |
| `finishDmxUpdate(changed, prevAdvanced)` | Sends a client packet if **values changed** or **`prev*` caught up**; `setChanged()` only when values actually changed |
| `read()` / `write()` | Persists and restores `prevPan`, `prevTilt`, `prevFocus`, `prevIntensity`, RGB after `super.read()` |
| `lightTick()` (client) | Advances `prev*` each tick for stable interpolation between packets |

All affected DMX fixtures now extend this base (PARs, moving heads, strobes, blinders, etc.).

### Renderers — `ExtraLightsRenderer` / `ExtraLightsFixtureRenderer`

- `shouldRenderBeam() → false` — beam is handled only in Extra Lights' `beforeRenderBeam`, no duplicate Theatrical beam.
- Renderers migrated to these base classes.

### `BaseExtraLightsBlockEntity`

- Extends `ExtraLightsLightBlockEntity`.
- `consumeExtendedChannels()` returns a `boolean` to contribute to the `changed` flag.

### Fixture config UI — `ExtraLightsConfigScreen`

| Change | Detail |
|--------|--------|
| Layout | 300px centered panel; labels rendered **above** fields, not inside widgets |
| Pan / tilt | Sliders show numeric value only; labels drawn separately |
| Personality / network | Label above, button shows selected value only |
| Save / Cancel | Explicit buttons; closing without Save no longer sends packets to the server |
| Screen routing | `CHANNEL_MENU` → DMX + mode + network (no manual pan/tilt); `CHANNEL_PANTILT` → includes pan/tilt sliders |
| i18n | Added `fixture.position`, `fixture.personality`, `screen.artnetconfig.network.unknown` (+ `fr_fr.json`) |

### Configuration card — `ConfigurationCardHelper` + `ExtraLightsLightBlock`

All Extra Lights fixture blocks now extend `ExtraLightsLightBlock`, which overrides Configuration Card handling with automatic universe wrapping.

| Method | Purpose |
|--------|---------|
| `fitsInUniverse(address, channelCount)` | Returns whether `address + channelCount - 1 ≤ 512` |
| `resolvePatch(universe, address, channelCount)` | On apply: if channels overflow, patch at **universe + 1, address 1** |
| `advancePatch(universe, address, channelCount)` | On auto-increment: if next address > 512, move to **universe + 1, address 1** |
| `applyToFixture(tag, consumer)` | Applies network, resolved universe/address, and updates card NBT for the next patch |

**Example**

```
Card: Universe 1, address 500, auto-increment ON
Patch Atomic Strobe (34ch):
  → Fixture: Universe 2 @ 1   (500 + 34 - 1 = 533 > 512)
  → Next card address: Universe 2 @ 35
```

## DMX flow (unified pattern)

```java
boolean prevAdvanced = beginDmxUpdate();
int _pi = intensity, _pr = red, /* … */, _pp = pan, _pt = tilt;

// read DMX channels…

boolean changed = intensity != _pi || /* … */ || pan != _pp || tilt != _pt;
// + extended channels: changed |= consumeExtendedChannels(...);

finishDmxUpdate(changed, prevAdvanced);
```

## Test plan

- [ ] Fade a moving head from 0 to 100% intensity → stable beam, no flickering
- [ ] Move pan/tilt via DMX → smooth motion, no need to click the fixture
- [ ] Change a fixture's DMX address → no pan flicker when patching
- [ ] Verify PAR, moving head (e.g. Moving 500), strobe, and Atomic Strobe
- [ ] Confirm only one beam is visible per fixture (no double beam)
- [ ] Open config on a moving head → clean panel, no overlapping text on fields or buttons
- [ ] Open config on a PAR → pan/tilt sliders visible and readable
- [ ] Click **Cancel** → no DMX / network changes applied
- [ ] Click **Save** → address, universe, personality, and network update correctly
- [ ] Configuration card at Universe 1 @ 500 → patch Atomic Strobe (34ch) → fixture lands on Universe 2 @ 1
- [ ] With auto-increment ON, next card address after above patch is Universe 2 @ 35
- [ ] Patch a small fixture that still fits (e.g. 3ch @ 500) → stays on same universe, next address 503
- [ ] Patch several fixtures in a row near end of universe → card wraps cleanly without manual universe edits

## Key files

```
common/.../blockentities/ExtraLightsLightBlockEntity.java
common/.../client/blockentities/ExtraLightsRenderer.java
common/.../client/blockentities/ExtraLightsFixtureRenderer.java
common/.../blockentities/BaseExtraLightsBlockEntity.java
common/.../client/gui/ExtraLightsConfigScreen.java
common/.../client/ExtraLightsClientScreens.java
common/.../blocks/ExtraLightsLightBlock.java
common/.../util/ConfigurationCardHelper.java
common/.../resources/assets/theatricalextralights/lang/fr_fr.json
```
