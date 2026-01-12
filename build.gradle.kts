plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

group = "com.jarvis"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Discord (JDA)
    implementation("net.dv8tion:JDA:5.0.0-beta.20")

    // HTTP Client for API calls
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-client-logging:2.3.7")

    // Gemini API (Google GenAI SDK - Official GA version)
    implementation("com.google.genai:google-genai:1.4.1")

    // MCP (WebSocket for MCP communication)
    implementation("io.ktor:ktor-client-websockets:2.3.7")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Configuration
    implementation("com.typesafe:config:1.4.3")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

application {
    mainClass.set("com.jarvis.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.jarvis.MainKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
