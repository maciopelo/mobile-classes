package com.example.maciej_koch_czw_8_00

/*
* Maciej Koch grupa czwartek 8:00
*
* Zrealizowane podpunkty:
* 1. a), b), c), d) -> wykonano zgodnie z wszystkimi podpunktami
*
* 2.
*   a) Interfejs według własnej implementacji
*   b) Własna implementacja logiki na zasadzie ONP
*
* 3.
*   a) poprawna obłsuga kropki oraz wprowadzanie liczb zmiennoprzecinkowych
*   b) obsłużono wielokrotne naciskanie symbolu operacji
*   c) dzielenie przez 0
*
* 4.
*   Aktualizacja zawartości wyswietlacza poprzez setter setResult() oraz zmienna
*   przechowująca bieżące wyrażenie finalResultToEval
*
* 5.
*   Kalkulator umożliwia zmianę symbolu po wcisnieciu innego np. maja 5+ mozna zmienic na 5*
*   po kliknięciu *
*
* 6.
*   Obsługa zbyt długich wyrażen poprzez wyswietlenie ostatnich n znaków z całego wyrażenia
*
* 7.
*   Wykluczone przez punkt nr 9
*
* 8.
*   Dodano operacje potęgi oraz procentu (procent dziala w ten sposób ze np. majac 98 -- > 0.98)
*
* 9.
*   Wykonywanie kolejności działan zgonie z ONP
*
*
*  BŁĘDY
*  Kalkualtor zapewne posiada jakieś niedociągnięcia, których nie udalo mi się odkryć
*  nie mniej jednak posiada podstawową funkcjonalność zgodnie z poleceniem.
*
*  DODATKOWE
*  Stworzyłem enum class do przechowywania i wyciagania odpowiednich operacji
*
*
* */


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var clearButton: Button
    private lateinit var dotButton: Button
    private lateinit var calculateButton: Button
    private lateinit var percentageButton: Button
    private lateinit var result: TextView


    private lateinit var digits: Array<Button>
    private lateinit var operations: Array<Button>

    private var finalResultToEval: String = "0"


    private var calculator: Calculator = Calculator()


    override fun onCreate(savedInstanceState: Bundle?) {
        calculator.context = this.applicationContext
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clearButton = findViewById(R.id.ClearButton)
        dotButton = findViewById(R.id.DotButton)
        calculateButton = findViewById(R.id.CalculateButton)
        result = findViewById(R.id.Result)
        percentageButton = findViewById(R.id.PercentageButton)



        val buttonsIDs = arrayOf(R.id.NumButton0, R.id.NumButton1, R.id.NumButton2, R.id.NumButton3,
                R.id.NumButton4, R.id.NumButton5, R.id.NumButton6, R.id.NumButton7, R.id.NumButton8, R.id.NumButton9)

        val operationsIDs = arrayOf(R.id.AddButton, R.id.SubtractButton, R.id.MultiplyButton, R.id.DivideButton,R.id.PowerButton)


        digits = (buttonsIDs.map{ itemID -> findViewById(itemID) as Button}).toTypedArray()
        operations = (operationsIDs.map{ itemID -> findViewById(itemID) as Button}).toTypedArray()

        clearButton.setOnClickListener { clear() }
        dotButton.setOnClickListener { floatNumberPressed() }
        calculateButton.setOnClickListener { eval() }
        percentageButton.setOnClickListener { makePercentageResult() }
        digits.forEach { button -> button.setOnClickListener{digitPressed(it as Button) } }
        operations.forEach { button -> button.setOnClickListener{operationPressed(it as Button) } }


    }

    private fun makePercentageResult(){
        if(finalResultToEval != getString(R.string.error) || finalResultToEval != "0"){
            val newPercentageResult = (finalResultToEval.toDouble() / 100).toString()
            clear()
            setResult(newPercentageResult,true)
        }
    }


    private fun eval(){

        if(finalResultToEval != getString(R.string.error)){
            val res = calculator.infixIntoPostfix(finalResultToEval)
            val res1 = calculator.evalFormula(res)
            clear()
            if(res1 != "Infinity"){
                setResult("%.2f".format(res1.toDouble()),true)
            }else{
                setResult(getString(R.string.error),true)
            }
        }
    }


    private fun floatNumberPressed(){
        val dotSymbol = getString(R.string.dotSymbol)
        val isValid = validateDotNextSymbol()
        setResult(dotSymbol, isValid)
    }


    private fun operationPressed(operationButton: Button){
        val operation: String = operationButton.text.toString()
        val currentLastCharacter: String = result.text.last().toString()
        val availableOperations = Operations.values().map { getString(it.type) }
        val isValid = validateNextSymbol(operation)


        if (!availableOperations.contains(currentLastCharacter))
            setResult(" ${operation} ", isValid)
        else{
            swapResultLastChar(operation)
        }
    }


    private fun digitPressed(digitButton: Button){
        val newDigit: String = digitButton.text.toString()
        val isValid = validateNextDigit(newDigit)
        setResult(newDigit, isValid)

    }


    private fun clear(){
        result.text = "0"
        finalResultToEval = "0"
    }


    @SuppressLint("SetTextI18n")
    private fun setResult(symbol: String, isValid: Boolean){

        if(isValid){
            when(finalResultToEval){
                "0" -> {
                    finalResultToEval = symbol
                    result.text = finalResultToEval
                }
                else -> {
                    finalResultToEval += symbol
                    if (result.text.length >= 17){
                        result.text = "...${finalResultToEval.takeLast(16)}"
                    }
                    else {
                        result.text = finalResultToEval
                    }
                }
            }
        }
    }


    private fun swapResultLastChar(newChar: String){
        val chars = result.text.substring(0, result.text.length - 1)
        finalResultToEval = chars + newChar
        result.text = finalResultToEval
    }



    private fun validateNextDigit(newDigit: String):Boolean{
        val availableOperations = Operations.values().map { getString(it.type) }

        if(finalResultToEval == "0" && newDigit == "0" || finalResultToEval == getString(R.string.error)) return false

        if (finalResultToEval.length > 2 && availableOperations.contains(finalResultToEval[finalResultToEval.length - 2].toString())){
            if(finalResultToEval[finalResultToEval.length - 1] == '0') return false
            return true
        }

        return true
    }


    private fun validateNextSymbol(newSymbol: String):Boolean{
        val availableOperations = Operations.values().map { getString(it.type) }
        val dotSymbol = getString(R.string.dotSymbol)

        if(finalResultToEval == "0" || finalResultToEval == getString(R.string.error)) return false

        if(availableOperations.contains(finalResultToEval.last().toString()) || finalResultToEval.last().toString() == dotSymbol) return false

        return true
    }


    private fun validateDotNextSymbol():Boolean{
        val availableOperations = Operations.values().map { getString(it.type) }
        val dotSymbol = getString(R.string.dotSymbol)
        var index: Int = -1;

        if(finalResultToEval == "0" || finalResultToEval == getString(R.string.error)) return false

        for ((idx, chr) in finalResultToEval.reversed().withIndex()){
            if (chr.toString() == dotSymbol){
                index = idx
            }
        }

        val subResult = if(index == -1)  "" else finalResultToEval.substring(index)

        for (chr in subResult){
            for (op in availableOperations){
                if (chr.toString() != op) {
                    return true
                }
            }
        }


        if(!availableOperations.contains(finalResultToEval.last().toString()) && finalResultToEval.last().toString() != dotSymbol){
            return true
        }

        return false
    }

}