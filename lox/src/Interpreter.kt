import TokenType.*

class Interpreter : Expr.Visitor<Any?> {
    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            GREATER -> {
                checkNumberOperands(expr.operator, left, right);
                left as Double > right as Double
            }

            GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                left as Double >= right as Double
            }

            LESS -> {
                checkNumberOperands(expr.operator, left, right);
                (left as Double) < (right as Double)
            }

            LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                left as Double <= right as Double
            }

            BANG_EQUAL -> !isEqual(left, right)
            EQUAL_EQUAL -> isEqual(left, right)

            MINUS -> {
                checkNumberOperands(expr.operator, right, left)
                left as Double - right as Double
            }

            PLUS -> {
                if (left is Double && right is Double) return left + right
                if (left is String && right is String) return left + right

                throw RuntimeError(
                    expr.operator,
                    right?.javaClass?.simpleName + "Operands must be two number or two strings!"
                )
            }

            SLASH -> {
                checkNumberOperands(expr.operator, left, right);
                left as Double / right as Double
            }

            STAR -> {
                checkNumberOperands(expr.operator, left, right);
                left as Double * right as Double
            }

            else -> null
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? = evaluate(expr.expression)

    override fun visitLiteralExpr(expr: Expr.Literal): Any? = expr.value

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            BANG -> !isTruthy(right)
            MINUS -> -(right as Double)
            else -> null
        }
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number!")
    }


    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers!")
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false

        return a == b
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"

        if (obj is Double) {
            var text = obj.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }

        return obj.toString()
    }

    private fun evaluate(expr: Expr): Any? = expr.accept(this)

    fun interpret(expression: Expr) {
        try {
            val value = evaluate(expression)
            println(stringify(value))
        } catch (e: RuntimeError) {
            Lox.runtimeError(e)
        }
    }
}