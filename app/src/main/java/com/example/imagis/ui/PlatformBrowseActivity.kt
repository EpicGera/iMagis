// FILE_PATH: app/src/main/java/com/example/imagis/ui/PlatformBrowseActivity.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.example.imagis.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.imagis.api.Movie
import com.example.imagis.ui.screens.PlatformCatalogScreen
import com.example.imagis.ui.theme.BrutalistTheme

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
