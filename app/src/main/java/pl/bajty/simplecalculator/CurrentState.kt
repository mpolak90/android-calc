package pl.bajty.simplecalculator

import androidx.core.text.isDigitsOnly
import java.lang.Double.parseDouble
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

class CurrentState(private val mainActivity : MainActivity) {
    private var lastResult = "0"
    private var started = false
    private var currentState = "0"
    private val calc = CalculateEquation(mainActivity)

    private val storage: MutableList<String> = mutableListOf()

    fun getStorage() : MutableList<String> {
        return storage
    }

    fun getLastResult() : String {
        return lastResult
    }

    private fun start() {
        started = true
    }

    fun end() {
        started = false
        currentState = "0"
    }

    private fun isStarted() : Boolean {
        return started
    }

    fun getBin(number : String) : String {
        val int = number.toDouble().roundToInt();
        return Integer.toBinaryString(int)
    }

    fun getHex(number : String) : String {
        val int = number.toDouble().roundToInt()
        return Integer.toHexString(int)
    }

    fun addAns() {
        val lastResult = getLastResult()
        if (!isStarted()) {
            start()
        }
        val tempState = getState().split(" ").toMutableList()
        if (tempState.last().replace(".", "").isDigitsOnly()) {
            tempState[tempState.size - 1] = lastResult
        } else if (listOf<String>("+", "−", "×", "÷", "^").contains(tempState.last().takeLast(1))) {
            tempState.add(lastResult)
        }
        currentState = tempState.joinToString(" ")
    }

    fun clearLast() {
        if (isStarted()) {
            currentState = if (isNumeric(currentState.split(" ").last().replace("(", ""))) {
                currentState.substring(0, currentState.length - 1)
            } else if(currentState.takeLast(1) == "(" || currentState.takeLast(1) == ")") {
                if (currentState.takeLast(2) == " (") {
                    currentState.substring(0, currentState.length - 2)
                } else {
                    currentState.substring(0, currentState.length - 1)
                }
            } else {
                currentState.substring(0, currentState.length - 2)
            }
        }
        currentState = currentState.trim()
        if (currentState == "") {
            currentState = "0"
            end()
        }
    }

    fun continueSymbol(symbol : String) {
        if (isStarted()) {
            var tempState = currentState.split(" ").toMutableList()
            when {
                currentState.takeLast(1) == "." -> {
                    currentState = currentState.substring(0, currentState.length - 1) + " $symbol"
                }
                currentState.takeLast(1) == ")" -> {
                    currentState += " $symbol"
                }
                isNumeric(currentState.split(" ").last().replace("(", "")) -> {
                    if (tempState.last().contains("(")) {
                        val lastBracket = tempState.last().lastIndexOf('(')
                        tempState[tempState.size - 1] = tempState.last().substring(0, lastBracket + 1) + normalizeNumber(tempState.last().replace("(", ""))
                    } else {
                        tempState[tempState.size - 1] = normalizeNumber(tempState.last())
                    }
                    currentState = tempState.joinToString(" ")
                    currentState += " $symbol"
                }
                currentState.takeLast(1) == "(" -> {}
                currentState.takeLast(1) == "%" -> {
                    val normalize = normalizeNumber(tempState.last().replace("%", "").replace(")", "").replace("(", ""))
                    when {
                        currentState.takeLast(2) == ")%" -> {
                            tempState[tempState.size - 1] = "$normalize)% $symbol"
                        }
                        tempState.last().startsWith("(") -> {
                            tempState[tempState.size - 1] = "($normalize% $symbol"
                        }
                        else -> {
                            tempState[tempState.size - 1] = "$normalize% $symbol"
                        }
                    }
                    currentState = tempState.joinToString(" ")
                }

                else -> {
                    tempState = currentState.split(" ").toMutableList()
                    tempState[tempState.lastIndex] = symbol
                    currentState = tempState.joinToString(" ")
                }
            }
        } else {
            start()
            currentState = "0 $symbol"
        }
    }

    fun continueDigit(number : String) {
        if (!isStarted()) {
            start()
            currentState = number
        } else {
            currentState += if (!isNumeric(currentState.split(" ").last())) {
                //if (isNumeric(currentState.split(" ").last().replace("(", "")) || currentState.takeLast(1) == "(" || currentState.takeLast(1) == " ") {
                if (isNumeric(currentState.takeLast(1)) || currentState.takeLast(1) == "(" || currentState.takeLast(1) == ".") {
                    number
                } else {
                    if (currentState.takeLast(1) == ")") {
                        " × $number"
                    } else {
                        " $number"
                    }
                }
            } else {
                number
            }
        }
    }

    fun continuePercent() {
        if (currentState.takeLast(1).isDigitsOnly() || currentState.takeLast(1) == ")") {
            currentState += "%"
        }
    }

    fun addComma() {
        if (isNumeric(currentState.takeLast(1)) && !currentState.split(" ").last().contains(".")) {
            if (isStarted()) {
                currentState += "."
            } else {
                start()
                currentState = "0."
            }
        }
    }

    fun addOpenBracket() {
        if (isStarted()) {
            currentState += if (isNumeric(currentState.split(" ").last().replace("(", "")) || currentState.takeLast(1) == ")") {
                " ×" + " ("
            } else if (currentState.takeLast(1) == "(" || currentState.takeLast(1) == " ") {
                "("
            } else {
                " ("
            }
        } else {
            start()
            currentState = "("
        }
    }

    fun addCloseBracket() {
        if (currentState.filter { it == '(' }.count() > currentState.filter { it == ')' }.count()) {
            if (isNumeric(currentState.split(" ").last().replace("(", "")) || currentState.split(" ").last().takeLast(1) == ")" || currentState.takeLast(1) == "%") {
                currentState += ")"
            }
        }
    }

    fun getState() : String {
        return currentState
    }

    private fun isNumeric(string : String) : Boolean {
        try {
            parseDouble(string)
        } catch (e: NumberFormatException) {
            return false
        }
        return true
    }

    private fun normalizeNumber(number : String) : String {
        return if (number.takeLast(1) == ".") {
            number.substring(0, number.length - 1)
        } else if (number.contains(".") && number.takeLast(1) == "0") {
            normalizeNumber(number.substring(0, number.length - 1))
        } else {
            BigDecimal(number).toString()
        }
    }

    fun equality() {
        if (calc.correctBrackets(currentState)) {
            val result = calc.calculate(currentState)
            if (calc.isCorrectEquation(result)) {
                if (!calc.isZeroDivideException(result)) {
                    if (result == "Infinity" || result == "-Infinity") {
                        mainActivity.showToast(mainActivity.getString(R.string.infinity))
                    } else {
                        val simpleResult =
                            BigDecimal(result).setScale(10, RoundingMode.HALF_UP)
                                .toDouble().toString()
                        println(simpleResult)

                        lastResult = if (simpleResult.takeLast(2) == ".0") {
                            simpleResult.substring(0, simpleResult.length - 2)
                        } else {
                            simpleResult
                        }
                        storage.add("$currentState = $simpleResult")
                        currentState = lastResult
                    }
                }
            }
        }
    }
}