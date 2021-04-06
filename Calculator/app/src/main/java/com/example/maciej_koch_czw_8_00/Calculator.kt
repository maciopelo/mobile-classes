package com.example.maciej_koch_czw_8_00

import android.content.Context
import java.lang.Exception
import java.util.*
import kotlin.math.pow


class Calculator : ICalculator {


    lateinit var context: Context


    private fun priority(symbol: String):Int{
        return when(symbol){
            context.getString(Operations.ADD.type)-> 1
            context.getString(Operations.SUB.type)-> 1
            context.getString(Operations.DIV.type)-> 2
            context.getString(Operations.MUL.type)-> 2
            context.getString(Operations.POW.type) -> 3
            else -> -1
        }
    }

    private fun operation(symbol: String, a:Double, b:Double):Double{
        return when(symbol){
            context.getString(Operations.ADD.type) -> a + b
            context.getString(Operations.SUB.type) -> a - b
            context.getString(Operations.DIV.type)-> a / b
            context.getString(Operations.MUL.type)-> a * b
            context.getString(Operations.POW.type) -> a.pow(b)
            else -> throw Exception("Unknown operand")
        }
    }



    override fun infixIntoPostfix(formula: String):List<String> {


        val formulaArray = formula.split(" ").map { it.replace("\\s".toRegex(),"") }
        val ops = Operations.values().map { context.getString(it.type) }
        var result = ""

        val stack = Stack<String>()


        for( c in formula){
            if(!ops.contains(c.toString())){
                result += c
            }else{
                while (!stack.isEmpty() && priority(c.toString()) <= priority(stack.peek().toString())){
                    result += stack.pop()
                }
                stack.push(" ${c} ")
            }
        }

        while (!stack.isEmpty()){
            result += stack.pop();
        }


        return result.replace("\\s+".toRegex()," ").split(" ").subList(0,formulaArray.size);
    }


    override fun evalFormula(postfixFormula: List<String> ): String {

        var result: Double
        val stack = Stack<String>()
        val ops = Operations.values().map { context.getString(it.type) }

        for (token in postfixFormula){
            if (ops.contains(token)){
                val arg2 = stack.pop()
                val arg1 = stack.pop()
                result = operation(token,arg1.toDouble(),arg2.toDouble())
                stack.add(result.toString())
            }else{
                stack.add(token)
            }
        }

        return stack.pop()
    }
}