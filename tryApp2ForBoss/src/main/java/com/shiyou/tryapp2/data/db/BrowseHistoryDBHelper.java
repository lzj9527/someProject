package com.shiyou.tryapp2.data.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.extend.data.BaseDBHelper;
import android.extend.data.BaseData;
import android.extend.util.LogUtil;
import android.text.format.DateFormat;

import com.shiyou.tryapp2.Config;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse.GoodsDetail;

public class BrowseHistoryDBHelper extends BaseDBHelper
{
	public static class HistoryItem extends BaseData
	{
		public String dateString;
		public List<GoodsDetail> goodsList = new ArrayList<GoodsDetail>();
	}

	public static class HistoryList extends BaseData
	{
		public List<HistoryItem> list = new ArrayList<HistoryItem>();

		public HistoryItem getHistoryItem(String dateString)
		{
			for (HistoryItem item : list)
			{
				if (item.dateString.equals(dateString))
				{
					return item;
				}
			}
			return null;
		}
	}

	public static final String NAME_ID = "id";
	public static final String NAME_JSON = "json";
	public static final String NAME_TIME = "time";

	private static final String TABLE_NAME = "history";
	private static final int VERSION = Config.HistoryDB_Version;

	private static BrowseHistoryDBHelper mInstance = null;

	public static BrowseHistoryDBHelper getInstance()
	{
		if (mInstance == null)
			mInstance = new BrowseHistoryDBHelper();
		return mInstance;
	}

	private BrowseHistoryDBHelper()
	{
	}

	public synchronized boolean hasRecord(Context context, String id)
	{
		Cursor cursor = null;
		try
		{
			cursor = query(context, NAME_ID + '=' + id, null, null);
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

	public synchronized long insert(Context context, String id, String json)
	{
		ContentValues values = new ContentValues();
		values.put(NAME_ID, id);
		// values.put(NAME_TYPE, type);
		values.put(NAME_JSON, json);
		values.put(NAME_TIME, System.currentTimeMillis());
		return insert(context, values);
	}

	public synchronized long insert(Context context, GoodsDetail item)
	{
		return insert(context, item.id, GoodsDetail.toJson(item));
	}

	public synchronized int update(Context context, String id, String json)
	{
		ContentValues values = new ContentValues();
		values.put(NAME_JSON, json);
		values.put(NAME_TIME, System.currentTimeMillis());
		return update(context, values, NAME_ID + '=' + id, null);
	}

	public synchronized int update(Context context, GoodsDetail item)
	{
		return update(context, item.id, GoodsDetail.toJson(item));
	}

	public synchronized long put(Context context, GoodsDetail item)
	{
		if (hasRecord(context, item.id))
			return update(context, item);
		else
			return insert(context, item);
	}

	private GoodsDetail getGoodsDetail(Cursor cursor)
	{
		String json = cursor.getString(cursor.getColumnIndex(NAME_JSON));
		GoodsDetail item = GoodsDetail.fromJson(json);
		LogUtil.v(TAG, "getGoodsDetail: " + json);
		return item;
	}

	private String getDateString(Cursor cursor)
	{
		long time = cursor.getLong(cursor.getColumnIndex(NAME_TIME));
		CharSequence DATEFORMAT = "yyyy.MM.dd";
		String dateString = DateFormat.format(DATEFORMAT, time).toString();
		LogUtil.v(TAG, "getDateString: " + dateString);
		return dateString;
	}

	public synchronized GoodsDetail query(Context context, String id)
	{
		Cursor cursor = null;
		try
		{
			cursor = query(context, NAME_ID + '=' + id, null, null);
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				return getGoodsDetail(cursor);
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

	public synchronized List<GoodsDetail> queryAll(Context context, boolean ascOrder)
	{
		List<GoodsDetail> list = new ArrayList<GoodsDetail>();
		Cursor cursor = null;
		try
		{
			String orderBy = NAME_TIME + " " + (ascOrder ? "asc" : "desc");
			cursor = query(context, null, null, orderBy);
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				do
				{
					GoodsDetail item = getGoodsDetail(cursor);
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
		return list;
	}

	public synchronized HistoryList getHistoryList(Context context)
	{
		HistoryList list = new HistoryList();
		Cursor cursor = null;
		try
		{
			boolean ascOrder = false;
			String orderBy = NAME_TIME + " " + (ascOrder ? "asc" : "desc");
			cursor = query(context, null, null, orderBy);
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				do
				{
					GoodsDetail goods = getGoodsDetail(cursor);
					String dateString = getDateString(cursor);
					HistoryItem item = list.getHistoryItem(dateString);
					if (item != null)
					{
						item.goodsList.add(goods);
					}
					else
					{
						item = new HistoryItem();
						item.dateString = dateString;
						item.goodsList.add(goods);
						list.list.add(item);
					}
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
		return list;
	}

	public synchronized int delete(Context context, String id)
	{
		return delete(context, NAME_ID + '=' + id, null);
	}

	public synchronized int deleteAll(Context context)
	{
		return delete(context, null, null);
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
		sb.append(NAME_ID).append(" TEXT").append(',');
		// sb.append(NAME_TYPE).append(" INTEGER").append(',');
		sb.append(NAME_JSON).append(" TEXT").append(',');
		sb.append(NAME_TIME).append(" LONG");
		sb.append(')');
		return sb.toString();
	}
}
