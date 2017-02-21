import javax.servlet.http.*

class GatewayTest extends Test {

     final String                 apiKey = 'bogus'
     final HttpServletRequest    request = Mock(HttpServletRequest)
     final Meraki                 meraki = Mock(Meraki)
     final Linker                 linker = Mock(Linker)
     final MimeTypeComputer typeComputer = Mock(MimeTypeComputer)

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

     def "contentType returns text/html for state change setup (entry forms)"() {
         when:
         def gateway = new Gateway('GET',meraki,linker,typeComputer)
         def type = gateway.contentType()

         then:
         1 * meraki.verb() >> 'POST'

         expect:
         type == 'text/html'
     }

     def "contentType consults MimeTypeComputer with Meraki response for Meraki requests"() {
         def merakiResponse = 'this stuff'
         def computedType = 'whatever'

         when:
         def gateway = new Gateway('GET',meraki,linker,typeComputer)
         def type = gateway.contentType()

         then:
         1 * meraki.verb() >> 'GET'
         1 * meraki.json() >> merakiResponse
         1 * typeComputer.compute(merakiResponse) >> computedType

         expect:
         type == computedType
     }
}
