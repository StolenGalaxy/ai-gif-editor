plugins {
    id("java")
}

group = "com.stolengalaxy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.okhttp3:okhttp:5.3.0")
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
    implementation("commons-io:commons-io:2.21.0")
    implementation("com.sksamuel.scrimage:scrimage-core:4.3.8")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
