def port() { return 8080 }

def returnResponseFromMeraki() {
    response.contentType = 'application/json'
    try {
      println new MerakiBrowser(port(),request).response()
    } catch (e) {
      response.contentType = 'text/html'
      println e.message
    }
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
