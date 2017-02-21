import javax.servlet.http.*

class MimeTypeComputerTest extends Test {


    def "compute returns text/html for HTML"() {
        given:
        def computer = new MimeTypeComputer()

        expect:
        computer.compute('<html></html>') == 'text/html'
        computer.compute('<HTML></HTML>') == 'text/html'
    }

    def "compute returns application/json for JSON"() {
        given:
        def computer = new MimeTypeComputer()

        expect:
        computer.compute('[]') == 'application/json'
    }

    def "compute returns text/plain for anything other than HTML or JSON"() {
        given:
        def computer = new MimeTypeComputer()

        expect:
        computer.compute('[]') == 'application/json'
    }

}
