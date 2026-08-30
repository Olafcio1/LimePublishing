package pl.olafcio.limepublish.enums.extra.extended_platform

import groovy.transform.PackageScope
import pl.olafcio.limepublish.enums.Platform

import java.nio.file.Path

@PackageScope
class ExBungeecord {
    static final INSTANCE = new ExtendedPlatform() {
        @Override
        Platform[] getActualPlatforms() {
            return [Platform.BUNGEECORD, Platform.WATERFALL]
        }

        @Override
        boolean performChecks(Path path) {
            return false
        }
    }
}
