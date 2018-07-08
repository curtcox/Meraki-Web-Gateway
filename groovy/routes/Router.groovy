package routes

import gateway.*
import domain.*
import domain.*
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
        def gateway = Gateway.of(request,apiKey)
        write(gateway.contentType(),gateway.response())
    }

    def networks() {
        def networks = Networks.of(apiKey)
        write(networks.contentType(),networks.response())
    }

    def devices() {
        def devices = Devices.of(apiKey)
        write(devices.contentType(),devices.response())
    }

    def clients() {
        def clients = Clients.of(apiKey,timespan())
        write(clients.contentType(),clients.response())
    }

    def clientConnections() {
        def connections = ClientConnections.of(apiKey,timespan())
        write(connections.contentType(),connections.response())
    }

    int timespan() {
        def MAX = 60 * 60 * 24 * 30
        def value = request.getParameter('timespan') as Integer
        value == null ? MAX : value
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
            try {
                if (path=="/networks")          { networks(); return }
                if (path=="/devices")           { devices(); return }
                if (path=="/clients")           { clients(); return }
                if (path=="/clientConnections") { clientConnections(); return }
                gateway()
            } catch (e) {
                showError(e)
            }
        } else {
            write('text/html',check.promptForApiKey())
        }
    }

    static def serve(HttpServletRequest request,HttpServletResponse response) {
        def router = new Router(request,response)
        router.route()
    }
}
