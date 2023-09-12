val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.4"
}

group = "tk.apfelkuchenwege"
version = "0.0.1"

application {
    mainClass.set("tk.apfelkuchenwege.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
	implementation("org.postgresql:postgresql:42.5.1")
	implementation("com.h2database:h2:2.1.214")
	testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("com.sendgrid:sendgrid-java:4.9.3")
}
