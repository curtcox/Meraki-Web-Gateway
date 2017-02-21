import javax.servlet.http.*

class Gateway {

    final String method
    final Meraki meraki
    final Linker linker
    final MimeTypeComputer typeComputer

    static Gateway of(request, apiKey) {
        def method = request.method
        def command = "${request.pathInfo}?${request.queryString}"
        def params = Json.paramsFrom(request)
        def meraki = new Meraki(command, params, apiKey)
        def linker = new Linker(request)
        def typeComputer = new MimeTypeComputer()
        new Gateway(method,meraki,linker,typeComputer)
    }

    Gateway(method,meraki,linker,typeComputer) {
        this.method = method
        this.meraki = meraki
        this.linker = linker
        this.typeComputer = typeComputer
    }

    def contentType() {
        (infoRequest() || stateChangeRequest()) ? 'application/json' : 'text/html'
    }

    def response() {
        if (stateChangeSetupRequest()) {
            return linker.inputForParams()
        } else {
            return transformedMerakiResponse()
        }
    }

    def transformedMerakiResponse() {
        Json.from(linker.transform(meraki.json(), meraki.command()))
    }

    def infoRequest() {
        method == 'GET' && meraki.verb() == 'GET'
    }

    def stateChangeSetupRequest() {
        method == 'GET' && meraki.verb() != 'GET'
    }

    def stateChangeRequest() {
        method != 'GET' && meraki.verb() != 'GET'
    }

}
