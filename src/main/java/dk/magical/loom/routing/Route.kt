package dk.magical.loom.routing

import dk.magical.loom.request.HttpMethod
import dk.magical.loom.request.HttpRequest
import dk.magical.loom.response.HttpResponse

/**
 * Created by Christian on 20/10/2016.
 */
data class Route(val method: HttpMethod, val path: String, val handler: (HttpRequest, HttpResponse) -> Unit) {
    fun fullPath(basePath: String): String {
        return "${basePath}${path}"
    }

    override fun equals(other: Any?): Boolean {
        val route = other as? Route ?: return false
        return method == route.method && path == route.path
    }
}