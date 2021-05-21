/* 
 * Copyright (c) 2021 JumpIfZero <https://github.com/JumpIfZero>
 */
version = "0.0.1"

project.extra["PluginName"] = "Attack Range"
project.extra["PluginDescription"] = "Visually display weapon attack range"
project.extra["ProjectSupportUrl"] = "https://github.com/JumpIfZero"

tasks {
    jar {
        manifest {
            attributes(mapOf(
                    "Plugin-Version" to project.version,
                    "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                    "Plugin-Provider" to project.extra["PluginProvider"],
                    "Plugin-Description" to project.extra["PluginDescription"],
                    "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}
