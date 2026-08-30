package pl.olafcio.limepublish.enums.extra.extended_platform

import groovy.transform.PackageScope
import pl.olafcio.limepublish.enums.Platform

import java.nio.file.Path

@PackageScope
class ExSpigot {
    static final INSTANCE = new ExtendedPlatform() {
        @Override
        Platform[] getActualPlatforms() {
            return [Platform.SPIGOT, Platform.PAPER, Platform.PURPUR]
        }

        @Override
        boolean performChecks(Path path) {
            return false
        }
    }
}
