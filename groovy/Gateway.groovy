class Gateway {

    final request
    final Meraki meraki
    final Linker linker

    Gateway(request, apiKey) {
        this.request = request
        def command = "${request.pathInfo}?${request.queryString}"
        def params = Linker.jsonParamsFrom(request)
        meraki = new Meraki(command, params, apiKey)
        linker = new Linker(request)
    }

    def contentType() {
        return (infoRequest() || stateChangeRequest()) ? 'application/json' : 'text/html'
    }

    def response() {
        if (stateChangeSetupRequest()) {
            return linker.inputForParams()
        } else {
            return transformedMerakiResponse()
        }
    }

    def transformedMerakiResponse() {
        return Json.from(linker.transform(meraki.json(), meraki.command()))
    }

    def infoRequest() {
        return request.method == 'GET' && meraki.verb() == 'GET'
    }

    def stateChangeSetupRequest() {
        return request.method == 'GET' && meraki.verb() != 'GET'
    }

    def stateChangeRequest() {
        return request.method != 'GET' && meraki.verb() != 'GET'
    }

}
