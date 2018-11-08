package android.extend.loader;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;

import android.extend.BasicConfig;
import android.text.TextUtils;

public class HttpPostStringParams extends BasicHttpLoadParams
{
	protected String mPostContent;

	public HttpPostStringParams(List<NameValuePair> headers, String postContent)
	{
		super(true, headers, null);
		mPostContent = postContent;
	}

	public HttpPostStringParams(List<NameValuePair> headers)
	{
		this(headers, null);
	}

	public HttpPostStringParams(String postContent)
	{
		this(null, postContent);
	}

	@Override
	public HttpEntity makePostData(String url)
	{
		if (mPostEntity == null && !TextUtils.isEmpty(mPostContent))
		{
			try
			{
				mPostEntity = new StringEntity(mPostContent, BasicConfig.DefaultEncoding);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		return mPostEntity;
	}
}
