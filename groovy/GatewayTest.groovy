import javax.servlet.http.*

class GatewayTest extends Test {

     final String              apiKey = 'bogus'
     final HttpServletRequest request = Mock(HttpServletRequest)
     final Meraki              meraki = Mock(Meraki)
     final Linker              linker = Mock(Linker)

     def "Gateway can be made"() {
         given:
         def gateway = Gateway.of(request,apiKey)

         expect:
         gateway instanceof Gateway
     }

     def "Gateway constructs linker with given request"() {
         given:
         def gateway = Gateway.of(request,apiKey)
         def linker  = gateway.linker

         expect:
         linker.request == request
     }

     def "Gateway constructs Meraki correctly"() {
         given:
         def gateway = Gateway.of(request,apiKey)
         def meraki  = gateway.meraki

         expect:
         meraki.http.apiKey == apiKey
     }

}
