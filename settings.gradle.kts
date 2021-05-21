/*
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */

rootProject.name = "JumpIfZero public externals"

include(":shiftwalker")
include(":socketshootingstars")
include(":socketprivate")
include(":menuentrymodifier")
include("attackrange")

for (project in rootProject.children) {
    project.apply {
        projectDir = file(name)
        buildFileName = "$name.gradle.kts"

        require(projectDir.isDirectory) { "Project '${project.path} must have a $projectDir directory" }
        require(buildFile.isFile) { "Project '${project.path} must have a $buildFile build script" }
    }
}