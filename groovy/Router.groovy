import javax.servlet.http.*

class Router {

    final HttpServletRequest request
    final HttpServletResponse response
    def apiKey

    Router(request,response) {
        this.request = request
        this.response = response
    }

    def gateway() {
        try {
            def gateway = Gateway.of(request,apiKey)
            def    type = gateway.contentType()
            def content = gateway.response()
            write(type,content)
        } catch (e) {
            showError(e)
        }
    }

    def devices() {
        try {
            def devices = Devices.of(apiKey)
            def    type = devices.contentType()
            def content = devices.response()
            write(type,content)
        } catch (e) {
            showError(e)
        }
    }

    def clients() {
        try {
            def clients = Clients.of(apiKey)
            def    type = clients.contentType()
            def content = clients.response()
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
        def path = request.pathInfo
        if (path=="/")            { root();    return }
        if (path=="/exec")        { exec();    return }
        if (path=="/docs")        { docs();    return }
        if (path=="/favicon.ico") { return }
        def check = new KeyChecker(request)
        if (check.existingApiKey()) {
            apiKey = check.apiKeyFromSession()
            if (path=="/devices")     { devices(); return }
            if (path=="/clients")     { clients(); return }
            gateway()
        } else {
            write('text/html',check.promptForApiKey())
        }
    }

    static def serve(HttpServletRequest request,HttpServletResponse response) {
        def router = new Router(request,response)
        router.route()
    }
}
