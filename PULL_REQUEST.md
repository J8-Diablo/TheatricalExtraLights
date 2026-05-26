# Fix beam / pan / tilt flickering — unified DMX sync + fixture config UI + configuration card

## Summary

Introduce `ExtraLightsLightBlockEntity` and `ExtraLightsRenderer`, migrating numerous block entities and renderers to extend the new base. Replace ad-hoc `storePrev()` / `level.sendBlockUpdated()` / `setChanged()` calls with a unified DMX update flow: `beginDmxUpdate()`, snapshot previous values, compute a `changed` flag (including extended channels), and call `finishDmxUpdate(changed, prevAdvanced)`. Update `consumeExtendedChannels` to return a `boolean` so extended channel handling can contribute to change detection. This consolidates client sync logic and simplifies DMX value processing across many fixtures.

**Fixture configuration screen refactor** — Replace the inherited Theatrical config screens (`GenericManualPanTiltScreen` / `LabeledEditBox` at 10px height) with a dedicated `ExtraLightsConfigScreen`: clean panel layout with labels above fields, numeric-only slider values, separate Save / Cancel actions, distinct modes for moving heads (`CHANNEL_MENU`, DMX only) vs PARs (`CHANNEL_PANTILT`, includes manual pan/tilt), live DMX footprint preview, personality-aware channel count, and **Enter** to save / **Escape** to cancel.

**Configuration card — automatic universe wrap** — When patching fixtures with the Theatrical Configuration Card, if the fixture's channel footprint no longer fits in the remaining 512 channels of the current universe, the card automatically moves to the next universe at address 1. Auto-increment also wraps across universes (e.g. Universe 1 @ 500 + Atomic Strobe 34ch → patches at Universe 2 @ 1, next address Universe 2 @ 35). Network membership is enforced again, and chat feedback is fully rewritten with patch details, universe-wrap notices, and next-card-address info.

## Demo

### Beam / pan / tilt fix

In-game video: stable beams at full intensity, no flickering.

https://github.com/user-attachments/assets/4c154028-f0a4-4c10-9693-ad1a0738939f

### Fixture config UI

New configuration screen — no overlapping labels or text on buttons:

<img width="999" height="785" alt="Capture d'écran 2026-05-26 021833" src="https://github.com/user-attachments/assets/c7ccbe95-5645-4f96-b8a6-257d6faa0687" />

### Configuration card — universe wrap

In-game video: patching near the end of Universe 1 automatically wraps to Universe 2 when channels no longer fit.

https://github.com/user-attachments/assets/9e451d5f-0553-49d1-8179-88f7605b0ac9

## Why

Extra Lights fixtures flickered at full intensity, and pan/tilt stayed frozen or unstable until clicking the fixture. Three main causes:

1. **Double beam rendering** — Theatrical drew a beam on top of Extra Lights' (`beforeRenderBeam`), with different alpha values.
2. **Client-side `prev*` reset** — `BaseLightBlockEntity.read()` sets `prevPan = pan` on every network packet, breaking renderer interpolation.
3. **Incomplete or mistimed client sync** — scattered `sendBlockUpdated` calls, sometimes unconditional (flickering) or sometimes missing when only server-side `prev*` needed to catch up (tilt stuck until click).

**Config screen** — The old Theatrical UI stacked labels on top of 10px-tall edit boxes and buttons, causing unreadable overlapping text. Closing the screen also auto-committed changes with no way to cancel. There was no live preview of whether the selected address + personality channel count would fit in the current universe.

**Configuration card** — Patching a wide fixture near the end of a universe (e.g. address 500 with 34 channels) would overflow past channel 512. The card kept incrementing the address without switching universes, making large fixtures impossible to patch cleanly in sequence. The override in `ExtraLightsLightBlock` also dropped the network membership check, and the success message only showed a UUID instead of the network name with minimal context.

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
| **DMX footprint preview** | Live line under address/universe: `Universe 1 · channels 500–533 (34 ch)` or orange overflow warning with suggested universe/address |
| **Personality-aware footprint** | Channel count updates when cycling personality (7ch ↔ 10ch etc.); overflow warning recalculates immediately |
| **Keyboard shortcuts** | **Enter** → Save and close; **Escape** → Cancel without saving |
| i18n | Added UI + footprint keys in `en_us.json` and `fr_fr.json` |

### Configuration card — `ConfigurationCardHelper` + `ExtraLightsLightBlock`

All Extra Lights fixture blocks now extend `ExtraLightsLightBlock`, which overrides Configuration Card handling with automatic universe wrapping, network access control, and detailed chat feedback.

| Method / class | Purpose |
|----------------|---------|
| `fitsInUniverse(address, channelCount)` | Returns whether `address + channelCount - 1 ≤ 512` |
| `resolvePatch(universe, address, channelCount)` | On apply: if channels overflow, patch at **universe + 1, address 1** |
| `advancePatch(universe, address, channelCount)` | On auto-increment: if next address > 512, move to **universe + 1, address 1** |
| `applyToFixture(tag, consumer)` → `ApplyResult` | Applies network, resolved universe/address, updates card NBT; returns full patch metadata |
| `sendPatchMessages(...)` | Sends rewritten multi-line chat feedback |
| `TheatricalNetworkAccess` | Restores network membership check + resolves network **name** at runtime (Theatrical `networks` package not on common classpath) |

**Universe wrap example**

```
Card: Universe 1, address 500, auto-increment ON
Patch Atomic Strobe (34ch):
  → Fixture: Universe 2 @ 1   (500 + 34 - 1 = 533 > 512)
  → Next card address: Universe 2 @ 35
```

**Chat messages (replaces old single-line `item.configurationcard.success`)**

| Key | Example |
|-----|---------|
| `item.configurationcard.patched` | `Atomic Strobe patched on My Network — Universe 2 @ 1 (channels 1–34, 34 ch)` |
| `item.configurationcard.universe_wrap` | `Universe 1 @ 500 is full — switched to Universe 2 @ 1` |
| `item.configurationcard.next` | `Next on card: Universe 2 @ 35` |
| `item.configurationcard.next_universe_wrap` | `Next address exceeds 512 — card moved to Universe 2` |

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
- [ ] Press **Enter** in config screen → saves and closes
- [ ] Press **Escape** in config screen → closes without saving
- [ ] Config screen footprint shows correct channel range for current address + personality
- [ ] Cycle personality on a lyre (7ch ↔ 10ch) → footprint and overflow warning update live
- [ ] Address near end of universe with wide personality → orange overflow hint with suggested universe/address
- [ ] Configuration card at Universe 1 @ 500 → patch Atomic Strobe (34ch) → fixture lands on Universe 2 @ 1
- [ ] With auto-increment ON, next card address after above patch is Universe 2 @ 35
- [ ] Patch a small fixture that still fits (e.g. 3ch @ 500) → stays on same universe, next address 503
- [ ] Patch several fixtures in a row near end of universe → card wraps cleanly without manual universe edits
- [ ] Config card chat shows fixture name, network name, channel range, wrap notice, and next address
- [ ] Player not in network → config card returns FAIL (no patch applied)

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
common/.../util/TheatricalNetworkAccess.java
common/.../generated/resources/assets/theatricalextralights/lang/en_us.json
common/.../resources/assets/theatricalextralights/lang/fr_fr.json
```
