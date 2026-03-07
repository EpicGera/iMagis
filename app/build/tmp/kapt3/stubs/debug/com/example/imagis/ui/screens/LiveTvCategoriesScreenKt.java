package com.example.imagis.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u001e\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001ar\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\r2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\b0\r2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u000b2\u0014\b\u0002\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00132\u0014\b\u0002\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0013H\u0007\u00a8\u0006\u0015"}, d2 = {"CategoryCard", "", "playlist", "Lcom/example/imagis/data/PlaylistSource;", "onClick", "Lkotlin/Function0;", "ChannelCard", "channel", "Lcom/example/imagis/data/IptvChannel;", "LiveTvCategoriesScreen", "title", "", "playlists", "", "channels", "isLoading", "", "errorMessage", "onPlaylistClick", "Lkotlin/Function1;", "onChannelClick", "app_debug"})
public final class LiveTvCategoriesScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void LiveTvCategoriesScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.imagis.data.PlaylistSource> playlists, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.imagis.data.IptvChannel> channels, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.example.imagis.data.PlaylistSource, kotlin.Unit> onPlaylistClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.example.imagis.data.IptvChannel, kotlin.Unit> onChannelClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CategoryCard(com.example.imagis.data.PlaylistSource playlist, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ChannelCard(com.example.imagis.data.IptvChannel channel, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}