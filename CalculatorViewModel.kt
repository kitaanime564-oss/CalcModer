package com.calculator.app.ui

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.calculator.app.ads.AdMobManager
import com.calculator.app.util.CalculatorEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_EXPRESSION = "calc_expression"
        private const val KEY_RAD_MODE = "calc_rad_mode"
        private const val KEY_AC_COUNT = "calc_ac_count"
    }

    private val _uiState = MutableStateFlow(
        CalculatorUiState(
            expression = savedStateHandle.get<String>(KEY_EXPRESSION) ?: "",
            isRadMode = savedStateHandle.get<Boolean>(KEY_RAD_MODE) ?: false,
            acPressCount = savedStateHandle.get<Int>(KEY_AC_COUNT) ?: 0
        )
    )
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var adMobManager: AdMobManager? = null

    fun setAdMobManager(manager: AdMobManager) {
        this.adMobManager = manager
    }

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val newExpr = if (state.isEvaluated) digit else state.expression + digit
            saveExpression(newExpr)
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                errorMessage = null,
                previewResult = computePreview(newExpr, state.isRadMode)
            )
        }
    }

    fun onDecimal() {
        _uiState.update { state ->
            val expr = if (state.isEvaluated) "0" else state.expression
            val lastNumberToken = expr.takeLastWhile { it.isDigit() || it == '.' }
            if (lastNumberToken.contains('.')) return@update state

            val newExpr = if (expr.isEmpty() || !expr.last().isDigit()) expr + "0." else expr + "."
            saveExpression(newExpr)
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                errorMessage = null,
                previewResult = computePreview(newExpr, state.isRadMode)
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isEmpty()) {
                if (op == "-") {
                    val newExpr = "-"
                    saveExpression(newExpr)
                    return@update state.copy(expression = newExpr, isEvaluated = false)
                }
                return@update state
            }

            val lastChar = expr.last()
            val newExpr = if (lastChar in listOf('+', '-', '×', '÷', '^')) {
                expr.dropLast(1) + op
            } else {
                expr + op
            }

            saveExpression(newExpr)
            state.copy(
                expression = newExpr,
                isEvaluated = false,
                errorMessage = null,
                previewResult = computePreview(newExpr, state.isRadMode)
            )
        }
    }

    fun onClear(activity: Activity? = null) {
        val nextCount = _uiState.value.acPressCount + 1
        savedStateHandle[KEY_AC_COUNT] = nextCount

        // Interstitial Ad triggered on every 5th AC click
        if (nextCount % 5 == 0 && activity != null) {
            adMobManager?.showInterstitialAd(activity)
        }

        saveExpression("")
        _uiState.update {
            it.copy(
                expression = "",
                previewResult = "",
                isEvaluated = false,
                acPressCount = nextCount,
                errorMessage = null
            )
        }
    }

    fun clearHistory(activity: Activity? = null) {
        // Interstitial Ad triggered on clearing history
        if (activity != null) {
            adMobManager?.showInterstitialAd(activity)
        }
        _uiState.update { it.copy(history = emptyList(), isHistoryOpen = false) }
    }

    private fun saveExpression(expr: String) {
        savedStateHandle[KEY_EXPRESSION] = expr
    }

    private fun computePreview(expr: String, isRad: Boolean): String {
        if (expr.length < 2) return ""
        val last = expr.last()
        if (last in listOf('+', '-', '×', '÷', '^', '(', '.')) return ""

        return when (val res = CalculatorEvaluator.evaluate(expr, isRad)) {
            is CalculatorEvaluator.Result.Success -> res.value
            is CalculatorEvaluator.Result.Error -> ""
        }
    }
}