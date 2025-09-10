import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    alias(libs.plugins.publish)
}

setupJava()

description = "Natives for JWebView (All)"

dependencies {
    implementation(project(":natives:linux"))
    implementation(project(":natives:windows"))
    implementation(project(":natives:macos"))
}

//

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("${project.group}", "jwebview-natives-all", "${project.version}")
    pom {
        name.set("JWebView Natives All")
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
