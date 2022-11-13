package com.example.aop_part2_chapter03

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    private val diaryEditText: EditText by lazy {
        findViewById<EditText>(R.id.diaryEditText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val detailPreference = getSharedPreferences("diary", Context.MODE_PRIVATE)

        diaryEditText.setText(detailPreference.getString("detail", ""))

        val runnable = Runnable {
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit {
                putString("detail", diaryEditText.text.toString())
            }
            Log.d("DiaryActivity", "save!!${diaryEditText.text.toString()}")
        }

        diaryEditText.addTextChangedListener {
            Log.d("DiaryActivity", "TextChanged:: $it")
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 500)
            }
        }

}