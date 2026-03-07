package com.example.imagis.data;

/**
 * Data class representing an IPTV Channel.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u001d\b\u0086\b\u0018\u00002\u00020\u0001B[\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\f\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007H\u00c6\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\nH\u00c6\u0003J\u0010\u0010!\u001a\u0004\u0018\u00010\fH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0010J\u0010\u0010\"\u001a\u0004\u0018\u00010\fH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0010Jl\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\fH\u00c6\u0001\u00a2\u0006\u0002\u0010$J\u0013\u0010%\u001a\u00020\n2\b\u0010&\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\'\u001a\u00020\fH\u00d6\u0001J\t\u0010(\u001a\u00020\u0003H\u00d6\u0001R\u0015\u0010\r\u001a\u0004\u0018\u00010\f\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0016R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0015R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0015R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u0019\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015\u00a8\u0006)"}, d2 = {"Lcom/example/imagis/data/IptvChannel;", "", "name", "", "logoUrl", "streamUrl", "fallbackUrls", "", "group", "isSeries", "", "season", "", "episode", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;ZLjava/lang/Integer;Ljava/lang/Integer;)V", "getEpisode", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getFallbackUrls", "()Ljava/util/List;", "getGroup", "()Ljava/lang/String;", "()Z", "getLogoUrl", "getName", "getSeason", "getStreamUrl", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;ZLjava/lang/Integer;Ljava/lang/Integer;)Lcom/example/imagis/data/IptvChannel;", "equals", "other", "hashCode", "toString", "app_debug"})
public final class IptvChannel {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String logoUrl = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String streamUrl = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> fallbackUrls = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String group = null;
    private final boolean isSeries = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer season = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer episode = null;
    
    public IptvChannel(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String logoUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String streamUrl, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> fallbackUrls, @org.jetbrains.annotations.Nullable()
    java.lang.String group, boolean isSeries, @org.jetbrains.annotations.Nullable()
    java.lang.Integer season, @org.jetbrains.annotations.Nullable()
    java.lang.Integer episode) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLogoUrl() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStreamUrl() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getFallbackUrls() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getGroup() {
        return null;
    }
    
    public final boolean isSeries() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getSeason() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getEpisode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    public final boolean component6() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.imagis.data.IptvChannel copy(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String logoUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String streamUrl, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> fallbackUrls, @org.jetbrains.annotations.Nullable()
    java.lang.String group, boolean isSeries, @org.jetbrains.annotations.Nullable()
    java.lang.Integer season, @org.jetbrains.annotations.Nullable()
    java.lang.Integer episode) {
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