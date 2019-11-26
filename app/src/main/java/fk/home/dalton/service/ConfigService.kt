package fk.home.dalton.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.*
import androidx.core.app.NotificationCompat
import fk.home.dalton.R
import kotlinx.android.synthetic.main.config_head.view.*

class ConfigService : Service() {

    private var view: View? = null
    private var windowManager: WindowManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        addNotification()

        Log.d("FKZ", "config create")
        super.onCreate()
        view = LayoutInflater.from(this).inflate(R.layout.config_head, null)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0

        (getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.let { windowManager ->
            windowManager.addView(view, params)
            this.windowManager = windowManager
        }

        view?.setOnTouchListener(object: View.OnTouchListener {

            var lastAction = MotionEvent.ACTION_UP
            var startingX = 0
            var startingY = 0

            var startingTouchX = 0f
            var startingTouchY = 0f


            override fun onTouch(v: View?, event: MotionEvent): Boolean {

                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        startingX = params.x
                        startingY = params.y

                        startingTouchX = event.rawX
                        startingTouchY = event.rawY

                        lastAction = event.action
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            callDaltonService()
                        }
                        lastAction = event.action
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {

                        if (event.rawX != startingTouchX || event.rawY != startingTouchY) {

                            params.x = startingX + (event.rawX - startingTouchX).toInt()
                            params.y = startingY + (event.rawY - startingTouchY).toInt()

                            windowManager?.updateViewLayout(view, params)
                            lastAction = event.action
                        }
                        return true
                    }

                    else -> false
                }
            }

        })

        view?.closeBtn?.setOnClickListener {
            stopSelf()
        }
    }

    /*
        Adding notification so the service stays alive
     */
    private fun addNotification() { // create the notification
        val chan =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_eye)
            .setContentTitle(getText(R.string.app_running_background))
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun callDaltonService() {
        val intent = Intent(this, DaltonService::class.java)
        startService(intent)
    }

    override fun onDestroy() {
        Log.d("FKZ", "Config dying")
        super.onDestroy()
        view?.let { view ->
            windowManager?.removeView(view)
        }
    }

    private companion object {

        const val NOTIFICATION_CHANNEL_ID = "fk.home.dalton"
        const val CHANNEL_NAME = "Dalton Config Service"
        const val NOTIFICATION_ID = 314
    }
}