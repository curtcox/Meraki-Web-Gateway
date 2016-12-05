import groovy.json.*

class Meraki {

  def get(request,apiKey) {
      def page    = "https://n124.meraki.com/api/v0/$request"
      def command = "./get.sh $page $apiKey"
      System.err.println(command)
      def text = command.execute().text
      try {
          return new JsonSlurper().parseText(text)
      } catch (e) {
          def errorPage = text.replaceAll("page","page ($page)")
          throw new RuntimeException(errorPage,e)
      }
  }

}
