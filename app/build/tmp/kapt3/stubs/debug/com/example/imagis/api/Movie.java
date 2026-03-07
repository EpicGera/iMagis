package com.example.imagis.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\'\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001Bq\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\u000e\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u00a2\u0006\u0002\u0010\u0010J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010+\u001a\u0004\u0018\u00010\u000fH\u00c6\u0003\u00a2\u0006\u0002\u0010(J\u000b\u0010,\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010.\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0011\u00101\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000bH\u00c6\u0003J\u000b\u00102\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u00103\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0088\u0001\u00104\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00052\u0010\b\u0002\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u00c6\u0001\u00a2\u0006\u0002\u00105J\u0013\u00106\u001a\u0002072\b\u00108\u001a\u0004\u0018\u000109H\u00d6\u0003J\t\u0010:\u001a\u00020\u0003H\u00d6\u0001J\t\u0010;\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0013\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b\u0014\u0010\u0012R\u0011\u0010\u0015\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\u0012R\u0011\u0010\u0017\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b\u0018\u0010\u0012R\u0013\u0010\r\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0012R\u0011\u0010\u001a\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b\u001b\u0010\u0012R\u0011\u0010\u001c\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b\u001d\u0010\u0012R\u0019\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0012R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0012R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0012R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0012R\u0015\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u00a2\u0006\n\n\u0002\u0010)\u001a\u0004\b\'\u0010(\u00a8\u0006<"}, d2 = {"Lcom/example/imagis/api/Movie;", "Ljava/io/Serializable;", "id", "", "title", "", "name", "overview", "poster_path", "backdrop_path", "genre_ids", "", "release_date", "first_air_date", "vote_average", "", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Double;)V", "getBackdrop_path", "()Ljava/lang/String;", "displayRating", "getDisplayRating", "displayTitle", "getDisplayTitle", "displayYear", "getDisplayYear", "getFirst_air_date", "fullBackdropUrl", "getFullBackdropUrl", "fullPosterUrl", "getFullPosterUrl", "getGenre_ids", "()Ljava/util/List;", "getId", "()I", "getName", "getOverview", "getPoster_path", "getRelease_date", "getTitle", "getVote_average", "()Ljava/lang/Double;", "Ljava/lang/Double;", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Double;)Lcom/example/imagis/api/Movie;", "equals", "", "other", "", "hashCode", "toString", "app_debug"})
public final class Movie implements java.io.Serializable {
    private final int id = 0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String overview = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String poster_path = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String backdrop_path = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<java.lang.Integer> genre_ids = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String release_date = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String first_air_date = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double vote_average = null;
    
    public Movie(int id, @org.jetbrains.annotations.Nullable()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String overview, @org.jetbrains.annotations.Nullable()
    java.lang.String poster_path, @org.jetbrains.annotations.Nullable()
    java.lang.String backdrop_path, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.Integer> genre_ids, @org.jetbrains.annotations.Nullable()
    java.lang.String release_date, @org.jetbrains.annotations.Nullable()
    java.lang.String first_air_date, @org.jetbrains.annotations.Nullable()
    java.lang.Double vote_average) {
        super();
    }
    
    public final int getId() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOverview() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPoster_path() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBackdrop_path() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.Integer> getGenre_ids() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRelease_date() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFirst_air_date() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getVote_average() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFullPosterUrl() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFullBackdropUrl() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayYear() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayRating() {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.Integer> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.imagis.api.Movie copy(int id, @org.jetbrains.annotations.Nullable()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String overview, @org.jetbrains.annotations.Nullable()
    java.lang.String poster_path, @org.jetbrains.annotations.Nullable()
    java.lang.String backdrop_path, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.Integer> genre_ids, @org.jetbrains.annotations.Nullable()
    java.lang.String release_date, @org.jetbrains.annotations.Nullable()
    java.lang.String first_air_date, @org.jetbrains.annotations.Nullable()
    java.lang.Double vote_average) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}