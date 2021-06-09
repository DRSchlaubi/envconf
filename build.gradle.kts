import org.jetbrains.kotlin.gradle.internal.testing.TCServiceMessagesTestExecutionSpec
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import java.util.Base64

plugins {
    kotlin("multiplatform") version "1.5.10"
    id("org.jetbrains.dokka") version "1.4.32"
    id("org.ajoberstar.git-publish") version "2.1.3"
    `maven-publish`
    signing
}

group = "dev.schlaubi"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        nodejs()
    }

    explicitApi()

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }

    tasks {
        withType<Test> {
            environment(mapOf("HELLO" to "HELLO", "PREFIX_HELLO" to "HELLO"))
        }

        withType<KotlinJsTest> {
            testFramework =
                EnvConfTestFramework(org.jetbrains.kotlin.gradle.targets.js.testing.mocha.KotlinMocha(compilation))
        }

        dokkaHtml {
            outputDirectory.set(file("docs"))

            dokkaSourceSets {
                configureEach {
                    includeNonPublic.set(false)
                }

                if (asMap.containsKey("jsMain")) {
                    named("jsMain") {
                        displayName.set("JS")
                    }
                }

                if (asMap.containsKey("jvmMain")) {
                    named("jvmMain") {
                        jdkVersion.set(8)
                        displayName.set("JVM")
                    }
                }
            }
        }

        configure<org.ajoberstar.gradle.git.publish.GitPublishExtension> {
            repoUri.set("https://github.com/DRSchlaubi/envconf.git")
            branch.set("gh-pages")

            contents {
                from(file("docs"), "CNAME")
            }

            commitMessage.set("Update Docs")
        }
    }

    signing {
        val signingKey = findProperty("signingKey")?.toString()
        val signingPassword = findProperty("signingPassword")?.toString()
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(
                String(Base64.getDecoder().decode(signingKey.toByteArray())),
                signingPassword
            )
        }

        publishing.publications.withType<MavenPublication> {
            sign(this)
        }
    }
    
    publishing {
        repositories {
            maven {
                setUrl("https://schlaubi.jfrog.io/artifactory/envconf")

                credentials {
                    username = System.getenv("BINTRAY_USER")
                    password = System.getenv("BINTRAY_KEY")
                }
            }
        }

        publications {
            filterIsInstance<MavenPublication>().forEach { publication ->
                publication.pom {
                    name.set(project.name)
                    description.set("Kotlin library which makes it easy to use env variables for configs")
                    url.set("https://github.com/DRSchlaubi/envconf")

                    licenses {
                        license {
                            name.set("Apache-2.0 License")
                            url.set("https://github.com/DRSchlaubi/envconf/blob/main/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            name.set("Michael Rittmeister")
                            email.set("mail@schlaubi.me")
                            organizationUrl.set("https://schlau.bi")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/DRSchlaubi/envconf.git")
                        developerConnection.set("scm:git:https://github.com/DRSchlaubi/envconf.git")
                        url.set("https://github.com/DRSchlaubi/envconf")
                    }
                }
            }
        }
    }
}

class EnvConfTestFramework(
    private val delegate: org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTestFramework
) : org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTestFramework by delegate {
    override fun createTestExecutionSpec(
        task: KotlinJsTest,
        forkOptions: ProcessForkOptions,
        nodeJsArgs: MutableList<String>,
        debug: Boolean
    ): TCServiceMessagesTestExecutionSpec {
        val newProcessForkOptions = forkOptions.apply {
            setEnvironment("HELLO" to "HELLO")
        }
        return delegate.createTestExecutionSpec(task, newProcessForkOptions, nodeJsArgs, debug)
    }

}
