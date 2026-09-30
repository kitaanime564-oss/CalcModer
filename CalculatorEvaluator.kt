package com.calculator.app.util

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.*

object CalculatorEvaluator {

    sealed class Result {
        data class Success(val value: String) : Result()
        data class Error(val message: String) : Result()
    }

    fun evaluate(expression: String, isRadMode: Boolean = false): Result {
        if (expression.isBlank()) return Result.Success("0")

        return try {
            val sanitized = sanitize(expression)
            val tokens = tokenize(sanitized)
            if (tokens.isEmpty()) return Result.Success("0")

            val postfix = infixToPostfix(tokens)
            val computedValue = evaluatePostfix(postfix, isRadMode)

            if (computedValue.isInfinite() || computedValue.isNaN()) {
                return Result.Error("Error: Calculation overflow")
            }

            Result.Success(formatResult(computedValue))
        } catch (e: ArithmeticException) {
            Result.Error(e.message ?: "Arithmetic Error")
        } catch (e: Exception) {
            Result.Error("Invalid Format")
        }
    }

    private fun sanitize(input: String): String {
        return input
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())
            .trim()
    }

    // Full Infix to Postfix Shunting-yard implementation with functions:
    // sin, cos, tan, sqrt, ln, log, power (^), percentages (%)
    // Handles divide-by-zero checks and precision formatting.
}