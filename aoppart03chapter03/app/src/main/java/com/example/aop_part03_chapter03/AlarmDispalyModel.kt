package com.example.aop_part03_chapter03

data class AlarmDispalyModel (
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
){
    val timeText: String
        get() {
            val h = "%02d".format(if (hour<12) hour else hour - 12)
            val m = "%02d".format(minute)

            return "$h:$m"
        }
    val ampmText: String
        get() {
            return if (hour <12) "AM" else "PM"
        }
    val onOffText: String
        get(){
            return if (onOff) "알람끄기" else "알람 켜기"
        }

    fun makeDataForDB(): String{
        return "$hour:$minute"
    }
}