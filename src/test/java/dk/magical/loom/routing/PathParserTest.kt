package dk.magical.loom.routing

import com.google.common.truth.Truth
import dk.magical.loom.routing.PathParser
import org.junit.Assert.*
import org.junit.Test
import java.net.URI
import java.net.URL

/**
 * Created by Christian on 20/10/2016.
 */
class PathParserTest {
    @Test
    fun shouldEnsureSlashAtStart() {
        val parsedPath = PathParser.parse("user/id/name")
        Truth.assertThat(parsedPath).isEqualTo("/user/id/name")
    }

    @Test
    fun shouldRemoveSlashAtEnd() {
        val parsedPath = PathParser.parse("/user/id/name/")
        Truth.assertThat(parsedPath).isEqualTo("/user/id/name")
    }
}