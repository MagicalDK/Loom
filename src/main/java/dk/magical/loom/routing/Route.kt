package dk.magical.loom.routing

import dk.magical.loom.request.HttpMethod
import dk.magical.loom.request.HttpRequest
import dk.magical.loom.response.HttpResponse

/**
 * Created by Christian on 20/10/2016.
 */
data class Route(val method: HttpMethod, val path: String, val handler: (request: HttpRequest, response: HttpResponse) -> Unit) {

    fun fullPath(basePath: String): String {
        return "${basePath}${path}"
    }

    override fun equals(other: Any?): Boolean {
        val route = other as? Route ?: return false
        return method == route.method && path == route.path
    }

    fun matchers(basePath: String): List<(String, HttpMethod) -> Match> {
        val elements = fullPath(basePath).removePrefix("/").split("/")
        return elements.map { element ->
            if (element.startsWith("{") && element.endsWith("}")) {
                { path: String, method: HttpMethod ->
                    val match = path.isNotEmpty() && this.method == method
                    val key = element.removePrefix("{").removeSuffix("}")
                    Match(match, key, path)
                }
            } else {
                { path: String, method: HttpMethod ->
                    val match = path == element && this.method == method
                    Match(match, path, null)
                }
            }
        }
    }
}

data class Match(val match: Boolean, val Key: String, val value: String?)
