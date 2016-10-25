package dk.magical.routing

import dk.magical.request.HttpRequest
import dk.magical.response.HttpResponse

/**
 * Created by Christian on 20/10/2016.
 */
class RouteDispatcher() {
    fun dispatch(request: HttpRequest, response: HttpResponse, routers: List<Router>, error: (message: String) -> Unit) {
        validateRouters(routers)

        val route = findRoute(request, routers)
        if (route != null) {
            route.handler(request, response)
        } else {
            error("No handler for: ${request.method.name} ${request.path}.")
        }
    }

    private fun validateRouters(routers: List<Router>) {
        val knownPaths: MutableList<String> = mutableListOf()
        routers.forEach { router ->
            router.routes.forEach { route ->
                val fullPath = route.fullPath(router.basePath)
                if (knownPaths.contains(fullPath))
                    throw IllegalArgumentException("More than one router with the path: ${fullPath}")

                knownPaths.add(fullPath)
            }
        }
    }

    private fun findRouters(path: String, routers: List<Router>): List<Router> {
        return routers.filter { PathParser.parse(path).startsWith(it.basePath) }
    }

    private fun findRoute(request: HttpRequest, routers: List<Router>): Route? {
        val filteredRouters = findRouters(request.path, routers)
        filteredRouters.forEach { router ->
            val route = router.routes.find {
                val methodMatch = it.method == request.method
                val pathMatch = it.fullPath(router.basePath) == request.path
                methodMatch && pathMatch
            }

            if (route != null)
                return route
        }

        return null
    }
}