package dk.magical.loom

import dk.magical.loom.middleware.Middleware
import dk.magical.loom.request.HttpRequest
import dk.magical.loom.response.HttpResponse
import dk.magical.loom.routing.Route
import dk.magical.loom.routing.Router

/**
 * Created by Christian on 20/10/2016.
 */
class Dispatcher() {
    fun dispatch(request: HttpRequest, response: HttpResponse, routers: List<Router>, middleware: List<Middleware>, error: (message: String, HttpResponse) -> Unit) {
        val activeMiddleware = findMiddleware(request, middleware)
        val routeDetails = findRoute(request, routers)

        handleRequest(request, response, routeDetails, activeMiddleware, 0, error)
    }

    private fun handleRequest(request: HttpRequest, response: HttpResponse, routerDetail: Pair<Route, Map<String, String>>?, activeMiddleware: List<Middleware>, nextMiddlewareIndex: Int, error: (message: String, HttpResponse) -> Unit) {
        val nextMiddleware = activeMiddleware.getOrNull(nextMiddlewareIndex) ?: return executeRequest(request, response, routerDetail, error)

        nextMiddleware.callback (request, response) {
            handleRequest(request, response, routerDetail, activeMiddleware, nextMiddlewareIndex + 1, error)
        }
    }

    private fun executeRequest(request: HttpRequest, response: HttpResponse, routeDetails: Pair<Route, Map<String, String>>?, error: (message: String, HttpResponse) -> Unit) {
        if (routeDetails != null) {
            request.urlParameters = routeDetails.second
            routeDetails.first.handler(request, response)
        } else {
            error("No handler for: ${request.method.name} ${request.path}.", response)
        }
    }

    private fun findRoute(request: HttpRequest, routers: List<Router>): Pair<Route, Map<String, String>>? {
        routers.forEach { router ->
            router.routes.forEach { route ->
                val (match, parameters) = matchRoute(request, route, router.basePath)
                if (match)
                    return Pair(route, parameters)
            }
        }

        return null
    }

    private fun matchRoute(request: HttpRequest, route: Route, basePath: String): Pair<Boolean, Map<String, String>> {
        val conditions = route.conditions(basePath)
        val elements = request.path.removePrefix("/").split("/")
        if (conditions.size != elements.size)
            return Pair(false, mapOf())

        val parameters: MutableMap<String, String> = mutableMapOf()
        conditions.forEachIndexed { index, matcher ->
            val element = elements[index]
            val (match, key, value) = matcher(element, request.method)
            if (value != null)
                parameters[key] = value
            if (!match)
                return Pair(false, mapOf())
        }

        return Pair(true, parameters)
    }

    private fun findMiddleware(request: HttpRequest, middleware: List<Middleware>): List<Middleware> {
        return middleware.filter { matchMiddleware(request, it) }
    }

    private fun matchMiddleware(request: HttpRequest, middleware: Middleware): Boolean {
        val elements = request.path.removePrefix("/").split("/")
        val conditions = middleware.conditions()
        if (conditions.size != elements.size)
            return false

        conditions.forEachIndexed { index, condition ->
            val element = elements[index]
            if (condition(element, request.method))
                return false
        }

        return true
    }

}