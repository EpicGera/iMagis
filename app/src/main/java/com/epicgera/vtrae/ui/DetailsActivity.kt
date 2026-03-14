package com.epicgera.vtrae.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import com.epicgera.vtrae.R
import com.epicgera.vtrae.ui.components.VtrToastHost

class DetailsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment_container, MovieDetailsFragment())
                .commitNow()
        }

        // Inject a ComposeView overlay so VtrToastHost can render glassmorphic notifications
        injectToastOverlay()
    }

    private fun injectToastOverlay() {
        val rootView = findViewById<FrameLayout>(android.R.id.content)
        val composeView = ComposeView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                VtrToastHost()
            }
        }
        rootView.addView(composeView)
    }
}

