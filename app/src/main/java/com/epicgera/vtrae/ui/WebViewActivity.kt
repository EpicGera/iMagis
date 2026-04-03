package com.epicgera.vtrae.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import com.epicgera.vtrae.R

class WebViewActivity : FragmentActivity() {

    private lateinit var webView: WebView
    private lateinit var loadingSpinner: android.widget.ProgressBar

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalSystemUiVisibility: Int = 0
    private var originalOrientation: Int = 0
    private var isYouTubeLive: Boolean = false

    // ── Strict Auto-Hide Timeout Handling for WebView Controls ──
    private val hideTimeoutHandler = Handler(Looper.getMainLooper())
    private val hideControllerRunnable = Runnable {
        val container = findViewById<View>(R.id.control_bar_container)
        // Check if a dialog or input is currently active, avoid hiding if a child has focus unless it's just the buttons
        val focusedView = currentFocus
        if (customView == null) {
            container?.animate()?.alpha(0f)?.setDuration(300)?.withEndAction {
                container.visibility = View.GONE
                // Guarantee focus returns to the webview so it doesn't get trapped
                webView.requestFocus()
            }?.start()
        }
    }

    private fun resetHideTimeout() {
        if (customView != null) return // Don't show controls when in full-screen video mode

        val container = findViewById<View>(R.id.control_bar_container)
        if (container?.visibility != View.VISIBLE || container.alpha < 1f) {
            container?.visibility = View.VISIBLE
            container?.animate()?.alpha(1f)?.setDuration(300)?.start()
        }
        hideTimeoutHandler.removeCallbacks(hideControllerRunnable)
        hideTimeoutHandler.postDelayed(hideControllerRunnable, 3000)
    }

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

        // Detect YouTube TV mode from PlayerActivity
        isYouTubeLive = intent.getBooleanExtra("IS_YOUTUBE_TV", false) ||
            url.contains("youtube.com/watch") || url.contains("youtube.com/tv")

        setupWebView()
        webView.loadUrl(url)
        
        if (isYouTubeLive) {
            // Hide the control bar entirely for YouTube TV
            val container = findViewById<View>(R.id.control_bar_container)
            container?.visibility = View.GONE
        } else {
            resetHideTimeout()
        }
    }
    
    private fun setupButtons() {
        findViewById<android.widget.ImageButton>(R.id.btn_home_web).setOnClickListener {
            resetHideTimeout()
            webView.reload()
        }
        
        findViewById<android.widget.ImageButton>(R.id.btn_search_web).setOnClickListener {
            resetHideTimeout()
            showSearchDialog()
        }
        
        findViewById<android.widget.ImageButton>(R.id.btn_filter_web).setOnClickListener {
            resetHideTimeout()
            showFilterDialog()
        }
    }

    private fun showSearchDialog() {
        // Stop timeout while dialog is open
        hideTimeoutHandler.removeCallbacks(hideControllerRunnable)

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
                resetHideTimeout()
            }
            .setNegativeButton("CANCEL") { _, _ ->
                resetHideTimeout()
            }
            .setOnDismissListener {
                resetHideTimeout()
            }
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
        hideTimeoutHandler.removeCallbacks(hideControllerRunnable)
        
        val letters = (('A'..'Z') + "0-9".toList()).map { it.toString() }.toTypedArray()
        
        android.app.AlertDialog.Builder(this)
            .setTitle("Browse by Letter")
            .setItems(letters) { _, which ->
                val letter = letters[which]
                webView.loadUrl("https://jkanime.net/letra/$letter/")
            }
            .setNegativeButton("Cancel", null)
            .setOnDismissListener {
                resetHideTimeout()
            }
            .show()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.userAgentString = if (isYouTubeLive) {
            // Smart TV User-Agent for YouTube TV interface
            "Mozilla/5.0 (SMART-TV; Linux; Tizen 5.0) AppleWebKit/537.36 (KHTML, like Gecko) Version/5.0 TV Safari/537.36"
        } else {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
        }

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
                if (isYouTubeLive) {
                    injectYouTubeLiveMode()
                } else {
                    injectCSS()
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                val reqUrl = request?.url?.toString() ?: return false
                // Block navigation away from the video page in YouTube Live mode
                if (isYouTubeLive && !reqUrl.contains("youtube.com/watch") && !reqUrl.contains("accounts.google") && !reqUrl.contains("consent.youtube")) {
                    return true // Block
                }
                return false
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
                val container = findViewById<View>(R.id.control_bar_container)
                container?.clearAnimation() // Stop fading if playing
                container?.visibility = View.GONE

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
                
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
                
                resetHideTimeout() // Show controls again when exiting fullscreen
            }
        }
        
        webView.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN,
                android.view.MotionEvent.ACTION_UP -> {
                    resetHideTimeout()
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
                padding-top: env(safe-area-inset-top) !important;
                padding-bottom: env(safe-area-inset-bottom) !important;
                padding-left: env(safe-area-inset-left) !important;
                padding-right: env(safe-area-inset-right) !important;
                overscroll-behavior-y: none !important;
            }
            .anime__item, .card {
                transform: scale(1.05) translateZ(0) !important;
                will-change: transform !important;
                margin: 10px !important;
                transition: transform 0.2s ease-out !important;
            }
            .header__right {
                display: none !important;
            }
        """.trimIndent()
        
        val jsCss = """
            var style = document.createElement('style'); 
            style.innerHTML = `$css`; 
            document.head.appendChild(style);
            
            var meta = document.createElement('meta');
            meta.name = 'viewport';
            meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover';
            document.head.appendChild(meta);
            
            var metaDark = document.createElement('meta');
            metaDark.name = 'color-scheme';
            metaDark.content = 'dark';
            document.head.appendChild(metaDark);
        """.trimIndent()
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

    /**
     * Injects aggressive CSS/JS to transform YouTube into a clean TV player:
     * - Hides ALL UI chrome (header, sidebar, comments, recommendations, end screens)
     * - Makes the video player fill the entire viewport
     * - Auto-skips ads and hides ad overlays
     * - Auto-clicks play button
     */
    private fun injectYouTubeLiveMode() {
        val ytCss = """
            /* Hide EVERYTHING except the video player */
            #header, #masthead, #masthead-container, ytd-masthead,
            #related, #comments, #meta, #info, #owner,
            #secondary, #secondary-inner, #below,
            ytd-watch-next-secondary-results-renderer,
            ytd-compact-video-renderer, ytd-watch-metadata,
            ytd-merch-shelf-renderer, tp-yt-paper-dialog,
            .ytp-chrome-top, .ytp-show-cards-title,
            ytd-engagement-panel-section-list-renderer,
            ytd-popup-container, #cinematics,
            .ytd-watch-flexy #columns #secondary,
            .ytd-watch-flexy #below,
            #chips-wrapper, #chip-bar, ytd-feed-filter-chip-bar-renderer,
            .ytm-autonav-bar, .watch-below-the-player,
            .slim-video-information-renderer,
            .related-chips-slot-wrapper, .reel-shelf-header,
            ytm-related-video-list-renderer, ytm-comment-section-renderer,
            .player-controls-top, c3-toast,
            ytm-pivot-bar-renderer,
            .ytm-autonav-toggle-button-container,
            .single-column-watch-next-modern-panels,
            .watch-below-the-player, ytm-item-section-renderer,
            .slim-video-metadata-section, .compact-link-section,
            .video-secondary-info-renderer,
            ytm-engagement-panel-section-list-renderer {
                display: none !important;
            }

            /* Make player fill entire screen */
            html, body {
                background: #000 !important;
                overflow: hidden !important;
                margin: 0 !important;
                padding: 0 !important;
            }

            .html5-video-player, video,
            #player, #movie_player, .player-container,
            ytm-app, ytm-mobile-topbar-renderer,
            ytm-single-column-watch-next-results-renderer,
            .ytm-watch, #player-container-id {
                position: fixed !important;
                top: 0 !important;
                left: 0 !important;
                width: 100vw !important;
                height: 100vh !important;
                z-index: 9999 !important;
                background: #000 !important;
            }

            video {
                object-fit: contain !important;
                width: 100vw !important;
                height: 100vh !important;
            }

            /* Hide ad overlays */
            .ytp-ad-overlay-container, .ytp-ad-text-overlay,
            .ad-showing .ytp-ad-player-overlay,
            .ytp-ad-skip-button-slot, .ytp-ad-module,
            .ytd-promoted-sparkles-web-renderer,
            ytm-promoted-sparkles-web-renderer,
            .ad-interrupting, .ad-showing {
                display: none !important;
            }
        """.trimIndent()

        // Inject CSS first
        val cssEscaped = ytCss.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")
        val injectCssJs = "var s=document.createElement('style');s.innerHTML='$cssEscaped';document.head.appendChild(s);"
        webView.evaluateJavascript(injectCssJs, null)

        // Then inject the ad-skipper and auto-play logic
        val ytJs = """
            (function() {
                // Auto-skip ads and auto-play loop
                setInterval(function() {
                    // Skip ad button
                    var skipBtn = document.querySelector('.ytp-ad-skip-button, .ytp-ad-skip-button-modern, .ytp-skip-ad-button');
                    if (skipBtn) skipBtn.click();

                    // Close ad overlay
                    var closeBtn = document.querySelector('.ytp-ad-overlay-close-button');
                    if (closeBtn) closeBtn.click();

                    // Dismiss popups (cookie consent, etc)
                    var dismissBtn = document.querySelector('[aria-label="Dismiss"], .dismiss-button, tp-yt-paper-dialog #dismiss-button, .consent-bump-v2-lightbox .eom-buttons button');
                    if (dismissBtn) dismissBtn.click();

                    // Auto-play if paused
                    var video = document.querySelector('video');
                    if (video && video.paused && video.readyState >= 2) {
                        video.play();
                    }
                }, 1000);

                // Click fullscreen after a short delay
                setTimeout(function() {
                    var fsBtn = document.querySelector('.ytp-fullscreen-button, .fullscreen-icon');
                    if (fsBtn) fsBtn.click();
                }, 3000);
            })();
        """.trimIndent()

        webView.evaluateJavascript(ytJs, null)
    }

    override fun onResume() {
        super.onResume()
        webView.requestFocus()
        resetHideTimeout()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideTimeoutHandler.removeCallbacksAndMessages(null)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            // Reset hide timeout on any key press
            resetHideTimeout()
        }
        return super.dispatchKeyEvent(event)
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

