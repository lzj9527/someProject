package android.extend.widget.recycler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.extend.util.LogUtil;
import android.extend.util.ReflectHelper;
import android.extend.widget.recycler.BaseRecyclerAdapter.BaseViewHolder;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder>
{
	public static class BaseViewHolder extends RecyclerView.ViewHolder
	{
		AbsAdapterItem item;

		public BaseViewHolder(AbsAdapterItem item, View view)
		{
			super(view);
			this.item = item;
		}

		public RecyclerView getBindedRecyclerView()
		{
			return (RecyclerView)ReflectHelper.getDeclaredFieldValue(this,
					"android.support.v7.widget.RecyclerView.ViewHolder", "mOwnerRecyclerView");
		}
	}

	public final String TAG = getClass().getSimpleName();

	List<AbsAdapterItem> mItemList = Collections.synchronizedList(new ArrayList<AbsAdapterItem>());

	// List<String> mItemTypeList = Collections.synchronizedList(new ArrayList<String>());

	@Override
	public int getItemCount()
	{
		return mItemList.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		// AbsAdapterItem item = mItemList.get(position);
		// String name = item.getClass().getName();
		// int index = mItemTypeList.indexOf(name);
		// if (index == -1)
		// {
		// mItemTypeList.add(name);
		// index = mItemTypeList.size() - 1;
		// }
		return position;
	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LogUtil.v(TAG, "onCreateViewHolder: " + parent + "; " + viewType);
		AbsAdapterItem item = mItemList.get(viewType);
		View view = item.onCreateView(parent, viewType);
		return new BaseViewHolder(item, view);
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position)
	{
		LogUtil.v(TAG, "onBindViewHolder: " + holder + "; " + position);
		if (!holder.item.equals(mItemList.get(position)))
			holder.item = mItemList.get(position);
		holder.item.onBindView(holder, holder.itemView, position);
	}

	// @Override
	// public void onAttachedToRecyclerView(RecyclerView recyclerView)
	// {
	// LogUtil.v(TAG, "onAttachedToRecyclerView: " + recyclerView);
	// super.onAttachedToRecyclerView(recyclerView);
	// }
	//
	// @Override
	// public void onDetachedFromRecyclerView(RecyclerView recyclerView)
	// {
	// LogUtil.v(TAG, "onDetachedFromRecyclerView: " + recyclerView);
	// super.onDetachedFromRecyclerView(recyclerView);
	// }

	@Override
	public void onViewAttachedToWindow(BaseViewHolder holder)
	{
		LogUtil.v(TAG, "onViewAttachedToWindow: " + holder);
		holder.item.onViewAttachedToWindow(holder, holder.itemView);
	}

	@Override
	public void onViewDetachedFromWindow(BaseViewHolder holder)
	{
		LogUtil.v(TAG, "onViewDetachedFromWindow: " + holder);
		holder.item.onViewDetachedFromWindow(holder, holder.itemView);
	}

	@Override
	public void onViewRecycled(BaseViewHolder holder)
	{
		LogUtil.v(TAG, "onViewRecycled: " + holder);
		holder.item.onViewRecycled(holder, holder.itemView);
	}

	@Override
	public boolean onFailedToRecycleView(BaseViewHolder holder)
	{
		LogUtil.v(TAG, "onFailedToRecycleView: " + holder);
		return super.onFailedToRecycleView(holder);
	}

	// private void addItemType(AbsAdapterItem item)
	// {
	// String name = item.getClass().getName();
	// int index = mItemTypeList.indexOf(name);
	// if (index == -1)
	// {
	// mItemTypeList.add(name);
	// }
	// }
	//
	// private void addItemsType(Collection<AbsAdapterItem> items)
	// {
	// for (AbsAdapterItem item : items)
	// {
	// addItemType(item);
	// }
	// }

	public synchronized void addItem(AbsAdapterItem item, int position)
	{
		if (position < 0 || position > mItemList.size())
		{
			LogUtil.w(TAG, "addItem failed, the position=" + position + " is out of bounds, size=" + mItemList.size());
			return;
		}
		mItemList.add(position, item);
		item.mAttachedAdapter = this;
		notifyItemInserted(position);
	}

	public synchronized void addItem(AbsAdapterItem item)
	{
		int position = mItemList.size();
		mItemList.add(item);
		item.mAttachedAdapter = this;
		notifyItemInserted(position);
	}

	public synchronized void addItems(Collection<AbsAdapterItem> items, int position)
	{
		if (position < 0 || position > mItemList.size())
		{
			LogUtil.w(TAG, "addItems failed, the position=" + position + " is out of bounds, size=" + mItemList.size());
			return;
		}
		mItemList.addAll(position, items);
		for (AbsAdapterItem item : items)
		{
			item.mAttachedAdapter = this;
		}
		notifyItemRangeInserted(position, items.size());
	}

	public synchronized void addItems(Collection<AbsAdapterItem> items)
	{
		int position = mItemList.size();
		mItemList.addAll(items);
		for (AbsAdapterItem item : items)
		{
			item.mAttachedAdapter = this;
		}
		notifyItemRangeInserted(position, items.size());
	}

	public synchronized void removeItem(int position)
	{
		if (position < 0 || position >= mItemList.size())
		{
			LogUtil.w(TAG,
					"removeItem failed, the position=" + position + " is out of bounds, size=" + mItemList.size());
			return;
		}
		AbsAdapterItem item = mItemList.remove(position);
		item.mAttachedAdapter = null;
		notifyItemRemoved(position);
	}

	public synchronized void removeItem(AbsAdapterItem item)
	{
		int position = mItemList.indexOf(item);
		removeItem(position);
	}

	public void removeItems(int start, int count)
	{
		int end = start + count;
		if (start >= end || start < 0 || end > mItemList.size())
		{
			LogUtil.w(TAG, "removeItems failed, the position from " + start + " to " + end + " is out of bounds, size="
					+ mItemList.size());
			return;
		}
		int i = 0;
		while (i < count)
		{
			removeItem(start);
			i++;
		}
	}

	public void removeAllItems()
	{
		for (AbsAdapterItem item : mItemList)
		{
			item.mAttachedAdapter = null;
		}
		mItemList.clear();
		notifyDataSetChanged();
	}

	public void clear()
	{
		removeAllItems();
	}

	public int indexOfItem(AbsAdapterItem item)
	{
		return mItemList.indexOf(item);
	}

	public AbsAdapterItem getItem(int position)
	{
		if (position < 0 || position >= mItemList.size())
		{
			LogUtil.w(TAG, "getItem failed, the position=" + position + " is out of bounds, size=" + mItemList.size());
			return null;
		}
		return mItemList.get(position);
	}
}
