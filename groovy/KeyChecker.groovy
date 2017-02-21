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
      return apiKeyFromSession() != null
  }

  def apiKeyFromSession() {
      return session().getAttribute('apiKey')
  }

  def session() {
      return request.getSession()
  }

  def promptForApiKey() {
      return Page.of('prompt_for_key.html')
  }

}
