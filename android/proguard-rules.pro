# Add project specific ProGuard rules here.
# libGDX specific rules
-dontwarn com.badlogic.gdx.jnigen.**
-dontwarn com.badlogic.gdx.graphics.g2d.freetype.**

# Keep all classes in the game package
-keep class com.coinpusher.game.** { *; }

# Box2D
-keep class com.badlogic.gdx.physics.box2d.** { *; }

# Optimize
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
