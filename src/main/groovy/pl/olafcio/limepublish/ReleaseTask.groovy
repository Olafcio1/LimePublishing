package pl.olafcio.limepublish

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import pl.olafcio.limepublish.enums.FileKind
import pl.olafcio.limepublish.enums.Platform
import pl.olafcio.limepublish.enums.VersionType
import pl.olafcio.limepublish.enums.extra.ExtendedPlatform
import pl.olafcio.limepublish.errors.MiscError
import pl.olafcio.limepublish.errors.RequestError
import pl.olafcio.limepublish.util.FormData

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class ReleaseTask extends DefaultTask {
    @Internal
    ReleaseExtension config

    @TaskAction
    void release() {
        if (config.files == null)
            throw new MiscError("Missing files (did you forgot to add a 'files' section?)")

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
            throw new MiscError("Missing main file (did you forgot to add a 'files' section?)")

        var loaders
        var extended

        if (this.config.platforms.any { it instanceof ExtendedPlatform }) {
            for (var platform : this.config.platforms)
                if (platform instanceof ExtendedPlatform)
                    platform.performChecks(fileMap.get(filename_main))

            loaders = [Platform.JAVA_AGENT.name().toLowerCase().replace("_", "-")]
            extended = true
        } else {
            loaders = this.config.platforms.collect { it.getActualPlatforms() }
                                           .flatten()
                                           .collect { (Platform) it }
                                           .collect { it.name().toLowerCase().replace("_", "-") }

            extended = false
        }

        var req

        data.append("data", JsonOutput.toJson(req = [
                "name"          : this.config.name,
                "version_number": this.config.version_number,
                "changelog"     : this.config.changelog,
                "dependencies"  : this.config.dependencies.collect { [
                        "project_id"     : lookupProject(it.project),
                        "dependency_type": it.dependence.name().toLowerCase()
                ]},
                "game_versions" : this.config.minecraft,
                "version_type"  : this.config.version_type == VersionType.STABLE ? "release" : this.config.version_type.name().toLowerCase(),
                "loaders"       : loaders,
                "project_id"    : lookupProject(this.config.modrinth.slug),
                "file_parts"    : filenames,
                "primary_file"  : filename_main,
                "environment"   : this.config.environment.name().toLowerCase(),
                "file_types"    : filetypes,
                "featured"      : false
        ]).getBytes(StandardCharsets.UTF_8), [
                "Content-Type: application/json"
        ])

        int index = 0

        for (var entry : fileMap) {
            data.append("datafile${index++}", entry.getKey(), java.nio.file.Files.readAllBytes(entry.getValue()), [
                    "Content-Type: application/octet-executable"
            ])
        }

        data.end()

        String output

        try (var client = HttpClient.newHttpClient()) {
            var resp = client.send(HttpRequest.newBuilder()
                                                  .uri(URI.create("https://api.modrinth.com/v2/version"))
                                                  .method("POST", HttpRequest.BodyPublishers.ofByteArray(data.toByteArray()))
                                                  .header("Content-Type", "multipart/form-data; boundary=${data.boundary}")
                                                  .header("Authorization", this.config.modrinth.token.get())
                                                  .header("User-Agent", "Olafcio1/LimePublishing")
                                                  .build(), HttpResponse.BodyHandlers.ofString())

            if (resp.statusCode() >= 400)
                throw new RequestError("Modrinth call returned status code ${resp.statusCode()}\n  > body: ${resp.body()}")

            output = resp.body()
        } finally {
            data.close()
        }

        if (extended) {
            //noinspection GroovyVariableNotAssigned
            req.remove("primary_file")
            req.remove("project_id")
            req.remove("file_parts")
            req.remove("featured")

            var json = new JsonSlurper().parseText(output)
            var data2 = JsonOutput.toJson(req + [
                    'loaders': this.config.platforms.collect { it.getActualPlatforms() }
                                                    .flatten()
                                                    .collect { (Platform) it }
                                                    .collect { it.name().toLowerCase() },
                    'file_types': []
            ])

            try (var client = HttpClient.newHttpClient()) {
                var resp = client.send(HttpRequest.newBuilder()
                                                                       .uri(URI.create("https://api.modrinth.com/v3/version/${json['id']}"))
                                                                       .method("PATCH", HttpRequest.BodyPublishers.ofString(data2, StandardCharsets.UTF_8))
                                                                       .header("Content-Type", "application/json")
                                                                       .header("Authorization", this.config.modrinth.token.get())
                                                                       .header("User-Agent", "Olafcio1/LimePublishing")
                                                                       .build(), HttpResponse.BodyHandlers.ofString())

                if (resp.statusCode() >= 400)
                    throw new RequestError("Modrinth extended call returned status code ${resp.statusCode()}\n  > body: '${resp.body()}'")
            }
        }
    }

    @Internal
    private String lookupProject(String reference) {
        try (var client = HttpClient.newHttpClient()) {
            var resp = client.send(HttpRequest.newBuilder()
                                                  .uri(URI.create("https://api.modrinth.com/v3/project/${reference}"))
                                                  .method("GET", HttpRequest.BodyPublishers.noBody())
                                                  .header("Authorization", this.config.modrinth.token.get())
                                                  .header("User-Agent", "Olafcio1/LimePublishing")
                                                  .build(), HttpResponse.BodyHandlers.ofString())

            if (resp.statusCode() >= 400)
                throw new RequestError("Modrinth project lookup call returned status code ${resp.statusCode()}\n  > body: '${resp.body()}'")

            return new JsonSlurper().parseText(resp.body())['id']
        }
    }
}
