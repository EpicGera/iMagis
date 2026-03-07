package com.example.imagis.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0004H\u0002J\u0010\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0004H\u0002J\u0012\u0010\u0014\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0014J\b\u0010\u0017\u001a\u00020\u0011H\u0014J\u001a\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0016J\b\u0010\u001e\u001a\u00020\u0011H\u0014J\b\u0010\u001f\u001a\u00020\u0011H\u0002J\u0018\u0010 \u001a\u00020\u00112\u0006\u0010!\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020\u0004H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lcom/example/imagis/ui/VideoPlayerActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "contentId", "", "contentTitle", "contentType", "currentUrl", "episodeLabel", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "playerView", "Landroidx/media3/ui/PlayerView;", "posterUrl", "resumePositionMs", "", "initializePlayer", "", "url", "launchExternalPlayer", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onKeyDown", "", "keyCode", "", "event", "Landroid/view/KeyEvent;", "onStop", "saveWatchHistory", "showExternalPlayerDialog", "title", "message", "app_debug"})
@androidx.annotation.OptIn(markerClass = {androidx.media3.common.util.UnstableApi.class})
public final class VideoPlayerActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.Nullable()
    private androidx.media3.exoplayer.ExoPlayer player;
    private androidx.media3.ui.PlayerView playerView;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentUrl;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String contentId;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String contentTitle;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String episodeLabel;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String posterUrl;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String contentType;
    private long resumePositionMs = 0L;
    
    public VideoPlayerActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initializePlayer(java.lang.String url) {
    }
    
    /**
     * Shows a dialog offering to open the current video in an external player.
     * Falls back to a direct intent if only one player is available.
     */
    private final void showExternalPlayerDialog(java.lang.String title, java.lang.String message) {
    }
    
    /**
     * Launches the video in an external player using ACTION_VIEW.
     * Works with VLC, MX Player, Just Player, mpv, etc.
     */
    private final void launchExternalPlayer(java.lang.String url) {
    }
    
    private final void saveWatchHistory() {
    }
    
    @java.lang.Override()
    protected void onStop() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    @java.lang.Override()
    public boolean onKeyDown(int keyCode, @org.jetbrains.annotations.Nullable()
    android.view.KeyEvent event) {
        return false;
    }
}