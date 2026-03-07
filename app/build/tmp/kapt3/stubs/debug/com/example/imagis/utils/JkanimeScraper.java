package com.example.imagis.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u00072\u0006\u0010\u000e\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u0007H\u0086@\u00a2\u0006\u0002\u0010\u0012J\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00110\u00072\u0006\u0010\u0014\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\r0\u00072\u0006\u0010\u0016\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u0018\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\u000fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/example/imagis/utils/JkanimeScraper;", "", "()V", "BASE_URL", "", "USER_AGENT", "getAnimeDirectory", "", "Lcom/example/imagis/data/AnimeSeries;", "page", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getEpisodeServers", "Lcom/example/imagis/data/VideoServer;", "episodeUrl", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatestEpisodes", "Lcom/example/imagis/data/AnimeEpisode;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSeriesEpisodes", "seriesUrl", "getVideoServers", "url", "searchAnime", "query", "app_debug"})
public final class JkanimeScraper {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BASE_URL = "https://jkanime.net";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.imagis.utils.JkanimeScraper INSTANCE = null;
    
    private JkanimeScraper() {
        super();
    }
    
    /**
     * Fetches the latest added episodes from the homepage.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getLatestEpisodes(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.imagis.data.AnimeEpisode>> $completion) {
        return null;
    }
    
    /**
     * Extracts video embed links for a specific episode URL.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getVideoServers(@org.jetbrains.annotations.NotNull()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.imagis.data.VideoServer>> $completion) {
        return null;
    }
    
    /**
     * Fetches recent anime series by looking at the homepage updates.
     * The /directorio/ page is dynamically loaded via JS, so we use the homepage as a "recent series" directory.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAnimeDirectory(int page, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.imagis.data.AnimeSeries>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchAnime(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.imagis.data.AnimeSeries>> $completion) {
        return null;
    }
    
    /**
     * Fetches all episodes for a specific anime series URL using the AJAX JSON API.
     * Steps:
     * 1. Load the series page to extract the anime ID and CSRF token
     * 2. POST to /ajax/episodes/{id}/ with the CSRF token
     * 3. Parse the paginated JSON response
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getSeriesEpisodes(@org.jetbrains.annotations.NotNull()
    java.lang.String seriesUrl, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.imagis.data.AnimeEpisode>> $completion) {
        return null;
    }
    
    /**
     * Extracts direct streaming server URLs from a Jkanime episode page.
     * Jkanime embeds player URLs like jkplayer/um and jkplayer/umv in the HTML.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getEpisodeServers(@org.jetbrains.annotations.NotNull()
    java.lang.String episodeUrl, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.imagis.data.VideoServer>> $completion) {
        return null;
    }
}