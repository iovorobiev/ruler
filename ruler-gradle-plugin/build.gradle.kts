plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("maven-publish")
    id("signing")
    id("io.gitlab.arturbosch.detekt")
}

extra[EXT_POM_NAME] = "Ruler Gradle plugin"
extra[EXT_POM_DESCRIPTION] = "Gradle plugin for analyzing Android app size"

dependencies {
    compileOnly(gradleApi())
    compileOnly(Dependencies.ANDROID_GRADLE_PLUGIN)

    implementation(project(":ruler-models"))

    implementation(Dependencies.APK_ANALYZER) {
        exclude(group = "com.android.tools.lint") // Avoid leaking incompatible Lint versions to consumers
    }
    implementation(Dependencies.KOTLINX_SERIALIZATION_JSON)

    testRuntimeOnly(Dependencies.JUNIT_ENGINE)
    testImplementation(gradleTestKit())
    testImplementation(Dependencies.JUNIT_API)
    testImplementation(Dependencies.JUNIT_PARAMS)
    testImplementation(Dependencies.GOOGLE_TRUTH)
    testImplementation(Dependencies.GOOGLE_GUAVA)
    testImplementation(Dependencies.ANDROID_GRADLE_PLUGIN)
}

// Include the output of the frontend JS compilation in the plugin resources
sourceSets.main {
    resources.srcDir(project(":ruler-frontend").tasks.named("browserDistribution"))
}

tasks.withType<Test> {
    useJUnitPlatform()

    // Make plugin available to integration tests
    dependsOn("publishToMavenLocal", ":ruler-models:publishToMavenLocal")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("jvm") {
            from(components["java"])
        }
    }
    configurePublications(project)
}

signing {
    configureSigning(publishing.publications)
}
