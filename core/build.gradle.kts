plugins {
    kotlin("jvm")
}

val gdxVersion: String by rootProject.extra

dependencies {
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
