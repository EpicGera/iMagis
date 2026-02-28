package com.example.imagis.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import com.example.imagis.R

class WebViewActivity : FragmentActivity() {

    private lateinit var webView: WebView
    private lateinit var loadingSpinner: android.widget.ProgressBar

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalSystemUiVisibility: Int = 0
    private var originalOrientation: Int = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webview)
        loadingSpinner = findViewById(R.id.loading_spinner)
        
        setupButtons()

        val url = intent.getStringExtra("VIDEO_URL")

        if (url == null) {
            finish()
            return
        }

        setupWebView()
        webView.loadUrl(url)
    }
    
    private fun setupButtons() {
        findViewById<android.widget.ImageButton>(R.id.btn_home_web).setOnClickListener {
            webView.reload()
        }
        
        findViewById<android.widget.ImageButton>(R.id.btn_search_web).setOnClickListener {
            showSearchDialog()
        }
        
        findViewById<android.widget.ImageButton>(R.id.btn_filter_web).setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showSearchDialog() {
        // Custom In-App Keyboard to bypass system keyboard callback issues
        val context = this
        val layout = android.widget.LinearLayout(context)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(32, 32, 32, 32)
        
        // Display Text
        val display = android.widget.TextView(context)
        display.text = ""
        display.textSize = 24f
        display.setTextColor(android.graphics.Color.WHITE)
        display.setPadding(16, 16, 16, 16)
        display.background = android.graphics.drawable.GradientDrawable().apply {
            setColor(android.graphics.Color.DKGRAY)
            cornerRadius = 8f
        }
        layout.addView(display)
        
        // Keyboard Grid
        val scrollView = android.widget.ScrollView(context)
        val grid = android.widget.GridLayout(context)
        grid.columnCount = 6
        grid.alignmentMode = android.widget.GridLayout.ALIGN_BOUNDS
        
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toList()
        
        for (char in chars) {
            val btn = android.widget.Button(context)
            btn.text = char.toString()
            btn.setOnClickListener {
                display.text = display.text.toString() + char
            }
            grid.addView(btn)
        }
        
        scrollView.addView(grid)
        layout.addView(scrollView)
        
        // Actions
        val actionLayout = android.widget.LinearLayout(context)
        actionLayout.orientation = android.widget.LinearLayout.HORIZONTAL
        
        val btnSpace = android.widget.Button(context); btnSpace.text = "SPACE"
        btnSpace.setOnClickListener { display.text = display.text.toString() + " " }
        
        val btnBack = android.widget.Button(context); btnBack.text = "DEL"
        btnBack.setOnClickListener { 
            if (display.text.isNotEmpty()) {
                display.text = display.text.substring(0, display.text.length - 1) 
            }
        }
        
        val btnClear = android.widget.Button(context); btnClear.text = "CLEAR"
        btnClear.setOnClickListener { display.text = "" }

        actionLayout.addView(btnSpace)
        actionLayout.addView(btnBack)
        actionLayout.addView(btnClear)
        layout.addView(actionLayout)

        val dialog = android.app.AlertDialog.Builder(context)
            .setTitle("Search Anime")
            .setView(layout)
            .setPositiveButton("SEARCH") { _, _ ->
                val query = display.text.toString()
                if (query.isNotEmpty()) {
                    webView.loadUrl("https://jkanime.net/buscar/?q=$query")
                }
            }
            .setNegativeButton("CANCEL", null)
            .create()
            
        // Physical Keyboard Support
        dialog.setOnKeyListener { _, keyCode, event ->
             if (event.action == android.view.KeyEvent.ACTION_DOWN) {
                 val char = event.unicodeChar.toChar()
                 if (Character.isLetterOrDigit(char) || char == ' ') {
                     display.text = display.text.toString() + char
                     return@setOnKeyListener true
                 } else if (keyCode == android.view.KeyEvent.KEYCODE_DEL) {
                     if (display.text.isNotEmpty()) {
                         display.text = display.text.substring(0, display.text.length - 1)
                     }
                     return@setOnKeyListener true
                 }
             }
             false
        }

        dialog.show()
    }
    
    private fun showFilterDialog() {
        val letters = (('A'..'Z') + "0-9".toList()).map { it.toString() }.toTypedArray()
        
        android.app.AlertDialog.Builder(this)
            .setTitle("Browse by Letter")
            .setItems(letters) { _, which ->
                val letter = letters[which]
                webView.loadUrl("https://jkanime.net/letra/$letter/")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

        // Improve video playback and focus support
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        
        // Dark mode force if possible
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            settings.forceDark = WebSettings.FORCE_DARK_ON
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingSpinner.visibility = View.VISIBLE
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadingSpinner.visibility = View.GONE
                injectCSS()
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                customView = view
                customViewCallback = callback
                
                originalSystemUiVisibility = window.decorView.systemUiVisibility
                originalOrientation = requestedOrientation

                // Hide standard UI
                webView.visibility = View.GONE
                findViewById<View>(R.id.control_bar_container)?.visibility = View.GONE // Assuming I add an ID to the LinearLayout

                // Add custom view to DecorView
                (window.decorView as android.view.ViewGroup).addView(customView, android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                ))

                // Immersive mode
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }

            override fun onHideCustomView() {
                (window.decorView as android.view.ViewGroup).removeView(customView)
                customView = null
                
                // Restore UI
                window.decorView.systemUiVisibility = originalSystemUiVisibility
                requestedOrientation = originalOrientation
                
                webView.visibility = View.VISIBLE
                findViewById<View>(R.id.control_bar_container)?.visibility = View.VISIBLE
                
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
            }
        }
        
        webView.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN,
                android.view.MotionEvent.ACTION_UP -> {
                    if (!v.hasFocus()) {
                        v.requestFocus()
                    }
                }
            }
            false
        }
    }

    private fun injectCSS() {
        // Hide standard headers/footers/ads
        val css = """
            header, footer, .breadcrumb-option, .chat_float, #modal, .sol_sticky, .sol_sticky_container, .appGlobalMsj, .mainmenu, .fullmenu_container {
                display: none !important;
            }
            body {
                background-color: #121212 !important;
                color: #ffffff !important;
            }
            .anime__item, .card {
                transform: scale(1.05);
                margin: 10px;
            }
            .header__right {
                display: none !important;
            }
        """.trimIndent()
        
        val jsCss = "var style = document.createElement('style'); style.innerHTML = `$css`; document.head.appendChild(style);"
        webView.evaluateJavascript(jsCss, null)

        // Hide "External" Control Bar (Report, Download, Fullscreen, etc.)
        // Robust MutationObserver to catch it even if loaded lazily via JS
        val jsRemoveBadControls = """
            (function() {
                function hideBadControls() {
                    var expands = document.querySelectorAll('.fa-expand, .fa-arrows-alt');
                    expands.forEach(function(icon) {
                        var parent = icon.closest('.row') || icon.closest('.d-flex') || icon.parentElement.parentElement;
                        if (parent) {
                             if (parent.querySelector('.fa-download') || parent.querySelector('.fa-exclamation-triangle')) {
                                 parent.style.display = 'none';
                             }
                        }
                    });
                }
                
                // Run immediately
                hideBadControls();
                
                // And watch for changes
                var observer = new MutationObserver(function(mutations) {
                    hideBadControls();
                });
                
                observer.observe(document.body, { childList: true, subtree: true });
            })();
        """.trimIndent()
        webView.evaluateJavascript(jsRemoveBadControls, null)
    }

    override fun onResume() {
        super.onResume()
        webView.requestFocus()
    }

    override fun onBackPressed() {
        if (customView != null) {
            webView.webChromeClient?.onHideCustomView()
        } else if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
