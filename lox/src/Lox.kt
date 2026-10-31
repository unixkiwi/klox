class Lox {
    companion object {
        var hadError: Boolean = false

        fun execute(source: String) {
            val scanner: Scanner = Scanner(source)
            val tokens: List<Token> = scanner.tokenize()


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
    }
}
