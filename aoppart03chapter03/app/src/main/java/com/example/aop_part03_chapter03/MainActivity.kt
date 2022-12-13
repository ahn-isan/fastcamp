package com.example.aop_part03_chapter03

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //step0 뷰를 초기화해주기
        initOnoffButton()
        initChangeAlarmTimeButton()

        val model = fetchDataFromSharedPreferences()
        renderView(model)

        //step1 데이터 가져오기

        //step2 뷰에 데이터 그려주기

    }
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun initOnoffButton(){
        val onOffButton = findViewById<Button>(R.id.onOffButton)
        onOffButton.setOnClickListener {
            //데이터를 확인한다.
            val model = it.tag as? AlarmDispalyModel ?: return@setOnClickListener
            val newModel = saveAlarmModel(model.hour, model.minute, model.onOff.not())
            renderView(newModel)

            if (newModel.onOff){
                // 켜진 경우 -> 알람을 등록
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)
                    if (before(Calendar.getInstance())){
                        add(Calendar.DATE, 1)
                    }
                }

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }else{
                //꺼진 경우 -> 알람을 제거
                cancelAlarm()
            }
            //온오프에 따라 작업을 처리한다.
            //오프 -> 알람제거
            //온 -> 알람 등록

            //데이터 저장
        }
    }
    private fun initChangeAlarmTimeButton(){
        val chageAlarmButton = findViewById<Button>(R.id.changeAlarmTimeButton)
        chageAlarmButton.setOnClickListener {
            //현재 시간을 일단 가져온다.
            val calender = Calendar.getInstance()

            // TimePickDialogs 띄워줘서 시간을 설정하도록 하게끔 하고, 그 시간을 가져와서
            TimePickerDialog(this, { picker, hour, minute ->

               //데이터를 저장한다.
                val model = saveAlarmModel(hour, minute, false)
               // 뷰를 업데이트 한다.
                renderView(model)
               // 기존에 있던 알람을 삭제
                cancelAlarm()
            }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false)
                .show()

        }
    }
    private fun saveAlarmModel(
        hour: Int,
        minute: Int,
        onOff : Boolean
    ): AlarmDispalyModel{
        val model = AlarmDispalyModel(
            hour= hour,
            minute = minute,
            onOff = false
        )

        val sharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()){
            putString("alarm", model.makeDataForDB())
            putBoolean("onOff", model.onOff)
            commit()
        }
        return model
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun fetchDataFromSharedPreferences(): AlarmDispalyModel{
        val sharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)

        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "9:30") ?: "9:30"
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmDate = timeDBValue.split(":")

        val alarmModel = AlarmDispalyModel(
            hour = alarmDate[0].toInt(),
            minute = alarmDate[1].toInt(),
            onOff = onOffDBValue
        )
        // 보정
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)

        if((pendingIntent == null) and alarmModel.onOff){
            //알람은 꺼져있는데, 데이터는 켜져있는 경우
            alarmModel.onOff = false

        }else if ((pendingIntent != null) and alarmModel.onOff.not()){
            //알라음 켜져있는데, 데이터는 꺼져있는경우
            // 알람을 취소함
            pendingIntent.cancel()
        }
        return alarmModel
    }
        private fun renderView(model: AlarmDispalyModel){
            findViewById<TextView>(R.id.ampmTextView).apply{
                text = model.ampmText
            }
            findViewById<TextView>(R.id.timeTextView).apply {
                text= model.timeText
            }
            findViewById<Button>(R.id.onOffButton).apply {
                text = model.onOffText
                tag = model
            }

        }
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelAlarm(){
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel()
    }
    companion object{
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val ALARM_REQUEST_CODE = 1000
    }
}