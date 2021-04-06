package com.example.maciej_koch_czw_8_00

interface ICalculator {
    fun infixIntoPostfix(formula: String):List<String>
    fun evalFormula(formula: List<String> ):String
}