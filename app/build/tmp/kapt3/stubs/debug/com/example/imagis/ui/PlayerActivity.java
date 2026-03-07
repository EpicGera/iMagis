package com.example.imagis.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0007H\u0003J\u0012\u0010\u0013\u001a\u00020\u00112\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0015J\b\u0010\u0016\u001a\u00020\u0011H\u0014J\u0010\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u0007H\u0002J\b\u0010\u0019\u001a\u00020\u0011H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/example/imagis/ui/PlayerActivity;", "Landroidx/fragment/app/FragmentActivity;", "()V", "currentFallbackIndex", "", "fallbackUrls", "", "", "isVodPage", "", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "playerView", "Landroidx/media3/ui/PlayerView;", "totalAttempts", "videoUrl", "initializePlayer", "", "url", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onStop", "resolveVodStream", "pageUrl", "tryNextFallback", "Companion", "app_debug"})
public final class PlayerActivity extends androidx.fragment.app.FragmentActivity {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "PlayerActivity";
    @org.jetbrains.annotations.Nullable()
    private androidx.media3.exoplayer.ExoPlayer player;
    private androidx.media3.ui.PlayerView playerView;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String videoUrl;
    private boolean isVodPage = false;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<java.lang.String> fallbackUrls;
    private int currentFallbackIndex = -1;
    private int totalAttempts = 1;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.imagis.ui.PlayerActivity.Companion Companion = null;
    
    public PlayerActivity() {
        super();
    }
    
    @java.lang.Override()
    @androidx.annotation.OptIn(markerClass = {androidx.media3.common.util.UnstableApi.class})
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void resolveVodStream(java.lang.String pageUrl) {
    }
    
    @androidx.annotation.OptIn(markerClass = {androidx.media3.common.util.UnstableApi.class})
    private final void initializePlayer(java.lang.String url) {
    }
    
    /**
     * Attempts the next fallback URL. If all exhausted, shows error and finishes.
     */
    private final void tryNextFallback() {
    }
    
    @java.lang.Override()
    protected void onStop() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/example/imagis/ui/PlayerActivity$Companion;", "", "()V", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}