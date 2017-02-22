import javax.servlet.http.*

class LinkerTest extends Test {

    final HttpServletRequest request = Mock(HttpServletRequest)

    def "Linker can be made from factory"() {
        given:
        def linker = Linker.of(request)

        expect:
        linker instanceof Linker
        linker.request == request
    }

}
