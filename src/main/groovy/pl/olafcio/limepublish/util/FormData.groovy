package pl.olafcio.limepublish.util

import java.nio.charset.StandardCharsets

class FormData implements AutoCloseable {
    final var boundary = UUID.randomUUID().toString()
    final var data = new ByteArrayOutputStream()

    private static final CARRIAGE
            = '\r\n'.getBytes(StandardCharsets.UTF_8)

    void append(String name, byte[] data, List<String> headers) {
        this.data.write("--${boundary}\r\n"                                   .getBytes(StandardCharsets.UTF_8))
        this.data.write("Content-Disposition: form-data; name=\"${name}\"\r\n".getBytes(StandardCharsets.UTF_8))
        this.data.write(String.join("\r\n", headers)                  .getBytes(StandardCharsets.UTF_8))

        this.data.write(CARRIAGE)
        this.data.write(CARRIAGE)

        this.data.writeBytes(data)

        this.data.write(CARRIAGE)
    }

    void append(String name, String filename, byte[] data, List<String> headers) {
        this.data.write("--${boundary}\r\n"                                                             .getBytes(StandardCharsets.UTF_8))
        this.data.write("Content-Disposition: form-data; name=\"${name}\"; filename=\"${filename}\"\r\n".getBytes(StandardCharsets.UTF_8))
        this.data.write(String.join("\r\n", headers)                                            .getBytes(StandardCharsets.UTF_8))

        this.data.write(CARRIAGE)
        this.data.write(CARRIAGE)

        this.data.writeBytes(data)

        this.data.write(CARRIAGE)
    }

    void end() {
        this.data.write("--${boundary}--\r\n".getBytes(StandardCharsets.UTF_8))
    }

    byte[] toByteArray() {
        return data.toByteArray()
    }

    @Override
    void close() throws Exception {
        data.close()
    }
}
