plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.0.0-beta9"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.2"
}

group = "io.confluent"
version = "0.0.2-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

spotless {
    java {
        palantirJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
        toggleOffOn()
    }
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.apache.flink:flink-table-api-java:1.20.1")
    implementation("io.confluent.flink:confluent-flink-table-api-java-plugin:1.20-50")
}

springBoot {
    mainClass.set("io.confluent.select.SampleSelectApplication")
}

application {
    mainClass.set("io.confluent.select.SampleSelectApplication")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "io.confluent.select.SampleSelectApplication")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
