class PathScrubber {

    final path
    final apiKey

    PathScrubber(path, apiKey) {
        this.path = path
        this.apiKey = apiKey
    }

    def scrubbed() {
        def clean = path
        clean = deleteDoubleSlashes(path)
        clean = deleteInitialSlash(path)
        clean = delete(clean, "\\?null")
        clean = deleteApiKey(clean)
        clean = deleteFinalQuestionMark(clean)
        clean
    }

    def deleteDoubleSlashes(path) {
        path.replaceAll('//', '/')
    }

    def deleteInitialSlash(path) {
        path.startsWith('/')
            ? path.substring(1, path.length())
            : path
    }

    def deleteFinalQuestionMark(path) {
        path.endsWith('?')
            ? path.substring(0, path.length() - 1)
            : path
    }

    def deleteApiKey(path) {
        delete(path, "apiKey=$apiKey")
    }

    def delete(path, fragment) {
        path.replaceAll(fragment, '')
    }

}
