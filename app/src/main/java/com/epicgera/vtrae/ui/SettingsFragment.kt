package com.epicgera.vtrae.ui

import android.content.Context
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import com.epicgera.vtrae.utils.SyncEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withContext
import com.epicgera.vtrae.R
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class SettingsFragment : GuidedStepSupportFragment() {

    companion object {
        private const val ACTION_ID_CUSTOM_URL = 1L
        private const val ACTION_ID_SAVE_AND_SYNC = 2L
        private const val ACTION_ID_LANGUAGE = 3L
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            "Add Custom Playlist",
            "Enter your special M3U URL below to add it to your library.",
            "Settings",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val sharedPref = requireActivity().getSharedPreferences("VtraePrefs", Context.MODE_PRIVATE)
        val savedUrl = sharedPref.getString("CUSTOM_M3U_URL", "")

        val inputAction = GuidedAction.Builder(requireContext())
            .id(ACTION_ID_CUSTOM_URL)
            .title(savedUrl)
            .description("Click to type URL")
            .editable(true)
            .build()

        val saveAction = GuidedAction.Builder(requireContext())
            .id(ACTION_ID_SAVE_AND_SYNC)
            .title("Save & Sync Database")
            .description("Downloads the new movies immediately.")
            .build()
            
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentLang = if (!currentLocales.isEmpty) currentLocales.get(0)?.language else "en"
        val langDisplay = if (currentLang == "es") "Español" else "English"

        val languageAction = GuidedAction.Builder(requireContext())
            .id(ACTION_ID_LANGUAGE)
            .title(requireContext().getString(R.string.settings_language) + " ($langDisplay)")
            .description(requireContext().getString(R.string.settings_language_desc))
            .build()

        actions.add(inputAction)
        actions.add(saveAction)
        actions.add(languageAction)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == ACTION_ID_SAVE_AND_SYNC) {
            val urlAction = findActionById(ACTION_ID_CUSTOM_URL)
            val typedUrl = urlAction?.title?.toString() ?: ""

            if (typedUrl.startsWith("http")) {
                val sharedPref = requireActivity().getSharedPreferences("VtraePrefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("CUSTOM_M3U_URL", typedUrl).apply()

                com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_url_saved))

                lifecycleScope.launch(Dispatchers.IO) {
                    // Update SyncEngine to handle custom URLs if needed, or just trigger sync
                    val success = SyncEngine.syncPlaylists(requireContext(), forceSync = true)
                    
                    withContext(Dispatchers.Main) {
                        if (success) {
                            com.epicgera.vtrae.ui.components.VtrToastManager.showInfo(getString(R.string.msg_library_updated))
                            requireActivity().finish()
                        } else {
                            com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_failed_download_url))
                        }
                    }
                }
            } else {
                com.epicgera.vtrae.ui.components.VtrToastManager.showError(getString(R.string.msg_invalid_url))
            }
        } else if (action.id == ACTION_ID_LANGUAGE) {
            val currentLocales = AppCompatDelegate.getApplicationLocales()
            val currentLang = if (!currentLocales.isEmpty) currentLocales.get(0)?.language else "en"
            
            // Toggle English <-> Spanish
            val newLang = if (currentLang == "es") "en" else "es"
            
            val locales = LocaleListCompat.forLanguageTags(newLang)
            AppCompatDelegate.setApplicationLocales(locales)
            
            // Note: setApplicationLocales automatically recreates started activities
            // that are sensitive to locale changes.
        }
    }
}

