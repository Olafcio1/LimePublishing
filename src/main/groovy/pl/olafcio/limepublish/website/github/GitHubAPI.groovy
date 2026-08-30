package pl.olafcio.limepublish.website.github

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.Internal
import pl.olafcio.limepublish.ReleaseExtension
import pl.olafcio.limepublish.util.FormData

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Path

@Internal
class GitHubAPI {
    private ReleaseExtension config
    private FormData         data

    private HashMap<String, Path> fileMap
    private String                filename_main

    private List<String>      filenames
    private Map<String, Path> filetypes

    private HttpClient client

    GitHubAPI(object) {
        this.config = object['config'] as ReleaseExtension
        this.data   = object['data']   as FormData

        this.fileMap       = object['fileMap']       as HashMap<String, Path>
        this.filename_main = object['filename_main'] as String

        this.filenames = object['filenames'] as List<String>
        this.filetypes = object['filetypes'] as Map<String, Path>
    }

    void release() {
        try (var client = HttpClient.newHttpClient()) {
            var hashproc = new ProcessBuilder().command("git", "log", "origin/${this.config.github.branch}", "-1", "--format=%H")
                                                        .start()

            hashproc.waitFor()

            this.client = client

            createTag(
                    tag: this.config.github.nameTag,
                    hash: new String(hashproc.in.readAllBytes(), StandardCharsets.UTF_8),
                    message: this.config.changelog
            )

            var id = createRelease(
                    tag: this.config.github.nameTag,
                    branch: this.config.github.branch,
                    name: this.config.github.nameRelease,
                    message: this.config.changelog
            )

            for (var entry : fileMap)
                uploadRelease(
                        name: entry.getKey(),
                        file: entry.getValue(),
                        release: id
                )
        } finally {
            data.close()
        }
    }

    @Internal
    private void createTag(String tag, String hash, String message) {
        var data2 = JsonOutput.toJson([
                "tag": tag,
                "object": hash,
                "type": "commit",
                "message": message
        ])

        client.send(HttpRequest.newBuilder()
                               .method("POST", HttpRequest.BodyPublishers.ofString(data2, StandardCharsets.UTF_8))
                               .uri(URI.create("https://api.github.com/repos/${this.config.github.repository}/git/tags"))
                               .header("Accept", "application/vnd.github+json")
                               .header("Authorization", this.config.github.token.get())
                               .header("X-GitHub-Api-Version", "2026-03-10")
                               .build(), HttpResponse.BodyHandlers.discarding())
    }

    @Internal
    private void createTag(object) {
        createTag(object['tag'] as String, object['hash'] as String, object['message'] as String)
    }

    @Internal
    private String createRelease(String tag, String branch, String name, String message, boolean make_latest = true) {
        var data1 = JsonOutput.toJson([
                "tag_name": tag,
                "target_commitish": branch,
                "name": name,
                "body": message,
                "make_latest": "${make_latest}"
        ])

        var respraw = client.send(HttpRequest.newBuilder()
                                             .method("POST", HttpRequest.BodyPublishers.ofString(data1, StandardCharsets.UTF_8))
                                             .uri(URI.create("https://api.github.com/repos/${this.config.github.repository}/releases"))
                                             .header("Accept", "application/vnd.github+json")
                                             .header("Authorization", this.config.github.token.get())
                                             .header("X-GitHub-Api-Version", "2026-03-10")
                                             .build(), HttpResponse.BodyHandlers.ofByteArray()).body()

        var resp = new JsonSlurper().parseText(
                new String(respraw, StandardCharsets.UTF_8)
        )

        return resp['id']
    }

    @Internal
    private String createRelease(object) {
        return createRelease((String)object['tag'], (String)object['branch'], (String)object['name'], (String)object['message'], "make_latest" in object ? (boolean)object['make_latest'] : true)
    }

    @Internal
    private void uploadRelease(String name, Path file, String release) {
        client.send(HttpRequest.newBuilder()
                               .method("POST", HttpRequest.BodyPublishers.ofFile(file))
                               .uri(URI.create("https://uploads.github.com/repos/${this.config.github.repository}/releases/${release}/assets?name=${name}"))
                               .header("Accept", "application/vnd.github+json")
                               .header("Authorization", this.config.github.token.get())
                               .header("X-GitHub-Api-Version", "2026-03-10")
                               .header("Content-Type", "application/octet-stram")
                               .build(), HttpResponse.BodyHandlers.discarding())
    }

    @Internal
    private void uploadRelease(object) {
        uploadRelease((String)object['name'], (Path)object['file'], (String)object['release'])
    }
}
