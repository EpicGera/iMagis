// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/components/VtrToastManager.kt
package com.epicgera.vtrae.ui.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

enum class VtrToastType {
    INFO,
    ERROR
}

data class VtrToastMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val type: VtrToastType
)

/**
 * Singleton manager to coordinate global custom toasts across the app.
 */
object VtrToastManager {

    private val _messages = MutableStateFlow<List<VtrToastMessage>>(emptyList())
    val messages: StateFlow<List<VtrToastMessage>> = _messages.asStateFlow()

    fun showInfo(text: String) {
        show(text, VtrToastType.INFO)
    }

    fun showError(text: String) {
        show(text, VtrToastType.ERROR)
    }

    private fun show(text: String, type: VtrToastType) {
        val message = VtrToastMessage(text = text, type = type)
        _messages.update { currentList ->
            val mutableList = currentList.toMutableList()
            mutableList.add(message)
            mutableList
        }
    }

    fun dismiss(id: String) {
        _messages.update { currentList ->
            currentList.filterNot { it.id == id }
        }
    }
}

