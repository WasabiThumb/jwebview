import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    alias(libs.plugins.publish)
}

setupJava()

description = "Natives for JWebView (Linux AMD64)"

buildOrDemandNative(
    OperatingSystem.LINUX,
    Architecture.AMD64,
    "libjwebview.so"
)

//

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("${project.group}", "jwebview-natives-linux-amd64", "${project.version}")
    pom {
        name.set("JWebView Natives Linux AMD64")
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
