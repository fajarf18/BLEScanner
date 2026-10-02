package com.fajar.neartrace.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DeviceDao_Impl implements DeviceDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  private final EntityUpsertionAdapter<DeviceEntity> __upsertionAdapterOfDeviceEntity;

  public DeviceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM detected_devices";
        return _query;
      }
    };
    this.__upsertionAdapterOfDeviceEntity = new EntityUpsertionAdapter<DeviceEntity>(new EntityInsertionAdapter<DeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `detected_devices` (`address`,`name`,`lastRssi`,`strongestRssi`,`firstSeen`,`lastSeen`,`seenCount`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DeviceEntity entity) {
        statement.bindString(1, entity.getAddress());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindLong(3, entity.getLastRssi());
        statement.bindLong(4, entity.getStrongestRssi());
        statement.bindLong(5, entity.getFirstSeen());
        statement.bindLong(6, entity.getLastSeen());
        statement.bindLong(7, entity.getSeenCount());
      }
    }, new EntityDeletionOrUpdateAdapter<DeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `detected_devices` SET `address` = ?,`name` = ?,`lastRssi` = ?,`strongestRssi` = ?,`firstSeen` = ?,`lastSeen` = ?,`seenCount` = ? WHERE `address` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DeviceEntity entity) {
        statement.bindString(1, entity.getAddress());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindLong(3, entity.getLastRssi());
        statement.bindLong(4, entity.getStrongestRssi());
        statement.bindLong(5, entity.getFirstSeen());
        statement.bindLong(6, entity.getLastSeen());
        statement.bindLong(7, entity.getSeenCount());
        statement.bindString(8, entity.getAddress());
      }
    });
  }

  @Override
  public Object clear(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClear.acquire();
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
          __preparedStmtOfClear.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final DeviceEntity device, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfDeviceEntity.upsert(device);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DeviceEntity>> observeAll() {
    final String _sql = "SELECT * FROM detected_devices ORDER BY lastSeen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"detected_devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLastRssi = CursorUtil.getColumnIndexOrThrow(_cursor, "lastRssi");
          final int _cursorIndexOfStrongestRssi = CursorUtil.getColumnIndexOrThrow(_cursor, "strongestRssi");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "firstSeen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfSeenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "seenCount");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final int _tmpLastRssi;
            _tmpLastRssi = _cursor.getInt(_cursorIndexOfLastRssi);
            final int _tmpStrongestRssi;
            _tmpStrongestRssi = _cursor.getInt(_cursorIndexOfStrongestRssi);
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final int _tmpSeenCount;
            _tmpSeenCount = _cursor.getInt(_cursorIndexOfSeenCount);
            _item = new DeviceEntity(_tmpAddress,_tmpName,_tmpLastRssi,_tmpStrongestRssi,_tmpFirstSeen,_tmpLastSeen,_tmpSeenCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object find(final String address, final Continuation<? super DeviceEntity> $completion) {
    final String _sql = "SELECT * FROM detected_devices WHERE address = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, address);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DeviceEntity>() {
      @Override
      @Nullable
      public DeviceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLastRssi = CursorUtil.getColumnIndexOrThrow(_cursor, "lastRssi");
          final int _cursorIndexOfStrongestRssi = CursorUtil.getColumnIndexOrThrow(_cursor, "strongestRssi");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "firstSeen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfSeenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "seenCount");
          final DeviceEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final int _tmpLastRssi;
            _tmpLastRssi = _cursor.getInt(_cursorIndexOfLastRssi);
            final int _tmpStrongestRssi;
            _tmpStrongestRssi = _cursor.getInt(_cursorIndexOfStrongestRssi);
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final int _tmpSeenCount;
            _tmpSeenCount = _cursor.getInt(_cursorIndexOfSeenCount);
            _result = new DeviceEntity(_tmpAddress,_tmpName,_tmpLastRssi,_tmpStrongestRssi,_tmpFirstSeen,_tmpLastSeen,_tmpSeenCount);
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
