package android.extend.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.extend.ErrorInfo;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.loader.BitmapLoader.BitmapLoadParams;
import android.extend.loader.BitmapLoader.DecodeMode;
import android.extend.loader.BitmapLoader.IDecodeParams;
import android.extend.loader.BitmapLoader.OnBitmapLoadListener;
import android.extend.util.LogUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

public class ViewImageLoader
{
	public interface OnViewImageLoadListener
	{
		public Bitmap onViewImagePrepare(View view, Bitmap bitmap);

		public void onViewImageChanged(View view, boolean success);
	}

	public static final String TAG = ViewImageLoader.class.getSimpleName();

	private static ViewImageLoader mDefault;

	public static ViewImageLoader getDefault()
	{
		if (mDefault == null)
		{
			mDefault = new ViewImageLoader();
		}
		return mDefault;
	}

	private BitmapLoader mLoader = null;
	private List<View> mViewList = Collections.synchronizedList(new ArrayList<View>());

	public ViewImageLoader()
	{
		mLoader = new BitmapLoader();
	}

	public ViewImageLoader(int maxTaskCount)
	{
		mLoader = new BitmapLoader(maxTaskCount);
	}

	// public long startLoad(final View view, String url, LoadParams params, final OnViewImageLoadListener listener)
	// {
	// return startLoad(view, url, params, null, listener, false);
	// }
	//
	// public long startLoad(final View view, String url, LoadParams params, final OnViewImageLoadListener listener,
	// final boolean background)
	// {
	// return startLoad(view, url, params, null, listener, background);
	// }
	//
	// public long startLoad(final View view, String url, LoadParams params, IDecodeParams decodeParams,
	// final OnViewImageLoadListener listener)
	// {
	// return startLoad(view, url, params, decodeParams, listener, false);
	// }
	//
	// public long startLoad(final View view, String url, LoadParams params, IDecodeParams decodeParams,
	// final OnViewImageLoadListener listener, final boolean background)
	// {
	// return startLoad(view, url, params, decodeParams, listener, background, true);
	// }

	public long startLoad(final View view, String url, long imageMTime, final DecodeMode decodeMode,
			OnViewImageLoadListener listener, boolean background, boolean useBitmapCache)
	{
		return startLoad(view, url, imageMTime, new IDecodeParams()
		{
			@Override
			public float getScale()
			{
				return 1;
			}

			@Override
			public int getOutWidth()
			{
				return view.getWidth();
			}

			@Override
			public int getOutHeight()
			{
				return view.getHeight();
			}

			@Override
			public DecodeMode getDecodeMode()
			{
				return decodeMode;
			}
		}, listener, background, useBitmapCache);
	}

	public long startLoad(View view, String url, long imageMTime, IDecodeParams decodeParams,
			OnViewImageLoadListener listener, boolean background, boolean useBitmapCache)
	{
		BitmapLoadParams params = new BitmapLoadParams(decodeParams);
		params.mFileMTime = imageMTime;
		return startLoad(view, url, params, listener, background, useBitmapCache);
	}

	public long startLoad(final View view, String url, BitmapLoadParams params, final OnViewImageLoadListener listener,
			final boolean background, boolean useBitmapCache)
	{
		if (view == null)
		{
			throw new NullPointerException();
		}
		mViewList.add(view);
		return mLoader.startLoad(view, view.getContext(), url, params, new OnBitmapLoadListener()
		{
			@Override
			public void onLoadStarted(Object tag, String url)
			{
				LogUtil.d(TAG, "onLoadStarted: " + tag + "; " + url);
			}

			@Override
			public void onLoadFinished(Object tag, String url, Bitmap bitmap, DataFrom from)
			{
				LogUtil.d(TAG, "onLoadFinished: " + tag + "; " + url + "; bitmapSize=" + bitmap.getWidth() + "x"
						+ bitmap.getHeight() + "; " + from);
				onViewImageChanged(view, bitmap, listener, background);
			}

			@Override
			public void onLoadFailed(Object tag, String url, ErrorInfo error)
			{
				LogUtil.w(TAG, "onLoadFailed: " + tag + "; " + url + "\n" + error.toString());
				mViewList.remove(view);
				if (listener != null)
				{
					listener.onViewImageChanged(view, false);
				}
			}

			@Override
			public void onLoadCanceled(Object tag, String url)
			{
				LogUtil.d(TAG, "onLoadCanceled: " + tag + "; " + url);
				mViewList.remove(view);
				if (listener != null)
				{
					listener.onViewImageChanged(view, false);
				}
			}
		}, useBitmapCache);
	}

	public boolean cancel(long id)
	{
		return mLoader.cancel(id);
	}

	public boolean cancel(View view)
	{
		return mLoader.cancel(view);
	}

	public void cancelAll()
	{
		mLoader.cancelAll();
	}

	public void destory()
	{
		mLoader.destory();
	}

	private void onViewImageChanged(final View view, Bitmap bitmap, final OnViewImageLoadListener listener,
			final boolean background)
	{
		if (bitmap == null)
		{
			if (listener != null)
			{
				listener.onViewImageChanged(view, false);
			}
			return;
		}
		if (!mViewList.contains(view))
		{
			// if (!bitmap.isRecycled())
			// {
			// bitmap.recycle();
			// }
			LogUtil.w(TAG, view + " image load has canceled!");
			if (listener != null)
			{
				listener.onViewImageChanged(view, false);
			}
			return;
		}
		Bitmap oldBitmap = bitmap;
		Bitmap newBitmap = null;
		if (listener != null)
		{
			newBitmap = listener.onViewImagePrepare(view, oldBitmap);
		}
		// if (newBitmap != null && oldBitmap != newBitmap)
		// {
		// oldBitmap.recycle();
		// }
		// else
		if (newBitmap == null)
		{
			newBitmap = oldBitmap;
		}
		if (!mViewList.contains(view))
		{
			// if (!bitmap.isRecycled())
			// {
			// bitmap.recycle();
			// }
			LogUtil.w(TAG, view + " image load has canceled!");
			if (listener != null)
			{
				listener.onViewImageChanged(view, false);
			}
			return;
		}
		final Bitmap _bitmap = newBitmap;
		view.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (!background && view instanceof ImageView)
				{
					((ImageView)view).setImageBitmap(_bitmap);
				}
				else
				{
					view.setBackgroundDrawable(new BitmapDrawable(_bitmap));
				}
				if (listener != null)
				{
					listener.onViewImageChanged(view, true);
				}
			}
		});
	}
}
