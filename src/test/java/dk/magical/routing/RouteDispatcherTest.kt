package dk.magical.routing

import com.google.common.truth.Truth
import dk.magical.request.HttpMethod.GET
import dk.magical.request.HttpMethod.POST
import dk.magical.request.HttpRequest
import dk.magical.response.HttpResponse
import net.jodah.concurrentunit.Waiter
import org.junit.Test
import java.io.ByteArrayOutputStream
import kotlin.test.fail

/**
 * Created by Christian on 21/10/2016.
 */
class RouteDispatcherTest {
    val response: HttpResponse = HttpResponse(ByteArrayOutputStream())

    @Test
    fun shouldRejectNonExistingPath() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello", mapOf(), null)

        dispatcher.dispatch(request, response, listOf()) { message ->
            Truth.assertThat(message).isEqualTo("No handler for: ${request.method.name} ${request.path}.")
            waiter.resume()
        }

        waiter.await(1000)
    }

    @Test
    fun shouldCatchBasePath() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello", mapOf(), null)

        val router = Router("/hello")
        router.get("") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { fail(it) }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleLongBasePath() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/peter", mapOf(), null)

        val router = Router("/hello/peter")
        router.get("") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { fail(it) }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleBasePathAndRoute() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/peter", mapOf(), null)

        val router = Router("/hello")
        router.get("/peter") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { fail(it) }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleBasePathAndLongRoute() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/peter/name", mapOf(), null)

        val router = Router("/hello")
        router.get("/peter/name") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { fail(it) }

        waiter.await(1000)
    }

    @Test
    fun shouldRejectIfMethodMismatch() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(POST, "/hello", mapOf(), null)

        val router = Router("/hello")
        router.get("") { request, response -> }

        dispatcher.dispatch(request, response, listOf(router)) { message ->
            Truth.assertThat(message).isEqualTo("No handler for: ${request.method.name} ${request.path}.")
            waiter.resume()
        }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleMultiplePossibleRouters() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/user", mapOf(), null)

        val helloRouter = Router("/hello")
        helloRouter.get("") { request, response -> }

        val helloUserRouter = Router("/hello/user")
        helloUserRouter.get("") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(helloRouter, helloUserRouter)) { fail(it) }

        waiter.await(1000)
    }

    @Test(expected = IllegalArgumentException::class)
    fun ShouldRejectAddingExistingRouteToDifferentRouter() {
        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/user", mapOf(), null)

        val routerOne = Router("/hello")
        routerOne.get("user") { request, response -> }

        val routerTwo = Router("/hello/user")
        routerTwo.get("") { request, response -> }

        dispatcher.dispatch(request, response, listOf(routerOne, routerTwo)) { fail(it) }
    }
}