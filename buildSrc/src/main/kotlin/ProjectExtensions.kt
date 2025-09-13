import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.CoreJavadocOptions
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.FileInputStream
import java.io.FileOutputStream

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
    val destDir = this.layout.projectDirectory.dir("src/main/resources").dir(libPath)
    val destFile = destDir.file(libName)

    if (OperatingSystem.host() == os && Architecture.host() == arch) {
        val nativesProject = project(":natives")
        val nativesBuild = nativesProject.layout.buildDirectory
        val srcFile = if (os == OperatingSystem.WINDOWS) {
            nativesBuild.file("cmake/Release/${libName}")
        } else {
            nativesBuild.file("cmake/${libName}")
        }

        val copier = tasks.register("copyNatives") { task ->
            task.dependsOn(nativesProject.tasks.named("assemble"))
            task.doFirst {
                val destDirFile = destDir.asFile
                if (!destDirFile.isDirectory && !destDirFile.mkdirs()) {
                    throw Error("Failed to create directory: $destDirFile")
                }

                FileInputStream(srcFile.get().asFile).use { i ->
                    FileOutputStream(destFile.asFile, false).use { o ->
                        val buf = ByteArray(4096)
                        var n: Int
                        while (true) {
                            n = i.read(buf)
                            if (n == -1) break
                            o.write(buf, 0, n)
                        }
                        o.flush()
                    }
                }
            }
        }

        tasks.withType(ProcessResources::class.java) { task ->
            task.dependsOn(copier)
        }
    } else {
        tasks.named("assemble") { task ->
            task.doFirst {
                if (!destFile.asFile.exists()) {
                    throw Error("Foreign library \"${libName}\" not found")
                }
            }
        }
    }
}
