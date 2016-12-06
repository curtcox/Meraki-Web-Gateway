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
      request = deleteInitialSlash(request)
      request = delete(request,"\\?null")
      request = deleteApiKey(request)
      request = deleteFinalQuestionMark(request)
      return request
  }

  def deleteInitialSlash(request) {
      return request.startsWith('/')
          ? request.substring(1,request.length())
          : request
  }

  def deleteFinalQuestionMark(request) {
      return request.endsWith('?')
          ? request.substring(0,request.length() - 1)
          : request
  }

  def deleteApiKey(request) {
      return delete(request,"apiKey=$apiKey")
  }

  def delete(request,fragment) {
      return request.replaceAll(fragment,'')
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
