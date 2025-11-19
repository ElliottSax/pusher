buildscript {
    val kotlinVersion = "2.0.21"
    val gdxVersion = "1.12.1"
    val androidPluginVersion = "8.7.3"

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:$androidPluginVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }

    ext {
        set("gdxVersion", "1.12.1")
        set("box2DLightsVersion", "1.5")
        set("ashleyVersion", "1.7.4")
        set("aiVersion", "1.8.2")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
