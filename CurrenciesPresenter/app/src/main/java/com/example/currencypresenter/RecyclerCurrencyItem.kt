package com.example.currencypresenter

data class RecyclerCurrencyItem(val currencyName:String, val currencyCode:String, val currencyRate:Double, val isHigherThanPrev:Boolean, val table:String)