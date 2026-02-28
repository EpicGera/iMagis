package com.example.imagis.ui

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.imagis.R
import com.example.imagis.utils.SyncEngine
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Main entry point. Loads the BrowseFragment which displays our categories.
 * Note: Activity must extend FragmentActivity to use Leanback fragments.
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainFragment())
                .commitNow()
        }

        // Trigger the Database Sync in the background
        initializeDatabase()
    }

    private fun initializeDatabase() {
        Toast.makeText(this, "Updating Channel Database...", Toast.LENGTH_SHORT).show()
        
        lifecycleScope.launch(Dispatchers.IO) {
            // Run the sync engine (it will skip automatically if data already exists)
            val success = SyncEngine.syncPlaylists(this@MainActivity, forceSync = false)
            
            withContext(Dispatchers.Main) {
                if (success) {
                    // Only show if it actually did something or finished
                    // Toast.makeText(this@MainActivity, "Database Ready!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Database Sync Failed. Check Connection.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
