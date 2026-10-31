class Token(val type: TokenType, val lexeme: String, val literal: Any?, line: Int) {
    override fun toString(): String {
        return "$type $lexeme $literal"
    }
}