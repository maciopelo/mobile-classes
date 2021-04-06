package com.example.currencypresenter


import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray
import org.json.JSONObject

object TemporaryData {

    private var currenciesData =  mutableSetOf<RecyclerCurrencyItem>()
    private lateinit var goldRatesData: List<Pair<String,Double>>
    private lateinit var chosenCurrencyLast30Rates: List<Pair<String,Double>>
    private lateinit var chosenCurrencyName: String
    private lateinit var queue: RequestQueue


     fun prepare(context: Context){
         queue = newRequestQueue(context)
     }

    fun getCurrenciesData(): Set<RecyclerCurrencyItem> {
        return currenciesData
    }

    fun getGoldRatesData(): List<Pair<String,Double>> {
        return goldRatesData
    }

    fun getChosenCurrencyLast30Rates(): List<Pair<String,Double>> {
        return chosenCurrencyLast30Rates
    }

    fun addRequestToQueue(request: StringRequest){
        queue.add(request)
    }

    fun getChosenCurrencyName():String{
        return chosenCurrencyName
    }

    private fun isRateHigherThanPrev(rate:Double, prevRate:Double):Boolean{
        return  rate - prevRate > 0
    }


    fun loadCurrenciesData(response: String?, table:String){
        response?.let {
            val stringResponse = response.toString()
            val prevJsonResponse = JSONArray(stringResponse).get(0) as JSONObject
            val jsonResponse = JSONArray(stringResponse).get(1) as JSONObject
            val prevRates = prevJsonResponse.getJSONArray("rates")
            val rates = jsonResponse.getJSONArray("rates")
            val ratesCount = rates.length()
            for (i in 0 until ratesCount){
                val currencyName = rates.getJSONObject(i).getString("currency")
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                val isHigherThanPrev = isRateHigherThanPrev(rates.getJSONObject(i).getDouble("mid"),prevRates.getJSONObject(i).getDouble("mid"))
                val currencyObject = RecyclerCurrencyItem(currencyName, currencyCode, currencyRate,isHigherThanPrev,table)
                currenciesData.add(currencyObject)
            }

        }
    }


    fun loadGoldRatesData(response: String?){
        response?.let{
            val stringResponse = response.toString()
            val jsonArrayResponse = JSONArray(stringResponse)
            val goldRatesLength = jsonArrayResponse.length()
            val tmp = mutableListOf<Pair<String,Double>>()
            for (i in 0 until goldRatesLength){
                val dailyGoldRate = jsonArrayResponse.getJSONObject(i)
                tmp.add(Pair(dailyGoldRate.getString("data"),dailyGoldRate.getDouble("cena")))
            }
            goldRatesData = tmp
        }
    }


    fun loadChosenCurrencyData(response: String?){
        response?.let{
            val stringResponse = response.toString()
            chosenCurrencyName = JSONObject(stringResponse).getString("currency")
            val jsonArrayResponse = JSONObject(stringResponse).getJSONArray("rates")
            val chosenCurrencyRatesLength = jsonArrayResponse.length()
            val tmp = mutableListOf<Pair<String,Double>>()
            for (i in 0 until chosenCurrencyRatesLength){
                val singleDayCurrentRate = jsonArrayResponse.getJSONObject(i)
                tmp.add(Pair(singleDayCurrentRate.getString("effectiveDate"),singleDayCurrentRate.getDouble("mid")))
            }
            chosenCurrencyLast30Rates = tmp
        }
    }


}