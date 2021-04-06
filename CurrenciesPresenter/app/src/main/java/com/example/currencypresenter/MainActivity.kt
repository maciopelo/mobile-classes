/*
* 1. Layouty zostały przygotowane zgodnie z zasadami poznanymi na zajęciach oraz
* dodałem kilka obrazów w celach estetycznych. W przypadku słabego internetu lub braku uzyskania
* danych zostanie wyświetlony empty_layout z napisame "Loading...".
*
* 2. Ekran startowy zawiera trzy buttony, które prowadzą do kolejnych activiti'es.
*
* 3. Na liście walut znajdują się waluty z obu tabel (A i B) z API NBP co zostało obsłużone
* w zapytaniu.
*
* 4. Z wykorzystaniem RecyclerView w postaci listy są wyświetlane wszystkie waluty z tabel A i B
* gdzie każdy wiersz posiada od lewej flagę (wszystkie pansta posiadaj flagi związane z ich waluta,
* a te które zostały wymienione w instrukcji posiadają zgodnie z poleceniem np. USD -> flaga USA),
* kod waluty, aktualny kurs oraz strzałke czerwona lub zielona symbolizującą porównanie z kursem
* poprzednim. Flagi nie zostały zaczerpniete z żadnej biblioteki tylko umieściłem w pliku strings.xml
* ich unicod'y, które znalazlem w internecie i następnie wyśietlam je jako text/ikony. Po kliknięciu
* w dowolną walutę przenosi nas do szczegółów związanych z daną walutą i wyświetla ostatnie 2 kursy
* oraz wykresy zgodnie z instrukcją.
*
* 5. W zakładce złota wyświetlany jest aktualny kurs oraz wykres zmian z ostatnich 30dni.
*
* 6. Przelicznik walut również zostały obsłużony zgodnie z poleceniem według własnego pomysłu.
*
*
* DODATKOWO:
*  W zakładace złota, po kliknęciu na punkt symbolizująćy dany dzien z ostanich 30
*  na wykresie wyświetla nam sie kurs z tego dnia i jaki to dzień w takiej "interatykwnej"
*  formie, co pozwala użytkownikowi sprawdzać poszczególne dni.
*
*
* */


package com.example.currencypresenter

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    lateinit var currenciesListButton: Button
    lateinit var goldRateButton: Button
    lateinit var currencyConverterButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // prepare global data state
        TemporaryData.prepare(this)

        //fetch essential currencies data
        makeApiRequest()

        currenciesListButton = findViewById(R.id.currenciesListButton)
        goldRateButton = findViewById(R.id.goldRateButton)
        currencyConverterButton = findViewById(R.id.currencyConverterButton)



        currenciesListButton.setOnClickListener{
            val intent = Intent(this, CurrenciesListActivity::class.java)
            startActivity(intent)
        }

        goldRateButton.setOnClickListener{
            val intent = Intent(this, GoldActivity::class.java)
            startActivity(intent)
        }

        currencyConverterButton.setOnClickListener{
            val intent = Intent(this, CurrenciesConverterActivity::class.java)
            startActivity(intent)
        }


    }


    private fun makeApiRequest(){

        fun buildQuery(tableLetter:String) = Uri.Builder()
                .scheme("https")
                .authority("api.nbp.pl")
                .appendPath("api")
                .appendPath("exchangerates")
                .appendPath("tables")
                .appendPath(tableLetter)
                .appendPath("last")
                .appendPath("2")
                .appendQueryParameter("format","json").toString()


        val requestTableA = StringRequest(
                Request.Method.GET, buildQuery("A"),
                { response ->
                    TemporaryData.loadCurrenciesData(response,"A")
                },
                { error -> handleResponseError(error)  })


        val requestTableB = StringRequest(
                Request.Method.GET, buildQuery("B"),
                { response ->
                    TemporaryData.loadCurrenciesData(response,"B")
                },
                { error -> handleResponseError(error)  })

        TemporaryData.addRequestToQueue(requestTableA)
        TemporaryData.addRequestToQueue(requestTableB)

    }

    private fun handleResponseError(error: VolleyError?) {
        println("Error occurred - status: ${error?.message}")
    }

}


