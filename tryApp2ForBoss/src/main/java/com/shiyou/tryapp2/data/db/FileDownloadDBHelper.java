package com.shiyou.tryapp2.data.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.extend.data.BaseDBHelper;
import android.extend.util.LogUtil;

import com.shiyou.tryapp2.Config;
import com.shiyou.tryapp2.data.FileInfo;

public class FileDownloadDBHelper extends BaseDBHelper
{
	private static final String TABLE_NAME = "file_download";
	private static final int VERSION = Config.FileDownloadDB_Version;

	private static final String NAME_URL = "url";
	private static final String NAME_SIZE = "size";
	private static final String NAME_MTIME = "mtime";
	private static final String NAME_PATH = "path";
	private static final String NAME_DTIME = "dtime";

	private static FileDownloadDBHelper mInstance = null;

	public static FileDownloadDBHelper getInstance()
	{
		if (mInstance == null)
			mInstance = new FileDownloadDBHelper();
		return mInstance;
	}

	@Override
	protected String getTableName()
	{
		return TABLE_NAME;
	}

	@Override
	protected int getTableVersion()
	{
		return VERSION;
	}

	@Override
	protected String getSQLCreateContent()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("(_id INTEGER PRIMARY KEY,");
		sb.append(NAME_URL).append(" TEXT").append(',');
		sb.append(NAME_SIZE).append(" LONG").append(',');
		sb.append(NAME_MTIME).append(" INTEGER").append(',');
		sb.append(NAME_PATH).append(" TEXT").append(',');
		sb.append(NAME_DTIME).append(" LONG");
		sb.append(')');
		return sb.toString();
	}

	private static String getSelectionByUrl(String url)
	{
		return NAME_URL + "=?";
	}

	public synchronized boolean hasRecord(Context context, String url)
	{
		Cursor cursor = null;
		try
		{
			cursor = query(context, getSelectionByUrl(url), new String[] { url }, null);
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

	public synchronized long insert(Context context, String url, long size, int mtime, String path)
	{
		ContentValues values = new ContentValues();
		values.put(NAME_URL, url);
		values.put(NAME_SIZE, size);
		values.put(NAME_MTIME, mtime);
		values.put(NAME_PATH, path);
		values.put(NAME_DTIME, System.currentTimeMillis());
		return insert(context, values);
	}

	public synchronized long insert(Context context, FileInfo info)
	{
		return insert(context, info.url, info.size, info.filemtime, info.path);
	}

	public synchronized int update(Context context, String url, long size, int mtime, String path)
	{
		ContentValues values = new ContentValues();
		values.put(NAME_SIZE, size);
		values.put(NAME_MTIME, mtime);
		values.put(NAME_PATH, path);
		values.put(NAME_DTIME, System.currentTimeMillis());
		return update(context, values, getSelectionByUrl(url), new String[] { url });
	}

	public synchronized int update(Context context, FileInfo info)
	{
		return update(context, info.url, info.size, info.filemtime, info.path);
	}

	public synchronized long put(Context context, FileInfo info)
	{
		if (hasRecord(context, info.url))
			return update(context, info);
		else
			return insert(context, info);
	}

	public synchronized void delete(Context context, String url)
	{
		delete(context, getSelectionByUrl(url), new String[] { url });
	}

	public synchronized void deleteAll(Context context)
	{
		delete(context, null, null);
	}

	private FileInfo getFileInfo(Cursor cursor)
	{
		FileInfo info = new FileInfo();
		info.url = cursor.getString(cursor.getColumnIndex(NAME_URL));
		info.size = cursor.getLong(cursor.getColumnIndex(NAME_SIZE));
		info.filemtime = cursor.getInt(cursor.getColumnIndex(NAME_MTIME));
		info.path = cursor.getString(cursor.getColumnIndex(NAME_PATH));
		return info;
	}

	public synchronized FileInfo query(Context context, String url)
	{
		Cursor cursor = null;
		try
		{
			cursor = query(context, getSelectionByUrl(url), new String[] { url }, null);
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				return getFileInfo(cursor);
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

	public synchronized List<FileInfo> queryAll(Context context)
	{
		List<FileInfo> list = new ArrayList<FileInfo>();
		Cursor cursor = null;
		try
		{
			cursor = query(context, null, null, null);
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				do
				{
					list.add(getFileInfo(cursor));
				}
				while (cursor.moveToNext());
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
		return list;
	}

	public synchronized List<FileInfo> queryAll(Context context, boolean ascOrder)
	{
		List<FileInfo> list = new ArrayList<FileInfo>();
		Cursor cursor = null;
		try
		{
			String orderBy = NAME_DTIME + " " + (ascOrder ? "asc" : "desc");
			LogUtil.v(TAG, "queryAll: ascOrder=" + ascOrder + "; orderBy=" + orderBy);
			cursor = query(context, null, null, orderBy);
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				do
				{
					list.add(getFileInfo(cursor));
				}
				while (cursor.moveToNext());
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
		return list;
	}
}
