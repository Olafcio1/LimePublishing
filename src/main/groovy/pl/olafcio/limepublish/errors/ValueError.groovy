package pl.olafcio.limepublish.errors

class ValueError extends RuntimeException {
    ValueError(String message) {
        super(message)
    }
}
