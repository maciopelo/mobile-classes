package com.example.currencypresenter

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.*

class CurrencyDetails : AppCompatActivity() {

    private lateinit var theLatestRate: TextView
    private lateinit var thePrevRate: TextView
    private lateinit var chosenCurrencyName: TextView
    private lateinit var lastWeekRates: LineChart
    private lateinit var lastMonthChart: LineChart
    private var xAxisValues = mutableListOf<String>()
    private var ratesEntries = mutableListOf<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.currency_details_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        theLatestRate = findViewById(R.id.chosenCurrentRate)
        thePrevRate = findViewById(R.id.chosenPrevRate)
        chosenCurrencyName = findViewById(R.id.chosenCurrencyName)

        val chosenCurrencyCodeIndex:Int = intent.getIntExtra("Position",-1)
        val chosenCurrencyCode = TemporaryData.getCurrenciesData().elementAt(chosenCurrencyCodeIndex).currencyCode
        val chosenCurrencyTable = TemporaryData.getCurrenciesData().elementAt(chosenCurrencyCodeIndex).table

        makeApiRequest(chosenCurrencyCode,chosenCurrencyTable)

    }




    @SuppressLint("SetTextI18n")
    private fun makeApiRequest(currencyCode:String, currencyTable:String, nDaysBack: String = "30"){

        val url = Uri.Builder()
                .scheme("https")
                .authority("api.nbp.pl")
                .appendPath("api")
                .appendPath("exchangerates")
                .appendPath("rates")
                .appendPath(currencyTable)
                .appendPath(currencyCode)
                .appendPath("last")
                .appendPath(nDaysBack)
                .appendQueryParameter("format","json").toString()


        val chosenCurrencyRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    TemporaryData.loadChosenCurrencyData(response)
                    chosenCurrencyName.text = TemporaryData.getChosenCurrencyName().capitalize(Locale.ROOT)
                    theLatestRate.text = "The last one: ${prepareRateText(TemporaryData.getChosenCurrencyLast30Rates()[29])}"
                    thePrevRate.text = "The last but one: ${prepareRateText(TemporaryData.getChosenCurrencyLast30Rates()[28])}"
                    getDataForXYAxis(TemporaryData.getChosenCurrencyLast30Rates())
                    prepareChart(findViewById(R.id.weekChart), 280F, 0F, 1200,7)
                    prepareChart(findViewById(R.id.monthChart), 10F, 280F, 1200,30)
                },
                { error -> handleResponseError(error) })

        TemporaryData.addRequestToQueue(chosenCurrencyRequest)

    }

    private fun prepareRateText(rateAndData:Pair<String,Double>):String{
        return "${rateAndData.first} - ${rateAndData.second} "
    }

    private fun handleResponseError(error: VolleyError?) {
        println("Error occurred - status: ${error?.message}")
    }

    private fun getDataForXYAxis(chosenCurrencyData: List<Pair<String, Double>>) {
        chosenCurrencyData.forEachIndexed(){ index, pair ->
            xAxisValues.add(pair.first)
            ratesEntries.add(Entry(index.toFloat(), pair.second.toFloat()))
        }
    }


    private fun prepareChart( chart:LineChart, bottomOffset:Float, topOffset:Float, animationTime:Int = 1200 ,daysBack:Int) {



        val actualRatesEntries = when(daysBack){
            30 -> ratesEntries
            else -> ratesEntries.subList(ratesEntries.size-daysBack-1,ratesEntries.size-1)
        }

        val lineDataSet = LineDataSet(actualRatesEntries, "rates")
        val lineData = LineData(lineDataSet)


        // margins
        chart.extraRightOffset = 15F
        chart.extraBottomOffset = bottomOffset
        chart.extraTopOffset = topOffset


        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)


        //to hide background lines
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.setDrawGridLines(false)

        //to hide right Y and top X border
        val rightYAxis: YAxis = chart.axisRight
        rightYAxis.isEnabled = false
        val leftYAxis: YAxis = chart.axisLeft
        leftYAxis.isEnabled = true
        val topXAxis: XAxis = chart.xAxis
        topXAxis.isEnabled = false



        val xAxis: XAxis = chart.xAxis
        xAxis.granularity = 5f
        xAxis.setCenterAxisLabels(true)
        xAxis.isEnabled = true
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        lineDataSet.lineWidth = 5f
        lineDataSet.color = R.color.black
        lineDataSet.circleRadius = 7f
        lineDataSet.setCircleColor(R.color.purple_200)
        lineDataSet.setDrawValues(false);


        chart.data = lineData
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
        chart.animateX(animationTime)
        chart.invalidate()
        chart.legend.isEnabled = false
        chart.description.isEnabled = true
        chart.description.textSize = 16F
        chart.description.text = "Last $daysBack days"
    }

}