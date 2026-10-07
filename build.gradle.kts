buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.liquibase:liquibase-core:5.0.3")
    }
}

plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("org.jooq.jooq-codegen-gradle") version "3.21.7"
    id("org.liquibase.gradle") version "3.1.0"
}

group = "com.example"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

val jooqGeneratedDir = layout.buildDirectory.dir("generated-src/jooq/main")

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    runtimeOnly("org.postgresql:postgresql")

    jooqCodegen("org.postgresql:postgresql:42.7.13")

    liquibaseRuntime("org.liquibase:liquibase-core:5.0.3")
    liquibaseRuntime("info.picocli:picocli:4.7.7")
    liquibaseRuntime("org.postgresql:postgresql:42.7.13")
}

sourceSets {
    main {
        java {
            srcDir(jooqGeneratedDir)
        }
    }
}

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changelogFile" to "db/changelog/db.changelog-master.yaml",
            "searchPath" to "file:src/main/resources",
            "url" to "jdbc:postgresql://localhost:5432/petstore",
            "username" to "petstore",
            "password" to "petstore",
        )
    }
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://localhost:5432/petstore"
            user = "petstore"
            password = "petstore"
        }
        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
                includes = "pet"
            }
            target {
                packageName = "com.example.petstore.db.jooq.generated"
                directory = jooqGeneratedDir.get().asFile.absolutePath
            }
        }
    }
}

tasks.named("jooqCodegen") {
    dependsOn(tasks.named("update"))
}

tasks.named("compileJava") {
    dependsOn(tasks.named("jooqCodegen"))
}

val apiSpec = tasks.register<Copy>("apiSpec") {
    description = "Copies the OpenAPI specification into the build output as a standalone artifact."
    from("api/petstore.yaml")
    into(layout.buildDirectory.dir("libs"))
    rename { "petstore-openapi-${project.version}.yaml" }
}

tasks.named("assemble") {
    dependsOn(apiSpec)
}

tasks.processResources {
    from("api/petstore.yaml") {
        into("openapi")
    }
}
