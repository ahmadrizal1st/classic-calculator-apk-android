package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var display: EditText
    private var currentInput = ""
    private var lastButtonWasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupButtonClickListeners()
    }

    private fun initializeViews() {
        display = findViewById(R.id.display)
    }

    private fun setupButtonClickListeners() {
        // Number buttons
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btn00, R.id.btnDecimal
        )

        numberButtons.forEach { buttonId ->
            findViewById<Button>(buttonId).setOnClickListener { view ->
                onNumberButtonClick(view as Button)
            }
        }

        // Operator buttons
        val operatorButtons = listOf(
            R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide
        )

        operatorButtons.forEach { buttonId ->
            findViewById<Button>(buttonId).setOnClickListener { view ->
                onOperatorButtonClick(view as Button)
            }
        }

        // Function buttons
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsButtonClick() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearButtonClick() }
        findViewById<Button>(R.id.btnClearEntry).setOnClickListener { onClearEntryButtonClick() }
    }

    private fun onNumberButtonClick(button: Button) {
        val buttonText = button.text.toString()

        if (currentInput == "0" || currentInput == "Invalid Entry") {
            currentInput = ""
        }

        // Handle decimal point
        if (buttonText == ".") {
            if (currentInput.contains(".")) {
                return // Prevent multiple decimal points
            }
            if (currentInput.isEmpty()) {
                currentInput = "0"
            }
        }

        currentInput += buttonText
        display.setText(currentInput)
        lastButtonWasOperator = false
    }

    private fun onOperatorButtonClick(button: Button) {
        val operator = button.text.toString()

        if (currentInput.isEmpty()) {
            if (operator == "-") {
                currentInput = "-"
                display.setText(currentInput)
            }
            return
        }

        if (lastButtonWasOperator) {
            currentInput = currentInput.substring(0, currentInput.length - 1)
        }

        currentInput += operator
        display.setText(currentInput)
        lastButtonWasOperator = true
    }

    private fun onEqualsButtonClick() {
        if (currentInput.isEmpty() || lastButtonWasOperator) {
            return
        }

        try {
            // Replace × with * and ÷ with / for evaluation
            val expression = currentInput.replace("×", "*").replace("÷", "/")

            // Basic expression evaluation
            val result = evaluateExpression(expression)

            if (result.isInfinite() || result.isNaN()) {
                display.setText("Invalid Entry")
                currentInput = ""
                // Reset after 1.2 seconds like the original
                display.postDelayed({
                    display.setText("0")
                }, 1200)
            } else {
                // Remove .0 if it's an integer
                currentInput = if (result == result.toLong().toDouble()) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }
                display.setText(currentInput)
            }
        } catch (e: Exception) {
            display.setText("Invalid Entry")
            currentInput = ""
            // Reset after 1.2 seconds like the original
            display.postDelayed({
                display.setText("0")
            }, 1200)
        }

        lastButtonWasOperator = false
    }

    private fun evaluateExpression(expression: String): Double {
        // Simple expression evaluator for basic operations
        val parts = expression.split("(?<=[+\\-*/])|(?=[+\\-*/])".toRegex())

        if (parts.size < 3) {
            return expression.toDouble()
        }

        var result = parts[0].toDouble()

        var i = 1
        while (i < parts.size) {
            val operator = parts[i]
            val operand = parts[i + 1].toDouble()

            when (operator) {
                "+" -> result += operand
                "-" -> result -= operand
                "*" -> result *= operand
                "/" -> {
                    if (operand == 0.0) throw ArithmeticException("Division by zero")
                    result /= operand
                }
            }
            i += 2
        }

        return result
    }

    private fun onClearButtonClick() {
        currentInput = ""
        display.setText("0")
        lastButtonWasOperator = false
    }

    private fun onClearEntryButtonClick() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length - 1)
            display.setText(if (currentInput.isEmpty()) "0" else currentInput)
        }
    }
}