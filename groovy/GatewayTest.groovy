import javax.servlet.http.*

class GatewayTest extends Test {

     final String                 apiKey = 'bogus'
     final HttpServletRequest    request = Mock(HttpServletRequest)
     final Meraki                 meraki = Mock(Meraki)
     final Linker                 linker = Mock(Linker)
     final Json                     json = Mock(Json)
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
         def gateway = new Gateway('GET',meraki,linker,typeComputer,json)
         def type = gateway.contentType()

         then:
         1 * meraki.verb() >> 'POST'

         expect:
         type == 'text/html'
     }

     def "contentType consults MimeTypeComputer with Meraki response for Meraki requests"() {
         def response = 'from Meraki'
         def computedType = 'whatever'
         def command = 'devices'
         def object = []
         def linkedObject = ['link']
         def linkedJson = '[link]'

         when:
         def gateway = new Gateway('GET',meraki,linker,typeComputer,json)
         def type = gateway.contentType()

         then:
             meraki.verb() >> 'GET'
         1 * meraki.parsedJson()                >> object
         1 * meraki.command()                   >> command
         1 * linker.transform(object,command)   >> linkedObject
         1 * json.from(linkedObject)            >> linkedJson
         1 * typeComputer.compute(linkedJson)   >> computedType

         expect:
         type == computedType
     }
}
