package com.example.tapmegame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {


    private lateinit var  button: Button
    private lateinit var  scoreLabel: TextView
    private lateinit var  timeLabel: TextView
    private var score = 0
    private var gameStart = false
    private lateinit var timer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // przypisanie do zmiennych obiektow z layouta
        button = findViewById(R.id.button)
        scoreLabel = findViewById(R.id.scoreLabel)
        timeLabel = findViewById(R.id.timeLabel)

        button.setOnClickListener{ view -> buttonPressed() }


        timer = object : CountDownTimer(15000,1000){
            override fun onTick(millisUntilFinished: Long) {
                setTime(millisUntilFinished/1000)
            }
            override fun onFinish() {
                gameOver()
            }
        }

        setScore(0)
        setTime(0)

    }


    private fun buttonPressed() {
        if (gameStart){
            score += 1
            setScore(score)
        }else{
            gameStart = true
            val text = getString(R.string.buttonLabelGameStart)
            setButtonText(text)
            timer.start()
        }
    }

    private fun setButtonText(text: String) {
        button.text = text
    }


    private fun setScore(score: Int) {
        // to zwraca nowy string z tym %1$d  zamienionym na wartosc
        val text = getString(R.string.scoreLabelText, score)
        //val text = "Score: $score"
        scoreLabel.text = text
    }


    private fun setTime(time: Long) {
        val text = getString(R.string.timeLabelText,time)
        //val text = "Time left: $time"
        timeLabel.text = text
    }


    private fun gameOver(){
        setButtonText(getString(R.string.buttonLabelText))
        val toastText = getString(R.string.message, score)
        Toast.makeText(this@MainActivity, toastText, Toast.LENGTH_LONG).show()
        score = 0
        setTime(0)
        setScore(0)
    }

}