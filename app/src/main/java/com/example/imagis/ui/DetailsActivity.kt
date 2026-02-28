package com.example.imagis.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.imagis.R

class DetailsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment_container, MovieDetailsFragment())
                .commitNow()
        }
    }
}
