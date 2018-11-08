package android.extend.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.extend.util.LogUtil;

public abstract class BaseDBHelper
{
	public final String TAG = getClass().getSimpleName();

	private MyDBOpenHelper mOpenHelper = null;

	public SQLiteOpenHelper getSQLiteOpenHelper(Context context)
	{
		if (mOpenHelper == null)
		{
			mOpenHelper = new MyDBOpenHelper(context);
		}
		return mOpenHelper;
	}

	public SQLiteDatabase getWritableDatabase(Context context)
	{
		return getSQLiteOpenHelper(context).getWritableDatabase();
	}

	public SQLiteDatabase getReadableDatabase(Context context)
	{
		return getSQLiteOpenHelper(context).getReadableDatabase();
	}

	protected long insert(Context context, ContentValues values)
	{
		return getWritableDatabase(context).insert(getTableName(), null, values);
	}

	protected int update(Context context, ContentValues values, String whereClause, String[] whereArgs)
	{
		return getWritableDatabase(context).update(getTableName(), values, whereClause, whereArgs);
	}

	protected int delete(Context context, String whereClause, String[] whereArgs)
	{
		return getWritableDatabase(context).delete(getTableName(), whereClause, whereArgs);
	}

	protected Cursor query(Context context, String selection, String[] selectionArgs, String orderBy)
	{
		return getReadableDatabase(context).query(getTableName(), null, selection, selectionArgs, null, null, orderBy);
	}

	protected abstract String getTableName();

	protected abstract int getTableVersion();

	protected abstract String getSQLCreateContent();

	private String getDBName(String tableName)
	{
		if (!tableName.endsWith(".db"))
		{
			return tableName + ".db";
		}
		return tableName;
	}

	private class MyDBOpenHelper extends SQLiteOpenHelper
	{
		public MyDBOpenHelper(Context context)
		{
			super(context, getDBName(getTableName()), null, getTableVersion());
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			String tableName = getTableName();
			String content = getSQLCreateContent();
			LogUtil.d(TAG, "onCreate: " + tableName + ";\n" + content);
			db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " " + content + ";");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			LogUtil.d(TAG, "onUpgrade: " + oldVersion + "; " + newVersion);
			if (oldVersion != newVersion)
			{
				db.execSQL("DROP TABLE IF EXISTS " + getTableName() + ";");
				onCreate(db);
			}
		}
	}

	public synchronized boolean hasRecord(Context context)
	{
		Cursor cursor = null;
		try
		{
			cursor = query(context, null, null, null);
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
}
