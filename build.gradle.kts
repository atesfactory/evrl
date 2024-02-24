plugins {
    java
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-2"
}

group = "io.atesfactory"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter:2.7.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("evrl")
                description.set("A highly extensible last mile declarative resource loading and transformation library for Spring Boot")
                url.set("https://github.com/atesfactory/evrl")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit")
                    }
                }


                developers {
                    developer {
                        id.set("atesfactory")
                        name.set("atesfactory")
                        email.set("dev@atesfactory.io")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/atesfactory/evrl.git")
                    developerConnection.set("scm:git:ssh://github.com/atesfactory/evrl.git")
                    url.set("https://github.com/atesfactory/evrl/tree/main")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
