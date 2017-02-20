class MerakiHttp {

    final String merakiApiKey
    final Http http = new Http()

    MerakiHttp(merakiApiKey) {
        this.merakiApiKey = merakiApiKey
    }

    def post(url,json) {
        makeRequest('POST',url,json)
    }

    def put(url,json) {
        makeRequest('PUT',url,json)
    }

    def get(url) {
        makeRequest('GET',url,null)
    }

    def delete(url) {
        makeRequest('DELETE',url,null)
    }

    def relative(url) {
        'https://n124.meraki.com/api/v0/' + url
    }

    String makeRequest(verb,url,json) {
        makeAbsoluteRequest(verb,relative(url),json)
    }

    String makeAbsoluteRequest(action, urlString, json) {
        println "HTTP $action $urlString $json $merakiApiKey"
        final url = new URL(urlString)
        final connection = url.openConnection()
        connection.setRequestProperty('X-Cisco-Meraki-API-Key', merakiApiKey)
        connection.setRequestMethod(action)
        if (json!=null) {
            connection.setRequestProperty('Content-Type', 'application/json')
            connection.setDoOutput(true)
            writeJson(connection,json)
        }
        final response = http.response(connection)
        println "HTTP <<< $response"
        response
    }

    def writeJson(connection,json) {
        final out = new PrintWriter(connection.getOutputStream())
        out.println(json)
        out.close()
    }

}
