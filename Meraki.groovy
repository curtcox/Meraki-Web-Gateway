import groovy.json.*

class Meraki {

  final request
  final apiKey

  Meraki(request,apiKey) {
      this.request = request.replaceAll('//','/')
      this.apiKey  = apiKey
  }

  def command() {
      return "./get.sh ${page()} $apiKey"
  }

  def page() {
      return "https://n124.meraki.com/api/v0/${scrubbed(request)}"
  }

  def scrubbed(request) {
      if (request.startsWith('/')) {
          request = request.substring(1,request.length())
      }
      if (request.endsWith('?null')) {
          request = request.substring(0,request.length() - 5)
      }
      return request
  }

  def json() {
      def command = command()
      System.err.println(command)
      def text = command.execute().text
      try {
          return new JsonSlurper().parseText(text)
      } catch (e) {
          def errorPage = text.replaceAll("page","page (${page()})")
          throw new RuntimeException(errorPage,e)
      }
  }

}
