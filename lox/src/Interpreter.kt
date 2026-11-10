import Expr.Assign
import TokenType.*

class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Void?> {
    private var environment = Environment()

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

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        return environment.get(expr.name)
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

    private fun execute(stmt: Stmt) {
        stmt.accept(this)
    }

    fun executeBlock(
        statements: MutableList<Stmt?>,
        environment: Environment?
    ) {
        val previous: Environment = this.environment
        try {
            this.environment = environment!!

            for (statement in statements) {
                execute(statement!!)
            }
        } finally {
            this.environment = previous
        }
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Void? {
        executeBlock(stmt.statements, Environment(environment))
        return null
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Void? {
        evaluate(stmt.expression)
        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Void? {
        val value = evaluate(stmt.expression)
        println(stringify(value))
        return null
    }

    override fun visitVarStmt(stmt: Stmt.Var): Void? {
        var value: Any? = null
        value = evaluate(stmt.initializer)

        environment.define(stmt.name.lexeme, value)
        return null
    }

    override fun visitAssignExpr(expr: Assign): Any? {
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)
        return value
    }

    fun interpret(statements: List<Stmt>) {
        try {
            for (stmt in statements) {
                execute(stmt)
            }
        } catch (e: RuntimeError) {
            Lox.runtimeError(e)
        }
    }
}