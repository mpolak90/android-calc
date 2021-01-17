package pl.bajty.simplecalculator

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

class CalculateEquation(private val mainActivity : MainActivity) {
    fun calculate(equation: String): String {
        val operationCount =
                equation.filter { it == '×' }.count() +
                        equation.filter { it == '÷' }.count() +
                        equation.filter { it == '−' }.count() +
                        equation.filter { it == '^' }.count() +
                        equation.filter { it == '+' }.count()
        println("Liczba argumentów: $operationCount")
        return if (operationCount * 2 + 1 == equation.split(" ").size) {

            val withoutBrackets = calculateBrackets(equation)
            val result = calculateWithoutBrackets(withoutBrackets)
            println(result)
            result
        } else {
            "invalidArguments"
        }
    }

    private fun calculateWithoutBrackets(equation: String): String {
        println("start calculate")

        var tempEquation = equation

        if (isHavingPercent(tempEquation)) {
            tempEquation = calculatePercent(tempEquation)
        }

        if (isHavingPower(tempEquation)) {
            tempEquation = calculatePowers(tempEquation)
        }

        if (isFirstIterate(tempEquation)) {
            tempEquation = firstIterate(tempEquation)
        }

        if (isSecondIterate(tempEquation)) {
            tempEquation = secondIterate(tempEquation)
        }
        return tempEquation
    }

    fun correctBrackets(equation: String): Boolean {
        return if (equation.count { equation.contains('(') } == equation.count { equation.contains(')') }) {
            true
        } else {
            mainActivity.showToast(mainActivity.getString(R.string.bracketError))
            false
        }
    }

    private fun calculateBrackets(equation: String): String {
        if (equation.contains('(')) {
            var openBracket = 0
            var closeBracket = 0
            for ((index, character) in equation.split("").withIndex()) {
                if (character == "(") {
                    openBracket = index
                } else if (character == ")") {
                    closeBracket = index
                    break
                }
            }
            val tempResult = calculate(equation.substring(openBracket, closeBracket - 1))
            println("wynik z nawiasu: $tempResult")
            var tempEquation = equation.substring(0, openBracket - 1)
            println("równanie do nawiasu: $tempEquation")
            tempEquation += tempResult
            println("równanie z wynikiem: $tempEquation")
            tempEquation += equation.substring(closeBracket, equation.length)
            println("równanie całe: $tempEquation")
            return calculateBrackets(tempEquation)
        }
        return equation
    }

    private fun isFirstIterate(equation: String): Boolean {
        println("updated equation is $equation")
        return equation.contains(" × ") || equation.contains(" ÷ ")
    }

    private fun firstIterate(equation: String): String {
        if (isFirstIterate(equation)) {
            val equationArray = equation.split(" ")
            val newValue: String
            var updatedEquation = ""
            for (i in 1 until equationArray.size) {
                if (equationArray[i] == "×") {
                    println(equation)
                    val a = BigDecimal(equationArray[i - 1])
                    val b = BigDecimal(equationArray[i + 1])
                    newValue = a.times(b).toString()
                    for (j in 0..i - 2) {
                        updatedEquation += equationArray[j] + " "
                    }
                    updatedEquation += newValue
                    for (j in i + 2 until equationArray.size) {
                        updatedEquation += " " + equationArray[j]
                    }
                    println(updatedEquation)
                    return firstIterate(updatedEquation)
                } else if (equationArray[i] == "÷") {
                    val a = BigDecimal(equationArray[i - 1])
                    if (equationArray[i + 1].toDouble().toString() == "0.0") {
                        return "zeroDivideException"
                    }
                    val b = BigDecimal(equationArray[i + 1])
                    newValue = a.divide(b, 15, RoundingMode.CEILING).toString()
                    for (j in 0..i - 2) {
                        updatedEquation += equationArray[j] + " "
                    }
                    updatedEquation += newValue
                    for (j in i + 2 until equationArray.size) {
                        updatedEquation += " " + equationArray[j]
                    }
                    return firstIterate(updatedEquation)
                }
            }
        }
        return equation
    }

    private fun isSecondIterate(equation: String): Boolean {
        println("is second iterate for $equation")
        return equation.contains(" + ") || equation.contains(" − ")
    }

    private fun secondIterate(equation: String): String {
        if (isSecondIterate(equation)) {
            val equationArray = equation.split(" ")
            val newValue: String
            var updatedEquation = ""

            for (i in 1 until equationArray.size) {
                if (equationArray[i] == "+") {
                    val a = BigDecimal(equationArray[i - 1])
                    val b = BigDecimal(equationArray[i + 1])
                    newValue = a.plus(b).toString()
                    for (j in 0..i - 2) {
                        updatedEquation += equationArray[j] + " "
                    }
                    updatedEquation += newValue
                    for (j in i + 2 until equationArray.size) {
                        updatedEquation += " " + equationArray[j]
                    }
                    return secondIterate(updatedEquation)
                } else if (equationArray[i] == "−") {
                    val a = BigDecimal(equationArray[i - 1])
                    val b = BigDecimal(equationArray[i + 1])
                    newValue = a.minus(b).toString()
                    for (j in 0..i - 2) {
                        updatedEquation += equationArray[j] + " "
                    }
                    updatedEquation += newValue
                    for (j in i + 2 until equationArray.size) {
                        updatedEquation += " " + equationArray[j]
                    }
                    return secondIterate(updatedEquation)
                }
            }
        }
        return equation
    }

    private fun isHavingPower(equation: String) : Boolean {
        println("is having power for $equation")
        return equation.contains(" ^ ")
    }

    private fun calculatePowers(equation: String) : String {
        if (isHavingPower(equation)) {
            val equationArray = equation.split(" ")
            val newValue: String
            var updatedEquation = ""

            for (i in 1 until equationArray.size) {
                if (equationArray[i] == "^") {
                    val a = equationArray[i - 1].toDouble()
                    val b = equationArray[i + 1].toDouble()
                    newValue = a.pow(b).toString();
                    for (j in 0..i - 2) {
                        updatedEquation += equationArray[j] + " "
                    }
                    updatedEquation += newValue
                    for (j in i + 2 until equationArray.size) {
                        updatedEquation += " " + equationArray[j]
                    }
                    return calculatePowers(updatedEquation)
                }
            }
        }
        return equation
    }

    private fun isHavingPercent(equation: String) : Boolean {
        println("is having percent for $equation")
        return equation.contains("%")
    }

    private fun calculatePercent(equation: String) : String {
        if (isHavingPercent(equation)) {
            val equationArray = equation.split(" ")
            val newValue: String
            var updatedEquation = ""

            for (i in equationArray.indices) {
                if (isHavingPercent(equationArray[i])) {
                    val oldValue = BigDecimal(equationArray[i].replace("%", ""))
                    newValue = oldValue.times(BigDecimal("0.01")).toString()
                    for (j in 0 until i) {
                        updatedEquation += equationArray[j] + " "
                    }
                    updatedEquation += newValue
                    for (j in i + 1 until equationArray.size) {
                        updatedEquation += " " + equationArray[j]
                    }
                    return calculatePercent(updatedEquation)
                }
            }
        }
        return equation
    }


    fun isZeroDivideException(equation : String) : Boolean {
        return if (equation == "zeroDivideException") {
            mainActivity.showToast(mainActivity.getString(R.string.zeroDivideException))
            true
        } else {
            false
        }
    }

    fun isCorrectEquation(equation : String) : Boolean {
        return if (equation == "invalidArguments") {
            mainActivity.showToast(mainActivity.getString(R.string.invalidArguments))
            false
        } else {
            true
        }
    }
}