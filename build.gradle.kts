import com.vanniktech.maven.publish.SonatypeHost

allprojects {
    group = "io.github.wasabithumb"
    version = "0.1.0"
}

plugins {
    id("java-library")
    alias(libs.plugins.publish)
}

setupJava()

description = "Portable & fast WebView library for Java"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":api"))
    implementation(project(":internals"))
    compileOnly(libs.annotations)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.xpdy)
    testImplementation(project(":bridge"))
    testRuntimeOnly(project(":natives:${OperatingSystem.host().id}:${Architecture.host().id}"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    enabled = false
}

//

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("${project.group}", "jwebview", "${project.version}")
    pom {
        name.set("JWebView")
        description.set(project.description!!)
        inceptionYear.set("2025")
        url.set("https://github.com/WasabiThumb/jwebview")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("wasabithumb")
                name.set("Xavier Pedraza")
                url.set("https://github.com/WasabiThumb/")
            }
        }
        scm {
            url.set("https://github.com/WasabiThumb/jwebview/")
            connection.set("scm:git:git://github.com/WasabiThumb/jwebview.git")
        }
    }
}
