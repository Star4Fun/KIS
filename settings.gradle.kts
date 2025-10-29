rootProject.name = "KIS"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { mavenCentral() }
    versionCatalogs {
        create("libs") {
            // Versions
            version("java", "25")
            version("junit", "5.11.3")
            version("spotless", "8.0.0")
            version("hapiV2", "2.3")        // HAPI v2 example
            version("hapiFhirBom", "7.6.0") // HAPI FHIR BOM version

            // Libraries
            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter")
                .versionRef("junit")

            // HAPI HL7 v2 (with explicit versions from catalog)
            library("hapi-base", "ca.uhn.hapi", "hapi-base")
                .versionRef("hapiV2")
            library("hapi-structures-v25", "ca.uhn.hapi", "hapi-structures-v25")
                .versionRef("hapiV2")

            // HAPI FHIR (resolved via BOM -> no version here)
            library("hapi.fhir.base", "ca.uhn.hapi.fhir", "hapi-fhir-base")
                .withoutVersion()
            library("hapi.fhir.client", "ca.uhn.hapi.fhir", "hapi-fhir-client")
                .withoutVersion()
            library("hapi.fhir.structures.r4", "ca.uhn.hapi.fhir", "hapi-fhir-structures-r4")
                .withoutVersion()
        }
    }
}
