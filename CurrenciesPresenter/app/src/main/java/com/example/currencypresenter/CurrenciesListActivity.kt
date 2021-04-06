package com.example.currencypresenter

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest

class CurrenciesListActivity : AppCompatActivity(), RecyclerAdapter.OnItemClickListener {

    lateinit var recycler: RecyclerView
    lateinit var mainAdapter: RecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        mainAdapter = RecyclerAdapter(emptyList(),this,this)
        mainAdapter.dataList = TemporaryData.getCurrenciesData().toList()
        mainAdapter.notifyDataSetChanged()

        recycler = findViewById(R.id.mainRecyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = mainAdapter


    }


    // handling action after recyclerView item click
    override fun onItemClick(position: Int) {
        val intent = Intent(this, CurrencyDetails::class.java)
        intent.putExtra("Position", position)
        startActivity(intent)
    }

}

