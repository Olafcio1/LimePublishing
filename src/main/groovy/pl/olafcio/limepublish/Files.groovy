package pl.olafcio.limepublish

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

import java.nio.file.Path

abstract class Files {
    @Input
    abstract Property<Path> getMain()

    @Input
    Property<Path> getRequiredResourcePack() {
        return null
    }

    @Input
    Property<Path> getOptionalResourcePack() {
        return null
    }

    @Input
    Property<Path> getSourcesJar() {
        return null
    }

    @Input
    Property<Path> getDevJar() {
        return null
    }

    @Input
    Property<Path> getJavadocJar() {
        return null
    }

    @Input
    Property<Path> getUnknownJar() {
        return null
    }

    @Input
    Property<Path> getSignature() {
        return null
    }
}
