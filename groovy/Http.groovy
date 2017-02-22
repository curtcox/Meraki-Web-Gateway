import java.net.*

class Http {

    HttpResponse response(HttpURLConnection connection) {
        int status   = connection.responseCode
        def mimeType = connection.contentType
        def content  = (status<300) ? body(connection) : error(connection)
        new HttpResponse(content,mimeType,status)
    }

    def body(connection) {
        contents(connection.inputStream)
    }

    def error(connection) {
        contents(connection.errorStream)
    }

    def contents(stream) {
        final input = new BufferedReader(new InputStreamReader(stream))
        def lines = ''
        def line
        while ((line = input.readLine()) != null) {
            lines = lines + line
        }
        input.close()
        lines
    }

}
