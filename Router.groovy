def returnResponseFromMeraki(apiKey) {
    response.contentType = 'application/json'
    try {
        println new MerakiBrowser(request,apiKey).response()
    } catch (e) {
        response.contentType = 'text/html'
        println e.message
    }
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
    println new File('root.html').text
}

def docs() {
    response.contentType = 'text/plain'
    println Docs.docs()
}

SimpleGroovyServlet.run(8080) { ->
    if (request.pathInfo=="/")            { root(); return }
    if (request.pathInfo=="/docs")        { docs(); return }
    if (request.pathInfo=="/favicon.ico") { return }
    returnResponseFromMeraki()
}
