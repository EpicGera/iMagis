package com.example.imagis.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u0005H\u0002J\b\u0010\u0010\u001a\u00020\rH\u0002J\u0012\u0010\u0011\u001a\u00020\r2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0016J\b\u0010\u0014\u001a\u00020\rH\u0016J\"\u0010\u0015\u001a\u00020\r2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u000e\u0010\u0018\u001a\n\u0018\u00010\u0019j\u0004\u0018\u0001`\u001aH\u0016J\u0012\u0010\u001b\u001a\u00020\r2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0016J\u001c\u0010\u001c\u001a\u00020\r2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0016J\u0012\u0010\u001f\u001a\u00020\r2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0016J\u0012\u0010 \u001a\u00020\r2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0016J\b\u0010!\u001a\u00020\rH\u0016J\b\u0010\"\u001a\u00020\rH\u0002J\u0018\u0010#\u001a\u00020\r2\u0006\u0010$\u001a\u00020\u000b2\u0006\u0010%\u001a\u00020\u000bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/example/imagis/ui/TvSeasonsFragment;", "Landroidx/leanback/app/BrowseSupportFragment;", "Lcom/github/se_bastiaan/torrentstream/listeners/TorrentListener;", "()V", "rowsAdapter", "Landroidx/leanback/widget/ArrayObjectAdapter;", "torrentStream", "Lcom/github/se_bastiaan/torrentstream/TorrentStream;", "tvShowId", "", "tvShowName", "", "fetchEpisodesForSeason", "", "seasonNumber", "adapter", "loadSeasons", "onActivityCreated", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onStreamError", "torrent", "Lcom/github/se_bastiaan/torrentstream/Torrent;", "e", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onStreamPrepared", "onStreamProgress", "status", "Lcom/github/se_bastiaan/torrentstream/StreamStatus;", "onStreamReady", "onStreamStarted", "onStreamStopped", "setupEventListeners", "startTorrentStream", "magnetUrl", "title", "app_debug"})
public final class TvSeasonsFragment extends androidx.leanback.app.BrowseSupportFragment implements com.github.se_bastiaan.torrentstream.listeners.TorrentListener {
    private androidx.leanback.widget.ArrayObjectAdapter rowsAdapter;
    private int tvShowId = 0;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String tvShowName = "";
    @org.jetbrains.annotations.Nullable()
    private com.github.se_bastiaan.torrentstream.TorrentStream torrentStream;
    
    public TvSeasonsFragment() {
        super();
    }
    
    @java.lang.Override()
    public void onActivityCreated(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void loadSeasons() {
    }
    
    private final void fetchEpisodesForSeason(int seasonNumber, androidx.leanback.widget.ArrayObjectAdapter adapter) {
    }
    
    private final void setupEventListeners() {
    }
    
    private final void startTorrentStream(java.lang.String magnetUrl, java.lang.String title) {
    }
    
    @java.lang.Override()
    public void onStreamPrepared(@org.jetbrains.annotations.Nullable()
    com.github.se_bastiaan.torrentstream.Torrent torrent) {
    }
    
    @java.lang.Override()
    public void onStreamStarted(@org.jetbrains.annotations.Nullable()
    com.github.se_bastiaan.torrentstream.Torrent torrent) {
    }
    
    @java.lang.Override()
    public void onStreamError(@org.jetbrains.annotations.Nullable()
    com.github.se_bastiaan.torrentstream.Torrent torrent, @org.jetbrains.annotations.Nullable()
    java.lang.Exception e) {
    }
    
    @java.lang.Override()
    public void onStreamReady(@org.jetbrains.annotations.Nullable()
    com.github.se_bastiaan.torrentstream.Torrent torrent) {
    }
    
    @java.lang.Override()
    public void onStreamProgress(@org.jetbrains.annotations.Nullable()
    com.github.se_bastiaan.torrentstream.Torrent torrent, @org.jetbrains.annotations.Nullable()
    com.github.se_bastiaan.torrentstream.StreamStatus status) {
    }
    
    @java.lang.Override()
    public void onStreamStopped() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
}