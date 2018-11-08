package android.extend.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.annotation.TargetApi;
import android.content.Context;
import android.extend.BasicConfig;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;

public class HttpUtils
{
	public interface OnHttpListener
	{
		public void onHttpError(Context context, String url, Exception exception);

		public void onHttpResponse(Context context, String url, HttpResponse response);
	}

	public static final String TAG = "HttpUtils";

	private static DefaultHttpClient mHttpClient = null;

	// private static class MyRedirectHandler implements RedirectHandler
	// {
	// private Header mLocation = null;
	//
	// @Override
	// public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException
	// {
	// if (mLocation != null)
	// {
	// String url = mLocation.getValue();
	// LogUtil.d(TAG, "Redirect to: " + url);
	// return URI.create(url);
	// }
	// return null;
	// }
	//
	// @Override
	// public boolean isRedirectRequested(HttpResponse response, HttpContext context)
	// {
	// int statusCode = response.getStatusLine().getStatusCode();
	// LogUtil.v(TAG, "isRedirectRequested: statusCode = " + statusCode);
	// switch (statusCode)
	// {
	// case HttpStatus.SC_MOVED_TEMPORARILY:
	// case HttpStatus.SC_MOVED_PERMANENTLY:
	// case HttpStatus.SC_SEE_OTHER:
	// case HttpStatus.SC_TEMPORARY_REDIRECT:
	// mLocation = response.getFirstHeader("Location");
	// LogUtil.d(TAG, "getLocationHeader: " + mLocation);
	// if (mLocation != null)
	// {
	// // TODO
	// return true;
	// }
	// break;
	// }
	// return false;
	// }
	// }

	private static class MyRetryHandler implements HttpRequestRetryHandler
	{
		private int mRetryCount = 0;

		public MyRetryHandler(int count)
		{
			mRetryCount = count;
		}

		@Override
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
		{
			LogUtil.v(TAG, "retryRequest: " + executionCount);
			exception.printStackTrace();
			if (executionCount < mRetryCount)
			{
				return true;
			}
			return false;
		}
	}

	public static final DefaultHttpClient createHttpClient()
	{
		HttpParams params = new BasicHttpParams();
		// 最大链接数
		ConnManagerParams.setMaxTotalConnections(params, BasicConfig.HttpMaxTotalConnections);
		// 单个路由最大链接数
		ConnManagerParams.setMaxConnectionsPerRoute(params,
				new ConnPerRouteBean(BasicConfig.HttpMaxConnectionsPerRoute));
		// 链接等待超时
		ConnManagerParams.setTimeout(params, BasicConfig.HttpTimeout);
		// 链接超时
		HttpConnectionParams.setConnectionTimeout(params, BasicConfig.HttpConnectionTimeout);
		// 读取超时
		HttpConnectionParams.setSoTimeout(params, BasicConfig.HttpSoTimeout);
		// HTTP版本
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		// HTTP编码
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		// HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		try
		{
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sslfactory = new SSLSocketFactoryImpl(trustStore);
			schemeRegistry.register(new Scheme("https", sslfactory, 443));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		}

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, schemeRegistry);

		DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
		// 重试次数
		httpClient.setHttpRequestRetryHandler(new MyRetryHandler(BasicConfig.HttpRequestRetryCount));

		return httpClient;
	}

	public static final DefaultHttpClient getHttpClient()
	{
		if (mHttpClient == null)
		{
			mHttpClient = createHttpClient();
		}
		return mHttpClient;
	}

	private static final void setMaxTotalConnections(HttpClient httpClient, int maxTotalConnections)
	{
		if (maxTotalConnections > 0)
		{
			LogUtil.d(TAG, "setMaxTotalConnections: " + maxTotalConnections);
			ConnManagerParams.setMaxTotalConnections(httpClient.getParams(), maxTotalConnections);
		}
	}

	public static final void setMaxTotalConnections(int maxTotalConnections)
	{
		setMaxTotalConnections(getHttpClient(), maxTotalConnections);
	}

	private static final void setMaxConnectionsPerRoute(HttpClient httpClient, int maxConnectionsPerRoute)
	{
		if (maxConnectionsPerRoute > 0)
		{
			LogUtil.d(TAG, "setMaxConnectionsPerRoute: " + maxConnectionsPerRoute);
			ConnManagerParams.setMaxConnectionsPerRoute(httpClient.getParams(), new ConnPerRouteBean(
					maxConnectionsPerRoute));
		}
	}

	public static final void setMaxConnectionsPerRoute(int maxConnectionsPerRoute)
	{
		setMaxConnectionsPerRoute(getHttpClient(), maxConnectionsPerRoute);
	}

	private static final void setTimeout(HttpClient httpClient, long timeout)
	{
		if (timeout > 0)
		{
			LogUtil.d(TAG, "setTimeout: " + timeout);
			ConnManagerParams.setTimeout(httpClient.getParams(), timeout);
		}
	}

	private static final void setConnectionTimeout(HttpClient httpClient, int timeout)
	{
		if (timeout > 0)
		{
			LogUtil.d(TAG, "setConnectionTimeout: " + timeout);
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);
		}
	}

	private static final void setSoTimeout(HttpClient httpClient, int timeout)
	{
		if (timeout > 0)
		{
			LogUtil.d(TAG, "setSoTimeout: " + timeout);
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);
		}
	}

	public static final void setTimeout(long timeout)
	{
		setTimeout(getHttpClient(), timeout);
	}

	public static final void setConnectionTimeout(int timeout)
	{
		setConnectionTimeout(getHttpClient(), timeout);
	}

	public static final void setSoTimeout(int timeout)
	{
		setSoTimeout(getHttpClient(), timeout);
	}

	private static final void setRequestRetryCount(DefaultHttpClient httpClient, int count)
	{
		LogUtil.d(TAG, "setRequestRetryCount: " + count);
		httpClient.setHttpRequestRetryHandler(new MyRetryHandler(count));
	}

	public static final void setRequestRetryCount(int count)
	{
		setRequestRetryCount(getHttpClient(), count);
	}

	public static final void releaseInvalidConnections()
	{
		ClientConnectionManager ccm = getHttpClient().getConnectionManager();
		if (ccm != null)
		{
			ccm.closeExpiredConnections();
			ccm.closeIdleConnections(0, TimeUnit.MILLISECONDS);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static final HttpResponse doHttpRequest(Context context, String method, String url, HttpEntity entity,
												   List<Header> headers, HttpHost proxy) throws ClientProtocolException, SocketTimeoutException,
			UnknownHostException, SocketException, IOException, Exception
	{
		LogUtil.d(TAG, "doHttpRequest: " + context + " " + method + " " + url + " " + entity + " " + headers + " "
				+ proxy);
		releaseInvalidConnections();
		boolean isPost = false;
		if (!TextUtils.isEmpty(method) && method.equalsIgnoreCase(HttpPost.METHOD_NAME))
		{
			isPost = true;
		}
		HttpRequestBase httpRequest;
		if (isPost)
		{
			HttpPost httpPost = new HttpPost(url);
			if (entity != null)
			{
				httpPost.setEntity(entity);
			}
			httpRequest = httpPost;
		}
		else
		{
			HttpGet httpGet = new HttpGet(url);
			httpRequest = httpGet;
		}
		boolean hasUserAgent = false;
		if (headers != null && !headers.isEmpty())
		{
			for (Header header : headers)
			{
				LogUtil.v(TAG, "Header: " + header.getName() + " " + header.getValue());
				httpRequest.addHeader(header);
				if (header.getName().equals("User-Agent"))
					hasUserAgent = true;
			}
		}
		if (!hasUserAgent)
			httpRequest.addHeader("User-Agent", WebSettings.getDefaultUserAgent(context));

		HttpClient httpClient = getHttpClient();
		HttpHost defaultProxy = NetworkManager.getDefaultProxy(context);
		if (defaultProxy != null)
		{
			proxy = defaultProxy;
		}
		LogUtil.d(TAG, "doHttpRequest proxy = " + proxy);
		// HttpParams params = httpClient.getParams();
		HttpParams params = httpRequest.getParams();
		if (proxy != null)
		{
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		else
		{
			params.removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
		params.setParameter("Connection", "close");
		httpRequest.setParams(params);
		HttpResponse response = httpClient.execute(httpRequest);
		return response;
	}

	public static final void doHttpRequestAsync(final Context context, final String method, final String url,
			final HttpEntity entity, final List<Header> headers, final HttpHost proxy, final OnHttpListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					HttpResponse response = doHttpRequest(context, method, url, entity, headers, proxy);
					if (listener != null)
					{
						listener.onHttpResponse(context, url, response);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (listener != null)
					{
						listener.onHttpError(context, url, e);
					}
				}
			}
		}).start();
	}

	public static final InputStream getHttpContent(HttpResponse response) throws IllegalStateException, IOException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		LogUtil.w(TAG, "the " + response + " statusCode = " + statusCode);
		switch (statusCode)
		{
			case HttpStatus.SC_OK:
			case HttpStatus.SC_PARTIAL_CONTENT:
				return response.getEntity().getContent();
			default:
				throw new IllegalStateException();
		}
	}

	public static final HttpResponse doHttpGet(Context context, String url, List<Header> headers, HttpHost proxy)
			throws SocketTimeoutException, ClientProtocolException, UnknownHostException, SocketException, IOException,
			Exception
	{
		return doHttpRequest(context, HttpGet.METHOD_NAME, url, null, headers, proxy);
	}

	public static final void doHttpGetAsync(final Context context, final String url, final List<Header> headers,
			final HttpHost proxy, final OnHttpListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					HttpResponse response = doHttpGet(context, url, headers, proxy);
					if (listener != null)
					{
						listener.onHttpResponse(context, url, response);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (listener != null)
					{
						listener.onHttpError(context, url, e);
					}
				}
			}
		}).start();
	}

	public static final HttpResponse doHttpGet(Context context, String baseUrl, List<NameValuePair> params,
			List<Header> headers, HttpHost proxy) throws SocketTimeoutException, ClientProtocolException,
			UnknownHostException, SocketException, IOException, Exception
	{
		String url = makeHttpGetUrl(baseUrl, params);
		return doHttpGet(context, url, headers, proxy);
	}

	public static final void doHttpGetAsync(final Context context, final String baseUrl,
			final List<NameValuePair> params, final List<Header> headers, final HttpHost proxy,
			final OnHttpListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					HttpResponse response = doHttpGet(context, baseUrl, params, headers, proxy);
					if (listener != null)
					{
						listener.onHttpResponse(context, baseUrl, response);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (listener != null)
					{
						listener.onHttpError(context, baseUrl, e);
					}
				}
			}
		}).start();
	}

	public static final String makeHttpGetUrl(String baseUrl, List<NameValuePair> pairs)
	{
		if (pairs == null || pairs.isEmpty())
			return baseUrl;
		StringBuffer sb = new StringBuffer();
		sb.append(baseUrl);
		boolean firstNeedAndChar = false;
		if (baseUrl.lastIndexOf('?') == -1)
		{
			sb.append("?");
		}
		else if (!baseUrl.endsWith("?"))
		{
			if (!baseUrl.endsWith("&"))
			{
				firstNeedAndChar = true;
			}
		}

		int count = 0;
		for (NameValuePair pair : pairs)
		{
			if (firstNeedAndChar && count == 0)
			{
				sb.append('&');
			}
			if (count > 0)
			{
				sb.append('&');
			}
			sb.append(pair.getName()).append('=').append(pair.getValue());
			count++;
		}
		String result = sb.toString();
		LogUtil.d(TAG, "makeHttpGetUrl: " + result);
		return result;
	}

	public static final HttpResponse doHttpPost(Context context, String url, HttpEntity entity, List<Header> headers,
			HttpHost proxy) throws SocketTimeoutException, ClientProtocolException, UnknownHostException,
			SocketException, IOException, Exception
	{
		return doHttpRequest(context, HttpPost.METHOD_NAME, url, entity, headers, proxy);
	}

	public static final void doHttpPostAsync(final Context context, final String url, final HttpEntity entity,
			final List<Header> headers, final HttpHost proxy, final OnHttpListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					HttpResponse response = doHttpPost(context, url, entity, headers, proxy);
					if (listener != null)
					{
						listener.onHttpResponse(context, url, response);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (listener != null)
					{
						listener.onHttpError(context, url, e);
					}
				}
			}
		}).start();
	}

	public static final HttpResponse doHttpPost(Context context, String url, List<NameValuePair> params,
			List<Header> headers, HttpHost proxy) throws UnsupportedEncodingException, SocketTimeoutException,
			ClientProtocolException, UnknownHostException, SocketException, IOException, Exception
	{
		HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		return doHttpPost(context, url, entity, headers, proxy);
	}

	public static final void doHttpPostAsync(final Context context, final String url, final List<NameValuePair> params,
			final List<Header> headers, final HttpHost proxy, final OnHttpListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					HttpResponse response = doHttpPost(context, url, params, headers, proxy);
					if (listener != null)
					{
						listener.onHttpResponse(context, url, response);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (listener != null)
					{
						listener.onHttpError(context, url, e);
					}
				}
			}
		}).start();
	}
}
