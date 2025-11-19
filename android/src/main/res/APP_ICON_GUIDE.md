# App Icon Guide

This guide explains how to create and add app icons for the Coin Pusher game.

## Required Icon Sizes

Android requires icons in multiple densities to support different screen resolutions:

| Density | Folder | Size (px) | Description |
|---------|--------|-----------|-------------|
| mdpi | mipmap-mdpi | 48x48 | Medium density (~160 dpi) |
| hdpi | mipmap-hdpi | 72x72 | High density (~240 dpi) |
| xhdpi | mipmap-xhdpi | 96x96 | Extra-high density (~320 dpi) |
| xxhdpi | mipmap-xxhdpi | 144x144 | Extra-extra-high density (~480 dpi) |
| xxxhdpi | mipmap-xxxhdpi | 192x192 | Extra-extra-extra-high density (~640 dpi) |

Additionally, for **adaptive icons** (Android 8.0+):

| File | Size | Description |
|------|------|-------------|
| ic_launcher_foreground.xml | - | Foreground layer (108dp safe zone, 72dp visible) |
| ic_launcher_background.xml | - | Background layer (solid color or simple pattern) |
| ic_launcher.xml | - | Adaptive icon descriptor |

## Icon Design Guidelines

### Design Specifications:
- **Shape**: Circle, rounded square, or squircle (Android will mask it)
- **Safe Zone**: Keep important elements within 66dp diameter circle (for adaptive icons)
- **Colors**: Vibrant and recognizable
- **Style**: Simple, clear, scalable

### Theme Ideas for Coin Pusher:
1. **Golden Coin** - Single gold coin with shine effect
2. **Coin Stack** - Multiple coins stacked
3. **Coin Drop** - Coin falling with motion lines
4. **Arcade Machine** - Simplified arcade pusher view
5. **CP Letters** - Stylized "CP" initials with coins

### Color Palette Suggestions:
- **Primary**: Gold (#FFD700) - for coins
- **Accent**: Royal Blue (#4169E1) - for background
- **Highlight**: White (#FFFFFF) - for shine
- **Shadow**: Dark brown (#654321) - for depth

## Creating App Icons

### Method 1: Android Studio Image Asset Tool (Easiest)

1. **Open Android Studio**
2. Right-click on `app` (or `android`) module
3. Select **New → Image Asset**
4. Choose **Launcher Icons**
5. Select source:
   - **Image**: Upload a 512x512 PNG
   - **Clip Art**: Choose from built-in icons
   - **Text**: Create text-based icon
6. Configure:
   - **Name**: ic_launcher
   - **Trim**: Yes (removes transparent edges)
   - **Padding**: 0-20% (depending on design)
   - **Background Layer**: Solid color or image
7. Click **Next** → **Finish**

This automatically generates all mipmap sizes!

### Method 2: Online Icon Generators (Quick)

**easyappicon.com**:
1. Upload 1024x1024 PNG
2. Select "Android" platform
3. Download zip
4. Extract to appropriate mipmap folders

**appicon.co**:
1. Upload high-res PNG (1024x1024 or larger)
2. Choose Android
3. Download all sizes

**makeappicon.com**:
1. Upload 1024x1024 PNG
2. Generates all required sizes
3. Includes adaptive icon layers

### Method 3: Manual Creation (Most Control)

**Using GIMP**:
1. Create 512x512 canvas with transparent background
2. Design your icon
3. Export as PNG
4. Resize to each required size:
   - Image → Scale Image
   - Enter width/height
   - Export for each mipmap folder

**Using Inkscape**:
1. Create vector design (scalable)
2. Export PNG at each required resolution
3. Vectors scale perfectly without quality loss

**Using Photoshop/Affinity Photo**:
1. Create 512x512 design
2. Use Actions/Batch to export all sizes
3. Save each to appropriate folder

## Creating Adaptive Icons (Recommended for Modern Android)

Adaptive icons have two layers: foreground and background.

**Foreground Layer** (ic_launcher_foreground.xml):
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <group android:scaleX="0.7"
        android:scaleY="0.7"
        android:translateX="16.2"
        android:translateY="16.2">
        <!-- Your coin/icon design here -->
        <path
            android:fillColor="#FFD700"
            android:pathData="M54,18C35.2,18,20,33.2,20,52c0,18.8,15.2,34,34,34s34,-15.2,34,-34C88,33.2,72.8,18,54,18z"/>
        <path
            android:fillColor="#FFFFFF"
            android:pathData="M54,26c-14.4,0-26,11.6-26,26s11.6,26,26,26s26,-11.6,26,-26S68.4,26,54,26z"/>
    </group>
</vector>
```

**Background Layer** (ic_launcher_background.xml):
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#4169E1"
        android:pathData="M0,0h108v108h-108z"/>
</vector>
```

**Adaptive Icon Descriptor** (ic_launcher.xml):
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
```

## File Structure

After adding icons, your structure should look like:

```
android/src/main/res/
├── mipmap-mdpi/
│   └── ic_launcher.png (48x48)
├── mipmap-hdpi/
│   └── ic_launcher.png (72x72)
├── mipmap-xhdpi/
│   └── ic_launcher.png (96x96)
├── mipmap-xxhdpi/
│   └── ic_launcher.png (144x144)
├── mipmap-xxxhdpi/
│   └── ic_launcher.png (192x192)
├── mipmap-anydpi-v26/
│   └── ic_launcher.xml (adaptive icon)
├── drawable/
│   ├── ic_launcher_foreground.xml
│   └── ic_launcher_background.xml
└── values/
    └── ic_launcher_background.xml (if using solid color)
```

## Quick Start Templates

### Simple Gold Coin Icon (Text Description):
- **Background**: Royal blue circular gradient
- **Main**: Large gold coin (centered)
- **Highlight**: White shine on top-left
- **Shadow**: Dark shadow bottom-right
- **Border**: Thin gold ring

### Design Tools:
1. **Canva** (canva.com) - Easy drag-and-drop
2. **Figma** (figma.com) - Professional design tool
3. **Icon Kitchen** (icon.kitchen) - Android-specific
4. **Flaticon** (flaticon.com) - Download coin icons

## Free Icon Resources:

**Pre-made Coin Icons**:
- **Flaticon**: Search "gold coin" (some free with attribution)
- **Icons8**: coins icons
- **Freepik**: coin icons
- **Vecteezy**: vector coins

## Testing Your Icon

1. Build and install app:
   ```bash
   ./gradlew android:installDebug
   ```

2. Check launcher:
   - Icon appears on home screen?
   - Icon looks good on different backgrounds?
   - Icon recognizable at small size?

3. Test on multiple devices:
   - Phone with light theme
   - Phone with dark theme
   - Tablet (larger icon)

## Common Mistakes to Avoid

❌ **Don't**:
- Use too much detail (won't scale well)
- Use thin lines (invisible at small sizes)
- Place important elements near edges (gets clipped)
- Use transparency as background (shows launcher wallpaper)

✅ **Do**:
- Keep it simple and bold
- Use high contrast
- Test at actual size (not zoomed in)
- Include safe zone padding
- Use vibrant, saturated colors

## Current Status

⚠️ **Default icon in use** - The app currently uses Android's default icon.

Add custom icons following this guide to create a professional appearance on the Play Store and user devices.

## Play Store Requirements

For Google Play Store submission, you'll also need:

- **High-res icon**: 512x512 PNG (for Play Store listing)
- **Feature graphic**: 1024x500 PNG (banner)
- **Screenshots**: At least 2 (phone), 1440x2560 or higher

Create these alongside your launcher icons for a complete app listing.
