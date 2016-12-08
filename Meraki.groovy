import groovy.json.*

class Meraki {

    final path
    final apiKey
    final params

    Meraki(path, params, apiKey) {
        this.path = path.replaceAll('//', '/')
        this.params = params
        this.apiKey = apiKey
    }

    def command() {
        if (verb() == 'GET') {
            return "${verb()} ${page()} $apiKey"
        }
        return "${verb()} ${page()} $apiKey ${params}"
    }

    def verb() {
        if (matching('bind', 'unbind')) {
            return 'POST'
        }
        return 'GET'
    }

    def matching(... keys) {
        for (key in keys) {
            if (path.contains(key)) {
                return true
            }
        }
        return false
    }

    def page() {
        return "${scrubbed(path)}"
    }

    def scrubbed(path) {
        path = deleteInitialSlash(path)
        path = delete(path, "\\?null")
        path = deleteApiKey(path)
        path = deleteFinalQuestionMark(path)
        return path
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

    def json() {
        def result = exec()
        try {
            return new JsonSlurper().parseText(result)
        } catch (e) {
            def errorPage = result.replaceAll("page", "page (${page()})")
            throw new RuntimeException(errorPage, e)
        }
    }

    def exec() {
        def command = command()
        System.err.println("command=" + command)
        def withPath = "scripts/$command"
        def result = withPath.execute()
        def text = result.text
        return text
    }

}
