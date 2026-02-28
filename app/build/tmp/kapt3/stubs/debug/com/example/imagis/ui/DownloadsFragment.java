package com.example.imagis.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\fB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0006\u001a\u00020\u0007H\u0002J\u0012\u0010\b\u001a\u00020\u00072\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\b\u0010\u000b\u001a\u00020\u0007H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/example/imagis/ui/DownloadsFragment;", "Landroidx/leanback/app/BrowseSupportFragment;", "()V", "listRowAdapter", "Landroidx/leanback/widget/ArrayObjectAdapter;", "rowsAdapter", "loadRows", "", "onActivityCreated", "savedInstanceState", "Landroid/os/Bundle;", "updateDownloads", "DownloadCardPresenter", "app_debug"})
public final class DownloadsFragment extends androidx.leanback.app.BrowseSupportFragment {
    private androidx.leanback.widget.ArrayObjectAdapter rowsAdapter;
    private androidx.leanback.widget.ArrayObjectAdapter listRowAdapter;
    
    public DownloadsFragment() {
        super();
    }
    
    @java.lang.Override()
    public void onActivityCreated(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void loadRows() {
    }
    
    private final void updateDownloads() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010\f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\r"}, d2 = {"Lcom/example/imagis/ui/DownloadsFragment$DownloadCardPresenter;", "Landroidx/leanback/widget/Presenter;", "(Lcom/example/imagis/ui/DownloadsFragment;)V", "onBindViewHolder", "", "viewHolder", "Landroidx/leanback/widget/Presenter$ViewHolder;", "item", "", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "onUnbindViewHolder", "app_debug"})
    final class DownloadCardPresenter extends androidx.leanback.widget.Presenter {
        
        public DownloadCardPresenter() {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public androidx.leanback.widget.Presenter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        androidx.leanback.widget.Presenter.ViewHolder viewHolder, @org.jetbrains.annotations.NotNull()
        java.lang.Object item) {
        }
        
        @java.lang.Override()
        public void onUnbindViewHolder(@org.jetbrains.annotations.NotNull()
        androidx.leanback.widget.Presenter.ViewHolder viewHolder) {
        }
    }
}