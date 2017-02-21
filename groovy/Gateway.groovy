import javax.servlet.http.*

class Gateway {

    final String method
    final Meraki meraki
    final Linker linker
    final MimeTypeComputer typeComputer
    final Json json
    def String recordedResponse

    static Gateway of(request, apiKey) {
        def method = request.method
        def command = "${request.pathInfo}?${request.queryString}"
        def json   = new Json()
        def params = json.paramsFrom(request)
        def meraki = new Meraki(command, params, apiKey)
        def linker = new Linker(request)
        def typeComputer = new MimeTypeComputer()
        new Gateway(method,meraki,linker,typeComputer,json)
    }

    Gateway(method,meraki,linker,typeComputer,json) {
        this.method = method
        this.meraki = meraki
        this.linker = linker
        this.typeComputer = typeComputer
        this.json = json
    }

    def contentType() {
        stateChangeSetupRequest() ? 'text/html' : typeComputer.compute(response())
    }

    def response() {
        if (recordedResponse==null) {
            recordResponse()
        }
        recordedResponse
    }

    def recordResponse() {
        if (stateChangeSetupRequest()) {
            recordedResponse = linker.inputForParams()
        } else {
            recordedResponse = transformedMerakiResponse()
        }
    }

    def transformedMerakiResponse() {
        json.from(linker.transform(meraki.parsedJson(), meraki.command()))
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
