package pl.olafcio.limepublish

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

    void setProject(String value) {
        project = value
    }

    Dependence getDependence() {
        return dependence
    }
}
