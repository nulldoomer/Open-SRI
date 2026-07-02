plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "6.25.0"
}


spotless {
	java {
		target("src/**/*.java")
		targetExclude("build/**/*.java")
		targetExclude("src/test/**/*.java")
		licenseHeader(
			"""// SPDX-License-Identifier: Apache-2.0
            // Copyright (c) 2026 Nulldoomer

            """
		)

		removeUnusedImports()
		googleJavaFormat()
	}
}

group = "io.github.opensri"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Web and Security
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-security")

	// SRI Java SDK — resolved from source via the composite build declared in settings.gradle.kts
	implementation("io.github.nulldoomer:opensri:1.2.4")

	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("com.fasterxml.jackson.core:jackson-databind")

	// Observability
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

	// Rate Limit
	implementation("com.bucket4j:bucket4j-core:8.10.1")
	implementation("com.github.ben-manes.caffeine:caffeine")

	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// The SDK brings JAXB on the runtime classpath, and a couple of its transitive artifacts
// (e.g. jaxb-core) resolve to the same file more than once. They are identical, so exclude
// the duplicates when assembling the executable jar.
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
