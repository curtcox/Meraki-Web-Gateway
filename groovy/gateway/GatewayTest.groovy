package gateway

import meraki.*
import gateway.*
import json.*
import javax.servlet.http.*

class GatewayTest extends test.Test {

     final String                 apiKey = 'bogus'
     final HttpServletRequest    request = Mock(HttpServletRequest)
     final Meraki                 meraki = Mock(Meraki)
     final Linker                 linker = Mock(Linker)
     final Json                     json = Mock(Json)

     def "Gateway can be made"() {
         given:
         def gateway = Gateway.of(request,apiKey)

         expect:
         gateway instanceof Gateway
     }

     def "Gateway constructs linker with given request info"() {
         def server  = 's'
         def port    = 1986
         def path    = '/foo'

         when:
         def gateway = Gateway.of(request,apiKey)
         def linker  = gateway.linker

         then:
         request.getServerName() >> server
         request.getServerPort() >> port
         request.getPathInfo()   >> path

         expect:
         linker.server == server
         linker.port   == port
         linker.path   == path
     }

     def "Gateway constructs Meraki correctly"() {
         given:
         def gateway = Gateway.of(request,apiKey)
         def meraki  = gateway.meraki

         expect:
         meraki.http.apiKey == apiKey
     }

     def "contentType returns text/html for state change setup (entry forms)"() {
         when:
         def gateway = new Gateway('GET',meraki,linker,json)
         def type = gateway.contentType()

         then:
         1 * meraki.verb() >> 'POST'

         expect:
         type == 'text/html'
     }

     def "contentType returns application/json for GET passthrus"() {
         when:
         def gateway = new Gateway('GET',meraki,linker,json)
         def type = gateway.contentType()

         then:
         1 * meraki.verb() >> 'GET'

         expect:
         type == 'application/json'
     }

     def "response returns linked Meraki response"() {
         def object = ['from Meraki']
         def command = 'devices'
         def linkedObject = ['link']
         def linkedJson = '[link]'

         when:
         def gateway = new Gateway('GET',meraki,linker,json)
         def response = gateway.response()

         then:
         1 * meraki.verb()                    >> 'GET'
         1 * meraki.parsedJson()              >> object
         1 * meraki.command()                 >> command
         1 * linker.transform(object,command) >> linkedObject
         1 * json.from(linkedObject)          >> linkedJson

         expect:
         response == linkedJson
     }
}
