package dk.magical.loom.middleware

import dk.magical.loom.request.HttpMethod
import dk.magical.loom.request.HttpRequest
import dk.magical.loom.response.HttpResponse

/**
 * Created by Christian on 09/12/2016.
 */

data class Middleware(val methods: List<HttpMethod>, val path: String, val callback: (request: HttpRequest, response: HttpResponse, next: () -> Unit) -> Unit) {
    fun conditions(): List<(String, HttpMethod) -> Boolean> {
        val elements = path.removePrefix("/").split("/")
        return elements.map { element ->
            if (element.startsWith("{") && element.endsWith("}")) {
                { path: String, method: HttpMethod ->
                    path.isNotEmpty() && this.methods.contains(method)
                }
            } else {
                { path: String, method: HttpMethod ->
                    path == this.path && this.methods.contains(method)
                }
            }
        }
    }
}
