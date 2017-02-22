import javax.servlet.http.*

class Router {

    final HttpServletRequest request
    final HttpServletResponse response

    Router(request,response) {
        this.request = request
        this.response = response
    }

    def returnResponseFromMeraki(apiKey) {
        try {
            def gateway = Gateway.of(request,apiKey)
            def type = gateway.contentType()
            def content = gateway.response()
            write(type,content)
        } catch (e) {
            showError(e)
        }
    }

    def showError(e) {
        e.printStackTrace()
        def stack = e instanceof GroovyRuntimeException
        def type = stack ? 'text/plain' : 'text/html'
        def content = stack ? stringFor(e) : e.message
        write(type,content)
    }

    def stringFor(Throwable t) {
        def writer = new StringWriter()
        def printer = new PrintWriter(writer)
        t.printStackTrace(printer)
        return writer.toString()
    }

    def returnResponseFromMeraki() {
        def check = new KeyChecker(request)
        if (check.existingApiKey()) {
            return returnResponseFromMeraki(check.apiKeyFromSession())
        } else {
            write('text/html',check.promptForApiKey())
        }
    }

    def root() {
        write('text/html',Page.of('root.html'))
    }

    def docs() {
        write('text/plain',Docs.docs())
    }

    def exec() {
        write('text/html',Exec.prompt(request))
    }

    def write(contentType,content) {
        response.contentType = contentType
        response.writer.println content
    }

    def route() {
        if (request.pathInfo=="/")            { root(); return }
        if (request.pathInfo=="/exec")        { exec(); return }
        if (request.pathInfo=="/docs")        { docs(); return }
        if (request.pathInfo=="/favicon.ico") { return }
        returnResponseFromMeraki()
    }

    static def serve(HttpServletRequest request,HttpServletResponse response) {
        def router = new Router(request,response)
        router.route()
    }
}
