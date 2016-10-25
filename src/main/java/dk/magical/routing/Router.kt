package dk.magical.routing

import dk.magical.request.HttpMethod.*
import dk.magical.request.HttpRequest
import dk.magical.response.HttpResponse

/**
 * Created by Christian on 20/10/2016.
 */
class Router(val basePath: String) {
    val routes: MutableList<Route>

    init {
        routes = mutableListOf()
    }

    fun get(url: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        addRoute(Route(GET, PathParser.parse(url), handler))
    }

    fun post(url: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        addRoute(Route(POST, PathParser.parse(url), handler))
    }

    fun put(url: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        addRoute(Route(PUT, PathParser.parse(url), handler))
    }

    fun delete(url: String, handler: (HttpRequest, HttpResponse) -> Unit) {
        addRoute(Route(DELETE, PathParser.parse(url), handler))
    }

    private fun addRoute(route: Route) {
        if (routes.contains(route))
            throw IllegalArgumentException("Path already exists: ${route.method} - ${route.path}")
        routes.add(route)
    }
}