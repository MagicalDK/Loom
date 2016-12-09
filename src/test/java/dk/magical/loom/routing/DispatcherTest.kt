package dk.magical.loom.routing

import com.google.common.truth.Truth
import dk.magical.loom.Dispatcher
import dk.magical.loom.middleware.Middleware
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
class DispatcherTest {
    val response: HttpResponse = HttpResponse(ByteArrayOutputStream())

    @Test
    fun shouldRejectNonExistingPath() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/hello", mapOf(), mapOf(), null, mapOf())

        dispatcher.dispatch(request, response, listOf(), listOf()) { message, response ->
            Truth.assertThat(message).isEqualTo("No handler for: ${request.method.name} ${request.path}.")
            waiter.resume()
        }

        waiter.await(1000)
    }

    @Test
    fun shouldCatchBasePath() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/hello", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleLongBasePath() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/hello/peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello/peter")
        router.get("") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleBasePathAndRoute() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/hello/peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("/peter") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleBasePathAndLongRoute() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/hello/peter/name", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("/peter/name") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldRejectIfMethodMismatch() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(POST, "/hello", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("") { request, response -> }

        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response ->
            Truth.assertThat(message).isEqualTo("No handler for: ${request.method.name} ${request.path}.")
            waiter.resume()
        }

        waiter.await(1000)
    }

    @Test
    fun shouldHandleMultiplePossibleRouters() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/hello/user", mapOf(), mapOf(), null, mapOf())

        val helloRouter = Router("/hello")
        helloRouter.get("") { request, response -> fail("Wrong request") }

        val helloUserRouter = Router("/hello/user")
        helloUserRouter.get("") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(helloRouter, helloUserRouter), listOf()) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldAllowGenericUrls() {
        val waiter = Waiter()
        val dispatcher = Dispatcher()

        val router = Router("/hello")
        router.get("/{user}/name") { request, response ->
            Truth.assertThat(request).isEqualTo(request)
            waiter.resume()
        }

        var request = HttpRequest(GET, "/hello/user/name", mapOf(), mapOf(), null, mapOf())
        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response -> fail(message) }

        request = HttpRequest(GET, "/hello/player/name", mapOf(), mapOf(), null, mapOf())
        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response -> fail(message) }

        waiter.await(1000, 2)
    }

    @Test
    fun shouldGetUrlParameters() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/hello/user/Peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("/user/{name}") { request, response ->
            Truth.assertThat(request.urlParameters).containsExactly("name", "Peter")
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldgetMultipleUrlParameters() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/hello/user/12345/Peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/hello")
        router.get("/user/{userId}/{name}") { request, response ->
            Truth.assertThat(request.urlParameters).containsExactly("userId", "12345", "name", "Peter")
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldAllowGetAllAndGetOne() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/users", mapOf(), mapOf(), null, mapOf())

        val router = Router("/users")

        router.get("/") { request, response ->
            waiter.resume()
        }

        router.post("/") { request, response ->
            fail("Wrong router")
        }

        router.get("/{userId}") { request, response ->
            fail("Wrong router")
        }

        dispatcher.dispatch(request, response, listOf(router), listOf()) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldAbortFromMiddleware() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/users/Peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/users")

        router.get("/Peter") { request, response ->
            fail("Should not hit router")
        }

        val middleware = Middleware(listOf(GET), "/hello/users") { request, response, next ->
            waiter.resume()
        }

        dispatcher.dispatch(request, response, listOf(router), listOf(middleware)) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldPassMiddleware() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/users/Peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/users")

        router.get("/Peter") { request, response ->
            waiter.resume()
        }

        val middleware = Middleware(listOf(GET), "/users/Peter") { request, response, next -> next() }

        dispatcher.dispatch(request, response, listOf(router), listOf(middleware)) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldAbortFromSecondMiddleware() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/users/Peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/users")

        router.get("/Peter") { request, response ->
            fail("Should not hit router")
        }

        val firstMiddleware = Middleware(listOf(GET), "/users/Peter") { request, response, next -> next() }
        val secondMiddleware = Middleware(listOf(GET), "/users/Peter") { request, response, next -> waiter.resume() }

        dispatcher.dispatch(request, response, listOf(router), listOf(firstMiddleware, secondMiddleware)) { message, response -> fail(message) }

        waiter.await(1000)
    }

    @Test
    fun shouldPassTwoMiddleware() {
        val waiter = Waiter()

        val dispatcher = Dispatcher()
        val request = HttpRequest(GET, "/users/Peter", mapOf(), mapOf(), null, mapOf())

        val router = Router("/users")

        router.get("/Peter") { request, response ->
            waiter.resume()
        }

        val firstMiddleware = Middleware(listOf(GET), "/users/Peter") { request, response, next -> next() }
        val secondMiddleware = Middleware(listOf(GET), "/users/Peter") { request, response, next -> next() }

        dispatcher.dispatch(request, response, listOf(router), listOf(firstMiddleware, secondMiddleware)) { message, response -> fail(message) }

        waiter.await(1000)
    }
}
