package pl.olafcio.limepublish

import groovy.transform.PackageScope
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import pl.olafcio.limepublish.website.github.GitHubAPI
import pl.olafcio.limepublish.website.modrinth.ModrinthAPI

class LimePublishPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        var extension = project.extensions.create("release", ReleaseExtension)

        project.tasks.register("release", ReleaseTask) {
            it.config = extension
        }

        project.tasks.register("releaseModrinth", SimpleTask) {
            it.func = {
                if (extension.modrinth == null)
                    throw new NullPointerException("The 'modrinth' configuration in ReleaseTask is not set;\ntherefore, releaseModrinth cannot execute")

                new ModrinthAPI(ReleaseTask.argmap(extension)).release()
            }
        }

        project.tasks.register("releaseGitHub", SimpleTask) {
            it.func = {
                if (extension.github == null)
                    throw new NullPointerException("The 'github' configuration in ReleaseTask is not set;\ntherefore, releaseGitHub cannot execute")

                new GitHubAPI(ReleaseTask.argmap(extension)).release()
            }
        }
    }

    class SimpleTask extends DefaultTask {
        @Internal
        @PackageScope
        Runnable func

        @TaskAction
        void action() {
            func.run()
        }
    }
}
