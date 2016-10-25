package dk.magical

import dk.magical.request.HttpRequest
import dk.magical.request.HttpRequestParser
import dk.magical.response.HttpResponse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

/**
 * Created by Christian on 18/10/2016.
 */
class HttpServerSocket(port: Int) {
    val serverSocket: ServerSocket

    init {
        serverSocket = ServerSocket(port)
    }

    fun listen(handler: (HttpRequest, HttpResponse) -> Unit) {
        val socket = serverSocket.accept()
        val streamReader = InputStreamReader(socket.inputStream)
        val bufferedReader = BufferedReader(streamReader)

        val request = HttpRequestParser.parse(bufferedReader) ?: return
        val response = HttpResponse(socket.outputStream)
        handler(request, response)
    }
}