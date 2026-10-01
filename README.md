# Ping Display+Overlay (NeoForge)

A client-side Minecraft mod that gives you full control over how ping is displayed in the tab list and on the HUD — colored by latency, with a compact "staircase" indicator bar, plus optional player heads in the tab list.

**Minecraft version:** 26.1.2 · **Loader:** NeoForge

## Features

- **Tab list**: numeric ping value and/or a 5-step color-coded indicator bar next to each player's name, with player heads shown/hidden independently of the server's online-mode.
- **HUD overlay**: the same ping value/indicator, freely positioned anywhere on screen (percentage-based X/Y).
- **5-tier color thresholds** (Very Good / Good / Ok / Bad / Very Bad), fully customizable both in ms limits and colors.
- **Fast, real-time ping** for your own connection — uses the same ping/pong packet vanilla's F3+3 debug ping graph uses, instead of waiting for the server's slow (~30s) tab list broadcast.
- **Connection-loss detection**: if no ping response is received for a while, your own ping shows a distinct color and `-1` instead of a stale number.
- **Configurable tab row background**: linked to vanilla's own per-row background mechanism, so it covers the whole row and blends correctly with the vanilla "Text Background Opacity" option.
- **Adjustable tab row width** (with an in-game tooltip showing the vanilla default for reference).
- **Adjustable opacities** for heads, ping text/indicator, and tab background, independently for TAB and HUD.
- **In-game settings screen**: three tabs (General / TAB / HUD), each with a master Enable toggle, live-apply on change, and a per-setting Reset button.
- **Open settings**: press **Right Shift** in-game, or use the "Config" button in the mods list (NeoForge shows this natively, no extra mod needed).
- English is the default language. Add more languages by dropping a new `assets/pingdisplayoverlay/lang/<locale>.json` file, using the locale codes listed at [minecraft.wiki/w/Language](https://minecraft.wiki/w/Language) (e.g. `en_us` for English).

## Dependencies

None.