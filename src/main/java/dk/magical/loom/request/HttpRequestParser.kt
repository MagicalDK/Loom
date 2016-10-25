package dk.magical.loom.request

import dk.magical.loom.routing.PathParser
import java.io.BufferedReader

/**
 * Created by Christian on 18/10/2016.
 */

object HttpRequestParser {
    fun parse(reader: BufferedReader): HttpRequest? {
        val requestAndHeaderLines = HttpRequestParser.readRequestAndHeaderLines(reader)

        val requestLine = requestAndHeaderLines.first()
        val method = HttpRequestParser.method(requestLine) ?: return null
        val path = HttpRequestParser.path(requestLine)

        val headers = HttpRequestParser.headers(requestAndHeaderLines.drop(1))

        val contentLength = headers.get("Content-Length")?.toInt()
        val content = HttpRequestParser.content(reader, contentLength)

        return HttpRequest(method, path, headers, content)
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

    private fun content(reader: BufferedReader, contentLength: Int?): String? {
        if (contentLength == null)
            return null

        val stringBuilder = StringBuilder()
        for (i in 1..contentLength) {
            val characterIndex = reader.read()
            val characters = Character.toChars(characterIndex)
            stringBuilder.append(characters)
        }

        return stringBuilder.toString()
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

    private fun path(requestLine: String): String {
        val elements = requestLine.split(" ")
        val path = elements[1]
        return PathParser.parse(path)
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