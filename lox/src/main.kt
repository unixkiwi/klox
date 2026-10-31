import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readText
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size == 1) {
        val path = Path(args[0])

        if (path.isDirectory()) {
            System.err.println("${path.fileName} is a directory, only files are allowed!\n\nUsage: klox [script]")
            exitProcess(64)
        } else if (!path.exists()) {
            System.err.println("${path.fileName} does not exist!\n\nUsage: klox [script]")
            exitProcess(64)
        }

        Lox.execute(path.readText())
        if (Lox.hadError) exitProcess(65)
        if (Lox.hadRuntimeError) exitProcess(70);
    } else {
        System.err.println("Usage: klox [script]")
        exitProcess(64)
    }
}