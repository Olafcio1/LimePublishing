package pl.olafcio.limepublish.errors

class RequestError extends RuntimeException {
    RequestError(String message) {
        super(message)
    }
}
