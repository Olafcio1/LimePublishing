package pl.olafcio.limepublish.enums.group

import groovy.transform.Internal
import pl.olafcio.limepublish.enums.Platform
import pl.olafcio.limepublish.enums.extra.ExtendedPlatform

import java.nio.file.Path

sealed interface IPlatform permits Platform, ExtendedPlatform {
    @Internal
    default Platform[] getActualPlatforms() {
        return (Platform) this
    }

    @Internal
    default boolean performChecks(Path path) {
        return false
    }
}
