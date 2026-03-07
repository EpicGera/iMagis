package com.example.imagis.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import com.example.imagis.utils.SyncEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.imagis.R

class SettingsFragment : GuidedStepSupportFragment() {

    companion object {
        private const val ACTION_ID_CUSTOM_URL = 1L
        private const val ACTION_ID_SAVE_AND_SYNC = 2L
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
        val sharedPref = requireActivity().getSharedPreferences("iMagisPrefs", Context.MODE_PRIVATE)
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

        actions.add(inputAction)
        actions.add(saveAction)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == ACTION_ID_SAVE_AND_SYNC) {
            val urlAction = findActionById(ACTION_ID_CUSTOM_URL)
            val typedUrl = urlAction?.title?.toString() ?: ""

            if (typedUrl.startsWith("http")) {
                val sharedPref = requireActivity().getSharedPreferences("iMagisPrefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("CUSTOM_M3U_URL", typedUrl).apply()

                Toast.makeText(requireContext(), R.string.msg_url_saved, Toast.LENGTH_SHORT).show()

                lifecycleScope.launch(Dispatchers.IO) {
                    // Update SyncEngine to handle custom URLs if needed, or just trigger sync
                    val success = SyncEngine.syncPlaylists(requireContext(), forceSync = true)
                    
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(requireContext(), R.string.msg_library_updated, Toast.LENGTH_LONG).show()
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), R.string.msg_failed_download_url, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), R.string.msg_invalid_url, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
