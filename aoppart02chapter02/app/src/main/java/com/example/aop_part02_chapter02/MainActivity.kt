package com.example.aop_part02_chapter02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private val clearButton : Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }
    private val addButton : Button by lazy {
        findViewById<Button>(R.id.addButton)
    }
    private val runButton : Button by lazy {
        findViewById<Button>(R.id.runButton)
    }
    private val numberPicker : NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }
    private var didRun = false

    private val pickNumberSet = hashSetOf<Int>()

    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.textView1),
            findViewById<TextView>(R.id.textView2),
            findViewById<TextView>(R.id.textView3),
            findViewById<TextView>(R.id.textView4),
            findViewById<TextView>(R.id.textView5),
            findViewById<TextView>(R.id.textView6),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClear()
    }

    private fun initRunButton(){
        runButton.setOnClickListener{
            val list = getRandomNumber()

            didRun = true

            list.forEachIndexed{ index, number ->
                val textView = numberTextViewList[index]

                textView.text = number.toString()
                textView.isVisible = true

                setNumberBackground(number, textView)
            }
        }
    }
    private fun getRandomNumber():List<Int>{

        val numberList = mutableListOf<Int>()
            .apply {
                for (i in 1..45){
                    if (pickNumberSet.contains(i)){
                        continue
                    }
                    this.add(i)
                }
            }
        numberList.shuffle()

        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)

        return newList.sorted()
    }

    private fun initAddButton(){
        addButton.setOnClickListener{

            if (didRun){
                Toast.makeText(this,"????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.size >=5){
                Toast.makeText(this,"????????? 5???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.contains(numberPicker.value)){
                Toast.makeText(this,"?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true
            textView.text = numberPicker.value.toString()

            setNumberBackground(numberPicker.value, textView)

            textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)

            pickNumberSet.add(numberPicker.value)
        }
    }
    private  fun initClear(){
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            numberTextViewList.forEach {
                it.isVisible = false
            }
            didRun = false
        }
    }

    private fun setNumberBackground(number:Int, textView: TextView){
        when(number){
            in 1..10 ->textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            in 11..20 ->textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 ->textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 ->textView.background = ContextCompat.getDrawable(this, R.drawable.circlr_gray)
            else ->textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }
}