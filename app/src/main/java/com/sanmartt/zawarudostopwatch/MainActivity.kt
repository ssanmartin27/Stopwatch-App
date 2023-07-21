package com.sanmartt.zawarudostopwatch

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Animation.*
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    var stopped: Boolean = false
    var pauseAt: Long = 0
    var elapsedMillis: Long = 0
    var count: Double = 1.0
    var b:Double = 0.0
    var running = false
    var elapsedMillis1:Long = 0
    lateinit var gear: ImageView
    lateinit var circle:ImageView
    lateinit var mAdView : AdView


    fun lerp(a: Long, b:Double, c:Double):Double{
        return (a*(1.0-c))+(b*c)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        var displayMetrics = this.resources.displayMetrics
        var dpHeight = displayMetrics.heightPixels / displayMetrics.density
        var dpWidth = displayMetrics.widthPixels / displayMetrics.density
        var max1 = maxOf(dpHeight, dpHeight)
        Log.d("TAG1", "$dpHeight $dpWidth")
        val timer:Chronometer = findViewById(R.id.timer)
        val start: Button = findViewById(R.id.start)
        val stop: Button = findViewById(R.id.stop)
        val reset: Button = findViewById(R.id.reset)
        val skip: Button = findViewById(R.id.skip)
        val mp1: MediaPlayer = MediaPlayer.create(this,R.raw.resume)
        val mp2: MediaPlayer = MediaPlayer.create(this,R.raw.zawarudo)
        val mp3: MediaPlayer = MediaPlayer.create(this,R.raw.dusto)
        val mp4: MediaPlayer = MediaPlayer.create(this,R.raw.crimson)
        val shake:Animation = AnimationUtils.loadAnimation(this, R.anim.shake)
        gear = findViewById(R.id.imageView)
        circle = findViewById(R.id.imageView2)
        val circleanim1  = ObjectAnimator.ofFloat(circle, View.SCALE_X, 1F, max1)
        circleanim1.repeatCount = 1
        circleanim1.repeatMode = ValueAnimator.REVERSE
        val circleanim2 = ObjectAnimator.ofFloat(circle, View.SCALE_Y, 1F, max1)
        circleanim2.repeatCount = 1
        circleanim2.repeatMode = ValueAnimator.REVERSE
        val animatorset: AnimatorSet = AnimatorSet()
        animatorset.playTogether(circleanim1,circleanim2 )
        animatorset.duration = 1000
        val animator = ObjectAnimator.ofFloat(gear, View.ROTATION, -360F,0F)
        animator.duration = 2000
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = Animation.INFINITE
        animator.repeatMode = ObjectAnimator.RESTART
        val animator2 = ObjectAnimator.ofFloat(gear, View.ROTATION, 0F,-360F)
        animator2.duration = 2000
        animator2.interpolator = LinearInterpolator()
        animator2.repeatCount = Animation.INFINITE
        animator2.repeatMode = ObjectAnimator.RESTART


        var handler: Handler = Handler()
        var handler2:Handler = Handler()
        var handler3:Handler = Handler()
        var handler4:Handler = Handler()
        start.setOnClickListener {
            timer.base = SystemClock.elapsedRealtime()
            if (stopped){
            timer.base = SystemClock.elapsedRealtime()-pauseAt ;}
            if (!stopped){timer.start()}
            if (stopped){mp1.start() };
            //stopped = false
            running = true
        start.text = getString(R.string.start_time)

            if(!stopped){animator.interpolator = LinearInterpolator()
                animator.repeatCount = Animation.INFINITE
                animator.start()}
        stopped= false
        }
        mp1.setOnCompletionListener {timer.base = SystemClock.elapsedRealtime()-pauseAt; timer.start(); animator.repeatCount = Animation.INFINITE
           animator.interpolator = (LinearInterpolator())
            animator.start()
            animatorset.resume()
        animatorset.doOnEnd { circle.visibility = ImageView.INVISIBLE }}
        stop.setOnClickListener { pauseAt = SystemClock.elapsedRealtime()-timer.base;
            timer.stop();
            elapsedMillis1 = SystemClock.elapsedRealtime() - timer.base;
            mp2.start()
            circle.visibility = ImageView.VISIBLE
            handler4.postDelayed({animatorset.start(); animator.repeatCount=0; gear.startAnimation(shake)},1000)
            handler3.postDelayed({animatorset.pause()},2200)
            start.text = getString(R.string.resume) ;
            stopped= true
        running = false

            /*animator.repeatCount=0*/




        }
            reset.setOnClickListener {
                timer.stop()
                if (!stopped){
                elapsedMillis1 = SystemClock.elapsedRealtime() - timer.base;}
                mp3.start();
                animator.repeatCount = 0
                animator2.start()
                val runnable = object:Runnable {
                    public override fun run() {
                        println(count)
                        var c:Double = (count*14)/196
                        Log.d("TAG", "$count $c")
                        println (c)
                        var lerped:Long = (lerp(elapsedMillis1,b,c)).toLong()
                        timer.base = (SystemClock.elapsedRealtime()-(lerped))
                        // need to do tasks on the UI thread
                        if (count++ < 14)
                        {
                            handler.postDelayed(this, 1000)
                        }
                        else {count = 1.0
                            timer.stop()
                        timer.base = SystemClock.elapsedRealtime()
                        animator2.end() }
                    }
                }
                handler.post(runnable)

                    start.text = getString(R.string.start_time)
                    stopped = false
                running = false

                }

        /*mp3.setOnCompletionListener {
            timer.base = SystemClock.elapsedRealtime()  }*/
        skip.setOnClickListener {
            elapsedMillis = SystemClock.elapsedRealtime() - timer.base;
            timer.base = SystemClock.elapsedRealtime()-(elapsedMillis + 10000)
            mp4.start()
            if (animator2.isRunning){animator2.end()}
            animator.duration = 500
            handler2.postDelayed({ animator.repeatCount = Animation.INFINITE
            animator.duration = 2000
            animator.start()},1000)
        running = true}



    }

}




