package com.example.imagis.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\u000bB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0002J\u0012\u0010\u0007\u001a\u00020\u00062\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016J\b\u0010\n\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/example/imagis/ui/AnimeFragment;", "Landroidx/leanback/app/VerticalGridSupportFragment;", "()V", "mAdapter", "Landroidx/leanback/widget/ArrayObjectAdapter;", "loadData", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupFragment", "AnimeCardPresenter", "app_debug"})
public final class AnimeFragment extends androidx.leanback.app.VerticalGridSupportFragment {
    private androidx.leanback.widget.ArrayObjectAdapter mAdapter;
    
    public AnimeFragment() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupFragment() {
    }
    
    private final void loadData() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010\f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\r"}, d2 = {"Lcom/example/imagis/ui/AnimeFragment$AnimeCardPresenter;", "Landroidx/leanback/widget/Presenter;", "(Lcom/example/imagis/ui/AnimeFragment;)V", "onBindViewHolder", "", "viewHolder", "Landroidx/leanback/widget/Presenter$ViewHolder;", "item", "", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "onUnbindViewHolder", "app_debug"})
    final class AnimeCardPresenter extends androidx.leanback.widget.Presenter {
        
        public AnimeCardPresenter() {
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