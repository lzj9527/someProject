package android.extend.widget.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class ViewArrayAdapter<T extends AbsAdapterItem> extends BaseAdapter<T>
{
	private SparseArray<View> mViewArray = new SparseArray<View>();

	@Override
	public void removeItem(int position)
	{
		super.removeItem(position);
		mViewArray.remove(position);
	}

	@Override
	public void removeItem(T item)
	{
		int position = mItemList.indexOf(item);
		removeItem(position);
	}

	@Override
	public void clear()
	{
		mViewArray.clear();
		super.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// if (position < mViewArray.size())
		// {
		View view = mViewArray.get(position);
		// }
		if (view == null)
		{
			view = super.getView(position, convertView, parent);
			mViewArray.put(position, view);
		}
		else
		{
			T item = getItem(position);
			item.onUpdateView(view, position, parent);
			item.onLoadViewResource(view, position, parent);
		}
		return view;
	}

	public View getViewFromArray(int position)
	{
		return mViewArray.get(position);
	}

	public boolean isViewFromArray(View view)
	{
		for (int i = 0; i < mViewArray.size(); i++)
		{
			if (mViewArray.valueAt(i) == view)
				return true;
		}
		return false;
	}
}
