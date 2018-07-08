package gateway

import javax.servlet.http.*

class LinkerTest extends test.Test {

    final HttpServletRequest request = Mock(HttpServletRequest)

    def "Linker can be made from factory"() {
        def server = 'big iron'

        when:
        def linker = Linker.of(request)

        then:
        request.getServerName() >> server

        expect:
        linker instanceof Linker
        linker.server == server
    }

}
