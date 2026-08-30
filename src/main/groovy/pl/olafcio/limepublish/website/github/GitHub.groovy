package pl.olafcio.limepublish.website.github

import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input

class GitHub {
    String nameTag
    String nameRelease
    String branch
    String repository

    Provider<String> token

    @Input
    void nameTag(String value) {
        this.nameTag = value
    }

    @Input
    void nameRelease(String value) {
        this.nameRelease = value
    }

    @Input
    void branch(String value) {
        this.branch = value
    }

    @Input
    void repository(String value) {
        this.repository = value
    }

    @Input
    void token(Provider<String> value) {
        this.token = value
    }
}
