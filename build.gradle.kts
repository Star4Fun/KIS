plugins {
    application
    id("java-library")
    id("com.diffplug.spotless") version libs.versions.spotless.get()
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "21" // passt zu JDK 21; bei JDK 23 -> "23"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml"      
    )
}


java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get())) }
    withSourcesJar()
    withJavadocJar()
}

application {
    // Default entry point (can be changed)
    mainClass.set("com.example.kis.App")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    // Reproducible JARs
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.register<JavaExec>("runV2") {
    group = "application"
    description = "Run HL7 v2 demo"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.example.kis.hl7v2.DemoV2")
}
tasks.register<JavaExec>("runFhir") {
    group = "application"
    description = "Run FHIR demo"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.example.kis.fhir.DemoFhir")
}

spotless {
    java {
        googleJavaFormat()   // code style
        target("src/**/*.java")
    }
}

// ---- Dependencies ----
//
// Toggle HAPI v2 and/or FHIR below as you need.
// By default both are included so you can try each demo.

dependencies {
    // --- HL7 v2 ---
    implementation(libs.hapi.base) {
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "org.apache.commons", module = "commons-lang3")
        exclude(group = "commons-codec", module = "commons-codec")
    }
    implementation(libs.hapi.structures.v25)

    // --- HAPI FHIR: enforce BOM to avoid drift ---
    implementation(enforcedPlatform("ca.uhn.hapi.fhir:hapi-fhir-bom:${libs.versions.hapiFhirBom.get()}"))
    implementation(libs.hapi.fhir.base)
    implementation(libs.hapi.fhir.client)
    implementation(libs.hapi.fhir.structures.r4)

    // Align shared libs (newer line)
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("commons-codec:commons-codec:1.15")

    // DB driver + pool
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Pick one stack:
    // A) Lightweight JDBC
    // implementation("org.jooq:jooq:3.19.14") // optional if you want jOOQ

    // B) JPA/Hibernate
    implementation("org.hibernate.orm:hibernate-core:6.6.1.Final")

    // Migrations (highly recommended)
    implementation("org.flywaydb:flyway-core:10.18.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")

    // Simple runtime logger (pick one binding)
    runtimeOnly("org.slf4j:slf4j-simple:2.0.13")

    testImplementation(libs.junit.jupiter)
}

// Deterministic dependency graph (opt-in: run `./gradlew --write-locks`)
configurations.all {
    resolutionStrategy.failOnVersionConflict()
}
