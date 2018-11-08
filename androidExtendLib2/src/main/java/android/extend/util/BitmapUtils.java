package android.extend.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.extend.widget.showcase.IShowcaseDrawer;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

public class BitmapUtils
{
	public static final String TAG = BitmapUtils.class.getSimpleName();

	public static boolean isFitWidth(int srcWidth, int srcHeight, int outWidth, int outHeight)
	{
		if (outWidth * srcHeight <= srcWidth * outHeight)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static int computeOutHeight(int srcWidth, int srcHeight, int outWidth)
	{
		float ratio = (float)srcHeight / (float)srcWidth;
		int outHeight = Math.round(outWidth * ratio);
		if (outHeight < 1)
			outHeight = 1;
		return outHeight;
	}

	public static int computeOutWidth(int srcWidth, int srcHeight, int outHeight)
	{
		float ratio = (float)srcWidth / (float)srcHeight;
		int outWidth = Math.round(outHeight * ratio);
		if (outWidth < 1)
			outWidth = 1;
		return outWidth;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int outWidth, int outHeight)
	{
		int minSideLength = Math.min(outWidth, outHeight);
		int maxNumOfPixels = outWidth * outHeight;
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int sampleSize;
		if (initialSize <= 8)
		{
			sampleSize = 1;
			while (sampleSize < initialSize)
			{
				sampleSize <<= 1;
			}
		}
		else
		{
			sampleSize = (initialSize + 7) / 8 * 8;
		}
		LogUtil.v(TAG, "computeSampleSize: srcSize=" + options.outWidth + "x" + options.outHeight + "; outSize="
				+ outWidth + "x" + outHeight + "; sampleSize=" + sampleSize);
		return sampleSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
	{
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int)Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int)Math.min(Math.floor(w / minSideLength),
				Math.floor(h / minSideLength));
		if (upperBound < lowerBound)
		{
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1))
		{
			return 1;
		}
		else if (minSideLength == -1)
		{
			return lowerBound;
		}
		else
		{
			return upperBound;
		}
	}

	private static Bitmap decodeBitmapInSampleSize(String path, int outWidth, int outHeight,
			BitmapFactory.Options options)
	{
		options.inSampleSize = computeSampleSize(options, outWidth, outHeight);
		options.inJustDecodeBounds = false;
		options.inInputShareable = true;
		options.inPurgeable = true;
		return BitmapFactory.decodeFile(path, options);
	}

	private static Bitmap decodeBitmapInSampleSize(byte[] data, int outWidth, int outHeight,
			BitmapFactory.Options options)
	{
		options.inSampleSize = computeSampleSize(options, outWidth, outHeight);
		options.inJustDecodeBounds = false;
		options.inInputShareable = true;
		options.inPurgeable = true;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	public static BitmapFactory.Options getBitmapOptions(String path)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		return options;
	}

	public static BitmapFactory.Options getBitmapOptions(byte[] data)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		return options;
	}

	/**
	 * 该方法是为了避免大图片直接原图加载导致内存崩溃的问题，使用BitmapFactory.Options.inSampleSize来减少加载的像素
	 * 
	 * @param path 图片本地路径
	 * @param outWidth 图片输出宽度
	 * @param outHeight 图片输出高度
	 * @return {@link Bitmap}
	 * */
	public static Bitmap decodeBitmap(String path, int outWidth, int outHeight)
	{
		if (TextUtils.isEmpty(path))
		{
			throw new NullPointerException("the path is null!!!");
		}
		if (outWidth < 1 || outHeight < 1)
		{
			throw new IllegalArgumentException("outWidth=" + outWidth + "; outHeight=" + outHeight);
		}
		BitmapFactory.Options options = getBitmapOptions(path);
		return decodeBitmapInSampleSize(path, outWidth, outHeight, options);
	}

	public static Bitmap decodeBitmap(byte[] data, int outWidth, int outHeight)
	{
		if (data == null)
		{
			throw new NullPointerException("the data is null!!!");
		}
		if (outWidth < 1 || outHeight < 1)
		{
			throw new IllegalArgumentException("outWidth=" + outWidth + "; outHeight=" + outHeight);
		}
		BitmapFactory.Options options = getBitmapOptions(data);
		return decodeBitmapInSampleSize(data, outWidth, outHeight, options);
	}

	public static Bitmap decodeBitmap(String path, float scale)
	{
		if (TextUtils.isEmpty(path))
		{
			throw new NullPointerException("the path is null!!!");
		}
		if (scale <= 0)
		{
			throw new IllegalArgumentException("scale=" + scale);
		}
		BitmapFactory.Options options = getBitmapOptions(path);
		int outWidth = (int)(options.outWidth * scale);
		int outHeight = (int)(options.outHeight * scale);
		return decodeBitmapInSampleSize(path, outWidth, outHeight, options);
	}

	public static Bitmap decodeBitmap(byte[] data, float scale)
	{
		if (data == null)
		{
			throw new NullPointerException("the data is null!!!");
		}
		if (scale <= 0)
		{
			throw new IllegalArgumentException("scale=" + scale);
		}
		BitmapFactory.Options options = getBitmapOptions(data);
		int outWidth = (int)(options.outWidth * scale);
		int outHeight = (int)(options.outHeight * scale);
		return decodeBitmapInSampleSize(data, outWidth, outHeight, options);
	}

	public static Bitmap decodeBitmapFitWidth(String path, int outWidth)
	{
		if (TextUtils.isEmpty(path))
		{
			throw new NullPointerException("the path is null!!!");
		}
		if (outWidth < 1)
		{
			throw new IllegalArgumentException("outWidth=" + outWidth);
		}
		BitmapFactory.Options options = getBitmapOptions(path);
		return decodeBitmapFitWidthImpl(path, outWidth, options);
	}

	private static Bitmap decodeBitmapFitWidthImpl(String path, int outWidth, BitmapFactory.Options options)
	{
		int outHeight = computeOutHeight(options.outWidth, options.outHeight, outWidth);
		return decodeBitmapInSampleSize(path, outWidth, outHeight, options);
	}

	public static Bitmap decodeBitmapFitWidth(byte[] data, int outWidth)
	{
		if (data == null)
		{
			throw new NullPointerException("the data is null!!!");
		}
		if (outWidth < 1)
		{
			throw new IllegalArgumentException("outWidth=" + outWidth);
		}
		BitmapFactory.Options options = getBitmapOptions(data);
		return decodeBitmapFitWidthImpl(data, outWidth, options);
	}

	private static Bitmap decodeBitmapFitWidthImpl(byte[] data, int outWidth, BitmapFactory.Options options)
	{
		int outHeight = computeOutHeight(options.outWidth, options.outHeight, outWidth);
		return decodeBitmapInSampleSize(data, outWidth, outHeight, options);
	}

	public static Bitmap decodeBitmapFitHeight(String path, int outHeight)
	{
		if (TextUtils.isEmpty(path))
		{
			throw new NullPointerException("the path is null!!!");
		}
		if (outHeight < 1)
		{
			throw new IllegalArgumentException("outHeight=" + outHeight);
		}
		BitmapFactory.Options options = getBitmapOptions(path);
		return decodeBitmapFitHeightImpl(path, outHeight, options);
	}

	private static Bitmap decodeBitmapFitHeightImpl(String path, int outHeight, BitmapFactory.Options options)
	{
		int outWidth = computeOutWidth(options.outWidth, options.outHeight, outHeight);
		return decodeBitmapInSampleSize(path, outWidth, outHeight, options);
	}

	public static Bitmap decodeBitmapFitHeight(byte[] data, int outHeight)
	{
		if (data == null)
		{
			throw new NullPointerException("the data is null!!!");
		}
		if (outHeight < 1)
		{
			throw new IllegalArgumentException("outHeight=" + outHeight);
		}
		BitmapFactory.Options options = getBitmapOptions(data);
		return decodeBitmapFitHeightImpl(data, outHeight, options);
	}

	private static Bitmap decodeBitmapFitHeightImpl(byte[] data, int outHeight, BitmapFactory.Options options)
	{
		int outWidth = computeOutWidth(options.outWidth, options.outHeight, outHeight);
		return decodeBitmapInSampleSize(data, outWidth, outHeight, options);
	}

	public static Bitmap decodeBitmapFitSide(String path, int sideLength, boolean fitLongSide)
	{
		if (TextUtils.isEmpty(path))
		{
			throw new NullPointerException("the path is null!!!");
		}
		if (sideLength < 1)
		{
			throw new IllegalArgumentException("sideLength=" + sideLength);
		}
		BitmapFactory.Options options = getBitmapOptions(path);
		if (fitLongSide)
		{
			if (options.outWidth >= options.outHeight)
			{
				return decodeBitmapFitWidthImpl(path, sideLength, options);
			}
			else
			{
				return decodeBitmapFitHeightImpl(path, sideLength, options);
			}
		}
		else
		{
			if (options.outWidth <= options.outHeight)
			{
				return decodeBitmapFitWidthImpl(path, sideLength, options);
			}
			else
			{
				return decodeBitmapFitHeightImpl(path, sideLength, options);
			}
		}
	}

	public static Bitmap decodeBitmapFitSide(byte[] data, int sideLength, boolean fitLongSide)
	{
		if (data == null)
		{
			throw new NullPointerException("the data is null!!!");
		}
		if (sideLength < 1)
		{
			throw new IllegalArgumentException("sideLength=" + sideLength);
		}
		BitmapFactory.Options options = getBitmapOptions(data);
		if (fitLongSide)
		{
			if (options.outWidth >= options.outHeight)
			{
				return decodeBitmapFitWidthImpl(data, sideLength, options);
			}
			else
			{
				return decodeBitmapFitHeightImpl(data, sideLength, options);
			}
		}
		else
		{
			if (options.outWidth <= options.outHeight)
			{
				return decodeBitmapFitWidthImpl(data, sideLength, options);
			}
			else
			{
				return decodeBitmapFitHeightImpl(data, sideLength, options);
			}
		}
	}

	public static Bitmap decodeBitmapFitRect(String path, int outWidth, int outHeight)
	{
		if (TextUtils.isEmpty(path))
		{
			throw new NullPointerException("the path is null!!!");
		}
		if (outWidth < 1 || outHeight < 1)
		{
			throw new IllegalArgumentException("outWidth=" + outWidth + "; outHeight=" + outHeight);
		}
		BitmapFactory.Options options = getBitmapOptions(path);
		if (isFitWidth(options.outWidth, options.outHeight, outWidth, outHeight))
		{
			return decodeBitmapFitWidthImpl(path, outWidth, options);
		}
		else
		{
			return decodeBitmapFitHeightImpl(path, outHeight, options);
		}
	}

	public static Bitmap decodeBitmapFitRect(byte[] data, int outWidth, int outHeight)
	{
		if (data == null)
		{
			throw new NullPointerException("the data is null!!!");
		}
		if (outWidth < 1 || outHeight < 1)
		{
			throw new IllegalArgumentException("outWidth=" + outWidth + "; outHeight=" + outHeight);
		}
		BitmapFactory.Options options = getBitmapOptions(data);
		if (isFitWidth(options.outWidth, options.outHeight, outWidth, outHeight))
		{
			return decodeBitmapFitWidthImpl(data, outWidth, options);
		}
		else
		{
			return decodeBitmapFitHeightImpl(data, outHeight, options);
		}
	}

	public static boolean saveBitmapToFile(Bitmap bitmap, File destFile, CompressFormat format)
	{
		boolean result = false;
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(destFile, false);
			if (format == null)
				format = CompressFormat.JPEG;
			result = bitmap.compress(format, 100, fos);
			fos.flush();
			// bitmap.recycle();
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		}
		return result;
	}

	public static boolean saveBitmapToFile(Bitmap bitmap, File destFile)
	{
		return saveBitmapToFile(bitmap, destFile, null);
	}

	// public static Bitmap scaleBitmap(Bitmap source, int outWidth, int outHeight, ScaleType scaleType)
	// {
	// if (scaleType == null)
	// scaleType = ScaleType.FIT_CENTER;
	// switch (scaleType)
	// {
	// case CENTER:
	//
	// case FIT_XY:
	// return Bitmap.createScaledBitmap(source, outWidth, outHeight, true);
	//
	// }
	// }

	public static Bitmap scaleBitmap(Bitmap source, int outWidth, int outHeight)
	{
		return Bitmap.createScaledBitmap(source, outWidth, outHeight, true);
	}

	public static Bitmap scaleBitmapFitWidth(Bitmap source, int outWidth)
	{
		int outHeight = computeOutHeight(source.getWidth(), source.getHeight(), outWidth);
		return Bitmap.createScaledBitmap(source, outWidth, outHeight, true);
	}

	public static Bitmap scaleBitmapFitHeight(Bitmap source, int outHeight)
	{
		int outWidth = computeOutWidth(source.getWidth(), source.getHeight(), outHeight);
		return Bitmap.createScaledBitmap(source, outWidth, outHeight, true);
	}

	public static Bitmap scaleBitmapFitSide(Bitmap source, int sideLengh, boolean fitMaxSide)
	{
		if (fitMaxSide)
		{
			if (source.getWidth() >= source.getHeight())
			{
				return scaleBitmapFitWidth(source, sideLengh);
			}
			else
			{
				return scaleBitmapFitHeight(source, sideLengh);
			}
		}
		else
		{
			if (source.getWidth() <= source.getHeight())
			{
				return scaleBitmapFitWidth(source, sideLengh);
			}
			else
			{
				return scaleBitmapFitHeight(source, sideLengh);
			}
		}
	}

	public static Bitmap scaleBitmapAutoFit(Bitmap source, int outWidth, int outHeight)
	{
		if (isFitWidth(source.getWidth(), source.getHeight(), outWidth, outHeight))
		{
			return scaleBitmapFitWidth(source, outWidth);
		}
		else
		{
			return scaleBitmapFitHeight(source, outHeight);
		}
	}

	public static Bitmap scaleBitmapAutoFitIfNeed(Bitmap source, int outWidth, int outHeight)
	{
		if (outWidth < source.getWidth() || outHeight < source.getHeight())
		{
			return BitmapUtils.scaleBitmapAutoFit(source, outWidth, outHeight);
		}
		else
		{
			return source;
		}
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees, float px, float py)
	{
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees, px, py);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public static Bitmap rotateBitmapInCenter(Bitmap bitmap, float degrees)
	{
		float px = (float)bitmap.getWidth() / 2f;
		float py = (float)bitmap.getHeight() / 2f;
		return rotateBitmap(bitmap, degrees, px, py);
	}

	public static Bitmap mirrorBitmap(Bitmap bitmap, boolean vertical)
	{
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		if (vertical)
			matrix.postScale(1.0f, -1.0f);// 镜像垂直翻转
		else
			matrix.postScale(-1.0f, 1.0f); // 镜像水平翻转
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		if (drawable == null)
		{
			return null;
		}
		if (drawable instanceof BitmapDrawable)
		{
			BitmapDrawable bd = (BitmapDrawable)drawable;
			return bd.getBitmap();
		}
		int w = drawable.getIntrinsicWidth();
		if (w <= 0)
			w = 2;
		int h = drawable.getIntrinsicHeight();
		if (h <= 0)
			h = 2;
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap viewToBitmap(View view)
	{
		if (view == null)
			return null;
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		return bitmap;
	}

	public enum ClipType
	{
		START, END, CENTER,
	}

	public static Bitmap createCircleBitmap(Bitmap bitmap, ClipType clipType)
	{
		int squareSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
		float radius = (float)squareSize / 2f;
		Bitmap output = Bitmap.createBitmap(squareSize, squareSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		// Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		// RectF rectF = new RectF(0, 0, squareSize, squareSize);
		if (clipType == null)
			clipType = ClipType.CENTER;
		float left = 0f, top = 0f;
		if (bitmap.getWidth() < bitmap.getHeight())
		{
			switch (clipType)
			{
				case START:
					// rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
					break;
				case END:
					top = bitmap.getHeight() - bitmap.getWidth();
					// rect = new Rect(0, top, bitmap.getWidth(), top + bitmap.getWidth());
					break;
				case CENTER:
					top = (bitmap.getHeight() - bitmap.getWidth()) / 2;
					// rect = new Rect(0, top, bitmap.getWidth(), top + bitmap.getWidth());
					break;
			}
		}
		else if (bitmap.getWidth() > bitmap.getHeight())
		{
			switch (clipType)
			{
				case START:
					// rect = new Rect(0, 0, bitmap.getHeight(), bitmap.getWidth());
					break;
				case END:
					left = bitmap.getWidth() - bitmap.getHeight();
					// rect = new Rect(left, 0, left + bitmap.getHeight(), bitmap.getHeight());
					break;
				case CENTER:
					left = (bitmap.getWidth() - bitmap.getHeight()) / 2;
					// rect = new Rect(left, 0, left + bitmap.getHeight(), bitmap.getHeight());
					break;
			}
		}
		canvas.drawCircle(radius, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// canvas.drawBitmap(bitmap, rect, rectF, paint);
		canvas.drawBitmap(bitmap, -left, -top, paint);
		return output;
	}

	public static Bitmap createOvalBitmap(Bitmap bitmap)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		canvas.drawOval(rectF, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rectF, paint);
		return output;
	}

	public static Bitmap createRoundRectBitmap(Bitmap bitmap, float rx, float ry)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, rx, ry, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rectF, paint);
		return output;
	}

	public static Bitmap createShowcaseBitmap(int width, int height, int maskColor, IShowcaseDrawer[] drawers)
	{
		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		output.eraseColor(maskColor);

		Canvas canvas = new Canvas(output);
		// // clear canvas
		// canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		// // draw solid background
		// canvas.drawColor(maskColor);

		Paint paint = new Paint();
		paint.setColor(0xFFFFFF);
		paint.setAlpha(0);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);

		for (IShowcaseDrawer drawer : drawers)
		{
			drawer.draw(canvas, paint);
		}

		return output;
	}

	public static Bitmap createShowcaseBitmap(int width, int height, int maskColor, List<IShowcaseDrawer> drawerList)
	{
		IShowcaseDrawer[] drawers = new IShowcaseDrawer[drawerList.size()];
		drawers = drawerList.toArray(drawers);
		return createShowcaseBitmap(width, height, maskColor, drawers);
	}
}
