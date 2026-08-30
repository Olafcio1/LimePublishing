package pl.olafcio.limepublish.enums.extra.extended_platform

import groovy.transform.PackageScope
import pl.olafcio.limepublish.enums.group.IPlatform

abstract non-sealed class ExtendedPlatform implements IPlatform {
    @PackageScope
    ExtendedPlatform() {}

    public static final ExtendedPlatform AVOID = ExAvoid.INSTANCE
    public static final ExtendedPlatform PAPER_AND_FORKS = ExPaper.INSTANCE
    public static final ExtendedPlatform SPIGOT_AND_FORKS = ExSpigot.INSTANCE
    public static final ExtendedPlatform FABRIC_AND_FORKS = ExFabric.INSTANCE
    public static final ExtendedPlatform BUNGEECORD_AND_FORKS = ExBungeecord.INSTANCE
}
