package dk.magical.response

import com.google.common.truth.Truth
import dk.magical.Status
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Christian on 26/10/2016.
 */
class HttpResponseBuilderTest {
    @Test
    fun shouldBuildStatusLine() {
        val response = HttpResponseBuilder.build(Status.OK, null)
        Truth.assertThat(response).contains("HTTP/1.1 200 OK")
    }

    @Test
    fun shouldAddCorrectContentLength() {
        val response = HttpResponseBuilder.build(Status.OK, "Hello")
        Truth.assertThat(response).contains("Content-Length: ${"Hello".length}")
    }

    @Test
    fun shouldAddBodyLineSeperator() {
        val response = HttpResponseBuilder.build(Status.OK, "Hello")
        Truth.assertThat(response).contains("")
    }

    @Test
    fun shouldAddBodyContent() {
        val response = HttpResponseBuilder.build(Status.OK, "Hello")
        Truth.assertThat(response).contains("Hello")
    }
}