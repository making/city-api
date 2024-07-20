buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.spring.javaformat:spring-javaformat-gradle-plugin:0.0.41")
    }
}


plugins {
    java
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.spring.javaformat") version "0.0.41"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.0.5")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("am.ik.spring:retryable-client-http-request-interceptor:0.5.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// No plain jar is needed.
tasks.jar {
    enabled = false
}
