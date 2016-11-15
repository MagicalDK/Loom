package dk.magical.loom.routing

import dk.magical.loom.request.HttpRequest
import dk.magical.loom.response.HttpResponse

/**
 * Created by Christian on 20/10/2016.
 */
class RouteDispatcher() {
    fun dispatch(request: HttpRequest, response: HttpResponse, routers: List<Router>, error: (message: String) -> Unit) {
        validateRouters(routers, error)

        val routeDetails = findRoute(request, routers)
        if (routeDetails != null) {
            request.urlParameters = routeDetails.second
            routeDetails.first.handler(request, response)
        } else {
            error("No handler for: ${request.method.name} ${request.path}.")
        }
    }

    private fun validateRouters(routers: List<Router>, error: (message: String) -> Unit) {
        val knownPaths: MutableList<String> = mutableListOf()
        routers.forEach { router ->
            router.routes.forEach { route ->
                val fullPath = route.fullPath(router.basePath)
                if (knownPaths.contains(fullPath))
                    error("More than one router with the path: ${fullPath}")
                else
                    knownPaths.add(fullPath)
            }
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
        val matchers = route.matchers(basePath)
        val elements = request.path.removePrefix("/").split("/")
        if (matchers.size != elements.size)
            return Pair(false, mapOf())

        val parameters: MutableMap<String, String> = mutableMapOf()
        matchers.forEachIndexed { index, matcher ->
            val element = elements[index]
            val (match, key, value) = matcher(element, request.method)
            if (value != null)
                parameters[key] = value
            if (!match)
                return Pair(false, mapOf())
        }

        return Pair(true, parameters)
    }
}