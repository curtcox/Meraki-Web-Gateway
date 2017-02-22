import groovy.servlet.*
import javax.servlet.http.*
import javax.servlet.*

class GroovyServletAdapter extends HttpServlet {

    Closure requestHandler
    ServletContext context

    void init(ServletConfig config) {
        super.init(config)
        context = config.servletContext
    }

    void service(HttpServletRequest request, HttpServletResponse response) {
        requestHandler.binding = new ServletBinding(request, response, context)
        use (ServletCategory) {
            requestHandler.call()
        }
    }

}
