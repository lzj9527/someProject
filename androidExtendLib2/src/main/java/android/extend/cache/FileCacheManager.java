package android.extend.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.content.Context;
import android.extend.util.DataUtils;
import android.extend.util.FileUtils;
import android.extend.util.LogUtil;
import android.text.TextUtils;

public class FileCacheManager
{
	public static class CacheItem
	{
		public String key;
		public String path;
		// public long cache_time;
		public long expires_time;
		public long file_mtime;

		@Override
		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			sb.append(this.getClass().getSimpleName()).append(":");
			sb.append(" key=").append(key);
			sb.append("; path=").append(path);
			sb.append("; expires_time=").append(expires_time);
			sb.append("; file_mtime=").append(file_mtime);
			return sb.toString();
		}
	}

	public static class CacheInputStream extends InputStream
	{
		private Context mContext = null;
		private File mCacheFile = null;
		private CacheItem mCacheItem = null;
		private InputStream mInputStream = null;
		private OutputStream mOutputStream = null;
		private boolean mCachedFailed = false;
		private boolean mReadFinished = false;

		// public CacheInputStream(InputStream is, String path)
		// {
		// initialize(is, path);
		// }
		//
		// public CacheInputStream(InputStream is, File file)
		// {
		// initialize(is, file);
		// }
		//
		// public CacheInputStream(InputStream is, OutputStream os)
		// {
		// initialize(is, os);
		// }

		// private void initialize(InputStream is, String path)
		// {
		// File file = null;
		// if (!TextUtils.isEmpty(path))
		// {
		// file = new File(path);
		// }
		// initialize(is, file);
		// }
		//
		// private void initialize(InputStream is, File file)
		// {
		// OutputStream os = null;
		// if (file != null)
		// {
		// try
		// {
		// os = new FileOutputStream(file);
		// }
		// catch (FileNotFoundException e)
		// {
		// e.printStackTrace();
		// os = null;
		// }
		// }
		// initialize(is, os);
		// }
		//
		// private void initialize(InputStream is, OutputStream os)
		// {
		// if (is == null)
		// {
		// throw new NullPointerException();
		// }
		// mInputStream = is;
		// mOutputStream = os;
		// }

		public CacheInputStream(Context context, InputStream is, File cacheFile, CacheItem item)
		{
			initialize(context, is, cacheFile, item);
		}

		private void initialize(Context context, InputStream is, File cacheFile, CacheItem item)
		{
			if (context == null || is == null || item == null)
			{
				throw new NullPointerException();
			}
			mContext = context;
			mInputStream = is;
			if (cacheFile != null && cacheFile.exists())
			{
				try
				{
					mOutputStream = new FileOutputStream(cacheFile);
					mCacheFile = cacheFile;
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			mCacheItem = item;
		}

		private void onCachedResult()
		{
			// LogUtil.v(TAG, "onCachedResult: " + mCachedFailed + "; " + mCacheFile + "; " + mCacheItem.toString());
			if (mCacheFile != null && mCacheFile.exists())
			{
				if (mCachedFailed || !mReadFinished)
				{
					mCacheFile.delete();
				}
				else
				{
					File file = FileCacheManager.getCachedFile(mContext, mCacheItem.key);
					if (file != null && !file.getAbsolutePath().equals(mCacheFile.getAbsolutePath()))
						file.delete();
					FileCacheManager.updateCacheRecord(mContext, mCacheItem);
				}
			}
		}

		@Override
		public int available() throws IOException
		{
			return mInputStream.available();
		}

		@Override
		public void close() throws IOException
		{
			if (mOutputStream != null)
			{
				try
				{
					mOutputStream.flush();
					mOutputStream.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					mCachedFailed = true;
				}
			}
			onCachedResult();
			mInputStream.close();
		}

		@Override
		public void mark(int readlimit)
		{
			mInputStream.mark(readlimit);
		}

		@Override
		public boolean markSupported()
		{
			return mInputStream.markSupported();
		}

		@Override
		public int read() throws IOException
		{
			int oneByte = -1;
			try
			{
				oneByte = mInputStream.read();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				mCachedFailed = true;
				onCachedResult();
				throw new IOException(e.getMessage());
			}
			if (oneByte == -1)
			{
				mReadFinished = true;
			}
			else if (oneByte != -1 && mOutputStream != null)
			{
				try
				{
					mOutputStream.write(oneByte);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					mCachedFailed = true;
				}
			}
			return oneByte;
		}

		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException
		{
			int count = 0;
			try
			{
				count = mInputStream.read(buffer, offset, length);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				mCachedFailed = true;
				onCachedResult();
				throw new IOException(e.getMessage());
			}
			if (count == -1)
			{
				mReadFinished = true;
			}
			else if (count > 0 && mOutputStream != null)
			{
				try
				{
					mOutputStream.write(buffer, offset, count);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					mCachedFailed = true;
				}
			}
			return count;
		}

		@Override
		public int read(byte[] buffer) throws IOException
		{
			int count = 0;
			try
			{
				count = mInputStream.read(buffer);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				mCachedFailed = true;
				onCachedResult();
				throw new IOException(e.getMessage());
			}
			if (count == -1)
			{
				mReadFinished = true;
			}
			else if (count > 0 && mOutputStream != null)
			{
				try
				{
					mOutputStream.write(buffer, 0, count);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					mCachedFailed = true;
				}
			}
			return count;
		}

		@Override
		public synchronized void reset() throws IOException
		{
			try
			{
				mInputStream.reset();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				mCachedFailed = true;
				onCachedResult();
				throw new IOException(e.getMessage());
			}
		}

		@Override
		public long skip(long byteCount) throws IOException
		{
			try
			{
				return mInputStream.skip(byteCount);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				mCachedFailed = true;
				onCachedResult();
				throw new IOException(e.getMessage());
			}
		}
	}

	public static final String TAG = "cache";
	public static final String LogFile = "cache.txt";

	public static final int UNLIMITED_TIME = -1;
	public static final int UNKNOW_MTIME = -1;
	// private static final Random mRandom = new Random();
	private static final Object mLock = new Object();

	// private static String getRandomFileName(String suffix)
	// {
	// StringBuffer sb = new StringBuffer();
	// sb.append(System.currentTimeMillis());
	// sb.append(mRandom.nextInt());
	// sb.append(suffix);
	// return sb.toString();
	// }

	public static File getExternalCacheDirectory(Context context)
	{
		return FileUtils.getExternalDirectory(context, "cache");
	}

	public static File getCacheDirectory(Context context)
	{
		if (FileUtils.checkExternalStorageMounted())
		{
			return getExternalCacheDirectory(context);
		}
		else
		{
			return context.getCacheDir();
		}
	}

	private static long getExpiresTime(long cacheMillisTime)
	{
		long expires_time;
		if (cacheMillisTime > 0)
		{
			expires_time = System.currentTimeMillis() + cacheMillisTime;
		}
		else
		{
			expires_time = UNLIMITED_TIME;
		}
		return expires_time;
	}

	public static File createNewCacheFile(Context context, String prefix, String suffix) throws Exception
	{
		if (TextUtils.isEmpty(prefix))
		{
			prefix = "cache";
		}
		if (TextUtils.isEmpty(suffix))
		{
			suffix = ".cache";
		}
		File directory = getCacheDirectory(context);
		File file = FileUtils.createTempFile(context, directory, prefix, suffix);
		return file;
	}

	public static CacheInputStream createNewCache(Context context, File cacheFile, String key, long cacheMillisTime,
			long fileMTime, InputStream is)
	{
		if (TextUtils.isEmpty(key))
		{
			return null;
		}
		if (cacheFile == null)
		{
			return null;
		}
		CacheItem item = new CacheItem();
		item.key = key;
		item.path = cacheFile.getAbsolutePath();
		item.expires_time = getExpiresTime(cacheMillisTime);
		item.file_mtime = fileMTime;
		return new CacheInputStream(context, is, cacheFile, item);
	}

	public static CacheInputStream createNewCache(Context context, String prefix, String suffix, String key,
			long cacheMillisTime, long fileMTime, InputStream is)
	{
		File cacheFile = null;
		try
		{
			cacheFile = createNewCacheFile(context, prefix, suffix);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return createNewCache(context, cacheFile, key, cacheMillisTime, fileMTime, is);
	}

	private static boolean checkCacheExistsImpl(Context context, CacheItem item, long fileMTime)
	{
		if (item == null)
		{
			return false;
		}
		LogUtil.v(TAG, "checkCache: " + item.toString() + "; fileMTime=" + fileMTime);
		File file = new File(item.path);
		// 文件已更新
		if (fileMTime > 0 && fileMTime != item.file_mtime)
		{
			LogUtil.w(TAG, "checkCache, fileMTime update...");
			if (file.exists())
			{
				file.delete();
			}
			FileCacheDB.delete(context, item.key);
			return false;
		}
		// 缓存已过期
		if (item.expires_time > 0 && System.currentTimeMillis() > item.expires_time)
		{
			LogUtil.w(TAG, "checkCache, has expires...");
			if (file.exists())
			{
				file.delete();
			}
			FileCacheDB.delete(context, item.key);
			return false;
		}
		// 缓存文件已不存在
		if (!file.exists())
		{
			LogUtil.w(TAG, "checkCache, file not exists...");
			FileCacheDB.delete(context, item.key);
			return false;
		}
		// 缓存文件大小为0
		if (file.length() == 0)
		{
			LogUtil.w(TAG, "checkCache, file length is 0...");
			FileCacheDB.delete(context, item.key);
			return false;
		}
		return true;
	}

	private static boolean checkCacheExistsWithLock(Context context, CacheItem item, long fileMTime)
	{
		synchronized (mLock)
		{
			return checkCacheExistsImpl(context, item, fileMTime);
		}
	}

	public static boolean checkCacheExists(Context context, String key, long fileMTime)
	{
		CacheItem item = FileCacheDB.query(context, key);
		return checkCacheExistsWithLock(context, item, fileMTime);
	}

	public static boolean checkCacheExists(Context context, String key)
	{
		CacheItem item = FileCacheDB.query(context, key);
		return checkCacheExistsWithLock(context, item, UNKNOW_MTIME);
	}

	public static CacheItem getCacheItemWithUncheck(Context context, String key)
	{
		return FileCacheDB.query(context, key);
	}

	public static CacheItem getCacheItem(Context context, String key, long fileMTime)
	{
		CacheItem item = FileCacheDB.query(context, key);
		if (checkCacheExistsWithLock(context, item, fileMTime))
		{
			return item;
		}
		else
		{
			return null;
		}
	}

	public static CacheItem getCacheItem(Context context, String key)
	{
		return getCacheItem(context, key, UNKNOW_MTIME);
	}

	public static String getCachedFilePathWithUncheck(Context context, String key)
	{
		CacheItem item = getCacheItemWithUncheck(context, key);
		if (item != null)
		{
			return item.path;
		}
		return null;
	}

	public static String getCachedFilePath(Context context, String key, long fileMTime)
	{
		CacheItem item = getCacheItem(context, key, fileMTime);
		if (item != null)
		{
			return item.path;
		}
		return null;
	}

	public static String getCachedFilePath(Context context, String key)
	{
		return getCachedFilePath(context, key, UNKNOW_MTIME);
	}

	public static File getCachedFileWithUncheck(Context context, String key)
	{
		CacheItem item = getCacheItemWithUncheck(context, key);
		if (item != null)
		{
			return new File(item.path);
		}
		return null;
	}

	public static File getCachedFile(Context context, String key, long fileMTime)
	{
		CacheItem item = getCacheItem(context, key, fileMTime);
		if (item != null)
		{
			return new File(item.path);
		}
		return null;
	}

	public static File getCachedFile(Context context, String key)
	{
		return getCachedFile(context, key, UNKNOW_MTIME);
	}

	public static InputStream getCachedFileInStreamWithUncheck(Context context, String key)
	{
		File file = getCachedFileWithUncheck(context, key);
		LogUtil.v(TAG, "getCachedFileInStreamWithUncheck: " + key + "; " + file);
		if (file != null && file.exists())
		{
			try
			{
				return new FileInputStream(file);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static InputStream getCachedFileInStream(Context context, String key, long fileMTime)
	{
		File file = getCachedFile(context, key, fileMTime);
		LogUtil.v(TAG, "getCachedFileInStream: " + key + "; " + file);
		if (file != null)
		{
			try
			{
				return new FileInputStream(file);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static InputStream getCachedFileInStream(Context context, String key)
	{
		return getCachedFileInStream(context, key, UNKNOW_MTIME);
	}

	public static byte[] getCachedFileData(Context context, String key, long fileMTime)
	{
		InputStream is = getCachedFileInStream(context, key, fileMTime);
		try
		{
			if (is != null)
			{
				byte[] data = DataUtils.readInStreamData(is, 10);
				return data;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		return null;
	}

	public static byte[] getCachedFileData(Context context, String key)
	{
		return getCachedFileData(context, key, UNKNOW_MTIME);
	}

	private static void deleteCacheImpl(Context context, CacheItem item)
	{
		if (item == null)
		{
			return;
		}
		File file = new File(item.path);
		if (file.exists())
		{
			file.delete();
		}
		FileCacheDB.delete(context, item.key);
	}

	private static void deleteCacheWithLock(Context context, CacheItem item)
	{
		synchronized (mLock)
		{
			deleteCacheImpl(context, item);
		}
	}

	public static void deleteCache(Context context, String key)
	{
		CacheItem item = FileCacheDB.query(context, key);
		deleteCacheWithLock(context, item);
	}

	public static void clearExpiresCaches(Context context)
	{
		synchronized (mLock)
		{
			List<CacheItem> list = FileCacheDB.queryAll(context);
			for (CacheItem item : list)
			{
				checkCacheExistsImpl(context, item, UNKNOW_MTIME);
			}
		}
	}

	public static void clearAllCaches(Context context)
	{
		synchronized (mLock)
		{
			FileCacheDB.deleteAll(context);
			File directory = getExternalCacheDirectory(context);
			FileUtils.deleteFiles(directory);
			directory = context.getCacheDir();
			FileUtils.deleteFiles(directory);
		}
	}

	public static void updateCacheTime(Context context, String key, long cacheMillisTime)
	{
		FileCacheDB.updateExpiresTime(context, key, getExpiresTime(cacheMillisTime));
	}

	public static void updateFileMTime(Context context, String key, long fileMTime)
	{
		FileCacheDB.updateFileMTime(context, key, fileMTime);
	}

	public static void updateCacheRecord(Context context, String key, String path, long cacheMillisTime, long fimeMTime)
	{
		if (FileCacheDB.hasRecord(context, key))
		{
			FileCacheDB.update(context, key, path, getExpiresTime(cacheMillisTime), fimeMTime);
		}
		else
		{
			FileCacheDB.insert(context, key, path, getExpiresTime(cacheMillisTime), fimeMTime);
		}
	}

	public static void updateCacheRecord(Context context, CacheItem item)
	{
		if (FileCacheDB.hasRecord(context, item.key))
		{
			FileCacheDB.update(context, item);
		}
		else
		{
			FileCacheDB.insert(context, item);
		}
	}
}
