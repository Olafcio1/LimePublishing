package pl.olafcio.limepublish

import org.gradle.api.Plugin
import org.gradle.api.Project

class LimePublishPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.tasks.register("release", ReleaseTask)
    }
}
