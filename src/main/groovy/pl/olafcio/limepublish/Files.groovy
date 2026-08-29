package pl.olafcio.limepublish

import org.gradle.api.tasks.Input

import java.nio.file.Path

class Files {
    Path main
    Path requiredResourcePack
    Path optionalResourcePack
    Path sourcesJar
    Path devJar
    Path javadocJar
    Path unknownJar
    Path signature

    @Input
    Path main(Path path) {
        this.main = path
    }

    @Input
    Path requiredResourcePack(Path path) {
        this.requiredResourcePack = path
    }

    @Input
    Path optionalResourcePack(Path path) {
        this.optionalResourcePack = path
    }

    @Input
    Path sourcesJar(Path path) {
        this.sourcesJar = path
    }

    @Input
    Path devJar(Path path) {
        this.devJar = path
    }

    @Input
    Path unknownJar(Path path) {
        this.unknownJar = path
    }

    @Input
    Path javadocJar(Path path) {
        this.javadocJar = path
    }

    @Input
    Path signature(Path path) {
        this.signature = path
    }
}
