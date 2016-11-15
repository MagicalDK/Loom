package dk.magical.loom

import dk.magical.loom.request.HttpRequest
import dk.magical.loom.request.HttpRequestParser
import dk.magical.loom.response.HttpResponse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.util.concurrent.ExecutorService

/**
 * Created by Christian on 18/10/2016.
 */
class HttpServerSocket(private val executorService: ExecutorService, private val requestParser: HttpRequestParser, port: Int) {
    val serverSocket: ServerSocket

    init {
        serverSocket = ServerSocket(port)
    }

    fun listen(handler: (HttpRequest, HttpResponse) -> Unit) {
        val socket = serverSocket.accept()

        executorService.execute {
            val streamReader = InputStreamReader(socket.inputStream, Charsets.UTF_8)
            val bufferedReader = BufferedReader(streamReader)

            val request = requestParser.parse(bufferedReader) ?: return@execute
            val response = HttpResponse(socket.outputStream)
            handler(request, response)
        }
    }
}