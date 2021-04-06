package com.example.currencypresenter

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class GoldActivity : AppCompatActivity(), OnChartValueSelectedListener {


    private lateinit var currentGoldValue: TextView
    private lateinit var chosenRate: TextView
    private lateinit var chart: LineChart
    private var xAxisValues = mutableListOf<String>()
    private var ratesEntries = mutableListOf<Entry>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gold_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentGoldValue = findViewById(R.id.currentGoldValue)
        chosenRate = findViewById(R.id.chosenRate)

        makeApiRequest()

    }


    @SuppressLint("SetTextI18n")
    private fun makeApiRequest() {

        val url = Uri.Builder()
                .scheme("https")
                .authority("api.nbp.pl")
                .appendPath("api")
                .appendPath("cenyzlota")
                .appendPath("last")
                .appendPath("30")
                .appendQueryParameter("format", "json").toString()


        val goldRateRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    TemporaryData.loadGoldRatesData(response)
                    currentGoldValue.text = "Current gold rate: ${TemporaryData.getGoldRatesData().last().second}"
                    getDataForXYAxis(TemporaryData.getGoldRatesData())
                    prepareLineChart(1000,30)
                },
                { error -> handleResponseError(error) })


        TemporaryData.addRequestToQueue(goldRateRequest)

    }


    private fun handleResponseError(error: VolleyError?) {
        println("Error occurred - status: ${error?.message}")
    }



    private fun prepareLineChart(animationTime:Int, daysBack:Int) {

        chart = findViewById(R.id.goldChart)

        val lineDataSet = LineDataSet(ratesEntries, "rates")
        val lineData = LineData(lineDataSet)

        // margins
        chart.extraRightOffset = 15F
        chart.extraBottomOffset = 15F
        chart.extraTopOffset = 100F


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
        lineDataSet.setDrawValues(false)

        chart.data = lineData
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
        chart.animateX(animationTime)
        chart.invalidate()
        chart.legend.isEnabled = false
        chart.description.isEnabled = true
        chart.description.textSize = 16F
        chart.description.text = "Last $daysBack days"

        // handle onClick
        chart.setOnChartValueSelectedListener(this)

    }


    private fun getDataForXYAxis(goldRatesData: List<Pair<String, Double>>) {
        goldRatesData.forEachIndexed(){ index, pair ->
            xAxisValues.add(pair.first)
            ratesEntries.add(Entry(index.toFloat(), pair.second.toFloat()))
        }
    }



    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val x:Float =e!!.x
        val chosenRateDate = TemporaryData.getGoldRatesData()[x.toInt()].first
        val chosenRateValue = TemporaryData.getGoldRatesData()[x.toInt()].second
        val finalOutput = "The gold rate on $chosenRateDate was equal: $chosenRateValue"
        chosenRate.text = finalOutput
    }


    override fun onNothingSelected() {
        Log.i("Entry selected", "Nothing selected.")
    }

}
