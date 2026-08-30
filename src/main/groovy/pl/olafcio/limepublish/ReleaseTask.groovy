package pl.olafcio.limepublish

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import pl.olafcio.limepublish.enums.FileKind
import pl.olafcio.limepublish.errors.MiscError
import pl.olafcio.limepublish.util.FormData
import pl.olafcio.limepublish.website.github.GitHubAPI
import pl.olafcio.limepublish.website.modrinth.ModrinthAPI

import java.nio.file.Path

class ReleaseTask extends DefaultTask {
    @Internal
    ReleaseExtension config

    @TaskAction
    void release() {
        if (config.files == null)
            throw new MiscError("Missing files (did you forget to add a 'files' section?)")

        var data    = new FormData()
        var fileMap = new HashMap<String, Path>()

        var filenames = []
        var filetypes = Map.of()

        var filename_main = null

        for (var fn : config.files) {
            if (fn.getValue() == null)
                continue

            var newFN = fn.getValue().getFileName().toString()

            filenames.add(newFN)

            fileMap.put(newFN, fn.getValue())

            if (fn.getKey() == FileKind.MAIN) {
                filename_main = newFN
            } else {
                filetypes.put(newFN, fn.getKey().name().toLowerCase().replace("_", "-"))
            }
        }

        if (filename_main == null)
            throw new MiscError("Missing main file (did you forget to add a 'files' section?)")

        if (config.modrinth != null) {
            new ModrinthAPI(config: config, data: data,
                            fileMap: fileMap, filename_main: filename_main,
                            filenames: filenames, filetypes: filetypes).release()
        }

        if (config.github != null) {
            new GitHubAPI(config: config, data: data,
                          fileMap: fileMap, filename_main: filename_main,
                          filenames: filenames, filetypes: filetypes).release()
        }
    }
}
