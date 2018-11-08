package android.extend.cache;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.extend.util.LogUtil;
import android.graphics.Bitmap;
import android.text.TextUtils;

public class BitmapCacheManager
{
	public static final String TAG = BitmapCacheManager.class.getSimpleName();

	private static Map<String, SoftReference<Bitmap>> mCacheBitmaps = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

	public static void put(String url, Bitmap bitmap)
	{
		if (TextUtils.isEmpty(url) || bitmap == null)
		{
			return;
		}
		// LogUtil.d(TAG, "put: " + url + "; " + bitmap);
		if (bitmap.isRecycled())
		{
			LogUtil.w(TAG, url + " " + bitmap + " isRecycled!!!");
			return;
		}
		mCacheBitmaps.put(url, new SoftReference<Bitmap>(bitmap));
	}

	public static Bitmap get(String url)
	{
		if (TextUtils.isEmpty(url))
		{
			return null;
		}
		SoftReference<Bitmap> bitmapReference = mCacheBitmaps.get(url);
		// LogUtil.d(TAG, "get: " + url + "; " + bitmapReference);
		if (bitmapReference == null)
		{
			return null;
		}
		Bitmap bitmap = bitmapReference.get();
		if (bitmap != null && bitmap.isRecycled())
		{
			LogUtil.w(TAG, url + " " + bitmap + " isRecycled!!!");
			mCacheBitmaps.remove(url);
			return null;
		}
		return bitmap;
	}

	public static void remove(String url)
	{
		SoftReference<Bitmap> bitmapReference = mCacheBitmaps.remove(url);
		Bitmap bitmap = bitmapReference.get();
		if (bitmap != null && !bitmap.isRecycled())
		{
			bitmap.recycle();
		}
	}

	public static void clear()
	{
		synchronized (mCacheBitmaps)
		{
			Collection<SoftReference<Bitmap>> list = mCacheBitmaps.values();
			for (SoftReference<Bitmap> bmr : list)
			{
				Bitmap bm = bmr.get();
				if (bm != null && !bm.isRecycled())
				{
					bm.recycle();
				}
			}
		}
		mCacheBitmaps.clear();
	}
}
