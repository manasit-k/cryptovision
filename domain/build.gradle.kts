plugins {
    id("java-library")
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Javax Inject for DI annotations
    implementation("javax.inject:javax.inject:1")
    
    // Coroutines
    implementation(libs.bundles.coroutines)
    
    // Testing
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
