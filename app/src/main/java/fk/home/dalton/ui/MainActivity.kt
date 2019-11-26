package fk.home.dalton.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fk.home.dalton.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMainFragment()
    }

    private fun loadMainFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, MainFragment())
            .commitAllowingStateLoss()
    }
}
