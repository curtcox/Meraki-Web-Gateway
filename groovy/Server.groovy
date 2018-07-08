import server.*
import routes.*

JettyLauncher.run() { -> Router.serve(request,response) }
