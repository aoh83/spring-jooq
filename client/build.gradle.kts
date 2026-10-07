plugins {
    java
    id("org.openapi.generator") version "7.25.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

val generatedSources = layout.buildDirectory.dir("generated-sources/openapi")

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

sourceSets {
    main {
        java {
            srcDir(generatedSources.map { it.dir("src/main/java") })
        }
    }
}

val generateClient by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    group = "openapi"
    description = "Generates a Java client from api/petstore.yaml."
    inputSpec.set(rootProject.file("api/petstore.yaml").absolutePath)
    outputDir.set(generatedSources)
    generatorName.set("java")
    library.set("native")
    groupId.set("com.example")
    apiPackage.set("com.example.petstore.client.api")
    modelPackage.set("com.example.petstore.client.model")
    invokerPackage.set("com.example.petstore.client")
    additionalProperties.putAll(
        mapOf(
            "artifactId" to "petstore-client",
            "openApiNullable" to "false",
        ),
    )
}

tasks.compileJava {
    dependsOn(generateClient)
}
