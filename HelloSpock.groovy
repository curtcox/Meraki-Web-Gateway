@Grab('org.spockframework:spock-core:0.7-groovy-2.0')
import spock.lang.*

class HelloSpock extends Specification {

    def "some list tests"() {
        setup:
        def list = [1, 2, 3]

        when:
        list << 4

        then:
        list.size() == old(list.size()) + 1
    }
}
