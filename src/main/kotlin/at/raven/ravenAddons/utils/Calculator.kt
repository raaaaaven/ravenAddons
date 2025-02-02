package at.raven.ravenAddons.utils

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object Calculator {
    fun String.calc(): Double? {
        val tokens = tokenize(this)
        if (tokens.isEmpty()) {
            ChatUtils.warning("Invalid input '$this'")
            return null
        }

        val rpn = toRPN(tokens)
        return evaluateRPN(rpn)
    }

    private fun calcCommand(input: String) {
        ChatUtils.chat("$input = ${input.calc()}")
    }

    private fun tokenize(expression: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var numberBuffer = ""

        for (char in expression) {
            if (char.isDigit() || char == '.') {
                numberBuffer += char
            } else {
                if (numberBuffer.isNotEmpty()) {
                    tokens.add(Token.Number(numberBuffer.toDouble()))
                    numberBuffer = ""
                }
                when (char) {
                    in OperatorType.getValidChars() -> {
                        val operator = OperatorType.entries.find { char in it.validChars }
                            ?: run {
                                ChatUtils.warning("Unknown operator: $char")
                                return emptyList()
                            }
                        tokens.add(Token.Operator(operator))
                    }

                    '(' -> tokens.add(Token.LeftParen)
                    ')' -> tokens.add(Token.RightParen)
                    else -> {
                        ChatUtils.warning("Invalid character in expression: $char")
                        return emptyList()
                    }
                }
            }
        }

        if (numberBuffer.isNotEmpty()) {
            tokens.add(Token.Number(numberBuffer.toDouble()))
        }

        return tokens
    }

    private fun toRPN(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val operators = ArrayDeque<Token>()

        for (token in tokens) {
            when (token) {
                is Token.Number -> output.add(token)
                is Token.Operator -> {
                    while (operators.isNotEmpty() && operators.last() is Token.Operator &&
                        (operators.last() as Token.Operator).type.priority >= token.type.priority
                    ) {
                        output.add(operators.removeLast())
                    }
                    operators.add(token)
                }

                Token.LeftParen -> operators.add(token)
                Token.RightParen -> {
                    while (operators.isNotEmpty() && operators.last() !is Token.LeftParen) {
                        output.add(operators.removeLast())
                    }
                    if (operators.isEmpty() || operators.last() !is Token.LeftParen) {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }
                    operators.removeLast()
                }
            }
        }

        while (operators.isNotEmpty()) {
            if (operators.last() is Token.LeftParen) {
                throw IllegalArgumentException("Mismatched parentheses")
            }
            output.add(operators.removeLast())
        }

        return output
    }

    private fun evaluateRPN(tokens: List<Token>): Double {
        val stack = ArrayDeque<Double>()

        for (token in tokens) {
            when (token) {
                is Token.Number -> stack.addLast(token.value)
                is Token.Operator -> {
                    val b = stack.removeLast()
                    val a = stack.removeLast()
                    stack.addLast(token.type.expression(a, b))
                }

                else -> {}
            }
        }

        return stack.last()
    }

    sealed class Token {
        data class Number(val value: Double) : Token()
        data class Operator(val type: OperatorType) : Token()
        object LeftParen : Token()
        object RightParen : Token()
    }

    enum class OperatorType(val validChars: Set<Char>, val priority: Int, val expression: (Double, Double) -> Double) {
        ADD(
            setOf('+'),
            0,
            { a, b -> a + b }
        ),
        SUBTRACT(
            setOf('-'),
            0,
            { a, b -> a - b }
        ),
        MULTIPLY(
            setOf('*', 'x'),
            1,
            { a, b -> a * b }
        ),
        DIVIDE(
            setOf('/'),
            1,
            { a, b -> a / b }
        );

        companion object {
            fun getValidChars(): List<Char> {
                val list = mutableListOf<Char>()
                entries.forEach { operator ->
                    operator.validChars.forEach { list.add(it) }
                }
                return list.toList()
            }
        }
    }

    @SubscribeEvent
    fun onCommand(event: CommandRegistrationEvent) {
        event.register("ratestcalc") {
            description = "dummy calculator test"
            callback { calcCommand(it.joinToString()) }
        }
    }
}
