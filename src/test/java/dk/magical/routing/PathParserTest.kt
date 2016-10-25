package dk.magical.routing

import com.google.common.truth.Truth
import org.junit.Assert.*
import org.junit.Test

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