package android.extend.widget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.AttributeSet;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PDFRendererView extends TouchImageView
{
	/**
	 * File descriptor of the PDF.
	 */
	private ParcelFileDescriptor mFileDescriptor;

	/**
	 * {@link android.graphics.pdf.PdfRenderer} to render the PDF.
	 */
	private PdfRenderer mPdfRenderer;

	/**
	 * Page that is currently shown on the screen.
	 */
	private PdfRenderer.Page mCurrentPage;

	public PDFRendererView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public PDFRendererView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public PDFRendererView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		super.setGestureMode(GestureMode.SINGLE);
		super.setMoveEnabled(true);
		super.setScaleEnabled(true);
		super.setRotateEnabled(false);
		super.setScaleType(ScaleType.FIT_CENTER);
	}

	/**
	 * Sets up a {@link android.graphics.pdf.PdfRenderer} and related resources.
	 */
	private void openRenderer(Context context) throws IOException
	{
		// This is the PdfRenderer we use to render the PDF.
		mPdfRenderer = new PdfRenderer(mFileDescriptor);
		LogUtil.v(TAG, "openRenderer finish: getPageCount=" + mPdfRenderer.getPageCount());
		showPageImpl(0);
	}

	/**
	 * Closes the {@link android.graphics.pdf.PdfRenderer} and related resources.
	 * 
	 * @throws java.io.IOException When the PDF file cannot be closed.
	 */
	private void closeRenderer() throws IOException
	{
		if (mCurrentPage != null)
			mCurrentPage.close();
		mCurrentPage = null;
		if (mPdfRenderer != null)
			mPdfRenderer.close();
		mPdfRenderer = null;
		if (mFileDescriptor != null)
			mFileDescriptor.close();
		mFileDescriptor = null;
	}

	/**
	 * Shows the specified page of PDF to the screen.
	 * 
	 * @param index The page index.
	 */
	private void showPageImpl(final int index)
	{
		if (index < 0 || mPdfRenderer.getPageCount() <= index)
		{
			return;
		}
		// Make sure to close the current page before opening another one.
		if (null != mCurrentPage)
		{
			mCurrentPage.close();
		}
		// Use `openPage` to open a specific page in PDF.
		mCurrentPage = mPdfRenderer.openPage(index);
		if (getWidth() == 0 || getHeight() == 0)
		{
			AndroidUtils.MainHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					showPageImpl(index);
				}
			}, 50L);
			return;
		}

		float xRadio = getWidth() / mCurrentPage.getWidth();
		float yRadio = getHeight() / mCurrentPage.getHeight();
		float radio = Math.max(xRadio, yRadio);
		if (radio < 1f)
			radio = 1f;
		int width = (int)(mCurrentPage.getWidth() * radio);
		int height = (int)(mCurrentPage.getHeight() * radio);

		// Important: the destination bitmap must be ARGB (not RGB).
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// Here, we render the page onto the Bitmap.
		// To render a portion of the page, use the second and third parameter. Pass nulls to get
		// the default result.
		// Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
		mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
		// We are ready to show the Bitmap to user.
		super.setImageBitmap(bitmap);
		LogUtil.v(TAG, "showPage finish: " + index + "; " + bitmap.getWidth() + "x" + bitmap.getHeight());
	}

	public void openFromAsset(String fileName) throws IOException
	{
		mFileDescriptor = getContext().getAssets().openFd(fileName).getParcelFileDescriptor();
		openRenderer(getContext());
	}

	public void openFromFile(String path) throws FileNotFoundException, IOException
	{
		File file = new File(path);
		mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		openRenderer(getContext());
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		try
		{
			closeRenderer();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets the number of pages in the PDF. This method is marked as public for testing.
	 * 
	 * @return The number of pages.
	 */
	public int getPageCount()
	{
		return mPdfRenderer.getPageCount();
	}

	public int getCurrentPageIndex()
	{
		return mCurrentPage.getIndex();
	}

	public void showPage(int index)
	{
		showPageImpl(index);
	}

	public void showPrevPage()
	{
		showPage(mCurrentPage.getIndex() - 1);
	}

	public void showNextPage()
	{
		showPage(mCurrentPage.getIndex() + 1);
	}
}
