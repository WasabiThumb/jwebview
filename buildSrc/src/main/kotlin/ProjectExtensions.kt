import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.CoreJavadocOptions
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.language.jvm.tasks.ProcessResources

//

fun Project.setupJava(
    compile: JavaVersion = JavaVersion.VERSION_1_8,
    toolchain: JavaLanguageVersion = JavaLanguageVersion.of(17)
) {
    this.extensions.findByType(JavaPluginExtension::class.java)?.run {
        this.sourceCompatibility = compile
        this.targetCompatibility = compile
        this.toolchain.languageVersion.set(toolchain)
    }
    this.tasks.withType(JavaCompile::class.java) {
        it.options.encoding = "UTF-8"
    }
    this.tasks.withType(Javadoc::class.java) {
        it.options.encoding = Charsets.UTF_8.name()
        (it.options as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
    }
}

fun Project.buildOrDemandNative(
    os: OperatingSystem,
    arch: Architecture,
    libName: String,
    libPath: String = "natives/${os.id}/${arch.id}"
) {
    if (OperatingSystem.host() == os && Architecture.host() == arch) {
        val nativesProject = project(":natives")
        val nativesBuild = nativesProject.layout.buildDirectory
        val nativesBinary = nativesBuild.file("cmake/${libName}")

        tasks.withType(ProcessResources::class.java) { task ->
            task.dependsOn(nativesProject.tasks.named("assemble"))
            task.from(nativesBinary) { spec ->
                spec.into(libPath)
            }
        }
    } else {
        val file = this.layout.projectDirectory.dir("src/main/resources")
            .dir(libPath)
            .file(libName)

        tasks.named("assemble") { task ->
            task.doFirst {
                if (!file.asFile.exists()) {
                    throw Error("Foreign library \"${libName}\" not found")
                }
            }
        }
    }
}
