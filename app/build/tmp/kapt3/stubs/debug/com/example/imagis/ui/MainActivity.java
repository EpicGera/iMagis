package com.example.imagis.ui;

/**
 * Main entry point — Compose for TV with Brutalist theme.
 * Replaces the old FragmentActivity + BrowseSupportFragment.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0007H\u0002J\u0010\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u000bH\u0002J\u0010\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u0010\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u0004H\u0002J\u0010\u0010\u0018\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u0004H\u0002J\b\u0010\u0019\u001a\u00020\u000fH\u0002J\b\u0010\u001a\u001a\u00020\u000fH\u0002J\u0012\u0010\u001b\u001a\u00020\u000f2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0014J4\u0010\u001e\u001a\u0004\u0018\u0001H\u001f\"\u0004\b\u0000\u0010\u001f2\u001c\u0010 \u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u001f0\"\u0012\u0006\u0012\u0004\u0018\u00010#0!H\u0082@\u00a2\u0006\u0002\u0010$R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/example/imagis/ui/MainActivity;", "Landroidx/activity/ComponentActivity;", "()V", "ANIMATION_GENRE", "", "animeDirectory", "Landroidx/compose/runtime/snapshots/SnapshotStateList;", "Lcom/example/imagis/data/AnimeSeries;", "contentMap", "Landroidx/compose/runtime/snapshots/SnapshotStateMap;", "", "Lcom/example/imagis/api/Movie;", "searchJob", "Lkotlinx/coroutines/Job;", "handleAnimeSeriesClick", "", "series", "handleMovieClick", "movie", "handleNavClick", "category", "Lcom/example/imagis/ui/screens/NavCategory;", "handleSearchAnime", "query", "handleSearchContent", "initializeDatabase", "loadTmdbContent", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "tryFetch", "T", "block", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class MainActivity extends androidx.activity.ComponentActivity {
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.snapshots.SnapshotStateMap<java.lang.String, java.util.List<com.example.imagis.api.Movie>> contentMap = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.snapshots.SnapshotStateList<com.example.imagis.data.AnimeSeries> animeDirectory = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ANIMATION_GENRE = "16";
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job searchJob;
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void loadTmdbContent() {
    }
    
    private final void handleSearchAnime(java.lang.String query) {
    }
    
    private final void handleSearchContent(java.lang.String query) {
    }
    
    private final <T extends java.lang.Object>java.lang.Object tryFetch(kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super T>, ? extends java.lang.Object> block, kotlin.coroutines.Continuation<? super T> $completion) {
        return null;
    }
    
    private final void handleMovieClick(com.example.imagis.api.Movie movie) {
    }
    
    private final void handleAnimeSeriesClick(com.example.imagis.data.AnimeSeries series) {
    }
    
    private final void handleNavClick(com.example.imagis.ui.screens.NavCategory category) {
    }
    
    private final void initializeDatabase() {
    }
}