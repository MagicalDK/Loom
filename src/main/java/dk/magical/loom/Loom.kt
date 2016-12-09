package dk.magical.loom

import dk.magical.loom.logging.Logger
import dk.magical.loom.middleware.Middleware
import dk.magical.loom.request.HttpMethod
import dk.magical.loom.request.HttpRequest
import dk.magical.loom.request.HttpRequestParser
import dk.magical.loom.response.HttpResponse
import dk.magical.loom.routing.PathParser
import dk.magical.loom.routing.Router
import java.util.concurrent.ExecutorService

/**
 * Created by Christian on 19/10/2016.
 */

class Loom(private val executorService: ExecutorService) {
    private val routers: MutableList<Router>
    private val middleware: MutableList<Middleware>
    private val dispatcher: Dispatcher

    init {
        routers = mutableListOf()
        middleware = mutableListOf()
        dispatcher = Dispatcher()
    }

    fun start(port: Int, error: (String, HttpResponse) -> Unit) {
        val server = HttpServerSocket(executorService, HttpRequestParser(), port)
        Logger.log("Listen for connection on port: ${port}")

        executorService.execute {
            while (true) {
                server.listen { request, response ->
                    dispatcher.dispatch(request, response,  routers, middleware, error)
                }
            }
        }
    }

    fun route(router: Router) {
        routers.add(router)
    }

    fun use(vararg methods: HttpMethod, path: String, callback: (request: HttpRequest, response: HttpResponse, next: () -> Unit) -> Unit) {
        val middleware = Middleware(methods.asList(), PathParser.parse(path), callback)
        this.middleware.add(middleware)
    }
}
