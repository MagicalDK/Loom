package dk.magical.response

import dk.magical.Status
import java.io.OutputStream
import java.io.PrintWriter

/**
 * Created by Christian on 25/10/2016.
 */
data class HttpResponse(private val outputStream: OutputStream) {
    private var status: Status = Status.OK
    private var body: String? = null

    fun status(status: Status): HttpResponse {
        this.status = status
        return this
    }

    fun body(body: String): HttpResponse {
        this.body = body
        return this
    }

    fun end() {
        val response = HttpResponseBuilder.build(status, body)

        val writer = PrintWriter(outputStream)
        response.forEach { line ->
            writer.println(line)
        }

        writer.flush()
        outputStream.close()
    }
}