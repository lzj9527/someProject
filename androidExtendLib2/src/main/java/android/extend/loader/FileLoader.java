package android.extend.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.extend.ErrorInfo;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.util.LogUtil;

public class FileLoader extends Loader
{
	public static class FileLoadTask extends LoadTask<LoadParams>
	{
		public FileLoadTask(Context context, String url, LoadParams params, BaseParser parser, CacheMode cacheMode)
		{
			super(context, url, params, parser, cacheMode);
		}

		@Override
		protected void onLoad()
		{
			InputStream is = null;
			try
			{
				if (mUrl.startsWith(PROTOCOL_ASSETS))
				{
					String path = mUrl.substring(PROTOCOL_ASSETS.length());
					LogUtil.d(TAG, PROTOCOL_ASSETS + " find " + " path: " + path);
					is = mContext.getAssets().open(path);
					mParser.onParse(null, is, mUrl, null, mParams, DataFrom.ASSET);
				}
				else
				{
					File file = new File(mUrl);
					LogUtil.d(TAG, mUrl + " file.exists(): " + file.exists() + "; file.canRead(): " + file.canRead());
					is = new FileInputStream(file);
					mParser.onParse(null, is, mUrl, null, mParams, DataFrom.FILE);
				}
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
				notifyError(ErrorInfo.ERROR_SECURITYEXCEPTION, null, e);
				return;
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				notifyError(ErrorInfo.ERROR_FILENOTFOUNDEXCEPTION, null, e);
				return;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				notifyError(ErrorInfo.ERROR_EXCEPTION, null, e);
				return;
			}
			catch (OutOfMemoryError e)
			{
				e.printStackTrace();
				notifyError(ErrorInfo.ERROR_OUTOFMEMORY, null, e);
				return;
			}
			finally
			{
				if (is != null)
				{
					try
					{
						is.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static FileLoader mDefault;

	public static FileLoader getDefault()
	{
		if (mDefault == null)
		{
			mDefault = new FileLoader();
		}
		return mDefault;
	}

	public FileLoader()
	{
		super();
	}

	public FileLoader(int maxTaskCount)
	{
		super(maxTaskCount);
	}

	@Override
	FileLoadTask createLoadTask(Context context, String url, LoadParams params, BaseParser parser, CacheMode cacheMode)
	{
		return new FileLoadTask(context, url, params, parser, cacheMode);
	}
}
