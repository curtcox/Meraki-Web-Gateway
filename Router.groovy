def port() { return 8080 }

def returnResponseFromMeraki() {
    response.contentType = 'application/json'
    println new MerakiBrowser(port(),request).response()
}

def root() {
    response.contentType = 'text/html'
    println new File('root.html').text
}

SimpleGroovyServlet.run(port()) { ->
    if (request.pathInfo=="/")            { root(); return }
    if (request.pathInfo=="/favicon.ico") { return }
    returnResponseFromMeraki()
}
