import groovy.json.*

class Meraki {

  def get(request) {
      def command = "./get.sh https://n124.meraki.com/api/v0/$request"
      System.err.println(command)
      return new JsonSlurper().parseText(command.execute().text)
  }

}
