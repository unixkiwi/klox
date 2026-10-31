class Lox {
    companion object {
        var hadError: Boolean = false

        fun execute(source: String) {
            val scanner: Scanner = Scanner(source)
            val tokens: List<Token> = scanner.tokenize()
            val parser = Parser(tokens)
            val expression = parser.parse()

            if (hadError || expression == null) return

            println(AstPrinter().print(expression))

            for (token in tokens) {
                println(token)
            }
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
    }
}
