package android.extend.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.extend.BasicConfig;
import android.extend.ErrorInfo;
import android.extend.cache.FileCacheManager;
import android.extend.loader.BaseParser;
import android.extend.loader.BasicHttpLoadParams;
import android.extend.loader.HttpLoader;
import android.extend.loader.HttpLoader.HttpLoadParams;
import android.extend.loader.Loader.LoadParams;
import android.extend.loader.UrlLoader;
import android.extend.util.FileUtils.FileNameInfo;
import android.text.TextUtils;

public class SimpleDownloader
{
	public static final String TAG = SimpleDownloader.class.getSimpleName();

	public interface OnDownloadListener
	{
		public void onDownloadStarted(Object tag, String url, String localPath);

		public void onDownloadProgress(Object tag, String url, String localPath, long count, long length, float speed);

		public void onDownloadFinished(Object tag, String url, String localPath, long totalTime);

		public void onDownloadCanceled(Object tag, String url, String localPath);

		public void onDownloadError(Object tag, String url, String localPath, ErrorInfo error);
	}

	private static ArrayList<DownloadParser> mParserList = new ArrayList<DownloadParser>();

	public static class DownloadParser extends BaseParser
	{
		private Object mTag;
		private String mUrl;
		private String mLocalPath;
		private HttpLoadParams mParams;
		private OnDownloadListener mListener;
		private boolean mContinued = false;
		private long mStartTime;

		private static final int mMaxRetryCount = 3;
		private int mRetryCount = 0;

		public DownloadParser(Context context, Object tag, String url, String localPath, HttpLoadParams params,
				OnDownloadListener listener, boolean continued)
		{
			super(context);
			mTag = tag;
			mUrl = url;
			mParams = params;
			mListener = listener;
			if (TextUtils.isEmpty(localPath))
				mLocalPath = makeFilePath(url);
			else
				mLocalPath = localPath;
			mContinued = continued;
		}

		public DownloadParser(Context context, Object tag, String url, String directoryPath, String fileName,
				HttpLoadParams params, OnDownloadListener listener, boolean continued)
		{
			super(context);
			mTag = tag;
			mUrl = url;
			mParams = params;
			mListener = listener;
			if (TextUtils.isEmpty(directoryPath))
			{
				directoryPath = getDownloadDirectory(context).getAbsolutePath();
			}
			if (TextUtils.isEmpty(fileName))
			{
				if (!BasicConfig.HideExtName)
				{
					fileName = FileUtils.getFileFullName(url);
				}
				else
				{
					FileNameInfo fileNameInfo = FileUtils.parseFileName(url);
					fileName = fileNameInfo.prefix + ".data";
				}
			}
			mLocalPath = directoryPath + File.separatorChar + fileName;
			mContinued = continued;
		}

		@Override
		public void cancel()
		{
			super.cancel();
			onDownloadCanceled();
		}

		@Override
		public void onStart()
		{
			super.onStart();
			onDownloadStarted();
		}

		private void onDownloadProgress(long count, long length, float speed)
		{
			if (mListener != null && !mCanceled)
			{
				mListener.onDownloadProgress(mTag, mUrl, mLocalPath, count, length, speed);
			}
		}

		private void onDownloadError(ErrorInfo error)
		{
			if (mListener != null)
			{
				mListener.onDownloadError(mTag, mUrl, mLocalPath, error);
			}
			synchronized (mParserList)
			{
				mParserList.remove(this);
			}
		}

		private void onDownloadStarted()
		{
			mStartTime = System.currentTimeMillis();
			if (mListener != null && !mCanceled)
			{
				mListener.onDownloadStarted(mTag, mUrl, mLocalPath);
			}
		}

		private void onDownloadFinished()
		{
			if (mListener != null && !mCanceled)
			{
				mListener.onDownloadFinished(mTag, mUrl, mLocalPath, System.currentTimeMillis() - mStartTime);
			}
			synchronized (mParserList)
			{
				mParserList.remove(this);
			}
		}

		private void onDownloadCanceled()
		{
			if (mListener != null)
			{
				mListener.onDownloadCanceled(mTag, mUrl, mLocalPath);
			}
			synchronized (mParserList)
			{
				mParserList.remove(this);
			}
		}

		// private long speedTime = 0;
		// private long speedCount = 0;
		// private float speed = 0.0f;

		@Override
		public void onParse(HttpResponse httpResponse, InputStream is, String url, String cacheKey, LoadParams params,
				DataFrom from)
		{
			boolean successed = false;
			FileOutputStream fos = null;
			long count = 0;
			long length = 0;
			long[] contentRange = null;
			File tmpFile = null;
			try
			{
				// if (httpResponse != null)
				// LogUtil.logHeaders(SimpleDownloader.TAG, "", httpResponse.getAllHeaders());
				switch (from)
				{
					case SERVER:
						switch (httpResponse.getStatusLine().getStatusCode())
						{
							case HttpStatus.SC_OK:
								length = HttpLoader.getContentLength(httpResponse);
								break;
							case HttpStatus.SC_PARTIAL_CONTENT:
								contentRange = HttpLoader.getContentRange(httpResponse);
								count = contentRange[0];
								length = contentRange[2];
								break;
						}
						break;
					case FILE:
						File file = new File(mUrl);
						length = file.length();
						file = null;
					case CACHE:
						String cachePath = FileCacheManager.getCachedFilePath(mContext, cacheKey);
						File cacheFile = new File(cachePath);
						length = cacheFile.length();
						cacheFile = null;
						break;
					case ASSET:
						length = is.available();
						break;
				}

				if (hasCanceled())
				{
					return;
				}

				String tmpFilePath = mLocalPath + ".tmp";
				LogUtil.i(TAG, mTag + " tmpFilePath = " + tmpFilePath);
				tmpFile = new File(tmpFilePath);

				File parentFile = tmpFile.getParentFile();
				if (parentFile.exists() && !parentFile.isDirectory())
					parentFile.delete();
				if (!parentFile.exists())
				{
					parentFile.mkdirs();
				}
				// FileUtils.setPermissions(parentFile.getAbsolutePath(), 0777, -1, -1);
				if (contentRange != null)
				{
					if (!tmpFile.exists())
						tmpFile.createNewFile();
				}
				else
				{
					if (tmpFile.exists())
					{
						tmpFile.delete();
					}
					tmpFile.createNewFile();
				}

				fos = new FileOutputStream(tmpFile, true);
				byte[] buffer = new byte[1024 * 10];
				int numread = 0;
				long startTime = System.currentTimeMillis();
				long speedCount = 0;
				float speed = 0.0f;
				// long startCountTime = startTime;
				while (true)
				{
					if (mCanceled)
					{
						return;
					}
					numread = is.read(buffer);
					if (numread < 0)
					{
						break;
					}

					if (mCanceled)
					{
						return;
					}
					fos.write(buffer, 0, numread);
					count += numread;
					speedCount += numread;

					long currTime = System.currentTimeMillis();
					long interval = currTime - startTime;
					// float speed = 0;
					// if (interval > 0)
					// {
					// speed = (float)(count * 1000) / (float)(interval * 1024);// 计算平均下载速度k/s
					// }
					if (interval > 999L)
					{
						speed = (float)(speedCount * 1000) / (float)(interval * 1024);// 计算下载速度k/s
						speedCount = 0;
						startTime = currTime;
					}
					// Logger.i(TAG, mTag + " " + count + "/" + length + " downloadSpeed = " + speed + " KB/S");
					onDownloadProgress(count, length, speed);
					// LogUtil.i(TAG, mTag + " " + numread + "/" + count + "/" +
					// length);
				}
				successed = true;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				ErrorInfo error = new ErrorInfo();
				error.errorCode = ErrorInfo.ERROR_IOEXCEPTION;
				error.description = e.getMessage();
				error.throwable = e;
				retry(error);
			}
			finally
			{
				if (fos != null)
				{
					try
					{
						fos.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				LogUtil.v(TAG, mTag + "; count=" + count + "; length=" + length + "; mCanceled=" + mCanceled);
				if (mCanceled)
				{
					return;
				}
				// if (count != length || (tmpFile != null && tmpFile.length() != length))
				// {// 下载文件大小不一致，重新下载
				// if (tmpFile != null)
				// tmpFile.delete();
				// mRetryCount = 0;
				// retry(null);
				// return;
				// }
				if (successed)
				{
					File file = new File(mLocalPath);
					if (file.exists())
					{
						file.delete();
					}
					if (tmpFile != null && tmpFile.renameTo(file))
					{
						// FileUtils.setPermissions(file.getAbsolutePath(), 0777, -1, -1);
						onDownloadFinished();
					}
					else
					{
						ErrorInfo error = new ErrorInfo();
						error.errorCode = ErrorInfo.ERROR_IOEXCEPTION;
						error.description = tmpFile.getAbsolutePath() + " rename to " + mLocalPath + " failed!!!";
						error.throwable = new IOException(error.description);
						onDownloadError(error);
					}
				}
			}
		}

		@Override
		public void onError(String url, LoadParams params, ErrorInfo error)
		{
			retry(error);
		}

		private void retry(ErrorInfo error)
		{
			if (hasCanceled())
			{
				return;
			}
			if (mRetryCount < mMaxRetryCount)
			{
				mRetryCount++;
				LogUtil.w(TAG, mTag + " retry count " + mRetryCount + " ;\n" + error.toString());
				if (mContinued)
				{
					long bytes = getTempFileBytes();
					if (bytes > 0)
					{
						if (mParams == null)
							mParams = new BasicHttpLoadParams(false);
						mParams.addHeader(new BasicNameValuePair("Range", "bytes=" + bytes + "-"));
					}
				}
				UrlLoader.getDefault().startLoad(mContext, mUrl, mParams, this, null);
			}
			else
			{
				onDownloadError(error);
			}
		}

		private String makeFilePath(String url)
		{
			if (!BasicConfig.HideExtName)
			{
				String fileName = FileUtils.getFileFullName(url);
				return getDownloadDirectory(mContext).getAbsolutePath() + File.separatorChar + fileName;
			}
			else
			{
				FileNameInfo fileName = FileUtils.parseFileName(url);
				return getDownloadDirectory(mContext).getAbsolutePath() + File.separatorChar + fileName.prefix
						+ ".data";
			}
		}

		public long getTempFileBytes()
		{
			String tmpFilePath = mLocalPath + ".tmp";
			LogUtil.i(TAG, mTag + " tmpFilePath = " + tmpFilePath);
			File tmpFile = new File(tmpFilePath);
			if (tmpFile.exists())
				return tmpFile.length();
			return 0;
		}
	}

	public static File getDownloadDirectory(Context context)
	{
		return FileUtils.getDirectory(context, "download");
	}

	public static DownloadParser startDownload(Context context, Object tag, String url, String localPath,
			HttpLoadParams params, OnDownloadListener listener, boolean continued)
	{
		if (context == null || TextUtils.isEmpty(url))
		{
			throw new NullPointerException();
		}
		DownloadParser parser = new DownloadParser(context, tag, url, localPath, params, listener, continued);
		mParserList.add(parser);
		// if (params == null)
		// params = new BasicHttpLoadParams(false);
		if (continued)
		{
			long bytes = parser.getTempFileBytes();
			if (bytes > 0)
			{
				if (params == null)
					params = new BasicHttpLoadParams(false);
				params.addHeader(new BasicNameValuePair("Range", "bytes=" + bytes + "-"));
			}
		}
		UrlLoader.getDefault().startLoad(context, url, params, parser, null);
		return parser;
	}

	public static DownloadParser startDownload(Context context, Object tag, String url, String localPath,
			HttpLoadParams params, OnDownloadListener listener)
	{
		return startDownload(context, tag, url, localPath, params, listener, false);
	}

	public static DownloadParser startDownload(Context context, Object tag, String url, String directoryPath,
			String fileName, HttpLoadParams params, OnDownloadListener listener, boolean continued)
	{
		if (context == null || TextUtils.isEmpty(url))
		{
			throw new NullPointerException();
		}
		DownloadParser parser = new DownloadParser(context, tag, url, directoryPath, fileName, params, listener,
				continued);
		mParserList.add(parser);
		if (continued)
		{
			long bytes = parser.getTempFileBytes();
			if (bytes > 0)
			{
				if (params == null)
					params = new BasicHttpLoadParams(false);
				params.addHeader(new BasicNameValuePair("Range", "bytes=" + bytes + "-"));
			}
		}
		UrlLoader.getDefault().startLoad(context, url, params, parser, null);
		return parser;
	}

	public static DownloadParser startDownload(Context context, Object tag, String url, String directoryPath,
			String fileName, HttpLoadParams params, OnDownloadListener listener)
	{
		return startDownload(context, tag, url, directoryPath, fileName, params, listener, false);
	}

	@SuppressWarnings("unchecked")
	public static DownloadParser cancelDownload(String url)
	{
		if (TextUtils.isEmpty(url))
			return null;
		ArrayList<DownloadParser> list = null;
		synchronized (mParserList)
		{
			list = (ArrayList<DownloadParser>)mParserList.clone();
		}
		for (DownloadParser parser : list)
		{
			if (parser.mUrl.equals(url))
			{
				parser.cancel();
				return parser;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void cancelAllDownload(String url)
	{
		if (TextUtils.isEmpty(url))
			return;
		ArrayList<DownloadParser> list = null;
		synchronized (mParserList)
		{
			list = (ArrayList<DownloadParser>)mParserList.clone();
		}
		for (DownloadParser parser : list)
		{
			if (parser.mUrl.equals(url))
			{
				parser.cancel();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void cancelAllDownload()
	{
		ArrayList<DownloadParser> list = null;
		synchronized (mParserList)
		{
			list = (ArrayList<DownloadParser>)mParserList.clone();
		}
		for (DownloadParser parser : list)
		{
			parser.cancel();
		}
	}
}
