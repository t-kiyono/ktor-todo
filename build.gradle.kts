plugins {
  kotlin("jvm") version "1.9.0"
  application
  id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

kotlin {
  jvmToolchain(17)
}

repositories {
  mavenCentral()
}

dependencies {
  // Ktor Server
  implementation("io.ktor:ktor-server-core:2.3.2")
  implementation("io.ktor:ktor-server-netty:2.3.2")
  implementation("io.ktor:ktor-server-content-negotiation:2.3.2")
  implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")
  implementation("io.ktor:ktor-server-status-pages:2.3.2")

  // SQLite + Exposed
  implementation("org.jetbrains.exposed:exposed-core:0.44.0")
  implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
  implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
  implementation("org.xerial:sqlite-jdbc:3.45.1.0")

  // Serialization
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

  // Logging
  implementation("ch.qos.logback:logback-classic:1.5.18")

  // --- Ktor テスト関連 ---
  testImplementation("io.ktor:ktor-server-tests:2.3.2")
  testImplementation("io.ktor:ktor-client-content-negotiation:2.3.2")

  // Kotest テストフレームワーク
  testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
  testImplementation("io.kotest:kotest-assertions-core:5.8.0")
  testImplementation("io.kotest:kotest-framework-engine:5.8.0")
}

tasks.test {
  useJUnitPlatform()
}

application {
  mainClass.set("MainKt")
}
