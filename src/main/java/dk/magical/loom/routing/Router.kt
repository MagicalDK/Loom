package dk.magical.loom.routing

import dk.magical.loom.request.HttpMethod
import dk.magical.loom.request.HttpMethod.*
import dk.magical.loom.request.HttpRequest
import dk.magical.loom.response.HttpResponse

/**
 * Created by Christian on 20/10/2016.
 */
class Router(val basePath: String) {
    val routes: MutableList<Route>

    init {
        routes = mutableListOf()
    }

    fun get(url: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        addRoute(Route(HttpMethod.GET, PathParser.parse(url), handler))
    }

    fun post(url: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        addRoute(Route(HttpMethod.POST, PathParser.parse(url), handler))
    }

    fun put(url: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        addRoute(Route(HttpMethod.PUT, PathParser.parse(url), handler))
    }

    fun delete(url: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        addRoute(Route(HttpMethod.DELETE, PathParser.parse(url), handler))
    }

    private fun addRoute(route: Route) {
        if (routes.contains(route))
            throw IllegalArgumentException("Path already exists: ${route.method} - ${route.path}")
        routes.add(route)
    }
}