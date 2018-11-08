package android.extend.loader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.content.Context;
import android.extend.ErrorInfo;
import android.extend.cache.FileCacheManager;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.util.DataUtils;
import android.extend.util.HttpUtils;
import android.extend.util.LogUtil;
import android.extend.util.NetworkManager;
import android.net.NetworkInfo;

public class HttpLoader extends Loader
{
	// public static final String TAG = HttpLoader.class.getSimpleName();

	public static final String NAME_CONTENT_TYPE = "Content-Type";
	public static final String NAME_CONTENT_LANGUAGE = "Content-Language";
	public static final String NAME_CONTENT_ENCODING = "Content-Encoding";
	public static final String NAME_CONTENT_LENGTH = "Content-Length";
	public static final String NAME_CONTENT_DISPOSITION = "Content-Disposition";
	public static final String NAME_CONTENT_RANGE = "Content-Range";
	public static final String NAME_CACHE_CONTROL = "Cache-Control";

	public static abstract class HttpLoadParams extends LoadParams
	{
		public abstract void addHeader(NameValuePair header);

		public abstract void addRequestParam(NameValuePair requestParam);

		public abstract boolean isPostRequest(String url);

		public abstract List<Header> makeHeaders(String url);

		public abstract String makeGetUrl(String url);

		public abstract HttpEntity makePostData(String url);

		public HttpHost getProxy(String url)
		{
			return null;
		}
	}

	private static Map<String, byte[]> mMemoryCache = Collections.synchronizedMap(new HashMap<String, byte[]>());

	public static void clearMemoryCache()
	{
		mMemoryCache.clear();
	}

	public static class HttpLoadTask extends LoadTask<HttpLoadParams>
	{
		public HttpLoadTask(Context context, String url, HttpLoadParams params, BaseParser parser, CacheMode cacheMode)
		{
			super(context, url, params, parser, cacheMode);
		}

		@SuppressWarnings("resource")
		@Override
		protected void onLoad()
		{
			if (hasCanceled())
			{
				return;
			}
			HttpResponse response = null;
			InputStream is = null;
			String cacheKey = null;
			try
			{
				if (mCacheMode == null && mParams != null)
				{
					mCacheMode = mParams.getCacheMode();
				}
				// post请求不予缓存
				if (mParams != null && mParams.isPostRequest(mUrl))
				{
					mCacheMode = CacheMode.NO_CACHE;
				}
				if (mCacheMode == null)
					mCacheMode = CacheMode.NO_CACHE;
				switch (mCacheMode)
				{
					case PERFER_CACHE:
						mCacheMode = CacheMode.PERFER_FILECACHE;
						break;
					case CACHE_ONLY:
						mCacheMode = CacheMode.FILECACHE_ONLY;
						break;
					case MEMORY_CACHE:
						mCacheMode = CacheMode.MEMORYCACHE_ONLY;
						break;
					case PERFER_MEMORY_OR_CACHE:
						mCacheMode = CacheMode.PERFER_MEMORY_OR_FILE;
						break;
					default:
						break;
				}
				boolean needWriteFileCache = false;// 是否需要写入文件缓存
				boolean needPutMemoryCache = false;// 是否需要放入内存缓存
				boolean needReadFileCache = false;// 是否需要读取文件缓存
				DataFrom dataFrom = DataFrom.CACHE;
				if (mCacheMode != CacheMode.NO_CACHE)
				{
					if (mParams != null)
						cacheKey = mParams.makeGetUrl(mUrl);
					else
						cacheKey = mUrl;
					// try
					// {
					// cacheKey = URLEncoder.encode(cacheKey, AppConfig.DEFAULT_ENCODING);
					// }
					// catch (UnsupportedEncodingException e)
					// {
					// e.printStackTrace();
					// }
					switch (mCacheMode)
					{
						case FILECACHE_ONLY:
							is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
							needWriteFileCache = true;
							dataFrom = DataFrom.CACHE;
							break;
						default:
							byte[] data = null;
							if (mCacheMode == CacheMode.MEMORYCACHE_ONLY
									|| mCacheMode == CacheMode.PERFER_MEMORY_OR_FILE
									|| mCacheMode == CacheMode.PERFER_MEMORY_OR_NETWORK)
							{
								data = mMemoryCache.get(cacheKey);
								needPutMemoryCache = true;
								dataFrom = DataFrom.CACHE;
							}
							if (data != null)
							{
								is = new ByteArrayInputStream(data);
							}
							else if (mCacheMode == CacheMode.PERFER_FILECACHE
									|| mCacheMode == CacheMode.PERFER_MEMORY_OR_FILE)
							{
								is = FileCacheManager.getCachedFileInStream(mContext, cacheKey,
										(mParams != null ? mParams.getFileMTime() : FileCacheManager.UNKNOW_MTIME));
								needWriteFileCache = true;
								dataFrom = DataFrom.CACHE;
							}
							else if (mCacheMode == CacheMode.PERFER_NETWORK
									|| mCacheMode == CacheMode.PERFER_MEMORY_OR_NETWORK)
							{
								needWriteFileCache = true;
								needReadFileCache = true;
							}
							break;
					}
					if (hasCanceled())
					{
						return;
					}
					if (is != null)
					{
						LogUtil.i(TAG, "the " + mUrl + " found cache in " + mCacheMode + " mode; " + is);
						try
						{
							mParser.onParse(response, is, mUrl, cacheKey, mParams, dataFrom);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							notifyError(ErrorInfo.ERROR_EXCEPTION, null, e);
						}
						catch (OutOfMemoryError e)
						{
							e.printStackTrace();
							notifyError(ErrorInfo.ERROR_OUTOFMEMORY, null, e);
						}
						return;
					}
					else
					{
						LogUtil.w(TAG, "notfound " + mUrl + " cache in " + mCacheMode + " mode.");
					}
				}
				try
				{
					NetworkInfo ni = NetworkManager.getActiveNetworkInfo(mContext);
					if (ni == null || !ni.isConnected())
					{
						throw new IOException();
					}
					response = doHttpRequest(mContext, mUrl, mParams);
				}
				catch (ClientProtocolException e)
				{
					e.printStackTrace();
					if (needReadFileCache)
					{
						is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
						dataFrom = DataFrom.CACHE;
					}
					else
					{
						releaseInvalidConnections();
						notifyError(ErrorInfo.ERROR_PROTOCOL, null, e);
						return;
					}
				}
				catch (SocketTimeoutException e)
				{
					e.printStackTrace();
					if (needReadFileCache)
					{
						is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
						dataFrom = DataFrom.CACHE;
					}
					else
					{
						releaseInvalidConnections();
						notifyError(ErrorInfo.ERROR_SOCKETTIMEOUT, null, e);
						return;
					}
				}
				catch (UnknownHostException e)
				{
					e.printStackTrace();
					if (needReadFileCache)
					{
						is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
						dataFrom = DataFrom.CACHE;
					}
					else
					{
						releaseInvalidConnections();
						notifyError(ErrorInfo.ERROR_UNKNOWHOST, null, e);
						return;
					}
				}
				catch (SocketException e)
				{
					e.printStackTrace();
					if (needReadFileCache)
					{
						is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
						dataFrom = DataFrom.CACHE;
					}
					else
					{
						releaseInvalidConnections();
						notifyError(ErrorInfo.ERROR_SOCKET, null, e);
						return;
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
					if (needReadFileCache)
					{
						is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
						dataFrom = DataFrom.CACHE;
					}
					else
					{
						releaseInvalidConnections();
						notifyError(ErrorInfo.ERROR_IOEXCEPTION, null, e);
						return;
					}
				}
				catch (SecurityException e)
				{
					e.printStackTrace();
					if (needReadFileCache)
					{
						is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
						dataFrom = DataFrom.CACHE;
					}
					else
					{
						releaseInvalidConnections();
						notifyError(ErrorInfo.ERROR_SECURITYEXCEPTION, null, e);
						return;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (needReadFileCache)
					{
						is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
						dataFrom = DataFrom.CACHE;
					}
					else
					{
						releaseInvalidConnections();
						notifyError(ErrorInfo.ERROR_EXCEPTION, null, e);
						return;
					}
				}
				if (hasCanceled())
				{
					return;
				}
				try
				{
					if (response != null)
					{
						LogUtil.logHeaders(TAG, "", response.getAllHeaders());
						int statusCode = response.getStatusLine().getStatusCode();
						LogUtil.w(TAG, "the " + mUrl + " request statusCode = " + statusCode);
						switch (statusCode)
						{
							case HttpStatus.SC_OK:
							case HttpStatus.SC_PARTIAL_CONTENT:
								InputStream cis = response.getEntity().getContent();
								if (isGzipEncoding(response))
								{
									is = new GZIPInputStream(cis);
								}
								else
								{
									is = cis;
								}
								if (needWriteFileCache)
								{
									String prefix = null;
									File cacheFile = (mParams != null ? mParams.getCacheFile(mUrl) : null);
									if (cacheFile == null)
									{
										cacheFile = FileCacheManager.createNewCacheFile(mContext, prefix, null);
									}
									long cacheTime = getCacheTime(response);
									if (cacheTime == 0)
									{
										cacheTime = (mParams != null ? mParams.getCacheTime()
												: FileCacheManager.UNLIMITED_TIME);
									}
									long cacheMillisTime = (cacheTime > 0 ? cacheTime * 1000
											: FileCacheManager.UNLIMITED_TIME);
									is = FileCacheManager.createNewCache(mContext, cacheFile, cacheKey,
											cacheMillisTime, (mParams != null ? mParams.getFileMTime()
													: FileCacheManager.UNKNOW_MTIME), is);
								}
								if (needPutMemoryCache)
								{
									byte[] data = DataUtils.readInStreamData(is, 10);
									mMemoryCache.put(cacheKey, data);
									is = new ByteArrayInputStream(data);
								}
								dataFrom = DataFrom.SERVER;
								break;
							default:
								if (needReadFileCache)
								{
									is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
									dataFrom = DataFrom.CACHE;
								}
								else
								{
									notifyError(ErrorInfo.ERROR_RESPONSECODE, "response code is " + statusCode,
											new IllegalStateException());
									return;
								}
								break;
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
					if (needReadFileCache)
					{
						is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
						dataFrom = DataFrom.CACHE;
					}
					else
					{
						notifyError(ErrorInfo.ERROR_IOEXCEPTION, null, e);
						return;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (needReadFileCache)
					{
						is = FileCacheManager.getCachedFileInStreamWithUncheck(mContext, cacheKey);
						dataFrom = DataFrom.CACHE;
					}
					else
					{
						notifyError(ErrorInfo.ERROR_EXCEPTION, null, e);
						return;
					}
				}
				catch (OutOfMemoryError e)
				{
					e.printStackTrace();
					notifyError(ErrorInfo.ERROR_OUTOFMEMORY, null, e);
					return;
				}
				if (hasCanceled())
				{
					return;
				}
				try
				{
					if (is != null)
						mParser.onParse(response, is, mUrl, cacheKey, mParams, dataFrom);
					else
					{
						LogUtil.w(TAG, "the " + mUrl
								+ " load failed, read data is null, please check network or sdcard.");
						notifyError(ErrorInfo.ERROR_NULLPOINTEREXCEPTION, null, new NullPointerException());
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					notifyError(ErrorInfo.ERROR_EXCEPTION, null, e);
				}
				catch (OutOfMemoryError e)
				{
					e.printStackTrace();
					notifyError(ErrorInfo.ERROR_OUTOFMEMORY, null, e);
				}
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

	private static HttpResponse doHttpRequest(Context context, String url, HttpLoadParams params)
			throws SocketTimeoutException, ClientProtocolException, UnknownHostException, SocketException, IOException,
			Exception
	{
		if (params == null)
		{
			return HttpUtils.doHttpRequest(context, HttpGet.METHOD_NAME, url, null, null, null);
		}
		if (params.isPostRequest(url))
		{
			return HttpUtils.doHttpRequest(context, HttpPost.METHOD_NAME, url, params.makePostData(url),
					params.makeHeaders(url), params.getProxy(url));
		}
		else
		{
			return HttpUtils.doHttpRequest(context, HttpGet.METHOD_NAME, params.makeGetUrl(url), null,
					params.makeHeaders(url), params.getProxy(url));
		}
	}

	private static void releaseInvalidConnections()
	{
		HttpUtils.releaseInvalidConnections();
	}

	public static boolean isGzipEncoding(HttpResponse response)
	{
		Header header = response.getFirstHeader(NAME_CONTENT_ENCODING);
		if (header != null)
		{
			String value = header.getValue();
			LogUtil.v(TAG, "isGzipEncoding: " + value);
			if (value != null && value.toLowerCase().contains("gzip"))
			{
				return true;
			}
		}
		return false;
	}

	public static long getContentLength(HttpResponse response)
	{
		long length = -1;
		Header header = response.getFirstHeader(NAME_CONTENT_LENGTH);
		if (header != null)
		{
			String value = header.getValue();
			LogUtil.v(TAG, "getContentLength: " + value);
			try
			{
				length = Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		return length;
	}

	public static long[] getContentRange(HttpResponse response)
	{
		Header header = response.getFirstHeader(NAME_CONTENT_RANGE);
		if (header != null)
		{
			String value = header.getValue();
			LogUtil.d(TAG, "getContentRange: " + value);
			int idx1 = value.indexOf("bytes") + "bytes".length();
			int idx2 = value.indexOf("-");
			int idx3 = value.indexOf("/");
			String str1 = value.substring(idx1, idx2).trim();
			String str2 = value.substring(idx2 + 1, idx3).trim();
			String str3 = value.substring(idx3 + 1).trim();
			LogUtil.v(TAG, "parse ContentRange value: " + str1 + "; " + str2 + "; " + str3);
			long[] result = new long[3];
			result[0] = Long.parseLong(str1);
			result[1] = Long.parseLong(str2);
			result[2] = Long.parseLong(str3);
			return result;
		}
		return null;
	}

	public static String getContentSuffix(HttpResponse response)
	{
		Header header = response.getFirstHeader(NAME_CONTENT_TYPE);
		if (header != null)
		{
			String value = header.getValue();
			LogUtil.v(TAG, "getContentSuffix: " + value);
			if (value != null)
			{
				value = value.toLowerCase();
				if (value.contains("avi"))
				{
					return ".avi";
				}
				else if (value.contains("xml"))
				{
					return ".xml";
				}
				else if (value.contains("bmp"))
				{
					return ".bmp";
				}
				else if (value.contains("css"))
				{
					return ".css";
				}
				else if (value.contains("gif"))
				{
					return ".gif";
				}
				else if (value.contains("html"))
				{
					return ".html";
				}
				else if (value.contains("jpeg"))
				{
					return ".jpeg";
				}
				else if (value.contains("jpg"))
				{
					return ".jpg";
				}
				else if (value.contains("mp3"))
				{
					return ".mp3";
				}
				else if (value.contains("mpeg4"))
				{
					return ".mp4";
				}
				else if (value.contains("png"))
				{
					return ".png";
				}
				else if (value.contains("wav"))
				{
					return ".wav";
				}
				else if (value.contains("wma"))
				{
					return ".wma";
				}
				else if (value.contains("wmv"))
				{
					return ".wmv";
				}
				else if (value.contains("vnd.wap.wml"))
				{
					return ".wml";
				}
				else if (value.contains("text"))
				{
					return ".txt";
				}
			}
		}
		return null;
	}

	public static String getContentCharset(HttpResponse response)
	{
		Header header = response.getFirstHeader(NAME_CONTENT_TYPE);
		if (header != null)
		{
			String value = header.getValue();
			LogUtil.v(TAG, "getContentCharset: " + value);
			if (value != null)
			{
				int index = value.toLowerCase().lastIndexOf("charset=");
				if (index != -1)
				{
					return value.substring(index + 1);
				}
			}
		}
		return null;
	}

	public static long getCacheTime(HttpResponse response)
	{
		try
		{
			Header header = response.getFirstHeader(NAME_CACHE_CONTROL);
			if (header == null)
				return 0;
			String value = header.getValue();
			LogUtil.v(TAG, "getCacheTime: " + value);
			if (value == null)
			{
				return 0;
			}
			if (value.toLowerCase().contains("no-cache"))
			{
				return 0;
			}
			else
			{
				String[] timestrs = value.split("=");
				if (timestrs.length > 1)
				{
					long time = Long.parseLong(timestrs[1]);
					return time;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	private static HttpLoader mDefault;

	public static HttpLoader getDefault()
	{
		if (mDefault == null)
		{
			mDefault = new HttpLoader();
		}
		return mDefault;
	}

	public HttpLoader()
	{
		super();
	}

	public HttpLoader(int maxTaskCount)
	{
		super(maxTaskCount);
	}

	@Override
	HttpLoadTask createLoadTask(Context context, String url, LoadParams params, BaseParser parser, CacheMode cacheMode)
	{
		return new HttpLoadTask(context, url, (HttpLoadParams)params, parser, cacheMode);
	}
}
