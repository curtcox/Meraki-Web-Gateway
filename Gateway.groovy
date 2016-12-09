import groovy.json.*

class Gateway {

    final request
    final meraki
    final linker

    Gateway(request, apiKey) {
        this.request = request
        def command = "${request.pathInfo}?${request.queryString}"
        def params = Linker.jsonParamsFrom(request)
        meraki = new Meraki(command, params, apiKey)
        linker = new Linker(request)
    }

    def contentType() {
        return (infoRequest() || stateChange()) ? 'application/json' : 'text/html'
    }

    def response() {
        if (stateChangeSetup()) {
            return linker.inputForParams()
        } else {
            return transformedMerakiResponse()
        }
    }

    def transformedMerakiResponse() {
        return JsonOutput.toJson(linker.transform(meraki.json(), meraki.command()))
    }

    def infoRequest() {
        return request.method == 'GET' && meraki.verb() == 'GET'
    }

    def stateChangeSetup() {
        return request.method == 'GET' && meraki.verb() != 'GET'
    }

    def stateChange() {
        return request.method != 'GET' && meraki.verb() != 'GET'
    }

}
