package pl.olafcio.limepublish

import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import pl.olafcio.limepublish.enums.Dependence
import pl.olafcio.limepublish.enums.Environment
import pl.olafcio.limepublish.enums.FileKind
import pl.olafcio.limepublish.enums.Platform
import pl.olafcio.limepublish.enums.VersionType
import pl.olafcio.limepublish.errors.MiscError
import pl.olafcio.limepublish.errors.RequestError
import pl.olafcio.limepublish.errors.ValueError
import pl.olafcio.limepublish.website.Modrinth

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class ReleaseTask extends DefaultTask {
    @TaskAction
    void release() {
        if (files == null)
            throw new MiscError("Missing files (did you forgot to add a 'files' section?)")

        final var boundary = UUID.randomUUID().toString()
        final var data = new ByteArrayOutputStream()

        var filenames = []
        var filetypes = []

        var filename_main = null

        int index = 0

        for (var fn : files) {
            if (fn.getValue() == null)
                continue

            var newFN = fn.getValue().getFileName().toString()

            filenames.add(newFN)

            data.write("--${boundary}\r\n"                                                                     .getBytes(StandardCharsets.UTF_8))
            data.write("Content-Disposition: form-data; name=\"datafile${index++}\"; filename=\"${newFN}\"\r\n".getBytes(StandardCharsets.UTF_8))
            data.write('Content-Type: application/octet-executable\r\n'                                        .getBytes(StandardCharsets.UTF_8))
            data.write('\r\n'                                                                                  .getBytes(StandardCharsets.UTF_8))

            if (fn.getKey() == FileKind.MAIN) {
                filename_main = newFN
            } else {
                filetypes.add(fn.getKey().name().toLowerCase().replace("_", "-"))
            }
        }

        if (filename_main == null)
            throw new MiscError("Missing main file (did you forgot to add a 'files' section?)")

        data.write("--${boundary}\r\n"                              .getBytes(StandardCharsets.UTF_8))
        data.write('Content-Disposition: form-data; name="data"\r\n'.getBytes(StandardCharsets.UTF_8))
        data.write('Content-Type: application/json\r\n'             .getBytes(StandardCharsets.UTF_8))
        data.write('\r\n'                                           .getBytes(StandardCharsets.UTF_8))

        data.writeBytes(JsonOutput.toJson([
                "name"          : this.name,
                "version_number": this.version_number,
                "changelog"     : this.changelog,
                "dependencies"  : this.dependencies.collect { [
                        "version_id"     : it.project,
                        "dependency_type": it.dependence.name().toLowerCase()
                ]},
                "game_versions" : this.minecraft,
                "version_type"  : this.version_type == VersionType.STABLE ? "release" : this.version_type.name().toLowerCase(),
                "loaders"       : this.platforms.collect { it.name().toLowerCase() },
                "project_id"    : this.modrinth.slug,
                "file_parts"    : [filenames],
                "primary_file"  : [filename_main],
                "environment"   : this.environment.name().toLowerCase(),
                "file_types"    : filetypes
        ]).getBytes(StandardCharsets.UTF_8))

        data.write("--${boundary}--\r\n".getBytes(StandardCharsets.UTF_8))

        try (var client = HttpClient.newHttpClient()) {
            var resp = client.send(HttpRequest.newBuilder()
                                                  .uri(URI.create("https://api.modrinth.com/v2/version"))
                                                  .method("POST", HttpRequest.BodyPublishers.ofByteArray(data.toByteArray()))
                                                  .header("Content-Type", "multipart/form-data; boundary=${boundary}")
                                                  .header("Authorization", this.modrinth.token.get())
                                                  .build(), HttpResponse.BodyHandlers.ofString())

            if (resp.statusCode() >= 400)
                throw new RequestError("Modrinth call returned status code ${resp.statusCode()}\n  > body: ${resp.body()}")
        } finally {
            data.close()
        }
    }

    ////////////
    /// NAME ///
    ////////////

    String name

    String getName() {
        return name
    }

    void setName(String value) {
        if (value.length() < 1 && value.length() > 64)
            throw new ValueError("A version name must have 1-64 characters")

        name = value
    }

    //////////////////////
    /// VERSION NUMBER ///
    //////////////////////

    String version_number

    String getVersion_number() {
        return version_number
    }

    void setVersion_number(String value) {
        if (value.length() < 1 && value.length() > 32)
            throw new ValueError("A version number must have 1-32 characters")

        var permitted = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM!@\$().\",`-_+"
        var index = 0

        for (var ch in value) {
            if (ch !in permitted)
                throw new ValueError("A version number must only contain the characters:\n  > '${permitted}'\n  > got '${ch}' at index ${index}")

            index++
        }

        version_number = value
    }

    ////////////////////
    /// VERSION TYPE ///
    ////////////////////

    VersionType versionType

    VersionType getVersion_type() {
        return versionType
    }

    void setVersion_type(VersionType value) {
        if (value == null)
            throw new ValueError("version_type cannot be 'null'")

        versionType = value
    }

    ///////////////////
    /// ENVIRONMENT ///
    ///////////////////

    Environment environment

    Environment getEnvironment() {
        return environment
    }

    void setEnvironment(Environment value) {
        if (value == null)
            throw new ValueError("environment cannot be 'null'")

        environment = value
    }

    /////////////////
    /// PLATFORMS ///
    /////////////////

    List<Platform> platforms

    List<Platform> getPlatforms() {
        return platforms
    }

    void getPlatforms(List<Platform> value) {
        if (value == null)
            throw new ValueError("platforms cannot be 'null'")
        else if (value == null)
            throw new ValueError("platforms cannot be empty")

        platforms = value
    }

    /////////////////
    /// MINECRAFT ///
    /////////////////

    List<String> minecraft

    List<String> getMinecraft() {
        return minecraft
    }

    void setMinecraft(List<String> value) {
        if (value == null)
            throw new ValueError("minecraft cannot be 'null'")
        else if (value == null)
            throw new ValueError("minecraft cannot be empty")

        minecraft = value
    }

    /////////////////
    /// CHANGELOG ///
    /////////////////

    String changelog

    String getChangelog() {
        return changelog
    }

    void setChangelog(String value) {
        if (value == null)
            throw new ValueError("changelog cannot be 'null'")

        changelog = value
    }

    ////////////////
    /// REQUIRES ///
    ////////////////

    List<Dependency> dependencies = new ArrayList<>()

    void requires(Closure<Dependency> configuration) {
        var dependency = new Dependency(Dependence.REQUIRED)

        configuration(dependency)

        if (dependency.getProject() == null)
            throw new ValueError("requires.project cannot be 'null'")
        else if (dependency.getProject().isEmpty())
            throw new ValueError("requires.project cannot be empty")

        dependencies.add(dependency)
    }

    /////////////
    /// FILES ///
    /////////////

    Map<FileKind, Path> files

    Map<FileKind, Path> getFiles() {
        return files
    }

    void files(Files configuration) {
        if (configuration == null)
            throw new ValueError("files cannot be 'null'")

        this.files = new HashMap<>()

        files.put(FileKind.MAIN, configuration.main.get())
        files.put(FileKind.REQUIRED_RESOURCE_PACK, configuration.requiredResourcePack.get())
        files.put(FileKind.OPTIONAL_RESOURCE_PACK, configuration.optionalResourcePack.get())
        files.put(FileKind.SOURCES_JAR, configuration.sourcesJar.get())
        files.put(FileKind.DEV_JAR, configuration.devJar.get())
        files.put(FileKind.JAVADOC_JAR, configuration.javadocJar.get())
        files.put(FileKind.UNKNOWN, configuration.unknownJar.get())
        files.put(FileKind.SIGNATURE, configuration.signature.get())
    }

    /////////////////////////
    /// WEBSITE: MODRINTH ///
    /////////////////////////

    Modrinth modrinth

    Modrinth getModrinth() {
        return modrinth
    }

    void modrinth(Modrinth value) {
        if (value == null)
            throw new ValueError("modrinth cannot be 'null'")

        modrinth = value
    }
}
