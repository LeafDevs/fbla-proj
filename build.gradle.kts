plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.8.10"
}

group = "me.leaf.devs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("io.ktor:ktor-server-core:2.3.0")
    implementation("io.ktor:ktor-server-netty:2.3.0")
    implementation("io.ktor:ktor-server-html-builder:2.3.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("org.slf4j:slf4j-api:2.0.0") // SLF4J API
    implementation("io.ktor:ktor-server-rate-limit:2.3.0")


}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "me.leaf.devs.MainKt" // Replace with your actual main class package
        )
    }
    from(sourceSets.main.get().output)

    // Include all dependencies in the fat JAR
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    // Handle duplicates by setting the strategy
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}



kotlin {
    jvmToolchain(17)
}
