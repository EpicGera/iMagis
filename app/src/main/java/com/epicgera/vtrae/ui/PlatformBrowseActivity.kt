// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/PlatformBrowseActivity.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.epicgera.vtrae.api.Movie
import com.epicgera.vtrae.ui.screens.PlatformCatalogScreen
import com.epicgera.vtrae.ui.theme.BrutalistTheme

class PlatformBrowseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrutalistTheme {
                PlatformCatalogScreen(
                    onMovieClick = { movie -> handleMovieClick(movie) }
                )
            }
        }
    }

    private fun handleMovieClick(movie: Movie) {
        if (movie.title == null && movie.name != null) {
            // TV Show
            val intent = Intent(this, TvSeasonsActivity::class.java)
            intent.putExtra("TV_SHOW_ID", movie.id)
            intent.putExtra("TV_SHOW_NAME", movie.name)
            startActivity(intent)
        } else {
            // Movie
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("MOVIE_EXTRA", movie)
            startActivity(intent)
        }
    }
}

