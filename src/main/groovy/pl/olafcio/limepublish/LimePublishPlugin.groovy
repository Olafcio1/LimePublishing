package pl.olafcio.limepublish

import org.gradle.api.Plugin
import org.gradle.api.Project

class LimePublishPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        var extension = project.extensions.create("release", ReleaseTask)

        project.tasks.register("release") { task ->
            task.doLast {
                extension.release()
            }
        }
    }
}
