package dk.magical.loom

import dk.magical.loom.routing.Router

/**
 * Created by Christian on 21/10/2016.
 */

fun main(args: Array<String>) {
    val loom = Loom(8080)

    val userRouter = Router("/user")

    userRouter.post("/name") { request, response ->
        println("User router:")
        println(request)

        response.status(Status.OK).body("Hello Christian æøå").end()
    }

    loom.route(userRouter)

    loom.start()
}
