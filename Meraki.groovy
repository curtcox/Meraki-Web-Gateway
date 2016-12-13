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
            return toJson(result)
        } catch (e) {
            def errorPage = result.replaceAll("page", "page ($path)")
            throw new RuntimeException(errorPage, e)
        }
    }

    def toJson(result) {
        return Json.parse(body_with_status_inserted(body(result),http_code(result)))
    }

    def body_with_status_inserted(body,status) {
        return body.replaceFirst('\\[',"[$status,")
    }

    def body(result) {
        return result.substring(0,start_of_status(result))
    }

    def http_code(result) {
        def code = result.substring(start_of_status(result))
        return "{ \"http_code\" : \"$code\"}"
    }

    def start_of_status(result) {
        return result.length() - status_length(result)
    }

    def status_length(result) {
        return 0
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
