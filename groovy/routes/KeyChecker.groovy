package routes

class KeyChecker {

  final request

  KeyChecker(request) {
      this.request = request
  }

  def existingApiKey() {
      def apiKey  = request.getParameter('apiKey')
      if (apiKey != null) {
          session().setAttribute('apiKey',apiKey)
          return true
      }
      apiKeyFromSession() != null
  }

  def apiKeyFromSession() {
      session().getAttribute('apiKey')
  }

  def session() {
      request.getSession()
  }

  def promptForApiKey() {
      Page.of('prompt_for_key.html')
  }

}
