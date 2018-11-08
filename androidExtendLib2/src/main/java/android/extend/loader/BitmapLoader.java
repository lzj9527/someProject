package android.extend.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.extend.BasicConfig;
import android.extend.ErrorInfo;
import android.extend.cache.BitmapCacheManager;
import android.extend.cache.FileCacheManager;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.loader.Loader.CacheMode;
import android.extend.loader.Loader.LoadParams;
import android.extend.util.BitmapUtils;
import android.extend.util.LogUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class BitmapLoader
{
	public enum DecodeMode
	{
		NONE,
		/**
		 * 适应缩放比例
		 * */
		FIT_SCALE,
		/**
		 * 自适应宽度
		 * */
		FIT_WIDTH,
		/**
		 * 自适应高度
		 * */
		FIT_HEIGHT,
		/**
		 * 自适应矩形
		 * */
		FIT_RECT,
		/**
		 * 以宽度为基准，固定缩放成方形
		 * */
		FIX_SQUARE_IN_WIDTH,
		/**
		 * 以高度为基准，固定缩放成方形
		 * */
		FIX_SQUARE_IN_HEIGHT,
		/**
		 * 固定宽高
		 * */
		FIX_XY,
	}

	public interface IDecodeParams
	{
		DecodeMode getDecodeMode();

		float getScale();

		int getOutWidth();

		int getOutHeight();
	}

	public static class BitmapLoadParams extends BasicHttpLoadParams implements IDecodeParams
	{
		private IDecodeParams mDecodeParams;

		public BitmapLoadParams()
		{
			super(false);
		}

		public BitmapLoadParams(IDecodeParams decodeParams)
		{
			super(false);
			mDecodeParams = decodeParams;
		}

		@Override
		public DecodeMode getDecodeMode()
		{
			if (mDecodeParams != null)
				return mDecodeParams.getDecodeMode();
			return null;
		}

		@Override
		public float getScale()
		{
			if (mDecodeParams != null)
				return mDecodeParams.getScale();
			return 1;
		}

		@Override
		public int getOutWidth()
		{
			if (mDecodeParams != null)
				return mDecodeParams.getOutWidth();
			return 0;
		}

		@Override
		public int getOutHeight()
		{
			if (mDecodeParams != null)
				return mDecodeParams.getOutHeight();
			return 0;
		}
	}

	public interface OnBitmapLoadListener
	{
		public void onLoadStarted(Object tag, String url);

		public void onLoadFinished(Object tag, String url, Bitmap bitmap, DataFrom from);

		public void onLoadCanceled(Object tag, String url);

		public void onLoadFailed(Object tag, String url, ErrorInfo error);
	}

	public static final String TAG = BitmapLoader.class.getSimpleName();

	private static BitmapLoader mDefault;

	public static BitmapLoader getDefault()
	{
		if (mDefault == null)
		{
			mDefault = new BitmapLoader();
		}
		return mDefault;
	}

	private UrlLoader mLoader = null;
	private List<BitmapParser> mParserList = Collections.synchronizedList(new ArrayList<BitmapParser>());

	public BitmapLoader()
	{
		this(BasicConfig.BitmapLoaderMaxTaskCount);
	}

	public BitmapLoader(int maxTaskCount)
	{
		mLoader = new UrlLoader(maxTaskCount);
	}

	public long startLoad(Object tag, Context context, String url, long imageMTime, OnBitmapLoadListener listener,
			IDecodeParams decodeParams, boolean useBitmapCache)
	{
		BitmapLoadParams params = new BitmapLoadParams(decodeParams);
		params.mFileMTime = imageMTime;
		return startLoad(tag, context, url, params, listener, useBitmapCache);
	}

	public long startLoad(Object tag, Context context, String url, BitmapLoadParams params,
			OnBitmapLoadListener listener, boolean useBitmapCache)
	{
		if (context == null || TextUtils.isEmpty(url))
		{
			throw new NullPointerException();
		}
		LogUtil.d(TAG, "startLoad: " + context + "; " + url + "; " + params + "; " + listener + "; " + useBitmapCache);
		if (useBitmapCache)
		{
			Bitmap bitmap = BitmapCacheManager.get(url);
			if (bitmap != null && listener != null)
			{
				listener.onLoadStarted(tag, url);
				listener.onLoadFinished(tag, url, bitmap, DataFrom.CACHE);
				return -1;
			}
		}
		if (listener != null)
		{
			listener.onLoadStarted(tag, url);
		}
		BitmapParser parser = new BitmapParser(context, tag, url, listener, params, useBitmapCache);
		mParserList.add(parser);
		long id = mLoader.startLoad(context, url, params, parser, CacheMode.PERFER_CACHE);
		return id;
	}

	// public long startLoad(Object tag, Context context, String url, LoadParams params, OnBitmapLoadListener listener,
	// boolean useBitmapCache)
	// {
	// return startLoad(tag, context, url, params, listener, null, useBitmapCache);
	// }
	//
	// public long startLoad(Object tag, Context context, String url, LoadParams params, OnBitmapLoadListener listener)
	// {
	// return startLoad(tag, context, url, params, listener, null, true);
	// }

	@SuppressWarnings("static-access")
	public boolean cancel(long id)
	{
		BitmapParser parser = removeParser(id);
		if (parser != null)
		{
			return mLoader.cancel(id);
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public boolean cancel(String url)
	{
		BitmapParser parser = removeParser(url);
		if (parser != null)
		{
			return mLoader.cancel(parser.getTaskId());
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public boolean cancel(Object tag)
	{
		BitmapParser parser = removeParser(tag);
		if (parser != null)
		{
			return mLoader.cancel(parser.getTaskId());
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public void cancelAll()
	{
		synchronized (mParserList)
		{
			for (BitmapParser parser : mParserList)
			{
				mLoader.cancel(parser.getTaskId());
			}
		}
		mParserList.clear();
	}

	private BitmapParser removeParser(Object tag)
	{
		LogUtil.v(TAG, "removeParser: tag=" + tag);
		if (tag == null)
		{
			return null;
		}
		synchronized (mParserList)
		{
			int size = mParserList.size();
			for (int i = 0; i < size; i++)
			{
				BitmapParser parser = mParserList.get(i);
				if (tag.equals(parser.mTag))
				{
					return mParserList.remove(i);
				}
			}
		}
		return null;
	}

	private BitmapParser removeParser(String url)
	{
		LogUtil.v(TAG, "removeParser: url=" + url);
		if (TextUtils.isEmpty(url))
		{
			return null;
		}
		synchronized (mParserList)
		{
			int size = mParserList.size();
			for (int i = 0; i < size; i++)
			{
				BitmapParser parser = mParserList.get(i);
				if (parser.mUrl.equals(url))
				{
					return mParserList.remove(i);
				}
			}
		}
		return null;
	}

	private BitmapParser removeParser(long id)
	{
		LogUtil.v(TAG, "removeParser: id=" + id);
		synchronized (mParserList)
		{
			int size = mParserList.size();
			for (int i = 0; i < size; i++)
			{
				BitmapParser parser = mParserList.get(i);
				if (parser.getTaskId() == id)
				{
					return mParserList.remove(i);
				}
			}
		}
		return null;
	}

	public void destory()
	{
		cancelAll();
		mLoader.destory();
	}

	private static Bitmap decodeBitmapFromFile(String path, IDecodeParams params)
	{
		Bitmap bitmap = null;
		if (params != null)
		{
			switch (params.getDecodeMode())
			{
				case FIT_SCALE:
					bitmap = BitmapUtils.decodeBitmap(path, params.getScale());
					break;
				case FIT_WIDTH:
					bitmap = BitmapUtils.decodeBitmapFitWidth(path, params.getOutWidth());
					break;
				case FIT_HEIGHT:
					bitmap = BitmapUtils.decodeBitmapFitHeight(path, params.getOutHeight());
					break;
				case FIT_RECT:
					bitmap = BitmapUtils.decodeBitmapFitRect(path, params.getOutWidth(), params.getOutHeight());
					break;
				case FIX_SQUARE_IN_WIDTH:
					bitmap = BitmapUtils.decodeBitmap(path, params.getOutWidth(), params.getOutWidth());
					break;
				case FIX_SQUARE_IN_HEIGHT:
					bitmap = BitmapUtils.decodeBitmap(path, params.getOutHeight(), params.getOutHeight());
					break;
				case FIX_XY:
					bitmap = BitmapUtils.decodeBitmap(path, params.getOutWidth(), params.getOutHeight());
					break;
				default:
					bitmap = BitmapFactory.decodeFile(path);
					break;
			}
		}
		else
		{
			bitmap = BitmapFactory.decodeFile(path);
		}
		return bitmap;
	}

	private static Bitmap decodeBitmapFromByteArray(byte[] data, IDecodeParams params) throws IOException
	{
		if (data == null)
		{
			return null;
		}
		Bitmap bitmap = null;
		if (params != null)
		{
			switch (params.getDecodeMode())
			{
				case FIT_SCALE:
					bitmap = BitmapUtils.decodeBitmap(data, params.getScale());
					break;
				case FIT_WIDTH:
					bitmap = BitmapUtils.decodeBitmapFitWidth(data, params.getOutWidth());
					break;
				case FIT_HEIGHT:
					bitmap = BitmapUtils.decodeBitmapFitHeight(data, params.getOutHeight());
					break;
				case FIT_RECT:
					bitmap = BitmapUtils.decodeBitmapFitRect(data, params.getOutWidth(), params.getOutHeight());
					break;
				case FIX_SQUARE_IN_WIDTH:
					bitmap = BitmapUtils.decodeBitmap(data, params.getOutWidth(), params.getOutWidth());
					break;
				case FIX_SQUARE_IN_HEIGHT:
					bitmap = BitmapUtils.decodeBitmap(data, params.getOutHeight(), params.getOutHeight());
					break;
				case FIX_XY:
					bitmap = BitmapUtils.decodeBitmap(data, params.getOutWidth(), params.getOutHeight());
					break;
				default:
					bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					break;
			}
			data = null;
		}
		else
		{
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		return bitmap;
	}

	private class BitmapParser extends BaseParser
	{
		Object mTag;
		String mUrl;
		OnBitmapLoadListener mListener;
		IDecodeParams mDecodeParams;
		boolean mUsecache;

		public BitmapParser(Context context, Object tag, String url, OnBitmapLoadListener listener,
				IDecodeParams decodeParams, boolean useCache)
		{
			super(context);
			mTag = tag;
			mUrl = url;
			mListener = listener;
			mDecodeParams = decodeParams;
			mUsecache = useCache;
		}

		@Override
		public void cancel()
		{
			super.cancel();
			if (mListener != null)
			{
				mListener.onLoadCanceled(mTag, mUrl);
			}
		}

		@Override
		public void onFinish()
		{
			mParserList.remove(this);
		}

		@Override
		public void onParse(HttpResponse httpResponse, InputStream is, String url, String cacheKey, LoadParams params,
				DataFrom from)
		{
			if (mCanceled)
			{
				return;
			}
			Bitmap bitmap = null;
			try
			{
				switch (from)
				{
					case SERVER:
						byte[] data = readInStreamData(is, 10);
						bitmap = decodeBitmapFromByteArray(data, mDecodeParams);
						data = null;
						break;
					case FILE:
						bitmap = decodeBitmapFromFile(url, mDecodeParams);
						break;
					case CACHE:
						is.close();
						String path = FileCacheManager.getCachedFilePath(mContext, cacheKey);
						bitmap = decodeBitmapFromFile(path, mDecodeParams);
						break;
					case ASSET:
						break;
				}
				if (bitmap == null)
				{
					if (mCanceled)
						return;
					if (is != null)
						bitmap = BitmapFactory.decodeStream(is);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				notifyError(url, params, ErrorInfo.ERROR_IOEXCEPTION, e.getMessage(), e);
				return;
			}
			if (mUsecache)
			{
				BitmapCacheManager.put(url, bitmap);
			}
			else if (mCanceled)
			{
				if (!bitmap.isRecycled())
				{
					bitmap.recycle();
				}
			}
			if (!mCanceled && mListener != null)
			{
				mListener.onLoadFinished(mTag, url, bitmap, from);
			}
		}

		@Override
		public void onError(String url, LoadParams params, ErrorInfo error)
		{
			LogUtil.w(TAG, "onError: " + url + " " + params + "\n" + error.toString());
			if (!mCanceled && mListener != null)
			{
				mListener.onLoadFailed(mTag, url, error);
			}
		}
	}
}
