package dk.magical.loom.request

import dk.magical.loom.routing.PathParser
import java.io.BufferedReader
import java.net.URI
import java.nio.file.Path

/**
 * Created by Christian on 18/10/2016.
 */

class HttpRequestParser{
    fun parse(reader: BufferedReader): HttpRequest? {
        val requestAndHeaderLines = readRequestAndHeaderLines(reader)

        val requestLine = requestAndHeaderLines.first()
        val method = method(requestLine) ?: return null
        val uri = uri(requestLine)

        val path = uri.path
        val queryParameters = queryParameters(uri)
        val headers = headers(requestAndHeaderLines.drop(1))

        val contentLength = headers.get("Content-Length")?.toInt()
        val content = body(reader, contentLength)

        return HttpRequest(method, path, queryParameters, headers, content, mapOf())
    }

    private fun readRequestAndHeaderLines(reader: BufferedReader): List<String> {
        val lines: MutableList<String> = mutableListOf()
        while (true) {
            val line = reader.readLine()
            if (line == null || line.isEmpty())
                break

            lines.add(line)
        }

        return lines
    }

    private fun body(reader: BufferedReader, contentLength: Int?): String? {
        if (contentLength == null)
            return null

        val chars = CharArray(contentLength)
        val numberOfChars = reader.read(chars)
        return String(chars).substring(0, numberOfChars)
    }

    private fun method(requestLine: String): HttpMethod? {
        val elements = requestLine.split(" ")
        val methodString = elements.first()
        when (methodString) {
            "GET" -> return HttpMethod.GET
            "POST" -> return HttpMethod.POST
            "PUT" -> return HttpMethod.PUT
            "DELETE" -> return HttpMethod.DELETE
            else -> return null
        }
    }

    private fun uri(requestLine: String): URI {
        val elements = requestLine.split(" ")
        val path = PathParser.parse(elements[1])
        return URI(path)
    }

    private fun queryParameters(uri: URI): Map<String, String> {
        val map: MutableMap<String, String> = mutableMapOf()

        if (uri.query == null)
            return map

        val parameters = uri.query.split("&")
        parameters.forEach {
            val parameter = it.split("=")
            map[parameter[0]] = parameter[1]
        }

        return map
    }

    fun headers(headers: List<String>): Map<String, String> {
        val map: MutableMap<String, String> = mutableMapOf()
        headers.forEach { string ->
            val header = string.split(":", limit = 2)
            map.put(header.first(), header.last().trimStart())
        }
        return map
    }
}
