plugins {
    kotlin("jvm") version "2.2.20"
}

group = "com.jarvis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:6.3.0") { // replace $version with the latest version
        // Optionally disable audio natives to reduce jar size by excluding `opus-java` and `tink`
        // Gradle DSL:
        // exclude module: 'opus-java' // required for encoding audio into opus, not needed if audio is already provided in opus encoding
        // exclude module: 'tink' // required for encrypting and decrypting audio
        // Kotlin DSL:
        // exclude(module="opus-java") // required for encoding audio into opus, not needed if audio is already provided in opus encoding
        // exclude(module="tink") // required for encrypting and decrypting audio
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}