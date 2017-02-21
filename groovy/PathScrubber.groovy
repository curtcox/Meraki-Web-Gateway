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
        return clean
    }

    def deleteDoubleSlashes(path) {
        return path.replaceAll('//', '/')
    }

    def deleteInitialSlash(path) {
        return path.startsWith('/')
        ? path.substring(1, path.length())
        : path
    }

    def deleteFinalQuestionMark(path) {
        return path.endsWith('?')
        ? path.substring(0, path.length() - 1)
        : path
    }

    def deleteApiKey(path) {
        return delete(path, "apiKey=$apiKey")
    }

    def delete(path, fragment) {
        return path.replaceAll(fragment, '')
    }

}
