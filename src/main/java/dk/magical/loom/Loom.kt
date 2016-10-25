package dk.magical.loom

import dk.magical.loom.logging.Logger
import dk.magical.loom.routing.RouteDispatcher
import dk.magical.loom.routing.Router

/**
 * Created by Christian on 19/10/2016.
 */

class Loom(private val port: Int) {
    private val server: HttpServerSocket
    private val routers: MutableList<Router>
    private val dispatcher: RouteDispatcher

    init {
        server = HttpServerSocket(port)
        routers = mutableListOf()
        dispatcher = RouteDispatcher()
    }

    fun start() {
        Logger.log("Listen for connection on port: ${port}")

        while (true) {
            server.listen { request, response ->
                dispatcher.dispatch(request, response,  routers) { message ->
                    Logger.log("ERROR - ${message}")
                }
            }
        }
    }

    fun route(router: Router) {
        routers.add(router)
    }
}
