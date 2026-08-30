package pl.olafcio.limepublish

import org.gradle.api.Action
import org.gradle.work.DisableCachingByDefault
import pl.olafcio.limepublish.enums.*
import pl.olafcio.limepublish.enums.group.IPlatform
import pl.olafcio.limepublish.errors.ValueError
import pl.olafcio.limepublish.website.Modrinth

import java.nio.file.Path

@DisableCachingByDefault
class ReleaseExtension {
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
            if (!permitted.contains(ch))
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

    Set<IPlatform> platforms

    Set<IPlatform> getPlatforms() {
        return platforms
    }

    void setPlatforms(List<IPlatform> value) {
        if (value == null)
            throw new ValueError("platforms cannot be 'null'")
        else if (value.isEmpty())
            throw new ValueError("platforms cannot be empty")

        platforms = value.toSet()
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

    void requires(Action<Dependency> configuration) {
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

    void files(Action<Files> configuration) {
        if (configuration == null)
            throw new ValueError("files cannot be 'null'")

        var e = new Files()

        configuration(e)

        this.files = new HashMap<>()

        files.put(FileKind.MAIN, e.main)
        files.put(FileKind.REQUIRED_RESOURCE_PACK, e.requiredResourcePack)
        files.put(FileKind.OPTIONAL_RESOURCE_PACK, e.optionalResourcePack)
        files.put(FileKind.SOURCES_JAR, e.sourcesJar)
        files.put(FileKind.DEV_JAR, e.devJar)
        files.put(FileKind.JAVADOC_JAR, e.javadocJar)
        files.put(FileKind.UNKNOWN, e.unknownJar)
        files.put(FileKind.SIGNATURE, e.signature)
    }

    /////////////////////////
    /// WEBSITE: MODRINTH ///
    /////////////////////////

    Modrinth modrinth

    Modrinth getModrinth() {
        return modrinth
    }

    void modrinth(Action<Modrinth> configuration) {
        if (configuration == null)
            throw new ValueError("modrinth cannot be 'null'")

        modrinth = new Modrinth()

        configuration(modrinth)
    }
}
