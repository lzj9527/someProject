package android.extend.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import android.extend.BasicConfig;
import android.extend.util.LogUtil;

public class HttpFileUploadParams extends BasicHttpLoadParams
{
	private List<NameValuePair> mUploadFilePairs = new ArrayList<NameValuePair>();

	public HttpFileUploadParams(List<NameValuePair> headers, List<NameValuePair> requestParams, String uploadPartName,
			String filePath)
	{
		super(true, headers, requestParams);
		addUploadFile(uploadPartName, filePath);
	}

	public HttpFileUploadParams(List<NameValuePair> headers, List<NameValuePair> requestParams)
	{
		super(true, headers, requestParams);
	}

	public HttpFileUploadParams(List<NameValuePair> requestParams, String uploadPartName, String filePath)
	{
		super(true, requestParams);
		addUploadFile(uploadPartName, filePath);
	}

	public HttpFileUploadParams(List<NameValuePair> requestParams)
	{
		super(true, requestParams);
	}

	public HttpFileUploadParams(String uploadPartName, String filePath)
	{
		super(true);
		addUploadFile(uploadPartName, filePath);
	}

	public void addUploadFile(String uploadPartName, String filePath)
	{
		mUploadFilePairs.add(new BasicNameValuePair(uploadPartName, filePath));
	}

	@Override
	public HttpEntity makePostData(String url)
	{
		if (mPostEntity == null)
		{
			MultipartEntity entity = new MultipartEntity();
			if (mRequestParams != null && !mRequestParams.isEmpty())
			{
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
			}
			if (!mUploadFilePairs.isEmpty())
			{
				for (NameValuePair pair : mUploadFilePairs)
				{
					File file = new File(pair.getValue());
					if (file.exists())
					{
						LogUtil.v(TAG, "find upload file: name=" + pair.getName() + "; path=" + pair.getValue());
						entity.addPart(pair.getName(), new FileBody(file));
					}
					else
						LogUtil.w(TAG, pair.getValue() + " not exists", new FileNotFoundException());
				}
			}
			mPostEntity = entity;
		}
		return mPostEntity;
	}
}
