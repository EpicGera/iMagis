package com.example.imagis.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.imagis.R

class AnimeActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.anime_fragment_container, AnimeFragment())
                .commitNow()
        }
    }
}
