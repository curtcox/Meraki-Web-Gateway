def returnResponseFromMeraki(apiKey) {
    try {
        def gateway = Gateway.of(request,apiKey)
        response.contentType = gateway.contentType()
        println gateway.response()
    } catch (e) {
        showError(e)
    }
}

def showError(e) {
    if (e instanceof GroovyRuntimeException) {
        response.contentType = 'text/plain'
        println stringFor(e)
    } else {
        response.contentType = 'text/html'
    }
    println e.message
    e.printStackTrace()
}

def stringFor(e) {
    def writer = new StringWriter()
    def printer = new PrintWriter(writer)
    e.printStackTrace(printer)
    return writer.toString()
}

def returnResponseFromMeraki() {
    def check = new KeyChecker(request)
    if (check.existingApiKey()) {
        return returnResponseFromMeraki(check.apiKeyFromSession())
    } else {
        response.contentType = 'text/html'
        println check.promptForApiKey()
    }
}

def root() {
    response.contentType = 'text/html'
    println Page.of('root.html')
}

def docs() {
    response.contentType = 'text/plain'
    println Docs.docs()
}

def exec() {
    response.contentType = 'text/html'
    println Exec.prompt(request)
}

SimpleGroovyServlet.run(8080) { ->
    if (request.pathInfo=="/")            { root(); return }
    if (request.pathInfo=="/exec")        { exec(); return }
    if (request.pathInfo=="/docs")        { docs(); return }
    if (request.pathInfo=="/favicon.ico") { return }
    returnResponseFromMeraki()
}
