class Lox {
    companion object {
        private val interpreter = Interpreter()
        var hadError: Boolean = false
        var hadRuntimeError: Boolean = false

        fun execute(source: String) {
            val scanner: Scanner = Scanner(source)
            val tokens: List<Token> = scanner.tokenize()
            val parser = Parser(tokens)
            val expression = parser.parse()

            if (hadError || expression == null) return

            interpreter.interpret(expression)

            /*println(AstPrinter().print(expression))

            for (token in tokens) {
                println(token)
            }*/
        }

        fun error(line: Int, msg: String) {
            report(line, "", msg)
        }

        private fun report(line: Int, where: String, msg: String) {
            System.err.println("[line $line] Error $where: $msg")
            hadError = true
        }

        fun error(token: Token, message: String) {
            if (token.type == TokenType.EOF) {
                report(token.line, " at end", message)
            } else {
                report(token.line, "at '" + token.lexeme + "'", message)
            }
        }

        fun runtimeError(error: RuntimeError) {
            System.err.println("${error.message}\n[line ${error.token.line}]")
            hadRuntimeError = true
        }
    }
}
