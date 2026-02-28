package com.example.imagis.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\b\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J,\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\r\u001a\u00020\f2\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\u000f\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0010\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\"\u0010\u0011\u001a\u00020\u00122\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0013\u00a8\u0006\u0014"}, d2 = {"Lcom/example/imagis/api/TmdbService;", "", "getNowPlayingMovies", "Lcom/example/imagis/api/TmdbResponse;", "apiKey", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPopularMovies", "getPopularSeries", "getSeasonDetails", "Lcom/example/imagis/api/SeasonDetails;", "tvId", "", "seasonNumber", "(IILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTopRatedMovies", "getTrending", "getTvShowDetails", "Lcom/example/imagis/api/TvShowDetails;", "(ILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface TmdbService {
    
    @retrofit2.http.GET(value = "trending/all/week")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTrending(@retrofit2.http.Query(value = "api_key")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
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
}