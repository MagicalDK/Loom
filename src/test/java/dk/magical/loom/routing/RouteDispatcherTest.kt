package dk.magical.loom.routing

import com.google.common.truth.Truth
import dk.magical.loom.routing.RouteDispatcher
import dk.magical.loom.routing.Router
import dk.magical.loom.request.HttpMethod.GET
import dk.magical.loom.request.HttpMethod.POST
import dk.magical.loom.request.HttpRequest
import dk.magical.loom.response.HttpResponse
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
        val request = HttpRequest(GET, "/hello", mapOf(), mapOf(), null, mapOf())

        dispatcher.dispatch(request, response, listOf()) { message, response ->
            Truth.assertThat(message).isEqualTo("No handler for: ${request.method.name} ${request.path}.")
            waiter.resume()
        }

        waiter.await(1000)
    }

    @Test
    fun shouldCatchBasePath() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleLongBasePath() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello/peter")
        router.get("") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleBasePathAndRoute() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("/peter") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleBasePathAndLongRoute() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/peter/name", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("/peter/name") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldRejectIfMethodMismatch() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(POST, "/hello", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("") { request, response -> }

        dispatcher.dispatch(request, response, listOf(router)) { message, response ->
            Truth.assertThat(message).isEqualTo("No handler for: ${request.method.name} ${request.path}.")
            waiter.resume()
        }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleMultiplePossibleRouters() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/user", mapOf(), mapOf(), null, mapOf())

        val helloRouter = Router("/hello")
        helloRouter.get("") { request, response -> fail("Wrong request") }

        val helloUserRouter = Router("/hello/user")
        helloUserRouter.get("") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(helloRouter, helloUserRouter)) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun ShouldRejectAddingExistingRouteToDifferentRouter() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/user", mapOf(), mapOf(), null, mapOf())

        val routerOne = Router("/hello")
        routerOne.get("user") { request, response -> }

        val routerTwo = Router("/hello/user")
        routerTwo.get("") { request, response -> }

        dispatcher.dispatch(request, response, listOf(routerOne, routerTwo)) { message, response ->
            Truth.assertThat(message).isEqualTo("More than one router with the path: /hello/user")
            waiter.resume()
        }

        waiter.await(1000)
    }

    @Test
    fun shouldAllowGenericUrls() {
        val waiter = Waiter()
        val dispatcher = RouteDispatcher()

        val router = Router("/hello")
        router.get("/{user}/name") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        var request = HttpRequest(GET, "/hello/user/name", mapOf(), mapOf(), null, mapOf())
        dispatcher.dispatch(request, response, listOf(router)) { message, response -> fail(message) }

        request = HttpRequest(GET, "/hello/player/name", mapOf(), mapOf(), null, mapOf())
        dispatcher.dispatch(request, response, listOf(router)) { message, response -> fail(message) }

        waiter.await(1000, 2)
    }

    @Test
    fun shouldGetUrlParameters() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/user/Peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("/user/{name}") { request, response ->
            Truth.assertThat(request.urlParameters).containsExactly("name", "Peter")
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldgetMultipleUrlParameters() {
        val waiter = Waiter()

        val dispatcher = RouteDispatcher()
        val request = HttpRequest(GET, "/hello/user/12345/Peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("/user/{userId}/{name}") { request, response ->
            Truth.assertThat(request.urlParameters).containsExactly("userId", "12345", "name", "Peter")
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router)) { message, response -> fail(message) }

        waiter.await(1000)
    }
}
