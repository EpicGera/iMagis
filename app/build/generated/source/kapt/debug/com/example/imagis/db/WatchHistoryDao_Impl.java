package com.example.imagis.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WatchHistoryDao_Impl implements WatchHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WatchHistoryEntity> __insertionAdapterOfWatchHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfTrimToMax;

  public WatchHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWatchHistoryEntity = new EntityInsertionAdapter<WatchHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `watch_history` (`id`,`title`,`episodeLabel`,`status`,`positionMs`,`durationMs`,`posterUrl`,`videoUrl`,`type`,`timestamp`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WatchHistoryEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getEpisodeLabel() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEpisodeLabel());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getStatus());
        }
        statement.bindLong(5, entity.getPositionMs());
        statement.bindLong(6, entity.getDurationMs());
        if (entity.getPosterUrl() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPosterUrl());
        }
        if (entity.getVideoUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getVideoUrl());
        }
        if (entity.getType() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getType());
        }
        statement.bindLong(10, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM watch_history WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfTrimToMax = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        DELETE FROM watch_history \n"
                + "        WHERE id NOT IN (\n"
                + "            SELECT id FROM watch_history ORDER BY timestamp DESC LIMIT 10\n"
                + "        )\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final WatchHistoryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWatchHistoryEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, id);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object trimToMax(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfTrimToMax.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfTrimToMax.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<WatchHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM watch_history ORDER BY timestamp DESC LIMIT 10";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WatchHistoryEntity>>() {
      @Override
      @NonNull
      public List<WatchHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfEpisodeLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeLabel");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfPositionMs = CursorUtil.getColumnIndexOrThrow(_cursor, "positionMs");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfPosterUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "posterUrl");
          final int _cursorIndexOfVideoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "videoUrl");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<WatchHistoryEntity> _result = new ArrayList<WatchHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WatchHistoryEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpEpisodeLabel;
            if (_cursor.isNull(_cursorIndexOfEpisodeLabel)) {
              _tmpEpisodeLabel = null;
            } else {
              _tmpEpisodeLabel = _cursor.getString(_cursorIndexOfEpisodeLabel);
            }
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final long _tmpPositionMs;
            _tmpPositionMs = _cursor.getLong(_cursorIndexOfPositionMs);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpPosterUrl;
            if (_cursor.isNull(_cursorIndexOfPosterUrl)) {
              _tmpPosterUrl = null;
            } else {
              _tmpPosterUrl = _cursor.getString(_cursorIndexOfPosterUrl);
            }
            final String _tmpVideoUrl;
            if (_cursor.isNull(_cursorIndexOfVideoUrl)) {
              _tmpVideoUrl = null;
            } else {
              _tmpVideoUrl = _cursor.getString(_cursorIndexOfVideoUrl);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new WatchHistoryEntity(_tmpId,_tmpTitle,_tmpEpisodeLabel,_tmpStatus,_tmpPositionMs,_tmpDurationMs,_tmpPosterUrl,_tmpVideoUrl,_tmpType,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id,
      final Continuation<? super WatchHistoryEntity> $completion) {
    final String _sql = "SELECT * FROM watch_history WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WatchHistoryEntity>() {
      @Override
      @Nullable
      public WatchHistoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfEpisodeLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeLabel");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfPositionMs = CursorUtil.getColumnIndexOrThrow(_cursor, "positionMs");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfPosterUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "posterUrl");
          final int _cursorIndexOfVideoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "videoUrl");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final WatchHistoryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpEpisodeLabel;
            if (_cursor.isNull(_cursorIndexOfEpisodeLabel)) {
              _tmpEpisodeLabel = null;
            } else {
              _tmpEpisodeLabel = _cursor.getString(_cursorIndexOfEpisodeLabel);
            }
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final long _tmpPositionMs;
            _tmpPositionMs = _cursor.getLong(_cursorIndexOfPositionMs);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpPosterUrl;
            if (_cursor.isNull(_cursorIndexOfPosterUrl)) {
              _tmpPosterUrl = null;
            } else {
              _tmpPosterUrl = _cursor.getString(_cursorIndexOfPosterUrl);
            }
            final String _tmpVideoUrl;
            if (_cursor.isNull(_cursorIndexOfVideoUrl)) {
              _tmpVideoUrl = null;
            } else {
              _tmpVideoUrl = _cursor.getString(_cursorIndexOfVideoUrl);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new WatchHistoryEntity(_tmpId,_tmpTitle,_tmpEpisodeLabel,_tmpStatus,_tmpPositionMs,_tmpDurationMs,_tmpPosterUrl,_tmpVideoUrl,_tmpType,_tmpTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
