package com.example.imagis.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ChannelDao _channelDao;

  private volatile FavoritesDao _favoritesDao;

  private volatile WatchHistoryDao _watchHistoryDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `channels` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `lowercaseName` TEXT NOT NULL, `streamUrl` TEXT NOT NULL, `logoUrl` TEXT, `groupName` TEXT, `isSeries` INTEGER NOT NULL, `season` INTEGER, `episode` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `favorites` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `posterUrl` TEXT NOT NULL, `type` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `watch_history` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `episodeLabel` TEXT, `status` TEXT NOT NULL, `positionMs` INTEGER NOT NULL, `durationMs` INTEGER NOT NULL, `posterUrl` TEXT, `videoUrl` TEXT, `type` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'b93c44478c1ab4948b850403263662aa')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `channels`");
        db.execSQL("DROP TABLE IF EXISTS `favorites`");
        db.execSQL("DROP TABLE IF EXISTS `watch_history`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsChannels = new HashMap<String, TableInfo.Column>(9);
        _columnsChannels.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("lowercaseName", new TableInfo.Column("lowercaseName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("streamUrl", new TableInfo.Column("streamUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("logoUrl", new TableInfo.Column("logoUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("groupName", new TableInfo.Column("groupName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("isSeries", new TableInfo.Column("isSeries", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("season", new TableInfo.Column("season", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("episode", new TableInfo.Column("episode", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChannels = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChannels = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChannels = new TableInfo("channels", _columnsChannels, _foreignKeysChannels, _indicesChannels);
        final TableInfo _existingChannels = TableInfo.read(db, "channels");
        if (!_infoChannels.equals(_existingChannels)) {
          return new RoomOpenHelper.ValidationResult(false, "channels(com.example.imagis.db.ChannelEntity).\n"
                  + " Expected:\n" + _infoChannels + "\n"
                  + " Found:\n" + _existingChannels);
        }
        final HashMap<String, TableInfo.Column> _columnsFavorites = new HashMap<String, TableInfo.Column>(5);
        _columnsFavorites.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("posterUrl", new TableInfo.Column("posterUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFavorites = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFavorites = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFavorites = new TableInfo("favorites", _columnsFavorites, _foreignKeysFavorites, _indicesFavorites);
        final TableInfo _existingFavorites = TableInfo.read(db, "favorites");
        if (!_infoFavorites.equals(_existingFavorites)) {
          return new RoomOpenHelper.ValidationResult(false, "favorites(com.example.imagis.db.FavoritesEntity).\n"
                  + " Expected:\n" + _infoFavorites + "\n"
                  + " Found:\n" + _existingFavorites);
        }
        final HashMap<String, TableInfo.Column> _columnsWatchHistory = new HashMap<String, TableInfo.Column>(10);
        _columnsWatchHistory.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("episodeLabel", new TableInfo.Column("episodeLabel", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("positionMs", new TableInfo.Column("positionMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("durationMs", new TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("posterUrl", new TableInfo.Column("posterUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("videoUrl", new TableInfo.Column("videoUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWatchHistory.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWatchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWatchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWatchHistory = new TableInfo("watch_history", _columnsWatchHistory, _foreignKeysWatchHistory, _indicesWatchHistory);
        final TableInfo _existingWatchHistory = TableInfo.read(db, "watch_history");
        if (!_infoWatchHistory.equals(_existingWatchHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "watch_history(com.example.imagis.db.WatchHistoryEntity).\n"
                  + " Expected:\n" + _infoWatchHistory + "\n"
                  + " Found:\n" + _existingWatchHistory);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "b93c44478c1ab4948b850403263662aa", "87ec8ebe43831d124879f750e0bf44e7");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "channels","favorites","watch_history");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `channels`");
      _db.execSQL("DELETE FROM `favorites`");
      _db.execSQL("DELETE FROM `watch_history`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ChannelDao.class, ChannelDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FavoritesDao.class, FavoritesDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WatchHistoryDao.class, WatchHistoryDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ChannelDao channelDao() {
    if (_channelDao != null) {
      return _channelDao;
    } else {
      synchronized(this) {
        if(_channelDao == null) {
          _channelDao = new ChannelDao_Impl(this);
        }
        return _channelDao;
      }
    }
  }

  @Override
  public FavoritesDao favoritesDao() {
    if (_favoritesDao != null) {
      return _favoritesDao;
    } else {
      synchronized(this) {
        if(_favoritesDao == null) {
          _favoritesDao = new FavoritesDao_Impl(this);
        }
        return _favoritesDao;
      }
    }
  }

  @Override
  public WatchHistoryDao watchHistoryDao() {
    if (_watchHistoryDao != null) {
      return _watchHistoryDao;
    } else {
      synchronized(this) {
        if(_watchHistoryDao == null) {
          _watchHistoryDao = new WatchHistoryDao_Impl(this);
        }
        return _watchHistoryDao;
      }
    }
  }
}
