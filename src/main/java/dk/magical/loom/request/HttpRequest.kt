package dk.magical.loom.request

/**
 * Created by Christian on 18/10/2016.
 */
data class HttpRequest(val method: HttpMethod, val path: String, val headers: Map<String, String>, val body: String?)

enum class HttpMethod() {
    GET, POST, PUT, DELETE
}