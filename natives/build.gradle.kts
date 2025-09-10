
plugins {
    alias(libs.plugins.cmake)
}

cmake {
    sourceFolder = File(projectDir, "source")
    buildSharedLibs = true
    buildConfig = "Release"
}

tasks.clean {
    dependsOn(tasks.getByName("cmakeClean"))
}

tasks.assemble {
    dependsOn(tasks.getByName("cmakeBuild"))
}
