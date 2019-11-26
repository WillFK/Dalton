package fk.home.dalton.service

import android.app.IntentService
import android.content.Intent
import android.provider.Settings

class DaltonService : IntentService("DaltonService") {

    override fun onHandleIntent(intent: Intent?) {
        daltonize(!isDaltonic())
    }

    private fun isDaltonic(): Boolean {
        return Settings.Secure.getInt(contentResolver, DISPLAY_DALTONIZER_ENABLED, 0) == 1
                && Settings.Secure.getInt(contentResolver, DISPLAY_DALTONIZER, 0) == 0;
    }

    private fun daltonize(grayscale: Boolean) {
        Settings.Secure.putInt(contentResolver, DISPLAY_DALTONIZER_ENABLED, if (grayscale) 1 else 0)
        Settings.Secure.putInt(contentResolver, DISPLAY_DALTONIZER, if (grayscale) 0 else -1)
    }

    private companion object {
        val DISPLAY_DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled"
        val DISPLAY_DALTONIZER = "accessibility_display_daltonizer"
    }
}