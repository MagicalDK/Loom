package dk.magical.response

import dk.magical.Status

/**
 * Created by Christian on 25/10/2016.
 */
object HttpResponseBuilder {
    fun build(status: Status, body: String?): List<String> {
        val list: MutableList<String> = mutableListOf()
        list.add(statusLine(status))
        list.add(contentLength(body))
        list.add("")
        if (body != null)
            list.add(body)

        return list
    }

    private fun statusLine(status: Status): String {
        return "HTTP/1.1 ${status.statusCode} ${status.message}"
    }

    private fun contentLength(body: String?): String {
        val length = if (body == null) 0 else body.length
        return "Content-Length: ${length}"
    }
}
