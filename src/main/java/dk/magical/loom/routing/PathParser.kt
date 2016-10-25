package dk.magical.loom.routing

/**
 * Created by Christian on 20/10/2016.
 */
object PathParser {
    fun parse(path: String): String {
        val strippedPath = path.removeSuffix("/")
        if (strippedPath.isEmpty())
            return strippedPath

        if (strippedPath[0] == '/')
            return strippedPath

        return "/${strippedPath}"
    }
}