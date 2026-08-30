package pl.olafcio.limepublish.website.modrinth

import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input

class Modrinth {
    String slug
    Provider<String> token

    @Input
    void slug(String value) {
        this.slug = value;
    }

    @Input
    void token(Provider<String> value) {
        this.token = value;
    }
}
