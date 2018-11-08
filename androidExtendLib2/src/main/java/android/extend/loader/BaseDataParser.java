package android.extend.loader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.extend.ErrorInfo;
import android.extend.loader.Loader.LoadParams;

public abstract class BaseDataParser extends BaseParser
{
	public BaseDataParser(Context context)
	{
		super(context);
	}

	@Override
	public void onParse(HttpResponse httpResponse, InputStream is, String url, String cacheKey, LoadParams params,
			DataFrom from)
	{
		try
		{
			byte[] data = readInStreamData(is, 10);
			if (mCanceled)
				return;
			onDataParse(data, url, cacheKey, params, from);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			notifyError(url, params, ErrorInfo.ERROR_IOEXCEPTION, null, e);
			return;
		}
	}

	public abstract void onDataParse(byte[] data, String url, String cacheKey, LoadParams params, DataFrom from);
}
