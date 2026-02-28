package com.example.imagis.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u0004\u0018\u00010\u00032\u0006\u0010\n\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\n\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\u000f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u001c\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\rH\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u0016\u001a\u00020\u0012H\u00a7@\u00a2\u0006\u0002\u0010\u0010\u00a8\u0006\u0017"}, d2 = {"Lcom/example/imagis/db/ChannelDao;", "", "findEpisodeStream", "", "seriesName", "targetSeason", "", "targetEpisode", "(Ljava/lang/String;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "findStreamByTitle", "searchQuery", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "findStreamCandidates", "", "Lcom/example/imagis/db/StreamCandidate;", "getChannelCount", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertAll", "", "channels", "Lcom/example/imagis/db/ChannelEntity;", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "nukeTable", "app_debug"})
@androidx.room.Dao()
public abstract interface ChannelDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.imagis.db.ChannelEntity> channels, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT streamUrl FROM channels WHERE lowercaseName LIKE \'%\' || :searchQuery || \'%\' LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findStreamByTitle(@org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @androidx.room.Query(value = "SELECT lowercaseName, streamUrl FROM channels WHERE lowercaseName LIKE \'%\' || :searchQuery || \'%\' LIMIT 20")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findStreamCandidates(@org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.imagis.db.StreamCandidate>> $completion);
    
    @androidx.room.Query(value = "SELECT streamUrl FROM channels WHERE isSeries = 1 AND lowercaseName LIKE \'%\' || :seriesName || \'%\' AND season = :targetSeason AND episode = :targetEpisode LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findEpisodeStream(@org.jetbrains.annotations.NotNull()
    java.lang.String seriesName, int targetSeason, int targetEpisode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM channels")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getChannelCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "DELETE FROM channels")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object nukeTable(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}