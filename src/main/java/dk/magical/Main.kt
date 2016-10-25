package dk.magical

import dk.magical.routing.Router

/**
 * Created by Christian on 21/10/2016.
 */

fun main(args: Array<String>) {
    val loom = Loom(8080)

    val userRouter = Router("/user")

    userRouter.get("/name") { request, response ->
        println("User router:")
        println(request)

        response.status(Status.OK).body("Hello Christian").end()
    }

    loom.route(userRouter)

    loom.start()
}
