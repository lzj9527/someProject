package android.extend.widget.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.view.View;
import android.view.ViewGroup;

public class BaseAdapter<T extends AbsAdapterItem> extends android.widget.BaseAdapter implements IAdapterExtend<T>
{
	public final String TAG = getClass().getSimpleName();

	private final OnDataSetObservable<T> mDataSetObservable = new OnDataSetObservable<T>();
	// protected View mAdapterView;
	protected List<T> mItemList = Collections.synchronizedList(new ArrayList<T>());
	private List<String> mItemTypeList = Collections.synchronizedList(new ArrayList<String>());

	// private boolean mItemTypeChanged = false;

	public BaseAdapter()
	{
	}
 
	@Override
	public void registerOnDataSetObserver(OnDataSetObserver observer)
	{
		mDataSetObservable.registerObserver(observer);
		super.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterOnDataSetObserver(OnDataSetObserver observer)
	{
		mDataSetObservable.unregisterObserver(observer);
		super.unregisterDataSetObserver(observer);
	}

	protected void notifyDataAdded(int position, T item)
	{
		mDataSetObservable.notifyDataAdded(position, item);
	}

	protected void notifyDataAdded(int position, Collection<T> itemCollection)
	{
		mDataSetObservable.notifyDataAdded(position, itemCollection);
	}

	protected void notifyDataRemoved(int position)
	{
		mDataSetObservable.notifyDataRemoved(position);
	}

	protected void notifyDataCleared()
	{
		mDataSetObservable.notifyDataCleared();
	}

	private void addItemInner(int position, T item)
	{
		try
		{
			mItemList.add(position, item);
			addItemType(item);
			notifyDataSetChanged();
			notifyDataAdded(position, item);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	private void addItemsInner(int position, Collection<T> itemCollection)
	{
		try
		{
			mItemList.addAll(position, itemCollection);
			addItemType(itemCollection);
			notifyDataSetChanged();
			notifyDataAdded(position, itemCollection);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	private void removeItemInner(int position)
	{
		try
		{
			// T item =
			mItemList.remove(position);
			// if (item != null)
			// {
			// item.onDismiss();
			// }
			notifyDataSetChanged();
			notifyDataRemoved(position);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	private void clearInner()
	{
		try
		{
			// List<T> itemList = new ArrayList<T>(mItemList);
			mItemList.clear();
			// mItemTypeList.clear();
			notifyDataSetChanged();
			notifyDataCleared();
			// synchronized (itemList)
			// {
			// for (AbsAdapterItem item : itemList)
			// {
			// item.onDismiss();
			// }
			// }
			// itemList.clear();
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	private void addItemType(T item)
	{
		String name = item.getClass().getName();
		int index = mItemTypeList.indexOf(name);
		if (index == -1)
		{
			mItemTypeList.add(name);
		}
	}

	private void addItemType(Collection<T> itemCollection)
	{
		for (T item : itemCollection)
		{
			addItemType(item);
		}
	}

	public void addItemImmediate(T item)
	{
		if (item == null)
			return;
		int position = mItemList.size();
		addItemInner(position, item);
	}

	@Override
	public void addItem(final T item)
	{
		if (item == null)
			return;
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				int position = mItemList.size();
				addItemInner(position, item);
			}
		});
	}

	public void addItemImmediate(int position, T item)
	{
		if (item == null)
			return;
		addItemInner(position, item);
	}

	@Override
	public void addItem(final int position, final T item)
	{
		if (item == null)
			return;
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				addItemInner(position, item);
			}
		});
	}

	public void addItemsImmediate(Collection<T> itemCollection)
	{
		if (itemCollection == null || itemCollection.isEmpty())
			return;
		int position = mItemList.size();
		addItemsInner(position, itemCollection);
	}

	@Override
	public void addItems(final Collection<T> itemCollection)
	{
		if (itemCollection == null || itemCollection.isEmpty())
			return;
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				int position = mItemList.size();
				addItemsInner(position, itemCollection);
			}
		});
	}

	public void addItemsImmediate(int position, Collection<T> itemCollection)
	{
		if (itemCollection == null || itemCollection.isEmpty())
			return;
		addItemsInner(position, itemCollection);
	}

	@Override
	public void addItems(final int position, final Collection<T> itemCollection)
	{
		if (itemCollection == null || itemCollection.isEmpty())
			return;
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				addItemsInner(position, itemCollection);
			}
		});
	}

	public void removeItemImmediate(int position)
	{
		removeItemInner(position);
	}

	@Override
	public void removeItem(final int position)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				removeItemInner(position);
			}
		});
	}

	public void removeItemImmediate(T item)
	{
		if (item == null)
			return;
		int position = mItemList.indexOf(item);
		removeItemInner(position);
	}

	@Override
	public void removeItem(final T item)
	{
		if (item == null)
			return;
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				int position = mItemList.indexOf(item);
				removeItemInner(position);
			}
		});
	}

	public void clearImmediate()
	{
		if (mItemList.isEmpty())
			return;
		clearInner();
	}

	@Override
	public void clear()
	{
		if (mItemList.isEmpty())
			return;
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				clearInner();
			}
		});
	}

	public int indexOfItem(T item)
	{
		return mItemList.indexOf(item);
	}

	@Override
	public int getCount()
	{
		return mItemList.size();
	}

	@Override
	public T getItem(int position)
	{
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		AbsAdapterItem item = mItemList.get(position);
		if (item == null)
		{
			return null;
		}
		if (convertView == null)
		{
			convertView = item.onCreateView(position, parent);
		}
		try
		{
			item.onUpdateView(convertView, position, parent);
			item.onLoadViewResource(convertView, position, parent);
		}
		catch (ClassCastException e)
		{
			LogUtil.w(TAG, "", e);
			convertView = item.onCreateView(position, parent);
			item.onUpdateView(convertView, position, parent);
			item.onLoadViewResource(convertView, position, parent);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
		return convertView;
	}

	@Override
	public int getItemViewType(int position)
	{
		AbsAdapterItem item = mItemList.get(position);
		if (item == null)
		{
			return -1;
		}
		String name = item.getClass().getName();
		int index = mItemTypeList.indexOf(name);
		if (index == -1)
		{
			mItemTypeList.add(name);
			index = mItemTypeList.size() - 1;
		}
		return index;
	}

	@Override
	public int getViewTypeCount()
	{
		if (mItemList.isEmpty())
		{
			return 1;
		}
		return mItemTypeList.size() + 1;
	}

	@Override
	public boolean isEnabled(int position)
	{
		AbsAdapterItem item = mItemList.get(position);
		if (item != null)
		{
			return item.isEnabled();
		}
		return false;
	}

	// public void setViewVisible(int position, View view, ViewGroup parent, boolean visible)
	// {
	// AbsAdapterItem item = mItemList.get(position);
	// if (item != null)
	// {
	// item.onViewVisibled(view, position, parent, visible);
	// }
	// }
}
