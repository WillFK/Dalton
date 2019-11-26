package fk.home.dalton.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.canDrawOverlays
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fk.home.dalton.R
import fk.home.dalton.service.ConfigService
import fk.home.dalton.service.DaltonService
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        openWidgetBtn?.setOnClickListener {
            startConfigHead()
        }

        toggleDaltonBtn?.setOnClickListener {
            if (!canDrawOverlays(activity)) {
                openPermissionScreen()
            } else {
                toggleGrayscale()
            }
        }
    }

    private fun startConfigHead() {
        activity?.let { activity ->
            if (!canDrawOverlays(activity)) {
                openPermissionScreen()
            } else {
                activity.startForegroundService(Intent(activity, ConfigService::class.java))
            }
        }
    }

    private fun toggleGrayscale() {
        activity?.let { activity ->
            activity.startService(Intent(activity, DaltonService::class.java))
        }
    }

    private fun openPermissionScreen() {
        activity?.let { activity ->
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.packageName)
            )
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
        }
    }

    private companion object {
        const val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    }
}
