import groovy.json.*

class Meraki {

  def get(request) {
      def command = "./get.sh https://n124.meraki.com/api/v0/$request"
      System.err.println(command)
      def text = command.execute().text
      try {
        return new JsonSlurper().parseText(text)
      } catch (e) {
         throw new RuntimeException(text,e)
      }
  }

}
