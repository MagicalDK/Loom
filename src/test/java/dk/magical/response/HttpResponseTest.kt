package dk.magical.response

import com.google.common.truth.Truth
import dk.magical.Status
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
}