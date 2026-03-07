package com.example.imagis.ui;

/**
 * Anime Series screen — Displays the poster and the list of episodes for an Anime Series.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0005H\u0002J\b\u0010\u0016\u001a\u00020\u0014H\u0002J\u0012\u0010\u0017\u001a\u00020\u00142\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0014R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R+\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u00078B@BX\u0082\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\f\u0010\r\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/example/imagis/ui/AnimeSeriesActivity;", "Landroidx/activity/ComponentActivity;", "()V", "episodes", "Landroidx/compose/runtime/snapshots/SnapshotStateList;", "Lcom/example/imagis/data/AnimeEpisode;", "<set-?>", "", "isLoading", "()Z", "setLoading", "(Z)V", "isLoading$delegate", "Landroidx/compose/runtime/MutableState;", "seriesImageUrl", "", "seriesTitle", "seriesType", "seriesUrl", "handleEpisodeClick", "", "episode", "loadEpisodes", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "app_debug"})
public final class AnimeSeriesActivity extends androidx.activity.ComponentActivity {
    @org.jetbrains.annotations.NotNull()
    private java.lang.String seriesUrl = "";
    @org.jetbrains.annotations.NotNull()
    private java.lang.String seriesTitle = "";
    @org.jetbrains.annotations.NotNull()
    private java.lang.String seriesType = "";
    @org.jetbrains.annotations.NotNull()
    private java.lang.String seriesImageUrl = "";
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.snapshots.SnapshotStateList<com.example.imagis.data.AnimeEpisode> episodes = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState isLoading$delegate = null;
    
    public AnimeSeriesActivity() {
        super();
    }
    
    private final boolean isLoading() {
        return false;
    }
    
    private final void setLoading(boolean p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void loadEpisodes() {
    }
    
    private final void handleEpisodeClick(com.example.imagis.data.AnimeEpisode episode) {
    }
}