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
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class ChannelDao_Impl implements ChannelDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChannelEntity> __insertionAdapterOfChannelEntity;

  private final SharedSQLiteStatement __preparedStmtOfNukeTable;

  public ChannelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChannelEntity = new EntityInsertionAdapter<ChannelEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `channels` (`id`,`name`,`lowercaseName`,`streamUrl`,`logoUrl`,`groupName`,`isSeries`,`season`,`episode`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChannelEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getLowercaseName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getLowercaseName());
        }
        if (entity.getStreamUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getStreamUrl());
        }
        if (entity.getLogoUrl() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLogoUrl());
        }
        if (entity.getGroupName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getGroupName());
        }
        final int _tmp = entity.isSeries() ? 1 : 0;
        statement.bindLong(7, _tmp);
        if (entity.getSeason() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getSeason());
        }
        if (entity.getEpisode() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getEpisode());
        }
      }
    };
    this.__preparedStmtOfNukeTable = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM channels";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<ChannelEntity> channels,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfChannelEntity.insert(channels);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object nukeTable(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfNukeTable.acquire();
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
          __preparedStmtOfNukeTable.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object findStreamByTitle(final String searchQuery,
      final Continuation<? super String> $completion) {
    final String _sql = "SELECT streamUrl FROM channels WHERE lowercaseName LIKE '%' || ? || '%' LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (searchQuery == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, searchQuery);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
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

  @Override
  public Object findStreamCandidates(final String searchQuery,
      final Continuation<? super List<StreamCandidate>> $completion) {
    final String _sql = "SELECT lowercaseName, streamUrl FROM channels WHERE lowercaseName LIKE '%' || ? || '%' LIMIT 20";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (searchQuery == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, searchQuery);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<StreamCandidate>>() {
      @Override
      @NonNull
      public List<StreamCandidate> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLowercaseName = 0;
          final int _cursorIndexOfStreamUrl = 1;
          final List<StreamCandidate> _result = new ArrayList<StreamCandidate>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StreamCandidate _item;
            final String _tmpLowercaseName;
            if (_cursor.isNull(_cursorIndexOfLowercaseName)) {
              _tmpLowercaseName = null;
            } else {
              _tmpLowercaseName = _cursor.getString(_cursorIndexOfLowercaseName);
            }
            final String _tmpStreamUrl;
            if (_cursor.isNull(_cursorIndexOfStreamUrl)) {
              _tmpStreamUrl = null;
            } else {
              _tmpStreamUrl = _cursor.getString(_cursorIndexOfStreamUrl);
            }
            _item = new StreamCandidate(_tmpLowercaseName,_tmpStreamUrl);
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
  public Object findEpisodeStream(final String seriesName, final int targetSeason,
      final int targetEpisode, final Continuation<? super String> $completion) {
    final String _sql = "SELECT streamUrl FROM channels WHERE isSeries = 1 AND lowercaseName LIKE '%' || ? || '%' AND season = ? AND episode = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (seriesName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, seriesName);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, targetSeason);
    _argIndex = 3;
    _statement.bindLong(_argIndex, targetEpisode);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
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

  @Override
  public Object getChannelCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM channels";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
