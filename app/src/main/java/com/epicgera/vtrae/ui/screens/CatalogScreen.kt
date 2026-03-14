// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/screens/CatalogScreen.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.compose.ui.focus.onFocusChanged
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.itemsIndexed
import coil.compose.AsyncImage
import com.epicgera.vtrae.api.Movie
import com.epicgera.vtrae.data.AnimeSeries
import com.epicgera.vtrae.ui.components.HeroSpotlight
import com.epicgera.vtrae.ui.components.PulsingFlixLoader
import com.epicgera.vtrae.ui.components.ShimmerPosterCard
import com.epicgera.vtrae.ui.components.flixFocus
import com.epicgera.vtrae.ui.theme.FlixAmber
import com.epicgera.vtrae.ui.theme.FlixBlack
import com.epicgera.vtrae.ui.theme.FlixCardSurface
import com.epicgera.vtrae.ui.theme.FlixGold
import com.epicgera.vtrae.ui.theme.FlixGray
import com.epicgera.vtrae.ui.theme.FlixRed
import com.epicgera.vtrae.ui.theme.FlixSurface
import com.epicgera.vtrae.ui.theme.FlixWhite

import androidx.compose.ui.res.stringResource
import com.epicgera.vtrae.R

// ── NAVIGATION CATEGORIES ──────────────────────────────────

enum class NavCategory(val labelResId: Int) {
    TRENDING(R.string.category_trending),
    POPULAR(R.string.category_popular),
    TOP_RATED(R.string.category_top_rated),
    MOVIES(R.string.nav_movies),
    SERIES(R.string.nav_series),
    PLATFORMS(R.string.nav_platforms),
    ANIME(R.string.nav_anime),
    LIVE_TV(R.string.nav_livetv),
    FAVORITES(R.string.nav_favorites),
    HISTORY(R.string.nav_history),
    DOWNLOADS(R.string.nav_downloads),
    SETTINGS(R.string.nav_settings),
}

// ── CONTENT SUB-TABS ───────────────────────────────────────

enum class ContentSubTab(val labelResId: Int) {
    MOVIES(R.string.nav_movies),
    TV_SERIES(R.string.nav_series),
    ANIME(R.string.nav_anime),
}

fun contentKey(category: NavCategory, subTab: ContentSubTab): String =
    "${category.name}_${subTab.name}"

val SUB_TAB_CATEGORIES = setOf(NavCategory.TRENDING, NavCategory.POPULAR, NavCategory.TOP_RATED)

// ── CATALOG SCREEN ─────────────────────────────────────────

@Composable
fun CatalogScreen(
    contentMap: Map<String, List<Movie>> = emptyMap(),
    animeDirectory: List<AnimeSeries> = emptyList(),
    onMovieClick: (Movie) -> Unit = {},
    onAnimeSeriesClick: (AnimeSeries) -> Unit = {},
    onSearchAnime: (String) -> Unit = {},
    onSearchContent: (String) -> Unit = {},
    onNavClick: (NavCategory) -> Unit = {},
) {
    var selectedCategory by remember { mutableStateOf(NavCategory.TRENDING) }
    var selectedSubTab by remember { mutableStateOf(ContentSubTab.MOVIES) }
    var searchQuery by remember { mutableStateOf("") }
    var sortAscending by remember { mutableStateOf(true) }

    val hasSubTabs = selectedCategory in SUB_TAB_CATEGORIES
    val currentKey = if (hasSubTabs) contentKey(selectedCategory, selectedSubTab)
                     else "${selectedCategory.name}_ALL"
    val currentMovies = contentMap[currentKey].orEmpty()

    // Hero movies: take from trending movies for the spotlight
    val heroMovies = contentMap[contentKey(NavCategory.TRENDING, ContentSubTab.MOVIES)]
        .orEmpty()
        .filter { it.backdrop_path != null }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(FlixBlack),
    ) {
        // ── LEFT: SIDE NAVIGATION ──────────────────────────
        SideNavColumn(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                selectedCategory = category
                searchQuery = ""
                selectedSubTab = ContentSubTab.MOVIES
                when (category) {
                    NavCategory.LIVE_TV,
                    NavCategory.DOWNLOADS,
                    NavCategory.SETTINGS -> onNavClick(category)
                    NavCategory.FAVORITES,
                    NavCategory.HISTORY -> onNavClick(category)
                    else -> { /* content category — grid updates */ }
                }
            },
            modifier = Modifier
                .width(220.dp)
                .fillMaxHeight(),
        )

        // ── DIVIDER ────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(FlixSurface),
        )

        // ── RIGHT PANE (CONTENT) ───────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            val isContentCategory = !setOf(
                NavCategory.LIVE_TV, NavCategory.DOWNLOADS,
                NavCategory.SETTINGS, NavCategory.PLATFORMS
            ).contains(selectedCategory)

            // Header: Search & Sort
            if (isContentCategory) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            when (selectedCategory) {
                                NavCategory.ANIME -> onSearchAnime(it)
                                else -> onSearchContent(it)
                            }
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = FlixWhite,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .flixFocus()
                            .clip(RoundedCornerShape(8.dp))
                            .background(FlixSurface)
                            .padding(14.dp),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "🔍  " + stringResource(R.string.search_hint),
                                    color = FlixGray,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp,
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Sort Toggle
                    Box(
                        modifier = Modifier
                            .flixFocus()
                            .clickable { sortAscending = !sortAscending }
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sortAscending) FlixRed else FlixAmber)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = if (sortAscending) stringResource(R.string.sort_az) else stringResource(R.string.sort_za),
                            color = FlixWhite,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── SUB-TAB ROW ──────────────────────────────────
            if (hasSubTabs) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ContentSubTab.entries.forEach { tab ->
                        val isSelected = tab == selectedSubTab
                        Box(
                            modifier = Modifier
                                .flixFocus()
                                .clickable { selectedSubTab = tab }
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) FlixRed else FlixSurface
                                )
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = stringResource(tab.labelResId),
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = FlixWhite,
                            )
                        }
                    }
                }
            }

            // ── CONTENT AREA ──────────────────────────────────
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (selectedCategory == NavCategory.PLATFORMS) {
                    PlatformCatalogScreen(onMovieClick = onMovieClick)
                } else if (selectedCategory == NavCategory.ANIME) {
                    val filteredAnime = animeDirectory
                        .filter { it.title.contains(searchQuery, ignoreCase = true) }
                        .let { list ->
                            if (sortAscending) list.sortedBy { it.title }
                            else list.sortedByDescending { it.title }
                        }

                    if (filteredAnime.isEmpty()) {
                        PulsingFlixLoader(message = stringResource(R.string.loading_anime))
                    } else {
                        AnimeGrid(
                            animes = filteredAnime,
                            onAnimeClick = onAnimeSeriesClick,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                } else if (isContentCategory) {
                    if (currentMovies.isEmpty()) {
                        PulsingFlixLoader(message = stringResource(R.string.loading))
                    } else {
                        val filteredMovies = currentMovies
                            .filter { it.displayTitle.contains(searchQuery, ignoreCase = true) }
                            .let { list ->
                                if (sortAscending) list.sortedBy { it.displayTitle }
                                else list.sortedByDescending { it.displayTitle }
                            }

                        // Scrollable content with Hero + Poster rows
                        TvLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            // Hero Spotlight at the top
                            if (heroMovies.isNotEmpty() && selectedCategory == NavCategory.TRENDING && searchQuery.isEmpty()) {
                                item {
                                    HeroSpotlight(
                                        movies = heroMovies,
                                        onMovieClick = onMovieClick,
                                        modifier = Modifier.padding(top = 8.dp),
                                    )
                                }
                            }

                            // Section header
                            item {
                                val categoryText = stringResource(selectedCategory.labelResId)
                                val subTabText = if (hasSubTabs) stringResource(selectedSubTab.labelResId) else stringResource(R.string.all)
                                Text(
                                    text = "$categoryText • $subTabText",
                                    color = FlixWhite,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                                )
                            }

                            // Horizontal poster row
                            item {
                                TvLazyRow(
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                ) {
                                    items(filteredMovies, key = { it.id }) { movie ->
                                        FlixPosterCard(
                                            movie = movie,
                                            onClick = { onMovieClick(movie) },
                                        )
                                    }
                                }
                            }

                            // If we have other sub-tabs content, show them too as extra rows
                            if (hasSubTabs && searchQuery.isEmpty()) {
                                val otherTabs = ContentSubTab.entries.filter { it != selectedSubTab }
                                otherTabs.forEach { tab ->
                                    val otherKey = contentKey(selectedCategory, tab)
                                    val otherMovies = contentMap[otherKey].orEmpty()
                                    if (otherMovies.isNotEmpty()) {
                                        item {
                                            Text(
                                                text = stringResource(tab.labelResId),
                                                color = FlixGray,
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                                            )
                                        }
                                        item {
                                            TvLazyRow(
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                            ) {
                                                items(otherMovies, key = { it.id }) { movie ->
                                                    FlixPosterCard(
                                                        movie = movie,
                                                        onClick = { onMovieClick(movie) },
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── SIDE NAVIGATION COLUMN ─────────────────────────────────

@Composable
private fun SideNavColumn(
    selectedCategory: NavCategory,
    onCategorySelected: (NavCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    TvLazyColumn(
        modifier = modifier
            .background(FlixBlack)
            .padding(vertical = 24.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        // App title
        item {
            Text(
                text = "VTR Æ",
                color = FlixRed,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 12.dp, bottom = 16.dp),
            )
        }

        itemsIndexed(NavCategory.entries.toList()) { _, category ->
            NavItem(
                label = stringResource(category.labelResId),
                isSelected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = if (isSelected) FlixRed else if (isFocused) Color.Cyan else Color.Transparent
    val backgroundColor = if (isSelected) FlixRed else if (isFocused) Color.Cyan.copy(alpha=0.15f) else Color.Transparent

    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "scale")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.SansSerif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 15.sp,
            color = if (isFocused) Color.Cyan else FlixWhite,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

// ── FLIX POSTER CARD ───────────────────────────────────────
// Rounded poster card with gradient overlay, title, and rating badge.

@Composable
private fun FlixPosterCard(
    movie: Movie,
    onClick: () -> Unit,
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "scale")
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Color.Cyan else Color.Transparent,
        label = "borderColor"
    )
    val cardAlpha by animateFloatAsState(targetValue = if (isFocused) 1.0f else 0.8f, label = "alpha")

    Column(
        modifier = Modifier
            .width(160.dp)
            .scale(scale)
            .alpha(cardAlpha)
            .clip(RoundedCornerShape(12.dp))
            .border(3.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .focusable(interactionSource = interactionSource),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(FlixCardSurface),
        ) {
            // Poster image
            AsyncImage(
                model = movie.fullPosterUrl,
                contentDescription = movie.displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
            )

            // Bottom gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, FlixBlack.copy(alpha = 0.85f)),
                        )
                    )
            )

            // Rating badge (top-right)
            val rating = movie.displayRating
            if (rating.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(FlixBlack.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "⭐ $rating",
                        color = FlixGold,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
            }

            // Title at bottom
            Text(
                text = movie.displayTitle,
                color = FlixWhite,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
            )
        }
    }
}

// ── ANIME SERIES GRID ─────────────────────────────────────

@Composable
private fun AnimeGrid(
    animes: List<AnimeSeries>,
    onAnimeClick: (AnimeSeries) -> Unit,
    modifier: Modifier = Modifier,
) {
    TvLazyVerticalGrid(
        columns = TvGridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(
            top = 16.dp,
            end = 16.dp,
            bottom = 32.dp,
            start = 8.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier,
    ) {
        items(
            count = animes.size,
            key = { index -> animes[index].seriesUrl }
        ) { index ->
            val anime = animes[index]
            AnimeSeriesCard(
                anime = anime,
                onClick = { onAnimeClick(anime) }
            )
        }
    }
}

@Composable
private fun AnimeSeriesCard(
    anime: AnimeSeries,
    onClick: () -> Unit,
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "scale")
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Color.Cyan else Color.Transparent,
        label = "borderColor"
    )
    val cardAlpha by animateFloatAsState(targetValue = if (isFocused) 1.0f else 0.8f, label = "alpha")

    Column(
        modifier = Modifier
            .width(160.dp)
            .scale(scale)
            .alpha(cardAlpha)
            .clip(RoundedCornerShape(12.dp))
            .border(3.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .focusable(interactionSource = interactionSource),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FlixCardSurface),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
            )

            // Bottom gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, FlixBlack.copy(alpha = 0.85f)),
                        )
                    )
            )

            // Type badge (top-right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(FlixRed)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = anime.type.uppercase(),
                    color = FlixWhite,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }

            // Title at bottom
            Text(
                text = anime.title,
                color = FlixWhite,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
            )
        }
    }
}

