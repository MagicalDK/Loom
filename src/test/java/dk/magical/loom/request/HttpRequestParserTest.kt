package dk.magical.loom.request

import com.google.common.truth.Truth
import dk.magical.loom.request.HttpMethod
import dk.magical.loom.request.HttpRequestParser
import org.junit.Test
import java.io.BufferedReader
import java.io.StringReader

/**
 * Created by Christian on 18/10/2016.
 */
class HttpRequestParserTest {
    @Test
    fun shouldParseGetMethod() {
        val reader = reader(listOf("GET / HTTP/1.1"))
        val httpRequest = HttpRequestParser.parse(reader)
        Truth.assertThat(httpRequest?.method).isEqualTo(HttpMethod.GET)
    }

    @Test
    fun shouldParsePostMethod() {
        val reader = reader(listOf("POST / HTTP/1.1"))
        val httpRequest = HttpRequestParser.parse(reader)
        Truth.assertThat(httpRequest?.method).isEqualTo(HttpMethod.POST)
    }

    @Test
    fun shouldParsePutMethod() {
        val reader = reader(listOf("PUT / HTTP/1.1"))
        val httpRequest = HttpRequestParser.parse(reader)
        Truth.assertThat(httpRequest?.method).isEqualTo(HttpMethod.PUT)
    }

    @Test
    fun shouldParseDeleteMethod() {
        val reader = reader(listOf("DELETE / HTTP/1.1"))
        val httpRequest = HttpRequestParser.parse(reader)
        Truth.assertThat(httpRequest?.method).isEqualTo(HttpMethod.DELETE)
    }

    @Test
    fun shouldParsePath() {
        val reader = reader(listOf("POST user/12345/name/ HTTP/1.1"))
        val httpRequest = HttpRequestParser.parse(reader)
        Truth.assertThat(httpRequest?.path).isEqualTo("/user/12345/name")
    }

    @Test
    fun shouldParseHeaders() {
        val request = listOf<String>(
                "POST /hello HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Postman-Token: 59998382-9aca-ada6-2550-5a29b26da58a",
                "Cache-Control: no-cache")

        val reader = reader(request)
        val httpRequest = HttpRequestParser.parse(reader)
        Truth.assertThat(httpRequest?.headers?.size).isEqualTo(4)

        Truth.assertThat(httpRequest?.headers).containsEntry("Host", "localhost:8080")
        Truth.assertThat(httpRequest?.headers).containsEntry("Connection", "keep-alive")
        Truth.assertThat(httpRequest?.headers).containsEntry("Postman-Token", "59998382-9aca-ada6-2550-5a29b26da58a")
        Truth.assertThat(httpRequest?.headers).containsEntry("Cache-Control", "no-cache")
    }

    @Test
    fun shouldParseContent() {
        val request = listOf<String>(
                "POST /hello HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 11",
                "Postman-Token: 59998382-9aca-ada6-2550-5a29b26da58a",
                "Cache-Control: no-cache",
                "",
                "Hello World")

        val reader = reader(request)
        val httpRequest = HttpRequestParser.parse(reader)
        Truth.assertThat(httpRequest?.body).isEqualTo("Hello World")
    }

    private fun reader(request: List<String>): BufferedReader {
        val buffer = StringBuilder()
        request.forEach { buffer.append(it).append("\n") }
        return BufferedReader(StringReader(buffer.toString()))
    }
}