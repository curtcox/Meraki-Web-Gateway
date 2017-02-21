import javax.servlet.http.*

class GatewayTest extends Test {

    final HttpServletRequest request = Mock(HttpServletRequest)

    def "Linker can be made"() {
        given:
        def linker = new Linker(request)

        expect:
        linker instanceof Linker
        linker.request == request
    }

}
