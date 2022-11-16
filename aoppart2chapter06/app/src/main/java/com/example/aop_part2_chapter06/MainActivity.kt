package com.example.aop_part2_chapter06

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById<TextView>(R.id.remainMinutesTextView)
    }
    private val remainSecondsTextView: TextView by lazy {
        findViewById<TextView>(R.id.remainSecondsTextView)
    }
    private val seekBar: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar)
    }
    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    private val soundPool = SoundPool.Builder().build()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()

    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener(
            object :SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser){ //user 기준을 위해 설정
                    updateRemainTime(progress * 60 * 1000L)

                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    currentCountDownTimer?.cancel()
                    currentCountDownTimer = null
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return

                    currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
                    currentCountDownTimer?.start()

                    tickingSoundId?.let { soundId ->
                    // -1 = loopForever
                    soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
                    }
                }
            }
        )
    }

    private fun createCountDownTimer(initialMillis:Long) =
        object: CountDownTimer(initialMillis, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                updateRemainTime(0)
                updateSeekBar(0)

                completeCountDown()

            }
        }
    private fun completeCountDown(){
        soundPool.autoPause()
        bellSoundId?.let{soundId ->
            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMills: Long){
        val remainSeconds = remainMills / 1000

        remainMinutesTextView.text = "%02d".format(remainSeconds/60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private  fun updateSeekBar(remainMillis:Long){
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

    private fun initSounds(){
       tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1) //priority 호환성을 위해 1 추천한다고 함!
       bellSoundId = soundPool.load(this, R.raw.timer_bell, 1) //priority 호환성을 위해 1 추천한다고 함!
    }
}