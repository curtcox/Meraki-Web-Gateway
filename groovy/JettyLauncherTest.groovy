import spock.lang.*
import javax.servlet.http.*

class JettyLauncherTest extends test.Test {

    def "create returns Server"() {
        def launcher = new JettyLauncher(2086)
        def server   = launcher.create({ -> System.err.println('created') })

        expect:
        server != null
    }

}
