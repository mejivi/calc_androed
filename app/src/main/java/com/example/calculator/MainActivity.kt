package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }

    Column(modifier = Modifier.fillMaxSize()) {
        // ДИСПЛЕЙ
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Правильное использование weight
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (expression.isEmpty() || expression == result) "" else result,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // КНОПКИ
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.2f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Научный ряд
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("sqrt", "pi", "^", "!").forEach { label ->
                    CalcButton(
                        text = if(label == "sqrt") "√" else if(label == "pi") "π" else label,
                        containerColor = Color.Transparent,
                        modifier = Modifier.weight(1f),
                        fontSize = 22.sp,
                        onClick = {
                            expression += when(label) {
                                "sqrt" -> "sqrt("
                                "pi" -> "pi"
                                else -> label
                            }
                            result = tryEvaluate(expression)
                        }
                    )
                }
            }

            // Основные ряды
            val rows = listOf(
                listOf("AC", "( )", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+")
            )

            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { label ->
                        val bgColor = when (label) {
                            "AC" -> MaterialTheme.colorScheme.secondary
                            "÷", "×", "-", "+" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        }
                        CalcButton(label, bgColor, Modifier.weight(1f)) {
                            when (label) {
                                "AC" -> { expression = ""; result = "0" }
                                "×" -> { expression += "*"; result = tryEvaluate(expression) }
                                "÷" -> { expression += "/"; result = tryEvaluate(expression) }
                                "( )" -> {
                                    val left = expression.count { it == '(' }
                                    val right = expression.count { it == ')' }
                                    expression += if (left > right && expression.lastOrNull()?.isDigit() == true) ")" else "("
                                    result = tryEvaluate(expression)
                                }
                                else -> {
                                    expression += label
                                    result = tryEvaluate(expression)
                                }
                            }
                        }
                    }
                }
            }

            // Нижний ряд
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalcButton("0", MaterialTheme.colorScheme.primary, Modifier.weight(2.1f)) {
                    expression += "0"; result = tryEvaluate(expression)
                }
                CalcButton(".", MaterialTheme.colorScheme.primary, Modifier.weight(1f)) { expression += "." }
                CalcButton("=", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f)) {
                    if (result.isNotEmpty() && result != "Error") expression = result
                }
            }
        }
    }
}

@Composable
fun CalcButton(
    text: String,
    containerColor: Color,
    modifier: Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 28.sp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.aspectRatio(if (text == "0") 2.1f else 1f),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = MaterialTheme.colorScheme.onBackground),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(text = text, fontSize = fontSize, fontWeight = FontWeight.Medium)
    }
}

fun tryEvaluate(expr: String): String {
    if (expr.isEmpty()) return "0"
    return try {
        val cleaned = expr.replace("×", "*").replace("÷", "/")
        val expression = ExpressionBuilder(cleaned).build()
        val res = expression.evaluate()
        if (res % 1 == 0.0) res.toInt().toString() else String.format("%.2f", res)
    } catch (e: Exception) { "" }
}