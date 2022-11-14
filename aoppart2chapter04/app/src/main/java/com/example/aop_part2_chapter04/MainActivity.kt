package com.example.aop_part2_chapter04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import com.example.aop_part2_chapter04.model.History
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val expressionTextView : TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }
    private val resultTextView : TextView by lazy{
        findViewById<TextView>(R.id.resultTextView)
    }

    private val historyLayout : View by lazy {
        findViewById<View>(R.id.historyLayout)
    }
    private val historyLinearLayout : LinearLayout by lazy{
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }

    private var isOperator = false
    private var hasOperator = false

    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    }

    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMulti -> operatorButtonClicked("*")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonDivider -> operatorButtonClicked("/")
            R.id.buttonModulo -> operatorButtonClicked("%")

        }
    }
    private fun operatorButtonClicked(operator:String){
        if (expressionTextView.text.isEmpty()){
            return
        }
        when{
            isOperator -> {
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + operator
            }
            hasOperator ->{
                Toast.makeText(this,"연산자는 한번만 사용할 수 있습니다.",Toast.LENGTH_LONG).show()
                return
            }
            else ->{
                expressionTextView.append(" $operator")
            }
        }
        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length -1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        expressionTextView.text = ssb
        isOperator = true
        hasOperator = true
    }
    private fun numberButtonClicked(number: String) {

        if (isOperator){
            expressionTextView.append(" ")
        }
        isOperator = false


        val expressionText = expressionTextView.text.split(" ")
        if (expressionText.isNotEmpty() && expressionText.last().length >= 15){
            Toast.makeText(this,"15자리 까지만 사용할 수 있습니다.",Toast.LENGTH_LONG).show()
            return
        }else if (number == "0" && expressionText.last().isEmpty()){
            Toast.makeText(this,"0은 제일 앞에 올 수 없습니다.",Toast.LENGTH_LONG).show()
            return
        }
        expressionTextView.append(number)
        //resultTextView 실시간으로 계산 결과를 넣어야 하는 기능
        resultTextView.text = calculateExpression()

    }

    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")

        if (expressionTextView.text.isEmpty() || expressionTexts.size == 1){
            return
        }

        if (expressionTexts.size !=3 && hasOperator){
            Toast.makeText(this,"아직 완성되지 않은 수식입니다.",Toast.LENGTH_LONG).show()
            return
        }

        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()){
            Toast.makeText(this,"오류가 발생했습니다.",Toast.LENGTH_LONG).show()
            return
        }
        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        //todo 디비에 넣어주는 부분

        Thread(Runnable {
            db.historyDao().insertHistory(History(null,expressionText,resultText))
        }).start()


        resultTextView.text = ""
        expressionTextView.text = resultText

        isOperator = false
        hasOperator = false
    }
    private fun calculateExpression():String{
        val expressionTexts = expressionTextView.text.split(" ")

        if (hasOperator.not() || expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()){
            return ""
        }
        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when(op) {
            "+" ->(exp1 + exp2).toString()
            "-" ->(exp1 - exp2).toString()
            "*" ->(exp1 * exp2).toString()
            "/" ->(exp1 / exp2).toString()
            "%" ->(exp1 % exp2).toString()
            else -> ""
        }
    }

    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        hasOperator = false
        isOperator = false
    }

    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()
        Thread(Runnable {
            db.historyDao().getAll().reversed().forEach{

                runOnUiThread{
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row, null)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"

                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()
    }
    fun closeHistoryButtonClicked(v: View){
        historyLayout.isVisible = false
    }
    fun historyClearButtonClicked(v: View){
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()

        //TODO 디비에서 모든 기록삭제
        //TODO 뷰에서 모든 기록 삭제
    }

}
//확장 함수
fun String.isNumber() : Boolean{
    return try {
        //무한대의 숫자를 다루는 BigInteger 사용
        this.toBigInteger()
        true
    }catch (e: java.lang.NumberFormatException){
        false
    }
}