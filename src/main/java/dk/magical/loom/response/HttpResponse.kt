package dk.magical.loom.response

import dk.magical.loom.Status
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

/**
 * Created by Christian on 25/10/2016.
 */
data class HttpResponse(private val outputStream: OutputStream) {
    private var status: Status = Status.OK
    private var body: String? = null
    private val headers: MutableMap<String, String> = mutableMapOf()

    fun status(status: Status): HttpResponse {
        this.status = status
        return this
    }

    fun body(body: String): HttpResponse {
        this.body = body
        return this
    }

    fun header(key: String, value: String): HttpResponse {
        headers.put(key, value)
        return this
    }

    fun headers(vararg headers: Pair<String, String>): HttpResponse {
        this.headers.putAll(headers)
        return this
    }

    fun end() {
        val response = HttpResponseBuilder.build(status, headers, body)

        val outputStreamWriter = OutputStreamWriter(outputStream, Charsets.UTF_8)
        val writer = PrintWriter(outputStreamWriter)
        response.forEach { line ->
            writer.println(line)
        }

        writer.flush()
        outputStream.close()
    }
}