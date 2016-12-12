import groovy.json.*

class Meraki {

    final path
    final apiKey
    final params

    Meraki(path, params, apiKey) {
        this.path = new PathScrubber(path,apiKey).scrubbed()
        this.params = params
        this.apiKey = apiKey
    }

    def command() {
        if (verb() == 'GET') {
            return "${verb()} $path $apiKey"
        }
        return "${verb()} $path $apiKey $params"
    }

    def verb() {
        if (matching('bind', 'unbind', 'claim')) {
            return 'POST'
        }
        return 'GET'
    }

    def matching(... keys) {
        for (key in keys) {
            if (path.endsWith("/$key")) {
                return true
            }
        }
        return false
    }

    def json() {
        def result = exec()
        try {
            return new JsonSlurper().parseText(result)
        } catch (e) {
            def errorPage = result.replaceAll("page", "page ($path)")
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
