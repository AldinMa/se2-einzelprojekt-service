import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    kotlin("plugin.spring") version "2.3.10"
    jacoco
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "at.aau.serg"
version = "1.0.0"

repositories {
    mavenCentral()
}

springBoot {
    mainClass.set("at.aau.serg.SpringAppKt")
}

jacoco {
    toolVersion = "0.8.13"
}

val coverageTargets = listOf(
    "at/aau/serg/services/GameResultService.class",
    "at/aau/serg/controllers/LeaderboardController.class",
    "at/aau/serg/controllers/GameResultController.class"
)

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    implementation("io.github.pdvrieze.xmlutil:core:0.91.3")
    implementation("io.github.pdvrieze.xmlutil:serialization:0.91.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.10.0")
    implementation("io.ktor:ktor-client-cio:3.4.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport", "jacocoTestCoverageVerification")
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    classDirectories.setFrom(
        files(layout.buildDirectory.dir("classes/kotlin/main")).asFileTree.matching {
            include(coverageTargets)
        }
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.test)

    classDirectories.setFrom(
        files(layout.buildDirectory.dir("classes/kotlin/main")).asFileTree.matching {
            include(coverageTargets)
        }
    )

    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.0".toBigDecimal()
            }
        }
    }
}

kotlin {
    jvmToolchain(21)
}