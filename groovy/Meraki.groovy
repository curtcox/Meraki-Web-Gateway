class Meraki {

    final String path
    final params
    final MerakiHttp http

    Meraki(path, params, apiKey) {
        System.out.println("$path $params $apiKey")
        this.path   = new PathScrubber(path,apiKey).scrubbed()
        this.params = params
        this.http   = new MerakiHttp(apiKey)
    }

    String deletePath() {
        path.substring(0,path.indexOf('/delete'))
    }

    String verb() {
        if (matching('bind', 'unbind', 'claim')) {
            return 'POST'
        }
        if (matching('delete')) {
            return 'DELETE'
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

    String body(result) {
        return result.substring(0,start_of_status(result))
    }

    def http_code(result) {
        def code = result.substring(start_of_status(result))
        return "{ \"http_code\" : \"$code\"}"
    }

    def start_of_status(result) {
        return result.length() - status_length(result)
    }

    int status_length(result) {
        return 0
    }

    String command() {
        if (verb() == 'GET') {
            return "GET $path"
        }

        if (verb() == 'DELETE') {
            return "DELETE ${deletePath()}"
        }

        return "POST $path $params"
    }

    String exec() {
        if (verb() == 'GET') {
            return http.get(path)
        }

        if (verb() == 'DELETE') {
            return http.delete(deletePath())
        }

        return http.post(path,params)
    }

}
