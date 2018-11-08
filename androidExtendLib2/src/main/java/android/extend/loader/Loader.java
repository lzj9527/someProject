package android.extend.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.extend.BasicConfig;
import android.extend.ErrorInfo;
import android.extend.cache.FileCacheManager;
import android.extend.util.LogUtil;
import android.text.TextUtils;

public abstract class Loader
{
	public static final String TAG = Loader.class.getSimpleName();

	public static final String PROTOCOL_HTTP = "http://";
	public static final String PROTOCOL_HTTPS = "https://";
	public static final String PROTOCOL_ASSETS = "file:///android_asset/";
	public static final String PROTOCOL_FILE = "file://";

	public static boolean isAssetUrl(String url)
	{
		if (TextUtils.isEmpty(url))
		{
			return false;
		}
		return url.startsWith(PROTOCOL_ASSETS);
	}

	public static boolean isFileUrl(String url)
	{
		if (TextUtils.isEmpty(url))
		{
			return false;
		}
		if (url.startsWith(PROTOCOL_ASSETS) || url.startsWith(PROTOCOL_HTTP) || url.startsWith(PROTOCOL_HTTPS))
		{
			return false;
		}
		return true;
	}

	public static String ensureFileUrl(String url)
	{
		if (!url.startsWith(PROTOCOL_FILE))
		{
			url = PROTOCOL_FILE + url;
		}
		return url;
	}

	public static boolean isHttpUrl(String url)
	{
		if (TextUtils.isEmpty(url))
		{
			return false;
		}
		return url.startsWith(PROTOCOL_HTTP) || url.startsWith(PROTOCOL_HTTPS);
	}

	public static String ensureHttpUrl(String url)
	{
		if (!url.startsWith(PROTOCOL_HTTP))
		{
			url = PROTOCOL_HTTP + url;
		}
		return url;
	}

	public static boolean isUrlString(String url)
	{
		return isAssetUrl(url) || isFileUrl(url) || isHttpUrl(url);
	}

	private static Integer mLock = Integer.valueOf(0);
	private static long mTaskID = 0;

	// public static long getNextID()
	// {
	// synchronized (mLock)
	// {
	// long taskID = mTaskID + 1;
	// if (taskID < 0)
	// {
	// taskID = 1;
	// }
	// return taskID;
	// }
	// }

	private static long getNextTaskID()
	{
		synchronized (mLock)
		{
			mTaskID++;
			if (mTaskID < 0)
			{
				mTaskID = 1;
			}
			return mTaskID;
		}
	}

	public static abstract class LoadParams
	{
		public final String TAG = getClass().getSimpleName();

		protected CacheMode mCacheMode = CacheMode.NO_CACHE;
		protected long mCacheTime = FileCacheManager.UNLIMITED_TIME;
		protected long mFileMTime = FileCacheManager.UNKNOW_MTIME;

		// 是否需要缓存
		public CacheMode getCacheMode()
		{
			return mCacheMode;
		}

		// 返回自定义的缓存文件
		public File getCacheFile(String url)
		{
			return null;
		}

		public void setCacheTime(long time)
		{
			mCacheTime = time;
		}

		// 返回自定义的缓存时间，以秒为单位
		public long getCacheTime()
		{
			return mCacheTime;
		}

		public void setFileMTime(long time)
		{
			mFileMTime = time;
		}

		// 返回文件最后更新时间
		public long getFileMTime()
		{
			return mFileMTime;
		}
	}

	public static abstract class LoadTask<T extends LoadParams> implements Runnable
	{
		public final String TAG = getClass().getSimpleName();
		protected final long mID = getNextTaskID();
		protected Context mContext;
		protected String mUrl;
		protected T mParams;
		protected BaseParser mParser;
		protected CacheMode mCacheMode;

		public LoadTask(Context context, String url, T params, BaseParser parser, CacheMode cacheMode)
		{
			mContext = context;
			mUrl = url;
			mParams = params;
			mParser = parser;
			mCacheMode = cacheMode;
		}

		protected boolean hasCanceled()
		{
			if (mParser.hasCanceled())
			{
				LogUtil.w(TAG, "the " + mUrl + " load hasCanceled...");
				return true;
			}
			return false;
		}

		protected synchronized void cancel()
		{
			try
			{
				mParser.cancel();
				// Thread.currentThread().interrupt();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void run()
		{
			try
			{
				if (hasCanceled())
				{
					return;
				}
				mParser.onStart();
				if (hasCanceled())
				{
					return;
				}
				onLoad();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				notifyError(ErrorInfo.ERROR_EXCEPTION, null, e);
				return;
			}
			finally
			{
				mParser.onFinish();
				mTaskList.remove(LoadTask.this);
			}
		}

		protected abstract void onLoad();

		protected void notifyError(int errorCode, String description, Throwable throwable)
		{
			if (hasCanceled())
			{
				return;
			}
			ErrorInfo error = new ErrorInfo();
			error.errorCode = errorCode;
			if (TextUtils.isEmpty(description))
			{
				error.description = throwable.getMessage();
			}
			else
			{
				error.description = description;
			}
			error.throwable = throwable;
			mParser.onError(mUrl, mParams, error);
		}
	}

	// public final String TAG = getClass().getSimpleName();
	private ExecutorService mExecutor;
	private static List<LoadTask<?>> mTaskList = Collections.synchronizedList(new ArrayList<LoadTask<?>>());

	Loader()
	{
		this(BasicConfig.LoaderMaxTaskCount);
	}

	Loader(int maxTaskCount)
	{
		mExecutor = Executors.newFixedThreadPool(maxTaskCount);
	}

	public static boolean cancel(long id)
	{
		if (id < 0)
		{
			return false;
		}
		synchronized (mTaskList)
		{
			for (LoadTask<?> task : mTaskList)
			{
				if (task.mID == id && !task.hasCanceled())
				{
					LogUtil.v(TAG, "cancel load id = " + id);
					task.cancel();
					return true;
				}
			}
		}
		return false;
	}

	public static boolean cancel(String url)
	{
		if (TextUtils.isEmpty(url))
		{
			return false;
		}
		synchronized (mTaskList)
		{
			for (LoadTask<?> task : mTaskList)
			{
				if (url.equals(task.mUrl) && !task.hasCanceled())
				{
					LogUtil.v(TAG, "cancel load url = " + url);
					task.cancel();
					return true;
				}
			}
		}
		return false;
	}

	public static void cancelAll(String url)
	{
		if (TextUtils.isEmpty(url))
		{
			return;
		}
		synchronized (mTaskList)
		{
			for (LoadTask<?> task : mTaskList)
			{
				if (url.equals(task.mUrl) && !task.hasCanceled())
				{
					task.cancel();
				}
			}
		}
	}

	public static void cancelAll(Context context)
	{
		if (context == null)
		{
			return;
		}
		synchronized (mTaskList)
		{
			for (LoadTask<?> task : mTaskList)
			{
				if (context == task.mContext && !task.hasCanceled())
				{
					task.cancel();
				}
			}
		}
	}

	public static void cancelAll()
	{
		synchronized (mTaskList)
		{
			for (LoadTask<?> task : mTaskList)
			{
				if (!task.hasCanceled())
				{
					task.cancel();
				}
			}
		}
	}

	public enum CacheMode
	{
		/** 不使用缓存 */
		NO_CACHE,
		/** 文件缓存优先，优先从文件缓存中读取数据，除非缓存数据已过期或数据有更新时才重新读取网络 */
		PERFER_FILECACHE,
		/**
		 * @deprecated Use {@link #PERFER_FILECACHE} instead.
		 */
		PERFER_CACHE,
		/** 网络优先，网络可用时优先从网络获取数据，失败后才从缓存读取数据 */
		PERFER_NETWORK,
		/** 只读文件缓存，无论缓存数据是否已过期或是否有更新均优先读取缓存，只有当缓存数据不存在时才读取网络 */
		FILECACHE_ONLY,
		/**
		 * @deprecated Use {@link #FILECACHE_ONLY} instead.
		 */
		CACHE_ONLY,
		/** 只读内存缓存，缓存数据保存在内存中，程序退出后清除 */
		MEMORYCACHE_ONLY,
		/**
		 * @deprecated Use {@link #MEMORYCACHE_ONLY} instead.
		 */
		MEMORY_CACHE,
		/** 存在内存缓存则优先读取内存，如没有则优先读取网络，失败后则从文件缓存中读取 */
		PERFER_MEMORY_OR_NETWORK,
		/** 存在内存缓存则优先读取内存，如没有则优先读取文件缓存，再没有则读取网络 */
		PERFER_MEMORY_OR_FILE,
		/**
		 * @deprecated Use {@link #PERFER_MEMORY_OR_FILE} instead.
		 */
		PERFER_MEMORY_OR_CACHE,
	}

	abstract LoadTask<?> createLoadTask(Context context, String url, LoadParams params, BaseParser parser,
			CacheMode cacheMode);

	public long startLoad(Context context, String url, LoadParams params, BaseParser parser, CacheMode cacheMode)
	{
		if (mExecutor.isShutdown())
		{
			throw new IllegalStateException("the executor has shutdown!!!");
		}
		if (context == null || TextUtils.isEmpty(url) || parser == null)
		{
			throw new NullPointerException();
		}
		LogUtil.d(TAG, this + " startLoad: " + context + " " + url + " " + params + " " + parser + " " + cacheMode);
		LoadTask<?> task = createLoadTask(context, url, params, parser, cacheMode);
		if (task == null)
		{
			return -1;
		}
		parser.mTaskId = task.mID;
		mTaskList.add(task);
		mExecutor.execute(task);
		return task.mID;
	}

	/**
	 * 销毁Loader，关闭线程池
	 * 
	 * 注意：已经销毁的Loader不能再次使用，必须重新生成新Loader
	 */
	public void destory()
	{
		mExecutor.shutdownNow();
		LogUtil.w(TAG, this + " destoryed!");
	}
}
