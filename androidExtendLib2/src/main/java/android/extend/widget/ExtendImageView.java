package android.extend.widget;

import android.content.Context;
import android.extend.data.FileData;
import android.extend.loader.BitmapLoader.DecodeMode;
import android.extend.loader.Loader.LoadParams;
import android.extend.loader.ViewImageLoader;
import android.extend.util.LogUtil;
import android.extend.widget.ViewObservable.OnViewObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;

public class ExtendImageView extends ImageView implements ViewObservable.IViewObservable, Checkable
{
	public interface OnImageLoadListener
	{
		public void onImageLoadStarted(ExtendImageView imageView);

		public void onImageLoadFinished(ExtendImageView imageView, boolean success);

		public void onImageLoadCanceled(ExtendImageView imageView);
	}

	public enum LoadStatus
	{
		UNLOAD, LOADING, LOADED, LOADFAILED
	}

	public final String TAG = getClass().getSimpleName();

	boolean mAutoRecyleBitmap = true;
	// int mWidth;
	// int mHeight;
	String mUrl;
	// String mDownloadDirectory;
	// BitmapLoadParams mParams;
	long mImageMTime;
	DecodeMode mDecodeMode = DecodeMode.NONE;
	Bitmap mBitmap = null;
	boolean mUseBitmapCache = true;
	// private View mProgressBar;
	OnImageLoadListener mImageLoadListener;
	LoadStatus mLoadStatus = LoadStatus.UNLOAD;// 加载状态
	boolean mLayouted = false;// 是否已经Layout过
	boolean mStartLoadCalled = false;// 加载方法是否调用过
	ViewObservable mViewObservable = new ViewObservable(this);
	boolean mChecked = false;

	public ExtendImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public ExtendImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ExtendImageView(Context context)
	{
		super(context);
	}

	@Override
	public boolean isInEditMode()
	{
		return true;
	}

	public void setAutoRecyleBitmap(boolean autoRecyle)
	{
		mAutoRecyleBitmap = autoRecyle;
	}

	// private void changeLayoutParams(int width, int height)
	// {
	// LogUtil.v(TAG, "changeLayoutParams: " + width + "; " + height);
	// LayoutParams params = super.getLayoutParams();
	// if (params == null)
	// {
	// params = new LayoutParams(width, height);
	// }
	// else
	// {
	// params.width = width;
	// params.height = height;
	// }
	// super.setLayoutParams(params);
	// }

	// public void setImageSize(int width, int height)
	// {
	// if (width <= 0 || height <= 0)
	// {
	// throw new IllegalArgumentException();
	// }
	// if (width == mWidth && height == mHeight)
	// {
	// return;
	// }
	// // LogUtil.v(TAG, "setImageSize: " + width + " " + height);
	// mWidth = width;
	// mHeight = height;
	// changeLayoutParams(width, height);
	// mLoaded = false;
	// }

	// public void setImageLoadProgressBar(View progressBar)
	// {
	// mProgressBar = progressBar;
	// }

	public void setImageLoadListener(OnImageLoadListener listener)
	{
		mImageLoadListener = listener;
	}

	@Deprecated
	public void setImageDataSource(String url, LoadParams params, DecodeMode mode)
	{
		setImageDataSource(url, -1, mode);
	}

	public void setImageDataSource(String url, long imageMTime, DecodeMode mode)
	{
		if (!TextUtils.isEmpty(mUrl) && mUrl.equals(url) && mImageMTime == imageMTime)
		{
			return;
		}
		LogUtil.d(TAG, "setImageDataSource: " + url + "; " + imageMTime + "; " + mode);
		mUrl = url;
		mImageMTime = imageMTime;
		mDecodeMode = mode;
		mLoadStatus = LoadStatus.UNLOAD;
	}

	public void setImageDataSource(FileData data, DecodeMode mode)
	{
		setImageDataSource(data.url, data.filemtime, mode);
	}

	// public void setImageDownloadDirectory(String directory)
	// {
	// mDownloadDirectory = directory;
	// }

	public void startImageLoad()
	{
		startImageLoad(mUseBitmapCache);
	}

	public void startImageLoad(boolean useBitmapCache)
	{
		LogUtil.v(TAG, "startImageLoad: " + this + "; " + useBitmapCache + "; " + mUrl + "; viewSize=" + getWidth()
				+ "x" + getHeight());
		mUseBitmapCache = useBitmapCache;
		if (mLoadStatus == LoadStatus.LOADED)
		{
			if (mImageLoadListener != null)
				mImageLoadListener.onImageLoadFinished(this, true);
			return;
		}
		if (mLoadStatus == LoadStatus.LOADING)
			return;
		if (getWidth() == 0 || getHeight() == 0 || !mLayouted)
		{
			mStartLoadCalled = true;
			return;
		}
		if (TextUtils.isEmpty(mUrl))
		{
			return;
		}
		// LogUtil.v(TAG, "startImageLoad: " + this + "; " + mUrl + "; viewSize=" + getWidth() + "x" + getHeight());
		// + "; mDownloadDirectory=" + mDownloadDirectory);
		// if (!TextUtils.isEmpty(mDownloadDirectory))
		// {
		// String name = FileUtils.getFileName(mUrl);
		// File file = new File(mDownloadDirectory, name);
		// if (file.exists())
		// {
		// LogUtil.i(TAG, "find local file: " + file);
		// startImageLoadImpl(file.getAbsolutePath());
		// }
		// else
		// {
		// HttpLoadParams params = null;
		// // if (mParams != null && mParams instanceof HttpLoadParams)
		// // params = (HttpLoadParams)mParams;
		// mLoadStatus = LoadStatus.LOADING;
		// SimpleDownloader.startDownload(getContext(), this, mUrl, file.getAbsolutePath(), params,
		// new OnDownloadListener()
		// {
		// @Override
		// public void onDownloadStarted(Object tag, String url, String localPath)
		// {
		// }
		//
		// @Override
		// public void onDownloadProgress(Object tag, String url, String localPath, long count,
		// long length, float speed)
		// {
		// }
		//
		// @Override
		// public void onDownloadFinished(Object tag, String url, String localPath, long totalTime)
		// {
		// startImageLoadImpl(localPath);
		// }
		//
		// @Override
		// public void onDownloadCanceled(Object tag, String url, String localPath)
		// {
		// }
		//
		// @Override
		// public void onDownloadError(Object tag, String url, String localPath, ErrorInfo error)
		// {
		// mLoadStatus = LoadStatus.LOADFAILED;
		// if (mImageLoadListener != null)
		// mImageLoadListener.onImageLoadFinished(ExtendImageView.this, false);
		// }
		// });
		// }
		// return;
		// }
		// AndroidUtils.MainHandler.post(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// if (mProgressBar != null)
		// mProgressBar.setVisibility(View.VISIBLE);
		// }
		// });
		startImageLoadImpl(mUrl);
	}

	private void startImageLoadImpl(String url)
	{
		mLoadStatus = LoadStatus.LOADING;
		if (mImageLoadListener != null)
			mImageLoadListener.onImageLoadStarted(this);
		ViewImageLoader.getDefault().startLoad(this, url, mImageMTime, mDecodeMode,
				new ViewImageLoader.OnViewImageLoadListener()
				{
					@Override
					public Bitmap onViewImagePrepare(View view, final Bitmap bitmap)
					{
						return null;
					}

					@Override
					public void onViewImageChanged(View view, boolean success)
					{
						LogUtil.d(TAG, "onViewImageChanged: " + view + "; " + success);
						if (success)
							mLoadStatus = LoadStatus.LOADED;
						else
							mLoadStatus = LoadStatus.LOADFAILED;
						// if (mProgressBar != null)
						// mProgressBar.setVisibility(View.GONE);
						if (mImageLoadListener != null)
							mImageLoadListener.onImageLoadFinished(ExtendImageView.this, success);
					}
				}, false, mUseBitmapCache);
	}

	public void cancelImageLoad()
	{
		mStartLoadCalled = false;
		if (mLoadStatus == LoadStatus.LOADING)
		{
			LogUtil.v(TAG, "cancelImageLoad: " + this);
			ViewImageLoader.getDefault().cancel(this);
			if (mImageLoadListener != null)
				mImageLoadListener.onImageLoadCanceled(this);
		}
	}

	@Override
	public void setImageBitmap(Bitmap bitmap)
	{
		super.setImageBitmap(bitmap);
		if (mAutoRecyleBitmap)
			recyleBitmapImpl(bitmap);
		mBitmap = bitmap;
	}

	@Override
	public void setImageDrawable(Drawable drawable)
	{
		Bitmap bitmap = null;
		if (drawable != null && drawable instanceof BitmapDrawable)
		{
			bitmap = ((BitmapDrawable)drawable).getBitmap();
		}
		if (mAutoRecyleBitmap)
			recyleBitmapImpl(bitmap);
		mBitmap = bitmap;
		super.setImageDrawable(drawable);
	}

	@Override
	public void setImageResource(int resId)
	{
		super.setImageResource(resId);
		// setImageDrawable(getDrawable());
	}

	@Override
	public void setImageURI(Uri uri)
	{
		super.setImageURI(uri);
		setImageDrawable(getDrawable());
	}

	public Bitmap getBitmap()
	{
		return mBitmap;
	}

	public void recyleBitmapImage()
	{
		cancelImageLoad();
		setImageBitmap(null);
		recyleBitmapImpl(null);
		mLoadStatus = LoadStatus.UNLOAD;
	}

	private void recyleBitmapImpl(Bitmap newbm)
	{
		if (mBitmap != null && mBitmap != newbm)
		{
			if (!mBitmap.isRecycled())
			{
				mBitmap.recycle();
				LogUtil.v(TAG, "recyleBitmap: " + this);
			}
			mBitmap = null;
			setImageMatrix(null);
		}
	}

	// @Override
	// protected void onSizeChanged(int w, int h, int oldw, int oldh)
	// {
	// // LogUtil.v(TAG, "onSizeChanged: " + w + "; " + h + "; " + oldw + "; " + oldh);
	// super.onSizeChanged(w, h, oldw, oldh);
	// mLayouted = true;
	// mWidth = w;
	// mHeight = h;
	// if (mStartLoadCalled)
	// {
	// mStartLoadCalled = false;
	// startImageLoad();
	// }
	// }

	// @Override
	// protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	// {
	// super.onLayout(changed, left, top, right, bottom);
	// }

	@Override
	public void registerObserver(OnViewObserver observer)
	{
		mViewObservable.registerObserver(observer);
	}

	@Override
	public void unregisterObserver(OnViewObserver observer)
	{
		mViewObservable.unregisterObserver(observer);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		try
		{
			super.onDraw(canvas);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, this + " onDraw error", e);
			recyleBitmapImage();
			startImageLoad();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewObservable.notifyOnMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		mViewObservable.notifyOnLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mViewObservable.notifyOnSizeChanged(w, h, oldw, oldh);
		mLayouted = true;
		if (w > 0 && h > 0 && mStartLoadCalled)
		{
			mStartLoadCalled = false;
			startImageLoad();
		}
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		mViewObservable.clear();
		if (mAutoRecyleBitmap)
			recyleBitmapImage();
	}

	@Override
	public void setChecked(boolean checked)
	{
		mChecked = checked;
		setActivated(checked);
	}

	@Override
	public boolean isChecked()
	{
		return mChecked;
	}

	@Override
	public void toggle()
	{
		setChecked(!mChecked);
	}
}
