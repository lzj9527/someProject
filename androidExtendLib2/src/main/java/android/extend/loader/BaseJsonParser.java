package android.extend.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.extend.BasicConfig;
import android.extend.ErrorInfo;
import android.extend.loader.Loader.LoadParams;
import android.extend.util.DataUtils;

import com.google.gson.JsonParseException;

public abstract class BaseJsonParser extends BaseParser
{
	public BaseJsonParser(Context context)
	{
		super(context);
	}

	@Override
	public void onParse(HttpResponse httpResponse, InputStream is, String url, String cacheKey, LoadParams params,
			DataFrom from)
	{
		try
		{
			String json = DataUtils.readString(is, BasicConfig.DefaultEncoding);
			onJsonParse(json, url, cacheKey, params, from);
		}
		catch (JsonParseException e)
		{
			e.printStackTrace();
			notifyError(url, params, ErrorInfo.ERROR_JSONEXCEPTION, null, e);
			return;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			notifyError(url, params, ErrorInfo.ERROR_UNSUPPORTEDENCODING, null, e);
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			notifyError(url, params, ErrorInfo.ERROR_IOEXCEPTION, null, e);
			return;
		}
	}

	public abstract void onJsonParse(String json, String url, String cacheKey, LoadParams params, DataFrom from);
}
