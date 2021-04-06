package com.example.currencypresenter

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.lang.Double.parseDouble


class CurrenciesConverterActivity : AppCompatActivity() {

    private lateinit var plnToOtherSpinner: Spinner
    private lateinit var otherToPlnSpinner: Spinner

    private lateinit var plnToOtherValue: TextView
    private lateinit var otherToPlnValue: TextView

    private lateinit var plnInput: EditText
    private lateinit var otherValueInput: EditText

    private lateinit var currenciesListAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.currencies_converter_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        plnInput = findViewById(R.id.plnInput)
        otherValueInput = findViewById(R.id.chosenCurrencyInput)

        plnToOtherValue = findViewById(R.id.plnToOtherValue)
        otherToPlnValue = findViewById(R.id.otherToPlnValue)

        plnToOtherSpinner =  findViewById(R.id.plnToOtherSpinner)
        otherToPlnSpinner =  findViewById(R.id.otherToPlnSpinner)

        val adapterData = prepareAdapterData()
        currenciesListAdapter = ArrayAdapter(this, R.layout.dropdown_item, adapterData)

        plnToOtherSpinner.adapter = currenciesListAdapter
        otherToPlnSpinner.adapter = currenciesListAdapter



        plnInput.setOnClickListener {
            calculateFromPlnToOtherCurrency(plnToOtherSpinner.selectedItemPosition)
        }

        otherValueInput.setOnClickListener {
            calculateFromOtherToPlnCurrency(otherToPlnSpinner.selectedItemPosition)
        }


    }

    private fun calculateFromOtherToPlnCurrency(position:Int) {
        val chosenCurrencyRate = TemporaryData.getCurrenciesData().elementAt(position).currencyRate
        val calculatedValue =  parseDouble(otherValueInput.text.toString()) * chosenCurrencyRate
        otherToPlnValue.text =  String.format("%.2f PLN",calculatedValue)
    }

    private fun calculateFromPlnToOtherCurrency(position:Int) {
        val chosenCurrencyRate = TemporaryData.getCurrenciesData().elementAt(position).currencyRate
        val calculatedValue =  parseDouble(plnInput.text.toString()) / chosenCurrencyRate
        plnToOtherValue.text = String.format("%.2f",calculatedValue)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        val inputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0)
        }
    }

    private fun prepareAdapterData(): List<String>{

        val resultArray = mutableListOf<String>()
        val currenciesCodes = TemporaryData.getCurrenciesData()

        for(i in currenciesCodes.indices){
            val singleCurrencyCode = currenciesCodes.elementAt(i).currencyCode
            val adapterItem = "$singleCurrencyCode ${RecyclerAdapter.flagEmojiFromUnicode(getString(singleCurrencyCode))}"
            resultArray.add(adapterItem)
        }

        return resultArray.toList()
    }

    private fun Context.getString(name: String): String {
        return resources.getString(resources.getIdentifier(name, "string", packageName))
    }


}