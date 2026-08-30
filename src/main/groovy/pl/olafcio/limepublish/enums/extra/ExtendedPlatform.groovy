package pl.olafcio.limepublish.enums.extra

import groovy.json.JsonSlurper
import pl.olafcio.limepublish.enums.Platform
import pl.olafcio.limepublish.enums.group.IPlatform
import pl.olafcio.limepublish.errors.MiscError

import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.zip.ZipFile

enum ExtendedPlatform implements IPlatform {
    AVOID {
        @Override
        Platform[] getActualPlatforms() {
            return [Platform.FABRIC, Platform.NEOFORGE, Platform.PAPER, Platform.PURPUR, Platform.SPONGE]
        }

        @Override
        boolean performChecks(Path path) {
            try (var zip = new ZipFile(path.toFile())) {
                var entry = zip.getEntry("avoid.mod.json")
                if (entry == null)
                    throw new MiscError("'avoid.mod.json' not present in input")

                try (var stream = zip.getInputStream(entry)) {
                    var raw = new String(stream.readAllBytes(), StandardCharsets.UTF_8)
                    var data = new JsonSlurper().parseText(raw)

                    var fields = [
                            'id': String,
                            'version': String,
                            'version-system': String,

                            'name': String,
                            'author': String,
                            'description': String,

                            'environment': { (String v) -> ["all", "client", "server"].contains(v.toLowerCase()) }
                    ]

                    for (var field : fields.entrySet()) {
                        var key = field.getKey()
                        var expect = field.getValue()

                        var value = data[key]

                        if (expect instanceof Class<?>) {
                            if (value == null)
                                throw new MiscError("'avoid.mod.json' doesn't have field '${key}': expected ${expect.getSimpleName()}")

                            if (!expect.isInstance(value))
                                throw new MiscError("'avoid.mod.json' has incorrect field '${key}': expected ${expect.getSimpleName()}, got ${value.getClass()}")
                        } else {
                            if (value != null && !expect(value))
                                throw new MiscError("'avoid.mod.json' has incorrect field '${key}'")
                        }
                    }
                }
            }

            return true
        }
    }
}
