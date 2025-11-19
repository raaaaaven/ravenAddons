package at.raven.ravenAddons.utils

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.ConfigFixEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object Calculator {
    fun String.calc(): Double? {
        val tokens = tokenize(this.replace(" ", ""))
        if (tokens.isEmpty()) return null

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
                    require(!(operators.isEmpty() || operators.last() !is Token.LeftParen)) { "Mismatched parentheses" }
                    operators.removeLast()
                }
            }
        }

        while (operators.isNotEmpty()) {
            require(operators.last() !is Token.LeftParen) { "Mismatched parentheses" }
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
        data object LeftParen : Token()
        data object RightParen : Token()
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
        event.register("calc") {
            description = "Calculates a given mathematical expression."
            callback { calcCommand(it.joinToString("")) }
        }
    }

    @SubscribeEvent
    fun onConfigFix(event: ConfigFixEvent) {
//         event.checkVersion(150) {
//             val tomlData = event.tomlData ?: return@checkVersion
//
//             tomlData.remove<Boolean>("general.miscellaneous.quick_maths!_solver")
//
//             event.tomlData = tomlData
//         }
    }
}
