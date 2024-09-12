package com.example.simuladorcredito

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los EditText y otros componentes
        val propertyValueEditText = findViewById<EditText>(R.id.propertyValue)
        val loanAmountEditText = findViewById<EditText>(R.id.loanAmount)
        val loanTermEditText = findViewById<EditText>(R.id.loanTerm)
        val interestRateEditText = findViewById<EditText>(R.id.interestRate)
        val simulateButton = findViewById<Button>(R.id.simulateButton)
        val simulationResultTextView = findViewById<TextView>(R.id.simulationResult)

        // Aplicar formato de número automáticamente
        val formatter = DecimalFormat("#,###")
        applyNumberFormatting(propertyValueEditText, formatter)
        applyNumberFormatting(loanAmountEditText, formatter)

        // Configuración del evento de clic del botón
        simulateButton.setOnClickListener {
            // Limpiar el resultado previo
            simulationResultTextView.text = ""
            simulationResultTextView.visibility = TextView.GONE

            // Obtención de valores ingresados por el usuario
            val propertyValue = propertyValueEditText.text.toString().replace("[,.]".toRegex(), "").toDoubleOrNull()
            val loanAmount = loanAmountEditText.text.toString().replace("[,.]".toRegex(), "").toDoubleOrNull()
            val loanTerm = loanTermEditText.text.toString().toIntOrNull()
            val interestRate = interestRateEditText.text.toString().toDoubleOrNull()

            // Validación de entrada de datos
            if (propertyValue == null || loanAmount == null || loanTerm == null || interestRate == null) {
                Toast.makeText(this, "Por favor, ingrese valores válidos en todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de tasa de interés
            if (interestRate < 12.0 || interestRate > 24.9) {
                Toast.makeText(this, "La tasa de interés debe ser entre 12.0% y 24.9%.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lógica para verificar las restricciones de valores
            if (propertyValue < 70000000) {
                Toast.makeText(this, "El valor de la propiedad no puede ser inferior a $70.000.000", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (loanAmount < 50000000 || loanAmount > propertyValue * 0.8) {
                Toast.makeText(this, "El monto del préstamo debe estar entre $50.000.000 y el 80% del valor de la propiedad.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (loanTerm < 5 || loanTerm > 20) {
                Toast.makeText(this, "El plazo debe estar entre 5 y 20 años.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cálculo de la simulación del crédito
            val monthlyInterestRate = interestRate / 12 / 100
            val numberOfPayments = loanTerm * 12
            val monthlyPayment = (loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -numberOfPayments.toDouble()))

            // Mostrar el resultado de la simulación
            val formattedPayment = NumberFormat.getNumberInstance(Locale("es", "CO")).format(monthlyPayment.toInt()) // Formato sin decimales
            simulationResultTextView.text = "Paga cuotas de $ $formattedPayment por mes"
            simulationResultTextView.visibility = TextView.VISIBLE
        }
    }

    // Función para aplicar formato automáticamente al campo de texto de entrada
    private fun applyNumberFormatting(editText: EditText, formatter: DecimalFormat) {
        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    editText.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[,]".toRegex(), "")
                    val formattedString = formatter.format(cleanString.toLongOrNull() ?: 0)

                    current = formattedString
                    editText.setText(formattedString)
                    editText.setSelection(formattedString.length)

                    editText.addTextChangedListener(this)
                }
            }
        })
    }
}