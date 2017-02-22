@Grapes([
    @Grab('org.eclipse.jetty:jetty-server:9.4.0.v20161208'),
    @Grab('org.eclipse.jetty:jetty-servlet:9.4.0.v20161208'),
    @Grab('javax.servlet:javax.servlet-api:3.1.0'),
    @GrabExclude('org.eclipse.jetty.orbit:javax.servlet')
])

import org.eclipse.jetty.server.*
import org.eclipse.jetty.servlet.*
import javax.servlet.http.*
import javax.servlet.*

class JettyLauncher {

    final int port

    JettyLauncher() {
        this(8080)
    }

    JettyLauncher(int port) {
        this.port = port
    }

    static def run(Closure requestHandler) {
        def launcher = new JettyLauncher()
        def server = launcher.create(requestHandler)
        server.start()
    }

    Server create(Closure requestHandler) {
        def server = new Server(port)
        context(server).addServlet(servlet(requestHandler), '/*')
        server
    }

    ServletHolder servlet(Closure requestHandler) {
        new ServletHolder(new GroovyServletAdapter(requestHandler: requestHandler))
    }

    ServletContextHandler context(Server server) {
        new ServletContextHandler(server, '/', ServletContextHandler.SESSIONS)
    }

}
