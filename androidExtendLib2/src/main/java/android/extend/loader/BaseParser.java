package android.extend.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.extend.ErrorInfo;
import android.extend.loader.Loader.LoadParams;
import android.extend.util.DataUtils;
import android.text.TextUtils;

public abstract class BaseParser
{
	public enum DataFrom
	{
		SERVER, FILE, CACHE, ASSET,
	}

	public final String TAG = getClass().getSimpleName();
	protected long mTaskId;
	protected Context mContext;
	protected boolean mCanceled = false;

	public BaseParser(Context context)
	{
		mContext = context;
	}

	public long getTaskId()
	{
		return mTaskId;
	}

	public synchronized void cancel()
	{
		mCanceled = true;
	}

	public synchronized boolean hasCanceled()
	{
		return mCanceled;
	}

	// // 是否需要缓存
	// public boolean needCache()
	// {
	// return false;
	// }
	//
	// // 返回自定义的缓存文件
	// public File getCacheFile(String url)
	// {
	// return null;
	// }
	//
	// // 返回自定义的缓存时间，以秒为单位
	// public long getCacheTime()
	// {
	// return FileCacheManager.UNLIMITED_TIME;
	// }
	//
	// // 返回文件最后更新时间
	// public long getFileMTime()
	// {
	// return FileCacheManager.UNKNOW_MTIME;
	// }

	protected void notifyError(String url, LoadParams params, int errorCode, String description, Throwable throwable)
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
		onError(url, params, error);
	}

	protected byte[] readInStreamData(InputStream is, int bufferKBSize) throws IOException
	{
		if (bufferKBSize <= 0)
		{
			bufferKBSize = 10;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			byte[] buffer = new byte[bufferKBSize * DataUtils.KB];
			int readCount;
			while ((readCount = is.read(buffer)) != -1)
			{
				if (mCanceled)
				{
					return null;
				}
				baos.write(buffer, 0, readCount);
			}
			byte[] data = baos.toByteArray();
			return data;
		}
		finally
		{
			if (baos != null)
			{
				baos.close();
			}
		}
	}

	public void onStart()
	{
	}

	public abstract void onParse(HttpResponse httpResponse, InputStream is, String url, String cacheKey,
			LoadParams params, DataFrom from);

	public abstract void onError(String url, LoadParams params, ErrorInfo error);

	public void onFinish()
	{
	}
}
