package android.extend.widget.waterfall;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.extend.cache.FileCacheManager;
import android.extend.loader.BitmapLoader.DecodeMode;
import android.extend.loader.BitmapLoader.IDecodeParams;
import android.extend.loader.ViewImageLoader;
import android.extend.loader.ViewImageLoader.OnViewImageLoadListener;
import android.extend.util.BitmapUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.util.ViewTools;
import android.extend.widget.ExtendImageView;
import android.extend.widget.ExtendImageView.LoadStatus;
import android.extend.widget.adapter.AbsAdapterItem;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class WaterfallAdapterItem extends AbsAdapterItem implements IDecodeParams, OnViewImageLoadListener
{
	public interface OnItemClickListener
	{
		public void onItemClick(View adapterView, ViewGroup parent, View view, int position, WaterfallData data);
	}

	public static class WaterfallData
	{
		public String imageUrl;
		// public LoadParams loadParams;
		public int imageResID;
		public int imageWidth;
		public int imageHeight;
		public long imageMTime = FileCacheManager.UNLIMITED_TIME;

		// int[] srcSize;
		// int[] outSize;

		private Map<String, Object> mDataMap = null;

		public void putData(String key, Object value)
		{
			if (TextUtils.isEmpty(key) || value == null)
			{
				throw new NullPointerException();
			}
			if (mDataMap == null)
			{
				mDataMap = Collections.synchronizedMap(new HashMap<String, Object>());
			}
			mDataMap.put(key, value);
		}

		public Object getData(String key)
		{
			if (TextUtils.isEmpty(key))
			{
				throw new NullPointerException();
			}
			if (mDataMap == null)
			{
				return null;
			}
			return mDataMap.get(key);
		}

		public void removeData(String key)
		{
			if (mDataMap == null || mDataMap.isEmpty())
			{
				return;
			}
			mDataMap.remove(key);
		}

		public void clearAllData()
		{
			if (mDataMap == null || mDataMap.isEmpty())
			{
				return;
			}
			mDataMap.clear();
		}
	}

	public final String TAG = getClass().getSimpleName();

	protected Context mContext;
	protected WaterfallData mData;
	protected int mLayoutResID;
	protected OnItemClickListener mItemClickListener;

	private LoadStatus mStatus = LoadStatus.UNLOAD;
	private int mParentWidth;
	private int mImageViewWidth;

	protected int mImageId;
	protected int mProgressId;

	public WaterfallAdapterItem(Context context, WaterfallData data, int layoutResID, OnItemClickListener clickListener)
	{
		if (context == null)
		{
			throw new NullPointerException();
		}
		mContext = context;
		mData = data;
		mLayoutResID = layoutResID;
		mItemClickListener = clickListener;

		mImageId = ResourceUtil.getId(mContext, "image");
		mProgressId = ResourceUtil.getId(mContext, "progress");
	}

	public WaterfallAdapterItem(Context context, WaterfallData data, int layoutResID)
	{
		this(context, data, layoutResID, null);
	}

	protected void changeStatus(LoadStatus status)
	{
		synchronized (this)
		{
			mStatus = status;
		}
	}

	private void cancelLoadImage(View view)
	{
		View imageView = findImageView(view);
		if (imageView == null)
			return;
		ViewImageLoader.getDefault().cancel(imageView);
	}

	private void showLoadingProgress(View imageView)
	{
		Object object = imageView.getTag(mProgressId);
		if (object != null)
		{
			((View)object).setVisibility(View.VISIBLE);
		}
	}

	private void hideLoadingProgress(View imageView)
	{
		Object object = imageView.getTag(mProgressId);
		if (object != null)
		{
			((View)object).setVisibility(View.GONE);
		}
	}

	@Override
	public View onCreateView(int position, ViewGroup parent)
	{
		View view = createView();

		final ImageView imageView = findImageView(view);
		if (mData.imageWidth < 1)
		{
			if (mData.imageResID > 0)
			{
				Drawable drawable = mContext.getResources().getDrawable(mData.imageResID);
				mData.imageWidth = drawable.getIntrinsicWidth();
				mData.imageHeight = drawable.getIntrinsicHeight();
				if (imageView != null)
				{
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
					imageView.setImageResource(mData.imageResID);
				}
			}
		}
		if (mData.imageWidth > 0 && mData.imageHeight > 0)
		{
			ensureViewLayoutParams(view);
			if (imageView != null)
			{
				if (view != imageView)
				{
					ensureViewLayoutParams(imageView);
				}
				ensureImageViewDimension(imageView);
			}
		}

		View progressView = view.findViewById(mProgressId);
		if (progressView != null)
		{
			progressView.setVisibility(View.GONE);
			if (imageView != null)
				imageView.setTag(mProgressId, progressView);
		}

		return view;
	}

	@Override
	public void onUpdateView(final View view, final int position, final ViewGroup parent)
	{
		mParentWidth = parent.getWidth();
	}

	@Override
	public void onLoadViewResource(View view, int position, ViewGroup parent)
	{
		loadViewImage(view, position);
	}

	@Override
	public void onRecycleViewResource(View view, int position, ViewGroup parent)
	{
		recycleViewImage(view, position);
	}

	@Override
	public void onItemClick(View adapterView, ViewGroup parent, View view, int position, long id)
	{
		if (mItemClickListener != null)
			mItemClickListener.onItemClick(adapterView, parent, view, position, mData);
	}

	protected View createView()
	{
		View view = null;
		if (mLayoutResID > 0)
		{
			view = View.inflate(mContext, mLayoutResID, null);
		}
		else
		{
			ExtendImageView imageView = new ExtendImageView(mContext);
			imageView.setId(mImageId);
			imageView.setScaleType(ScaleType.FIT_XY);
			view = imageView;
		}
		changeStatus(LoadStatus.UNLOAD);
		return view;
	}

	protected ImageView findImageView(View view)
	{
		ImageView imageView = (ImageView)view.findViewById(mImageId);
		if (imageView == null)
		{
			if (view instanceof ViewGroup)
			{
				ViewGroup group = (ViewGroup)view;
				int count = group.getChildCount();
				for (int i = 0; i < count; i++)
				{
					View child = group.getChildAt(i);
					if (child instanceof ImageView)
					{
						imageView = (ImageView)child;
						break;
					}
				}
			}
			else if (view instanceof ImageView)
			{
				imageView = (ImageView)view;
			}
		}
		return imageView;
	}

	protected void ensureViewLayoutParams(View view)
	{
		LayoutParams params = view.getLayoutParams();
		if (params == null)
		{
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		else
		{
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.WRAP_CONTENT;
		}
		view.setLayoutParams(params);
	}

	protected void ensureImageViewDimension(final ImageView imageView)
	{
		// if (imageView.getWidth() > 0)
		// {
		updateImageViewDimension(imageView);
		// }
		// else
		// {
		imageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				// if (imageView.getWidth() > 0)
				// {
				// imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				updateImageViewDimension(imageView);
				// }
			}
		});
		// }
	}

	protected void updateImageViewDimension(ImageView imageView)
	{
		int width = imageView.getWidth();
		if (mImageViewWidth == width)
		{
			return;
		}
		mImageViewWidth = width;
		int height = BitmapUtils.computeOutHeight(mData.imageWidth, mData.imageHeight, width);
		LayoutParams params = imageView.getLayoutParams();
		if (params.width != LayoutParams.MATCH_PARENT || params.height != height)
		{
			params.width = LayoutParams.MATCH_PARENT;
			params.height = height;
			LogUtil.i(TAG, "updateImageViewDimension: " + width + "x" + height + "; " + imageView);
			imageView.setLayoutParams(params);
		}
	}

	protected void loadViewImage(View view, int position)
	{
		if (TextUtils.isEmpty(mData.imageUrl))
		{
			// view.setVisibility(View.INVISIBLE);
			return;
		}
		switch (mStatus)
		{
			case LOADING:
			case LOADED:
				return;
			default:
				break;
		}
		View imageView = findImageView(view);
		if (imageView == null)
			return;
		LogUtil.v(TAG, "loadViewImage: " + view + "; " + position + "; " + mStatus);
		showLoadingProgress(imageView);
		if (imageView instanceof ExtendImageView)
		{
			((ExtendImageView)imageView).setImageDataSource(mData.imageUrl, null, getDecodeMode());
		}
		ViewImageLoader.getDefault().startLoad(imageView, mData.imageUrl, mData.imageMTime, this, this, false, true);
		changeStatus(LoadStatus.LOADING);
	}

	protected void recycleViewImage(View view, int position)
	{
		if (TextUtils.isEmpty(mData.imageUrl))
		{
			// view.setVisibility(View.INVISIBLE);
			return;
		}
		if (mStatus == LoadStatus.UNLOAD)
			return;
		LogUtil.v(TAG, "recycleViewImage: " + view + "; " + position + "; " + mStatus);
		switch (mStatus)
		{
			case LOADED:
				ImageView imageView = findImageView(view);
				if (imageView == null)
					return;
				ViewTools.recycleImageView(imageView);
				break;
			case LOADING:
				cancelLoadImage(view);
				break;
			default:
				break;
		}
		changeStatus(LoadStatus.UNLOAD);
	}

	@Override
	public DecodeMode getDecodeMode()
	{
		return DecodeMode.FIT_WIDTH;
	}

	@Override
	public float getScale()
	{
		return 0;
	}

	@Override
	public int getOutWidth()
	{
		return mParentWidth;
	}

	@Override
	public int getOutHeight()
	{
		return 0;
	}

	@Override
	public Bitmap onViewImagePrepare(View view, Bitmap bitmap)
	{
		return null;
	}

	@Override
	public void onViewImageChanged(View view, boolean success)
	{
		LogUtil.v(TAG, "onViewImageChanged: " + view + " " + success);
		hideLoadingProgress(view);
		if (success)
		{
			changeStatus(LoadStatus.LOADED);
		}
		else
		{
			changeStatus(LoadStatus.LOADFAILED);
		}
	}
}
