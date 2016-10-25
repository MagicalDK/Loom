package dk.magical.loom.response

import com.google.common.truth.Truth
import dk.magical.loom.Status
import dk.magical.loom.response.HttpResponse
import org.junit.Test
import java.io.ByteArrayOutputStream

/**
 * Created by Christian on 26/10/2016.
 */
class HttpResponseTest {
    @Test
    fun shouldSendResponse() {
        val outputStream = ByteArrayOutputStream()
        val response = HttpResponse(outputStream)
        response.status(Status.CREATED).body("Hello Peter").end()

        val byteArray = outputStream.toByteArray()
        val output = String(byteArray)
        Truth.assertThat(output).isEqualTo("HTTP/1.1 201 Created\n" + "Content-Length: ${"Hello Peter".length}\n" + "\n" + "Hello Peter\n")
    }

    @Test
    fun shouldSendHeaders() {
        val outputStream = ByteArrayOutputStream()
        val response = HttpResponse(outputStream)
        response.status(Status.CREATED).body("Hello Peter").headers("Name" to "Peter", "Age" to "35").end()

        val byteArray = outputStream.toByteArray()
        val output = String(byteArray)
        Truth.assertThat(output).isEqualTo(
                "HTTP/1.1 201 Created\n" +
                        "Content-Length: ${"Hello Peter".length}\n" +
                        "Name: Peter\n" +
                        "Age: 35\n" +
                        "\n" +
                        "Hello Peter\n")
    }
}