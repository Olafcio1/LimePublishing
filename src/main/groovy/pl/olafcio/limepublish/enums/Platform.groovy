package pl.olafcio.limepublish.enums

import pl.olafcio.limepublish.enums.group.IPlatform

enum Platform implements IPlatform {
    // Mods
    FABRIC,
    FORGE,
    NEOFORGE,
    QUILT,
    LITELOADER,
    RIFT,
    ORNITHE,
    NILLOADER,
    LEGACY_FABIC,
    BTA,
    BABRIC,
    RISUGAMIS_MODLOADER,
    JAVA_AGENT,

    // Plugins
    PAPER,
    PURPUR,
    SPIGOT,
    BUKKIT,
    SPONGE,
    FOLIA,
    BUNGEECORD,
    VELOCITY,
    WATERFALL,

    // Packs
    MINECRAFT,

    // Shaders
    OPTIFINE,
    IRIS,
    CANVAS,
    VANILLA_SHADER
}
