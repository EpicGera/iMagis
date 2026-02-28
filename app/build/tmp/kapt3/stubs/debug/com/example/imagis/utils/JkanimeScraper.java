package com.example.imagis.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0086@\u00a2\u0006\u0002\u0010\rJ\u001c\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00072\u0006\u0010\u0010\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\u0011R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/example/imagis/utils/JkanimeScraper;", "", "()V", "BASE_URL", "", "USER_AGENT", "getAnimeDirectory", "", "Lcom/example/imagis/data/AnimeEpisode;", "page", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatestEpisodes", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getVideoServers", "Lcom/example/imagis/data/VideoServer;", "url", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
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
     * Fetches the full anime directory.
     * Page 1: https://jkanime.net/directorio/
     * Page 2: https://jkanime.net/directorio/2/
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAnimeDirectory(int page, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.imagis.data.AnimeEpisode>> $completion) {
        return null;
    }
}