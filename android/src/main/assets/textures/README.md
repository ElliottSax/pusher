# Texture Assets

This directory contains texture images for the Coin Pusher game.

## Required Texture Files

All texture files should be in **PNG format** with transparency (alpha channel).

### Coin Textures:

1. **coin_gold.png** (128x128 pixels)
   - Circular gold coin sprite
   - Color: Warm gold/yellow (#FFD700)
   - Should include shine/highlight effect
   - Alpha channel for smooth edges

2. **coin_silver.png** (128x128 pixels)
   - Circular silver coin sprite
   - Color: Silver/gray (#C0C0C0)
   - Should include metallic shine
   - Alpha channel for smooth edges

3. **coin_bronze.png** (128x128 pixels)
   - Circular bronze coin sprite
   - Color: Bronze/copper (#CD7F32)
   - Should include metallic shine
   - Alpha channel for smooth edges

4. **coin_special.png** (128x128 pixels)
   - Circular special coin sprite
   - Color: Cyan/bright blue (#00FFFF)
   - Should include glow/sparkle effect
   - Alpha channel for smooth edges
   - Can be more elaborate/animated

## Texture Specifications

- **Format**: PNG with alpha channel
- **Size**: 128x128 pixels (will be scaled automatically)
- **DPI**: 72 dpi minimum
- **Color Mode**: RGBA (8-bit per channel)
- **File Size**: Keep under 50KB each

## Design Guidelines

### Style:
- **Circular shape** (use full 128x128 canvas, coin should be ~110px diameter)
- **Metallic appearance** with highlights and shadows
- **3D effect** with gradient shading
- **Smooth anti-aliased edges**
- **Consistent lighting** (light source from top-left)

### Layers (recommended):
1. Base color (flat circle)
2. Dark edge (creates depth)
3. Gradient shading (convex 3D effect)
4. Highlight (top-left shine)
5. Center emblem/text (optional - could be coin value or symbol)

## Creating Coin Textures

### Using Free Tools:

**GIMP (Free, Open Source)**:
1. Create new 128x128px image with transparency
2. Use Ellipse Select tool (hold Shift for perfect circle)
3. Fill with base color
4. Apply filters: Bump Map or Lighting Effects for 3D appearance
5. Add layer for highlight (white, low opacity, Gaussian blur)
6. Export as PNG

**Inkscape (Free, Vector Graphics)**:
1. Create circle shape
2. Apply radial gradient (dark edge to light center)
3. Add highlight ellipse with blur
4. Export at 128x128px

**Online Tools**:
- **Piskel** (piskelapp.com) - Pixel art editor
- **Photopea** (photopea.com) - Free Photoshop alternative
- **Canva** (canva.com) - Simple graphic design

### Free Coin Sprite Resources:
- **OpenGameArt.org** - Search "coin sprite"
- **Itch.io** - Free game assets section
- **Kenney.nl** - Free game assets (CC0 license)
- **Game-Icons.net** - Free SVG game icons

### Example Search Terms:
- "gold coin sprite"
- "2D coin texture"
- "game coin asset"
- "pixel art coin"

## Texture Atlas (Optional Enhancement)

For better performance, you can combine all 4 coin textures into a single atlas:

**coins.atlas** (texture atlas descriptor):
```
coins.png
size: 256,256
format: RGBA8888
filter: Linear,Linear
repeat: none
coin_gold
  rotate: false
  xy: 0, 0
  size: 128, 128
  orig: 128, 128
  offset: 0, 0
  index: -1
coin_silver
  rotate: false
  xy: 128, 0
  size: 128, 128
  orig: 128, 128
  offset: 0, 0
  index: -1
coin_bronze
  rotate: false
  xy: 0, 128
  size: 128, 128
  orig: 128, 128
  offset: 0, 0
  index: -1
coin_special
  rotate: false
  xy: 128, 128
  size: 128, 128
  orig: 128, 128
  offset: 0, 0
  index: -1
```

Use libGDX's TexturePacker tool to generate this automatically.

## Testing Textures

After adding texture files:
1. Place PNG files in this directory
2. Rebuild app: `./gradlew android:assembleDebug`
3. Install: `./gradlew android:installDebug`
4. Coins should now render with textures instead of solid shapes
5. Check that:
   - All 4 coin types display correctly
   - Textures are smooth (not pixelated)
   - Transparency works properly
   - Performance is still 60fps

## Current Status

⚠️ **Textures not yet added** - The game uses shape rendering (circles) as fallback.

Add the four PNG files listed above to enable textured coin rendering.

## Fallback Behavior

If texture files are missing:
- Game will use shape rendering (colored circles)
- 3D depth effect rendered with multiple circles
- Performance is slightly better with shapes
- Game remains fully playable
