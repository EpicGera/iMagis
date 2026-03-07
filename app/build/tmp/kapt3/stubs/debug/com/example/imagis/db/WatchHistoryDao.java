package com.example.imagis.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u000b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\f\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u0010"}, d2 = {"Lcom/example/imagis/db/WatchHistoryDao;", "", "deleteById", "", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "", "Lcom/example/imagis/db/WatchHistoryEntity;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "trimToMax", "upsert", "entry", "(Lcom/example/imagis/db/WatchHistoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface WatchHistoryDao {
    
    /**
     * Returns the most recent 10 entries, ordered newest-first.
     */
    @androidx.room.Query(value = "SELECT * FROM watch_history ORDER BY timestamp DESC LIMIT 10")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.imagis.db.WatchHistoryEntity>> $completion);
    
    /**
     * Single lookup by ID (TMDB ID / slug / URL).
     */
    @androidx.room.Query(value = "SELECT * FROM watch_history WHERE id = :id LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.imagis.db.WatchHistoryEntity> $completion);
    
    /**
     * Insert or replace an entry (upsert pattern).
     */
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object upsert(@org.jetbrains.annotations.NotNull()
    com.example.imagis.db.WatchHistoryEntity entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Remove a specific entry.
     */
    @androidx.room.Query(value = "DELETE FROM watch_history WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Enforce the 10-title cap.
     * Deletes all rows except the 10 most recent by timestamp.
     */
    @androidx.room.Query(value = "\n        DELETE FROM watch_history \n        WHERE id NOT IN (\n            SELECT id FROM watch_history ORDER BY timestamp DESC LIMIT 10\n        )\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object trimToMax(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}