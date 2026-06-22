rootProject.name = "playground-service"

// Consume the SRI Java SDK directly from source (composite build). Gradle substitutes
// the "io.github.opensri:sri-sdk-java" dependency with this included build, so changes
// in the SDK are picked up without publishing an artifact.
includeBuild("../sdk/sri-sdk-java")
