package pl.olafcio.limepublish

import org.gradle.api.tasks.Input
import org.jetbrains.annotations.ApiStatus
import pl.olafcio.limepublish.enums.Dependence

class Dependency {
    String project
    final Dependence dependence

    @ApiStatus.Internal
    Dependency(Dependence dependence) {
        this.dependence = dependence
    }

    String getProject() {
        return project
    }

    @Input
    void project(String value) {
        project = value
    }

    Dependence getDependence() {
        return dependence
    }
}
