package com.example.imagis.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0002J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\rR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/example/imagis/utils/TvProviderEngine;", "", "()V", "TAG", "", "TV_INPUT_SERVICE_CLASS", "getChannelId", "", "context", "Landroid/content/Context;", "getInputId", "setupCustomChannel", "", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class TvProviderEngine {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "TvProviderEngine";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TV_INPUT_SERVICE_CLASS = "com.example.imagis.service.ImagisTvInputService";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.imagis.utils.TvProviderEngine INSTANCE = null;
    
    private TvProviderEngine() {
        super();
    }
    
    /**
     * Generates the Input ID specific to our app. This is the key to bypassing the SecurityException.
     */
    private final java.lang.String getInputId(android.content.Context context) {
        return null;
    }
    
    /**
     * Safely queries the Android TV database ONLY for channels owned by our app.
     */
    public final long getChannelId(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0L;
    }
    
    /**
     * Publishes a master channel for iMagis to the Android TV Home Screen.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setupCustomChannel(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}