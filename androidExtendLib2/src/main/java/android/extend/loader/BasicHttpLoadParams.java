package android.extend.loader;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;

import android.extend.BasicConfig;
import android.extend.loader.HttpLoader.HttpLoadParams;
import android.extend.util.HttpUtils;
import android.extend.util.LogUtil;
import android.text.TextUtils;

public class BasicHttpLoadParams extends HttpLoadParams
{
	public enum PostEntityFormat
	{
		UrlEncoded, Multipart,
	}

	protected boolean mIsPost;
	protected List<NameValuePair> mHeaders = new ArrayList<NameValuePair>();
	protected List<NameValuePair> mRequestParams = new ArrayList<NameValuePair>();
	protected String mGetUrl = null;
	protected PostEntityFormat mPostEntityFormat = PostEntityFormat.UrlEncoded;
	protected HttpEntity mPostEntity = null;

	public BasicHttpLoadParams(boolean isPost, List<NameValuePair> headers, List<NameValuePair> requestParams)
	{
		setRequestPost(isPost);
		addHeaders(headers);
		addRequestParams(requestParams);
	}

	public BasicHttpLoadParams(boolean isPost, List<NameValuePair> requestParams)
	{
		this(isPost, null, requestParams);
	}

	public BasicHttpLoadParams(boolean isPost)
	{
		this(isPost, null, null);
	}

	public void setRequestPost(boolean isPost)
	{
		mIsPost = isPost;
	}

	public void setPostEntityFormat(PostEntityFormat format)
	{
		mPostEntityFormat = format;
	}

	@Override
	public void addHeader(NameValuePair header)
	{
		if (header != null)
		{
			for (NameValuePair pair : mHeaders)
			{
				if (pair.getName().equals(header.getName()))
				{
					LogUtil.w(TAG, "the header name " + header.getName() + " has existed, exec remove.");
					mHeaders.remove(pair);
					break;
				}
			}
			mHeaders.add(header);
		}
	}

	public void addHeaders(Collection<NameValuePair> headers)
	{
		if (headers != null && !headers.isEmpty())
		{
			for (NameValuePair header : headers)
				addHeader(header);
			// mHeaders.addAll(headers);
		}
	}

	@Override
	public void addRequestParam(NameValuePair requestParam)
	{
		if (requestParam != null)
		{
			for (NameValuePair pair : mRequestParams)
			{
				if (pair.getName().equals(requestParam.getName()))
				{
					LogUtil.w(TAG, "the requestParam name " + requestParam.getName() + " has existed, exec remove.");
					mRequestParams.remove(pair);
					break;
				}
			}
			mRequestParams.add(requestParam);
		}
	}

	public void addRequestParams(Collection<NameValuePair> requestParams)
	{
		if (requestParams != null && !requestParams.isEmpty())
		{
			for (NameValuePair pair : requestParams)
				addRequestParam(pair);
			// mRequestParams.addAll(requestParams);
		}
	}

	@Override
	public boolean isPostRequest(String url)
	{
		return mIsPost;
	}

	@Override
	public List<Header> makeHeaders(String url)
	{
		if (mHeaders != null && !mHeaders.isEmpty())
		{
			List<Header> headers = new ArrayList<Header>();
			for (NameValuePair pair : mHeaders)
			{
				headers.add(new BasicHeader(pair.getName(), pair.getValue()));
			}
			return headers;
		}
		return null;
	}

	@Override
	public String makeGetUrl(String url)
	{
		if (TextUtils.isEmpty(mGetUrl))
		{
			if (!mIsPost && mRequestParams != null && !mRequestParams.isEmpty())
				mGetUrl = HttpUtils.makeHttpGetUrl(url, mRequestParams);
		}
		if (!TextUtils.isEmpty(mGetUrl))
			return mGetUrl;
		return url;
	}

	@Override
	public HttpEntity makePostData(String url)
	{
		if (mPostEntity == null)
		{
			if (mIsPost && mRequestParams != null && !mRequestParams.isEmpty())
			{
				switch (mPostEntityFormat)
				{
					case UrlEncoded:
						try
						{
							mPostEntity = new UrlEncodedFormEntity(mRequestParams, BasicConfig.DefaultEncoding);
						}
						catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
						break;
					case Multipart:
						MultipartEntity entity = new MultipartEntity();
						for (NameValuePair pair : mRequestParams)
						{
							try
							{
								entity.addPart(pair.getName(),
										new StringBody(pair.getValue(), Charset.forName(BasicConfig.DefaultEncoding)));
							}
							catch (UnsupportedEncodingException e)
							{
								e.printStackTrace();
							}
						}
						mPostEntity = entity;
						break;
				}
			}
		}
		return mPostEntity;
	}

	// @Override
	// public boolean containsHeader(String key)
	// {
	// if (mHeaders != null && !mHeaders.isEmpty())
	// {
	// for (NameValuePair pair : mHeaders)
	// {
	// if (pair.getName().equals(key))
	// return true;
	// }
	// }
	// return false;
	// }
}
