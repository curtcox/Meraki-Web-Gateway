package gateway

import json.*
import meraki.*

class Gateway {

    final String method
    final Meraki meraki
    final Linker linker
    final Json json

    static Gateway of(request, apiKey) {
        def method = request.method
        def command = "${request.pathInfo}?${request.queryString}"
        def json   = new Json()
        def params = json.paramsFrom(request)
        def meraki = new Meraki(command, params, apiKey)
        def linker = Linker.of(request)
        new Gateway(method,meraki,linker,json)
    }

    Gateway(method,meraki,linker,json) {
        this.method = method
        this.meraki = meraki
        this.linker = linker
        this.json = json
    }

    def contentType() {
        stateChangeSetupRequest() ? 'text/html' : 'application/json'
    }

    def response() {
        stateChangeSetupRequest()
            ? linker.inputForParams()
            : transformedMerakiResponse()
    }

    def transformedMerakiResponse() {
        json.from(linker.transform(meraki.parsedJson(), meraki.command()))
    }

    def stateChangeSetupRequest() {
        method == 'GET' && meraki.verb() != 'GET'
    }
}
