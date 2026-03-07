package com.example.imagis.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0014\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u000f2\u000e\u0010\u0016\u001a\n\u0018\u00010\u0017j\u0004\u0018\u0001`\u0018H\u0016J\u0012\u0010\u0019\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u000fH\u0016J\u001c\u0010\u001a\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u000f2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0016J\u0012\u0010\u001d\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u000fH\u0016J\u0012\u0010\u001e\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u000fH\u0016J\b\u0010\u001f\u001a\u00020\u0011H\u0016J\u001e\u0010 \u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010!\u001a\u00020\u00072\u0006\u0010\"\u001a\u00020\u0007J\u000e\u0010#\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013R\u0016\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0019\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/example/imagis/data/TorrentRepository;", "Lcom/github/se_bastiaan/torrentstream/listeners/TorrentListener;", "()V", "_downloadState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/imagis/data/DownloadState;", "currentTitle", "", "downloadState", "Lkotlinx/coroutines/flow/StateFlow;", "getDownloadState", "()Lkotlinx/coroutines/flow/StateFlow;", "torrentStream", "Lcom/github/se_bastiaan/torrentstream/TorrentStream;", "currentTorrent", "Lcom/github/se_bastiaan/torrentstream/Torrent;", "initialize", "", "context", "Landroid/content/Context;", "onStreamError", "torrent", "e", "Ljava/lang/Exception;", "Lkotlin/Exception;", "onStreamPrepared", "onStreamProgress", "status", "Lcom/github/se_bastiaan/torrentstream/StreamStatus;", "onStreamReady", "onStreamStarted", "onStreamStopped", "startStream", "magnetUrl", "title", "stopStream", "app_debug"})
public final class TorrentRepository implements com.github.se_bastiaan.torrentstream.listeners.TorrentListener {
    @org.jetbrains.annotations.Nullable()
    private static com.github.se_bastiaan.torrentstream.TorrentStream torrentStream;
    @org.jetbrains.annotations.NotNull()
    private static java.lang.String currentTitle = "";
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<com.example.imagis.data.DownloadState> _downloadState = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<com.example.imagis.data.DownloadState> downloadState = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.imagis.data.TorrentRepository INSTANCE = null;
    
    private TorrentRepository() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.imagis.data.DownloadState> getDownloadState() {
        return null;
    }
    
    public final void initialize(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void startStream(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String magnetUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String title) {
    }
    
    public final void stopStream(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.github.se_bastiaan.torrentstream.Torrent currentTorrent() {
        return null;
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
}