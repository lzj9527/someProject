package android.extend.loader;

import android.content.Context;
import android.extend.loader.HttpLoader.HttpLoadParams;

public class UrlLoader extends Loader
{
	private static UrlLoader mDefault;

	public static UrlLoader getDefault()
	{
		if (mDefault == null)
		{
			mDefault = new UrlLoader();
		}
		return mDefault;
	}

	public UrlLoader()
	{
		super();
	}

	public UrlLoader(int maxTaskCount)
	{
		super(maxTaskCount);
	}

	@Override
	LoadTask<?> createLoadTask(Context context, String url, LoadParams params, BaseParser parser, CacheMode cacheMode)
	{
		if (isHttpUrl(url))
		{
			return new HttpLoader.HttpLoadTask(context, url, (HttpLoadParams)params, parser, cacheMode);
		}
		else if (isAssetUrl(url) || isFileUrl(url))
		{
			return new FileLoader.FileLoadTask(context, url, params, parser, cacheMode);
		}
		else
		{
			// throw new IllegalArgumentException(url + " unsupported protocol!!!");
			return new FileLoader.FileLoadTask(context, url, params, parser, cacheMode);
		}
		// return null;
	}
}
