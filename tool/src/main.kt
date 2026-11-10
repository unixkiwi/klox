import java.io.PrintWriter
import java.util.*
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>");
        exitProcess(64);
    }

    val outputDir = args[0];
    defineAst(
        outputDir, "Expr", listOf(
            "Binary   | val left: Expr, val operator: Token, val right: Expr",
            "Grouping | val expression: Expr",
            "Literal  | val value: Any?",
            "Unary    | val operator: Token, val right: Expr",
            "Variable | val name: Token"
        )
    )

    defineAst(
        outputDir, "Stmt", listOf(
            "Expression | val expression: Expr",
            "Print      | val expression: Expr",
            "Var        | val name: Token, val initializer: Expr"
        )
    );
}

private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, "UTF-8")

    writer.println("abstract class $baseName {")

    defineVisitor(writer, baseName, types)
    writer.println()

    for (type in types) {
        val className = type.split("|")[0].trim()
        val fields = type.split("|")[1].trim()
        defineType(writer, baseName, className, fields)
    }

    writer.println("  abstract fun <R> accept(visitor: Visitor<R>): R")

    writer.println("}")
    writer.close()
}

private fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>) {
    writer.println("  interface Visitor<R> {")

    for (type in types) {
        val typeName = type.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].trim { it <= ' ' }
        writer.println(
            "    fun visit" + typeName + baseName + "(" + baseName.lowercase(Locale.getDefault()) + ": " + typeName + "): R"
        )
    }

    writer.println("  }")
}

private fun defineType(writer: PrintWriter, baseName: String, className: String, fieldList: String) {
    writer.println("  class $className($fieldList) : $baseName() { ")

    writer.println("    override fun <R> accept(visitor: Visitor<R>): R = visitor.visit$className$baseName(this)")
    writer.println("  }")
    writer.println()
}