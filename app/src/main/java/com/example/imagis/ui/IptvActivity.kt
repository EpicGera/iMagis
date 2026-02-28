package com.example.imagis.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.imagis.R

class IptvActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iptv)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.iptv_fragment_container, IptvFragment())
                .commitNow()
        }
    }
}
