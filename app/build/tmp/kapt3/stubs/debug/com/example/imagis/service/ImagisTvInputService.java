package com.example.imagis.service;

/**
 * A stub TvInputService to allow inserting channels into the Android TV Provider.
 * This satisfies the TvProvider database requirement that the Input ID belongs to 
 * a registered TvInputService in the app's manifest, avoiding SecurityExceptions.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"}, d2 = {"Lcom/example/imagis/service/ImagisTvInputService;", "Landroid/media/tv/TvInputService;", "()V", "onCreateSession", "Landroid/media/tv/TvInputService$Session;", "inputId", "", "app_debug"})
public final class ImagisTvInputService extends android.media.tv.TvInputService {
    
    public ImagisTvInputService() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.media.tv.TvInputService.Session onCreateSession(@org.jetbrains.annotations.NotNull()
    java.lang.String inputId) {
        return null;
    }
}