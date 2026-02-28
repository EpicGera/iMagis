package com.example.imagis.ui

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.example.imagis.R
import com.example.imagis.api.Movie

/**
 * A Presenter is the Leanback equivalent of a RecyclerView.Adapter.
 * It builds the UI cards and binds our TMDB Movie data to them.
 */
class TmdbCardPresenter : Presenter() {

    // Dark background for idle cards, and a bright red to highlight the currently focused card.
    private val defaultBackgroundColor = Color.parseColor("#1A1A1A")
    private val selectedBackgroundColor = Color.parseColor("#E50914")

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // We create an anonymous subclass of ImageCardView to listen for focus changes
        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        // CRITICAL FOR TV: These allow the D-pad remote to highlight the card
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as Movie
        val cardView = viewHolder.view as ImageCardView

        // Set the text fields provided by the TMDB API
        cardView.titleText = movie.displayTitle
        cardView.contentText = movie.overview
        
        // Standard Leanback card dimensions (adjusted to 16:9 as requested)
        cardView.setMainImageDimensions(320, 180) 

        // Load the image from the web directly into the card
        Glide.with(viewHolder.view.context)
            .load(movie.fullPosterUrl)
            .centerCrop()
            .placeholder(R.drawable.app_icon_placeholder)
            .error(R.drawable.app_icon_placeholder)
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        // Clear image references so the Android Garbage Collector can free memory as you scroll
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) selectedBackgroundColor else defaultBackgroundColor
        // Both the main background and the text info area need their colors updated
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }
}
