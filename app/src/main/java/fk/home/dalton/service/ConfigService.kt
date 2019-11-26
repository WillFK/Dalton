package fk.home.dalton.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import fk.home.dalton.R
import kotlinx.android.synthetic.main.config_head.view.*

class ConfigService : Service() {

    private var view: View? = null
    private var windowManager: WindowManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
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

    private fun callDaltonService() {
        val intent = Intent(this, DaltonService::class.java)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.let { view ->
            windowManager?.removeView(view)
        }
    }
}