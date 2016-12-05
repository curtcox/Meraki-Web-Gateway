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
  if (existingApiKey()) {
      return returnResponseFromMeraki(apiKeyFromSession())
  } else {
      return promptForApiKey()
  }
}

def existingApiKey() {
    def apiKey  = request.getParameter('apiKey')
    if (apiKey != null) {
        session().setAttribute('apiKey',apiKey)
        return true
    }
    return apiKeyFromSession() != null
}

def apiKeyFromSession() {
    return session().getAttribute('apiKey')
}

def session() {
    return request.getSession()
}

def promptForApiKey() {
    response.contentType = 'text/html'
    println new File('prompt_for_key.html').text
}

def root() {
    response.contentType = 'text/html'
    println new File('root.html').text
}

SimpleGroovyServlet.run(8080) { ->
    if (request.pathInfo=="/")            { root(); return }
    if (request.pathInfo=="/favicon.ico") { return }
    returnResponseFromMeraki()
}
