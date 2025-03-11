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
        languageVersion = JavaLanguageVersion.of(17)
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

    implementation("org.apache.kafka:kafka-clients:3.8.1")
    implementation("org.apache.flink:flink-connector-base:1.20.1")
    implementation("org.apache.flink:flink-connector-kafka:3.4.0-1.20")
    implementation("org.apache.flink:flink-streaming-java:1.20.1")
    implementation("org.apache.flink:flink-avro-confluent-registry:1.20.1")
    implementation("org.apache.flink:flink-avro:1.20.1")
    implementation("org.apache.avro:avro:1.12.0")

    // <!-- Apache Flink dependencies -->
    // <!-- These dependencies should not be packaged into the JAR file. -->
    compileOnly("org.apache.flink:flink-statebackend-rocksdb:1.20.1")
}

springBoot {
    mainClass.set("io.confluent.stream.SensorsCategoriesApplication")
}

application {
    mainClass.set("io.confluent.stream.SensorsCategoriesApplication")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "io.confluent.stream.SensorsCategoriesApplication")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}