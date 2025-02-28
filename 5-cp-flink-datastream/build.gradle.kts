plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.2"
}

group = "io.confluent"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

spotless {
    java {
        googleJavaFormat().aosp().reflowLongStrings(false)
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

    implementation("org.apache.flink:flink-connector-base:1.20.1")
    implementation("org.apache.flink:flink-connector-kafka:3.4.0-1.20")

//    <!-- Apache Flink dependencies -->
//    <!-- These dependencies should not be packaged into the JAR file. -->

    compileOnly("org.apache.flink:flink-streaming-java:1.20.1")
    compileOnly("org.apache.flink:flink-clients:1.20.1")
}

springBoot {
    mainClass.set("io.confluent.stream.KafkaCopier")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//plugins {
//    id 'java'
//    id "application"
//}
//
//version = "1.0.0"
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    implementation group: 'org.slf4j', name: 'slf4j-nop', version: '1.7.36'
//    implementation group: 'org.apache.kafka', name: 'kafka-clients', version: '3.3.1'
//}
//
//ext {
//    javaMainClass = "example.ClientExample"
//}
//
//application {
//    mainClass.set(javaMainClass)
//}
//
//jar {
//    manifest {
//        attributes "Main-Class": javaMainClass
//    }
//}