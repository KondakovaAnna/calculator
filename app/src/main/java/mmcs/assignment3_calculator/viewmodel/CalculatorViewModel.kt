package mmcs.assignment3_calculator.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import kotlin.math.roundToInt

class CalculatorViewModel: BaseObservable(), Calculator {
    var input: String = "0"
    override var display = ObservableField<String>()

    override fun addDigit(dig: Int) {
        if (input == "0")
            input = ""
        input += dig.toString()
        display.set(input)
    }

    override fun addPoint() {
        if (!input.contains("."))
            input += "."
        display.set(input)
    }

    override fun addOperation(op: Operation) {
        if (op == Operation.ADD){
            if (!(input.last() == '+'))
                input += "+"
        }
        if (op == Operation.SUB){
            if (input == "0")
                input = ""
            if (!(input.last() =='-'))
                input += "-"
        }
        if (op == Operation.MUL){
            if (!(input.last() =='×'))
                input += "×"
        }
        if (op == Operation.DIV){
            if (!(input.last() =='÷'))
                input += "÷"
        }
        display.set(input)
    }

    override fun compute() {
        val values = ArrayDeque<Float>()
        val ops = ArrayDeque<Char>()

        var i = -1
        while (i < input.length-1) {
            i++
            val ch = input[i]
            if (ch == ' ')
                continue

            if (ch.isDigit()) {
                var dig = ""
                dig += ch
                var next = input.elementAtOrNull(i+1)
                while (next?.isDigit() == true || next == '.'){
                    dig += next
                    i++
                    next = input.elementAtOrNull(i+1)
                }
                values.add(dig.toFloat())
            }
            else if (ch == '(') {
                ops.add(ch)
            }
            else if (ch == ')') {
                while (ops.last() != '(') {
                    values.add(applyOp(
                        ops.removeLast(), values.removeLast(), values.removeLast()))
                }
                ops.removeLast()
            }
            else if (ch == '+' || ch == '-' || ch == '×' || ch == '÷') {
                while (ops.isNotEmpty() &&
                    hasPrecedence(ch, ops.last())) {
                    values.add(applyOp(ops.removeLast(), values.removeLast(), values.removeLast()))
                }
                ops.add(ch)
            }
        }
        while (ops.isNotEmpty()) {
            values.add(applyOp(ops.removeLast(), values.removeLast(), values.removeLast()))
        }

        val res = values.removeLast()
        var resToDisplay = res.toString()
        if (res - res.toInt() == 0f){
            resToDisplay = res.toInt().toString()
        }
        input = resToDisplay
        display.set(input)
    }

    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') {
            return false
        }
        return !((op1 == '×' || op1 == '÷') && (op2 == '+' || op2 == '-'))
    }

    private fun applyOp(op: Char, b: Float, a: Float): Float {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '×' -> a * b
            '÷' -> {
                if (b == 0f) {
                    throw UnsupportedOperationException("Cannot divide by zero!")
                }
                a / b
            }
            else -> 0f
        }
    }

    override fun clear() {
        input.dropLast(1)
        display.set(input)
    }

    override fun reset() {
        input = "0"
        display.set(input)
    }
}