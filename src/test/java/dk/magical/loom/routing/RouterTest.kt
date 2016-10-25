package dk.magical.loom.routing

import dk.magical.loom.routing.Router
import org.junit.Test

/**
 * Created by Christian on 23/10/2016.
 */
class RouterTest {
    @Test(expected = IllegalArgumentException::class)
    fun shouldRejectExistingRoute() {
        val router = Router("/hello")
        router.get("user") { request, response -> }
        router.get("user") { request, response -> }
    }

    @Test
    fun shouldAllowSamePathForDifferentMathods() {
        val router = Router("/hello")
        router.get("user") { request, response -> }
        router.post("user") { request, response -> }
    }
}