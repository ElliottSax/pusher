# Sound Assets

This directory contains sound effects for the Coin Pusher game.

## Required Sound Files

All sound files should be in **OGG format** (recommended for Android) or WAV format.

### File Names and Descriptions:

1. **coin_drop.ogg** (100-200ms)
   - Played when a coin is dropped onto the platform
   - Suggested: Metallic clink sound, medium pitch
   - Volume: Medium

2. **coin_collision.ogg** (50-150ms)
   - Played when coins collide with each other
   - Suggested: Light metallic tap, varies with impact force
   - Volume: Soft to medium (scaled by collision force)

3. **coin_collect.ogg** (200-400ms)
   - Played when a coin is collected
   - Suggested: Pleasant chime or reward sound
   - Volume: Medium-loud

4. **big_win.ogg** (500-1000ms)
   - Played when a special coin (50pts) is collected
   - Suggested: Celebratory fanfare or jackpot sound
   - Volume: Loud

## Audio Specifications

- **Format**: OGG Vorbis (preferred) or WAV
- **Sample Rate**: 44100 Hz
- **Bit Depth**: 16-bit
- **Channels**: Mono (stereo acceptable)
- **File Size**: Keep under 100KB each for optimal performance

## Finding/Creating Sounds

### Free Sound Resources:
- **Freesound.org** - https://freesound.org (CC0 and CC-BY licenses)
- **OpenGameArt.org** - https://opengameart.org/art-search-advanced?keys=coin
- **Zapsplat.com** - Free sound effects with attribution

### Search Terms:
- "coin drop"
- "metal clink"
- "coin collect"
- "casino win"
- "arcade reward"

### Creating Your Own:
1. Record real coins dropping/colliding
2. Use audio synthesis tools (bfxr, ChipTone, sfxr)
3. Edit in Audacity (free, open-source)

## Testing Sounds

After adding sound files:
1. Rebuild the app: `./gradlew android:assembleDebug`
2. Install on device: `./gradlew android:installDebug`
3. Check game settings to adjust volumes
4. Test different scenarios:
   - Drop single coin
   - Drop multiple coins rapidly
   - Collect normal coins
   - Collect special coins

## Current Status

⚠️ **Sound files not yet added** - The game will run without them, but audio will be silent.

Add the four OGG files listed above to this directory to enable sound effects.
