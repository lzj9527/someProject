package android.extend.cache;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.extend.BasicConfig;
import android.extend.cache.FileCacheManager.CacheItem;
import android.extend.util.LogUtil;
import android.text.TextUtils;

class FileCacheDB
{
	public static final String TAG = "FileCacheDB";

	public static final String NAME_KEY = "key";
	public static final String NAME_PATH = "path";
	// public static final String KEY_CACHE_TIME = "cache_time";
	public static final String NAME_EXPIRES_TIME = "expires_time";
	public static final String NAME_MTIME = "file_mtime";
	public static final String NAME_EXTENDS = "extends";

	private static final String TABLE_NAME = "cache";
	private static final String DB_NAME = "cache.db";
	private static final int VERSION = BasicConfig.CacheDBVersion;

	private static CacheDBOpenHelper mOpenHelper = null;

	private static SQLiteOpenHelper getSQLiteOpenHelper(Context context)
	{
		if (mOpenHelper == null)
		{
			mOpenHelper = new CacheDBOpenHelper(context);
		}
		return mOpenHelper;
	}

	private static SQLiteDatabase getWritableDatabase(Context context)
	{
		return getSQLiteOpenHelper(context).getWritableDatabase();
	}

	private static SQLiteDatabase getReadableDatabase(Context context)
	{
		return getSQLiteOpenHelper(context).getReadableDatabase();
	}

	private static long insert(Context context, ContentValues values)
	{
		return getWritableDatabase(context).insert(TABLE_NAME, null, values);
	}

	private static int update(Context context, ContentValues values, String whereClause, String[] whereArgs)
	{
		return getWritableDatabase(context).update(TABLE_NAME, values, whereClause, whereArgs);
	}

	private static int delete(Context context, String whereClause, String[] whereArgs)
	{
		return getWritableDatabase(context).delete(TABLE_NAME, whereClause, whereArgs);
	}

	private static Cursor query(Context context, String selection, String[] selectionArgs, String orderBy)
	{
		return getReadableDatabase(context).query(TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
	}

	// private static Cursor rawQuery(Context context, String sql, String[]
	// selectionArgs) {
	// return getReadableDatabase(context).rawQuery(sql, selectionArgs);
	// }

	private static String getSelectionByKey(String key)
	{
		return NAME_KEY + "=?";
	}

	public static synchronized boolean hasRecord(Context context, String key)
	{
		if (TextUtils.isEmpty(key))
		{
			return false;
		}
		Cursor cursor = null;
		try
		{
			cursor = query(context, getSelectionByKey(key), new String[] { key }, null);
			if (cursor.getCount() > 0)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
		return false;
	}

	public static void put(Context context, CacheItem item)
	{
		if (hasRecord(context, item.key))
			update(context, item);
		else
			insert(context, item);
	}

	public static void put(Context context, String key, String path, long expires_time, long file_mtime)
	{
		if (hasRecord(context, key))
			update(context, key, path, expires_time, file_mtime);
		else
			insert(context, key, path, expires_time, file_mtime);
	}

	public static synchronized long insert(Context context, CacheItem item)
	{
		if (item == null)
		{
			return -1;
		}
		return insert(context, item.key, item.path, item.expires_time, item.file_mtime);
	}

	public static synchronized long insert(Context context, String key, String path, long expires_time, long file_mtime)
	{
		if (TextUtils.isEmpty(key) || TextUtils.isEmpty(path))
		{
			return -1;
		}
		ContentValues values = new ContentValues();
		values.put(NAME_KEY, key);
		values.put(NAME_PATH, path);
		// values.put(KEY_CACHE_TIME, System.currentTimeMillis());
		values.put(NAME_EXPIRES_TIME, expires_time);
		values.put(NAME_MTIME, file_mtime);
		LogUtil.logToFile(FileCacheManager.LogFile, TAG, "insert key=" + key + "; path=" + path + "; expires_time="
				+ "; file_mtime=" + file_mtime);
		return insert(context, values);
	}

	public static synchronized int update(Context context, CacheItem item)
	{
		if (item == null)
		{
			return -1;
		}
		return update(context, item.key, item.path, item.expires_time, item.file_mtime);
	}

	public static synchronized int update(Context context, String key, String path, long expires_time, long file_mtime)
	{
		if (TextUtils.isEmpty(key) || TextUtils.isEmpty(path))
		{
			return -1;
		}
		ContentValues values = new ContentValues();
		values.put(NAME_KEY, key);
		values.put(NAME_PATH, path);
		// values.put(KEY_CACHE_TIME, System.currentTimeMillis());
		values.put(NAME_EXPIRES_TIME, expires_time);
		values.put(NAME_MTIME, file_mtime);
		LogUtil.logToFile(FileCacheManager.LogFile, TAG, "update key=" + key + "; path=" + path + "; expires_time="
				+ "; file_mtime=" + file_mtime);
		return update(context, values, getSelectionByKey(key), new String[] { key });
	}

	public static synchronized int updateExpiresTime(Context context, String key, long expires_time)
	{
		if (TextUtils.isEmpty(key))
		{
			return -1;
		}
		ContentValues values = new ContentValues();
		values.put(NAME_KEY, key);
		values.put(NAME_EXPIRES_TIME, expires_time);
		LogUtil.logToFile(FileCacheManager.LogFile, TAG, "updateExpiresTime key=" + key + "; expires_time="
				+ expires_time);
		return update(context, values, getSelectionByKey(key), new String[] { key });
	}

	public static synchronized int updateFileMTime(Context context, String key, long file_mtime)
	{
		if (TextUtils.isEmpty(key))
		{
			return -1;
		}
		ContentValues values = new ContentValues();
		values.put(NAME_KEY, key);
		values.put(NAME_MTIME, file_mtime);
		LogUtil.logToFile(FileCacheManager.LogFile, TAG, "updateFileMTime key=" + key + "; file_mtime=" + file_mtime);
		return update(context, values, getSelectionByKey(key), new String[] { key });
	}

	private static CacheItem getCacheItem(Cursor cursor, String key)
	{
		CacheItem item = new CacheItem();
		if (TextUtils.isEmpty(key))
		{
			item.key = cursor.getString(cursor.getColumnIndex(NAME_KEY));
		}
		else
		{
			item.key = key;
		}
		item.path = cursor.getString(cursor.getColumnIndex(NAME_PATH));
		item.expires_time = cursor.getLong(cursor.getColumnIndex(NAME_EXPIRES_TIME));
		item.file_mtime = cursor.getInt(cursor.getColumnIndex(NAME_MTIME));
		return item;
	}

	public static synchronized CacheItem query(Context context, String key)
	{
		if (TextUtils.isEmpty(key))
		{
			return null;
		}
		Cursor cursor = null;
		try
		{
			cursor = query(context, getSelectionByKey(key), new String[] { key }, null);
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				CacheItem item = getCacheItem(cursor, key);
				LogUtil.logToFile(FileCacheManager.LogFile, TAG, "query key=" + key + "; item=" + item.toString());
				return item;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
		return null;
	}

	public static synchronized List<CacheItem> queryAll(Context context)
	{
		Cursor cursor = null;
		try
		{
			cursor = query(context, null, null, null);
			if (cursor.getCount() > 0)
			{
				List<CacheItem> list = new ArrayList<CacheItem>();
				cursor.moveToFirst();
				do
				{
					CacheItem item = getCacheItem(cursor, null);
					list.add(item);
				}
				while (cursor.moveToNext());
				return list;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
		return null;
	}

	public static synchronized int delete(Context context, String key)
	{
		if (TextUtils.isEmpty(key))
		{
			return -1;
		}
		int result = delete(context, getSelectionByKey(key), new String[] { key });
		LogUtil.logToFile(FileCacheManager.LogFile, TAG, "delete key=" + key + "; result=" + result);
		return result;
	}

	public static synchronized int deleteAll(Context context)
	{
		int result = delete(context, null, null);
		LogUtil.logToFile(FileCacheManager.LogFile, TAG, "deleteAll result=" + result);
		return result;
	}

	private static final String getSQLCreateContent()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("(_id INTEGER PRIMARY KEY,");
		sb.append(NAME_KEY).append(" TEXT").append(',');
		sb.append(NAME_PATH).append(" TEXT").append(',');
		// sb.append(KEY_CACHE_TIME).append(" LONG").append(',');
		sb.append(NAME_EXPIRES_TIME).append(" LONG").append(',');
		sb.append(NAME_MTIME).append(" LONG").append(',');
		sb.append(NAME_EXTENDS).append(" BLOB");
		sb.append(')');
		return sb.toString();
	}

	private static class CacheDBOpenHelper extends SQLiteOpenHelper
	{
		public CacheDBOpenHelper(Context context)
		{
			super(context, DB_NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " " + getSQLCreateContent() + ";");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if (oldVersion != newVersion)
			{
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
				onCreate(db);
			}
		}
	}
}
