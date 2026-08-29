package pl.olafcio.limepublish.website

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input

abstract class Modrinth {
    @Input
    abstract Property<String> getSlug()

    @Input
    abstract Provider<String> getToken()
}
