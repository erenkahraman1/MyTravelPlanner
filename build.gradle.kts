plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    kotlin("jvm")
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

