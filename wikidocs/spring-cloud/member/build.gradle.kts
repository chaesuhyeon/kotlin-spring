plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.ecommerce"
version = "0.0.1-SNAPSHOT"
description = "ecommerce msa practice member project"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // ========== 테스트용 H2 데이터베이스 ==========
    runtimeOnly("com.h2database:h2")

    // ========== test ========== //
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.8.1") // Kotest Runner (JUnit5 위에서 동작)
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.8.1") // Kotest Assertions
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3") // Spring과 Kotest를 연동하기 위한 라이브러리
    testImplementation("io.mockk:mockk:1.13.10") // 코틀린 네이티브 Mocking 라이브러리
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
