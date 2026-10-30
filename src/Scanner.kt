import TokenType.*

class Scanner(val source: String) {
    private val tokens: MutableList<Token> = mutableListOf()
    private var start = 0
    private var current = 0
    private var line = 0

    companion object {
        private val keywords: Map<String, TokenType> = mapOf(
            "and" to AND,
            "class" to CLASS,
            "else" to ELSE,
            "false" to FALSE,
            "for" to FOR,
            "fun" to FUN,
            "if" to IF,
            "nil" to NIL,
            "or" to OR,
            "print" to PRINT,
            "return" to RETURN,
            "super" to SUPER,
            "this" to THIS,
            "true" to TRUE,
            "var" to VAR,
            "while" to WHILE
        )
    }

    fun tokenize(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        val c = consumeNext()
        when (c) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)

            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)

            '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) consumeNext()
                } else addToken(SLASH)
            }

            ' ', '\r', '\t' -> {}
            '\n' -> line++

            '"' -> string()

            else -> {
                if (isDigit(c)) number()
                else if (isAlpha(c)) identifier()
                else Lox.error(line, "Unexpected character.")
            }
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) consumeNext()

        val text = source.substring(start, current)
        val type = keywords[text] ?: IDENTIFIER

        addToken(type)
    }

    private fun number() {
        while (isDigit(peek())) consumeNext()

        if (peek() == '.' && isDigit(peekNext())) {
            consumeNext() // consume "."

            while (isDigit(peek())) consumeNext()
        }

        addToken(NUMBER, source.substring(start, current).toDouble())
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            consumeNext()
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        consumeNext() // for closing "

        addToken(STRING, source.substring(start + 1, current - 1))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd() || source[current] != expected) return false
        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun isAlpha(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z' || c == '_'

    private fun isDigit(c: Char): Boolean = c in '0'..'9'

    private fun isAlphaNumeric(c: Char): Boolean = isAlpha(c) || isDigit(c)

    private fun consumeNext(): Char = source[current++]

    private fun addToken(type: TokenType) = addToken(type, null)

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun isAtEnd(): Boolean = current >= source.length
}