package dk.magical.loom.response

import com.google.common.truth.Truth
import dk.magical.loom.Status
import dk.magical.loom.response.HttpResponseBuilder
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Christian on 26/10/2016.
 */
class HttpResponseBuilderTest {
    @Test
    fun shouldBuildStatusLine() {
        val response = HttpResponseBuilder.build(Status.OK, mapOf(), null)
        Truth.assertThat(response).contains("HTTP/1.1 200 OK")
    }

    @Test
    fun shouldAddCorrectContentLength() {
        val response = HttpResponseBuilder.build(Status.OK, mapOf(), "Hello")
        Truth.assertThat(response).contains("Content-Length: ${"Hello".length}")
    }

    @Test
    fun shouldAddBodyLineSeperator() {
        val response = HttpResponseBuilder.build(Status.OK, mapOf(), "Hello")
        Truth.assertThat(response).contains("")
    }

    @Test
    fun shouldAddBodyContent() {
        val response = HttpResponseBuilder.build(Status.OK, mapOf(), "Hello")
        Truth.assertThat(response).contains("Hello")
    }
}