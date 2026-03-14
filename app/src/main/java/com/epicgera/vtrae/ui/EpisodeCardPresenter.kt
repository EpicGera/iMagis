package com.epicgera.vtrae.ui

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.epicgera.vtrae.api.Episode
import com.epicgera.vtrae.R

class EpisodeCardPresenter : Presenter() {

    private val defaultBackgroundColor = Color.parseColor("#1A1A1A")
    private val selectedBackgroundColor = Color.parseColor("#E50914")

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val episode = item as Episode
        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = episode.displayTitle
        cardView.contentText = episode.overview
        // Make the cards wide for episode stills (16:9 ratio)
        cardView.setMainImageDimensions(320, 180) 

        Glide.with(viewHolder.view.context)
            .load(episode.fullStillUrl)
            //.centerCrop() // Optional: might crop important details, fitCenter or just load relies on ImageView scaleType
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) selectedBackgroundColor else defaultBackgroundColor
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }
}

