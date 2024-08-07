package com.example.app1

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayDeque

class MainActivity : AppCompatActivity() {

    private lateinit var txt_1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txt_1 = findViewById(R.id.txt_1)
    }

    fun presionarBot(view: View) {
        val num2: String = txt_1.text.toString()
        when(view.id) {
            R.id.button1 -> txt_1.text = num2 + "7"
            R.id.button2 -> txt_1.text = num2 + "8"
            R.id.button3 -> txt_1.text = num2 + "9"
            R.id.button11 -> txt_1.text = num2 + "+"
            R.id.button9 -> txt_1.text = num2 + "-"
            R.id.button10 -> txt_1.text = ""
            R.id.button5 -> txt_1.text = num2 + "4"
            R.id.button6 -> txt_1.text = num2 + "5"
            R.id.button7 -> txt_1.text = num2 + "6"
            R.id.button4 -> txt_1.text = num2 + "1"
            R.id.button8 -> txt_1.text = num2 + "2"
            R.id.button12 -> txt_1.text = num2 + "3"
            R.id.button13 -> txt_1.text = num2 + "*"
            R.id.button14 -> txt_1.text = num2 + "0"
            R.id.button15 -> txt_1.text = num2 + "^"
            R.id.button17 -> txt_1.text = num2 + "("
            R.id.button18 -> txt_1.text = num2 + ")"
            R.id.button19 -> txt_1.text = num2 + "/"
            R.id.button21 -> txt_1.text = num2 + "e^("
            R.id.button20 -> txt_1.text = num2 + "√("
            R.id.button22 -> txt_1.text = calcularResultado(num2)  // Botón "="
        }
    }

    private fun calcularResultado(input: String): String {
        val tokens = ArrayList<String>()
        val number = StringBuilder()
        var expectNegative = true

        // Parsear la entrada en tokens
        for (i in input.indices) {
            val char = input[i]

            if (char.isDigit() || char == '.' || (char == '-' && expectNegative)) {
                number.append(char)
                expectNegative = false
            } else {
                if (number.isNotEmpty()) {
                    tokens.add(number.toString())
                    number.clear()
                }
                tokens.add(char.toString())
                expectNegative = char == '(' || char in setOf('+', '-', '*', '/', '^', '√', 'e')
            }
        }
        if (number.isNotEmpty()) {
            tokens.add(number.toString())
        }

        return calculadora(tokens)
    }

    private fun calculadora(tokens: ArrayList<String>): String {
        val stack = ArrayDeque<Double>()
        val operators = setOf("+", "-", "*", "/", "^", "√", "e")
        val postfix = ArrayDeque<String>()
        val opStack = ArrayDeque<String>()
        val precedence = mapOf(
            "+" to 1, "-" to 1, "*" to 2, "/" to 2, "^" to 3, "√" to 3, "e" to 3
        )

        // Eliminar espacios y filtrar tokens vacíos
        val cleanedTokens = tokens.map { it.trim() }.filter { it.isNotEmpty() }

        fun toPostfix() {
            for (token in cleanedTokens) {
                when {
                    token.toDoubleOrNull() != null -> postfix.add(token)
                    token == "(" -> opStack.add(token)
                    token == ")" -> {
                        while (opStack.isNotEmpty() && opStack.last() != "(") {
                            postfix.add(opStack.removeLast())
                        }
                        opStack.removeLast()
                    }
                    token in operators -> {
                        while (opStack.isNotEmpty() && precedence[opStack.last()] ?: 0 >= (precedence[token] ?: 0)) {
                            postfix.add(opStack.removeLast())
                        }
                        opStack.add(token)
                    }
                    else -> {
                        return
                    }
                }
            }
            while (opStack.isNotEmpty()) {
                postfix.add(opStack.removeLast())
            }
        }

        fun evaluatePostfix(): String {
            for (token in postfix) {
                when {
                    token.toDoubleOrNull() != null -> stack.add(token.toDouble())
                    token in operators -> {
                        if (stack.size < 2 && token != "√" && token != "e") {
                            return "Error: Operadores binarios requieren dos operandos."
                        }
                        val b = stack.removeLast()
                        val a = if (stack.isNotEmpty()) stack.removeLast() else 0.0

                        val result = when (token) {
                            "+" -> a + b
                            "-" -> a - b
                            "*" -> a * b
                            "/" -> a / b
                            "^" -> Math.pow(a, b)
                            "√" -> Math.sqrt(b)
                            "e" -> Math.exp(b)
                            else -> {
                                return "Operador no soportado."
                            }
                        }
                        stack.add(result)
                    }
                    else -> {
                        return "Token no reconocido: $token"
                    }
                }
            }
            return if (stack.size != 1) {
                "Error: La expresión no está correctamente balanceada."
            } else {
                stack.last().toString()
            }
        }

        toPostfix()
        return evaluatePostfix()
    }
}
