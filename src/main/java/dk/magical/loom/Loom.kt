package dk.magical.loom

import dk.magical.loom.logging.Logger
import dk.magical.loom.request.HttpRequestParser
import dk.magical.loom.routing.RouteDispatcher
import dk.magical.loom.routing.Router
import java.util.concurrent.ExecutorService

/**
 * Created by Christian on 19/10/2016.
 */

class Loom(private val executorService: ExecutorService) {
    private val routers: MutableList<Router>
    private val dispatcher: RouteDispatcher

    init {
        routers = mutableListOf()
        dispatcher = RouteDispatcher()
    }

    fun start(port: Int) {
        val server = HttpServerSocket(executorService, HttpRequestParser(), port)
        Logger.log("Listen for connection on port: ${port}")

        executorService.execute {
            while (true) {
                server.listen { request, response ->
                    dispatcher.dispatch(request, response,  routers) { message ->
                        Logger.log("ERROR - ${message}")
                    }
                }
            }
        }
    }

    fun route(router: Router) {
        routers.add(router)
    }
}
