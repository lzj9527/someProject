package android.extend.widget.adapter;

import java.util.Collection;

import android.database.DataSetObserver;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;

public class BasePagerAdapter<T extends AbsAdapterItem> extends android.support.v4.view.PagerAdapter implements
		IAdapterExtend<T>
{
	public final String TAG = getClass().getSimpleName();

	private ViewGroup mViewParent;
	private ViewArrayAdapter<T> mViewArrayAdapter;
	private SparseArray<T> mItemArray = new SparseArray<T>();
	private DataSetObserver mDataSetObserver = new DataSetObserver()
	{
		@Override
		public void onChanged()
		{
			// LogUtil.v(TAG, "notifyDataSetChanged...");
			notifyDataSetChanged();
			// notifyItemsUpdate();
		}

		@Override
		public void onInvalidated()
		{
			notifyDataSetChanged();
		}
	};
	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			notifyPageSelected(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{
		}
	};

	public BasePagerAdapter()
	{
		mViewArrayAdapter = new ViewArrayAdapter<T>();
		mViewArrayAdapter.registerDataSetObserver(mDataSetObserver);
	}

	public BasePagerAdapter(ViewPager viewPager)
	{
		this();
		if (viewPager != null)
			viewPager.addOnPageChangeListener(mPageChangeListener);
	}

	@Override
	public void registerOnDataSetObserver(OnDataSetObserver observer)
	{
		mViewArrayAdapter.registerOnDataSetObserver(observer);
	}

	@Override
	public void unregisterOnDataSetObserver(OnDataSetObserver observer)
	{
		mViewArrayAdapter.unregisterOnDataSetObserver(observer);
	}

	@Override
	public void addItem(T item)
	{
		mViewArrayAdapter.addItem(item);
	}

	@Override
	public void addItem(int position, T item)
	{
		mViewArrayAdapter.addItem(position, item);
	}

	@Override
	public void addItems(Collection<T> itemList)
	{
		mViewArrayAdapter.addItems(itemList);
	}

	@Override
	public void addItems(int position, Collection<T> itemList)
	{
		mViewArrayAdapter.addItems(position, itemList);
	}

	@Override
	public void removeItem(int position)
	{
		mViewArrayAdapter.removeItem(position);
	}

	@Override
	public void removeItem(T item)
	{
		mViewArrayAdapter.removeItem(item);
	}

	@Override
	public void clear()
	{
		mViewArrayAdapter.clear();
	}

	@Override
	public int getCount()
	{
		return mViewArrayAdapter.getCount();
	}

	@Override
	public int getItemPosition(Object object)
	{
		int position;
		if (mViewArrayAdapter.isViewFromArray((View)object))
		{
			position = super.getItemPosition(object);
		}
		else
		{
			position = POSITION_NONE;
		}
		LogUtil.d(TAG, "getItemPosition: " + object + "; " + position);
		return position;
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		mViewParent = container;
		View view = mViewArrayAdapter.getView(position, null, container);
		container.addView(view);
		LogUtil.d(TAG, "instantiateItem: " + position + "; " + view);
		mItemArray.put(position, mViewArrayAdapter.getItem(position));
		return view;
	}

	@Override
	public void destroyItem(final ViewGroup container, final int position, Object object)
	{
		View view = mViewArrayAdapter.getViewFromArray(position);
		if (view == null)
			view = (View)object;
		LogUtil.d(TAG, "destroyItem: " + position + "; " + object + "; " + view);
		final T item = mItemArray.get(position);
		view.addOnAttachStateChangeListener(new OnAttachStateChangeListener()
		{
			@Override
			public void onViewDetachedFromWindow(View v)
			{
				v.removeOnAttachStateChangeListener(this);
				if (item != null)
				{
					LogUtil.d(TAG, "recycleView: " + position + "; " + v);
					item.onRecycleViewResource(v, position, container);
				}
			}

			@Override
			public void onViewAttachedToWindow(View v)
			{
			}
		});
		container.removeView(view);
		mItemArray.remove(position);
	}

	// @Override
	// public void setPrimaryItem(ViewGroup container, int position, Object object)
	// {
	// LogUtil.d(TAG, "setPrimaryItem: " + container + "; " + position + "; " + object);
	// super.setPrimaryItem(container, position, object);
	// View view = mViewArrayAdapter.getViewFromArray(position);
	// if (view != null)
	// {
	// mViewArrayAdapter.setViewVisible(position, view, container, true);
	// }
	// }

	// @Override
	// public void startUpdate(ViewGroup container)
	// {
	// LogUtil.d(TAG, "startUpdate..." + container);
	// super.startUpdate(container);
	// }
	//
	// @Override
	// public void finishUpdate(ViewGroup container)
	// {
	// LogUtil.d(TAG, "finishUpdate..." + container);
	// super.finishUpdate(container);
	// }

	public AbsAdapterItem getItem(int position)
	{
		return mViewArrayAdapter.getItem(position);
	}

	public View getItemView(int position)
	{
		return mViewArrayAdapter.getViewFromArray(position);
	}

	public void notifyPageSelected(final int position)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (mItemArray)
				{
					try
					{
						T item = mItemArray.get(position);
						View view = mViewArrayAdapter.getViewFromArray(position);
						item.onUpdateView(view, position, mViewParent);
					}
					catch (Exception e)
					{
						LogUtil.w(TAG, "", e);
					}
				}
			}
		});
	}
}
