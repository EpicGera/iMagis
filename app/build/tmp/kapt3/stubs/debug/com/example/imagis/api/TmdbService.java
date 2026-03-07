package com.example.imagis.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001JF\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\n\b\u0003\u0010\u0006\u001a\u0004\u0018\u00010\u00052\b\b\u0003\u0010\u0007\u001a\u00020\u00052\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u00052\n\b\u0003\u0010\t\u001a\u0004\u0018\u00010\u0005H\u00a7@\u00a2\u0006\u0002\u0010\nJF\u0010\u000b\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\n\b\u0003\u0010\u0006\u001a\u0004\u0018\u00010\u00052\b\b\u0003\u0010\u0007\u001a\u00020\u00052\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u00052\n\b\u0003\u0010\t\u001a\u0004\u0018\u00010\u0005H\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\f\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u000e\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u000f\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u0010\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ,\u0010\u0011\u001a\u00020\u00122\b\b\u0001\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0015\u001a\u00020\u00142\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u0018\u0010\u0017\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u0018\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u0019\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u001a\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u001b\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\"\u0010\u001c\u001a\u00020\u001d2\b\b\u0001\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u001eJ\"\u0010\u001f\u001a\u00020 2\b\b\u0001\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u001eJ\"\u0010!\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\"\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010#J\"\u0010$\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\"\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010#J\"\u0010%\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\"\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010#\u00a8\u0006&"}, d2 = {"Lcom/example/imagis/api/TmdbService;", "", "discoverMovies", "Lcom/example/imagis/api/TmdbResponse;", "apiKey", "", "genres", "sortBy", "watchProviders", "watchRegion", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "discoverTv", "getAiringTodaySeries", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getNowPlayingMovies", "getPopularMovies", "getPopularSeries", "getSeasonDetails", "Lcom/example/imagis/api/SeasonDetails;", "tvId", "", "seasonNumber", "(IILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTopRatedMovies", "getTopRatedSeries", "getTrending", "getTrendingMovies", "getTrendingTv", "getTvExternalIds", "Lcom/example/imagis/api/ExternalIdsResponse;", "(ILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTvShowDetails", "Lcom/example/imagis/api/TvShowDetails;", "searchMovies", "query", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchMulti", "searchTvShows", "app_debug"})
public abstract interface TmdbService {
    
    @retrofit2.http.GET(value = "trending/all/week")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTrending(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "trending/movie/week")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTrendingMovies(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "trending/tv/week")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTrendingTv(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "discover/movie")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object discoverMovies(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @retrofit2.http.Query(value = "with_genres")
    @org.jetbrains.annotations.Nullable()
    java.lang.String genres, @retrofit2.http.Query(value = "sort_by")
    @org.jetbrains.annotations.NotNull()
    java.lang.String sortBy, @retrofit2.http.Query(value = "with_watch_providers")
    @org.jetbrains.annotations.Nullable()
    java.lang.String watchProviders, @retrofit2.http.Query(value = "watch_region")
    @org.jetbrains.annotations.Nullable()
    java.lang.String watchRegion, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "discover/tv")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object discoverTv(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @retrofit2.http.Query(value = "with_genres")
    @org.jetbrains.annotations.Nullable()
    java.lang.String genres, @retrofit2.http.Query(value = "sort_by")
    @org.jetbrains.annotations.NotNull()
    java.lang.String sortBy, @retrofit2.http.Query(value = "with_watch_providers")
    @org.jetbrains.annotations.Nullable()
    java.lang.String watchProviders, @retrofit2.http.Query(value = "watch_region")
    @org.jetbrains.annotations.Nullable()
    java.lang.String watchRegion, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "search/tv")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchTvShows(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @retrofit2.http.Query(value = "query")
    @org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "search/movie")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchMovies(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @retrofit2.http.Query(value = "query")
    @org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "search/multi")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchMulti(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @retrofit2.http.Query(value = "query")
    @org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "movie/popular")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPopularMovies(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "tv/popular")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPopularSeries(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "tv/top_rated")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTopRatedSeries(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "tv/airing_today")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAiringTodaySeries(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "movie/now_playing")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getNowPlayingMovies(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "movie/top_rated")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTopRatedMovies(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TmdbResponse> $completion);
    
    @retrofit2.http.GET(value = "tv/{tv_id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTvShowDetails(@retrofit2.http.Path(value = "tv_id")
    int tvId, @retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.TvShowDetails> $completion);
    
    @retrofit2.http.GET(value = "tv/{tv_id}/season/{season_number}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSeasonDetails(@retrofit2.http.Path(value = "tv_id")
    int tvId, @retrofit2.http.Path(value = "season_number")
    int seasonNumber, @retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.SeasonDetails> $completion);
    
    @retrofit2.http.GET(value = "tv/{tv_id}/external_ids")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTvExternalIds(@retrofit2.http.Path(value = "tv_id")
    int tvId, @retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.api.ExternalIdsResponse> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}