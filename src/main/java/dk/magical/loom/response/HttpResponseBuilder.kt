package dk.magical.loom.response

import dk.magical.loom.Status

/**
 * Created by Christian on 25/10/2016.
 */
class HttpResponseBuilder {
    fun build(status: Status, headers: Map<String, String>, body: String?): List<String> {
        val list: MutableList<String> = mutableListOf()
        list.add(statusLine(status))
        list.add(contentLength(body))
        list.addAll(headerLiners(headers))
        list.add("")
        if (body != null)
            list.add(body)

        return list
    }

    private fun statusLine(status: Status): String {
        return "HTTP/1.1 ${status.statusCode} ${status.message}"
    }

    private fun contentLength(body: String?): String {
        val length = if (body == null) 0 else body.toByteArray(Charsets.UTF_8).size
        return "Content-Length: ${length}"
    }

    private fun headerLiners(headers: Map<String, String>): List<String> {
        return headers.map { "${it.key}: ${it.value}" }.toList()
    }
}
