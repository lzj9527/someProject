package android.extend.widget;

import android.content.Context;
import android.extend.data.FileData;
import android.extend.loader.BitmapLoader.DecodeMode;
import android.extend.util.LogUtil;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class Image360View extends ExtendImageView
{
	private FileData[][] mImageDataTable;
	// private boolean mFrameSequenceReverse = false;
	private boolean mRowFrameSequenceReverse = false;
	private boolean mColFrameSequenceReverse = false;
	private int mCurrentRowFrame = -1;
	private int mCurrentColFrame = -1;
	private PointF savedPoint = new PointF();
	private boolean mRowFrameLoop = false;
	private boolean mColFrameLoop = true;

	public Image360View(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public Image360View(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public Image360View(Context context)
	{
		super(context);
	}

	public void setImageDataArray(FileData[] array, int rowNum)
	{
		if (array == null || array.length == 0)
		{
			LogUtil.w(TAG, "setImageDataArray error: array == null || array.length == 0");
			return;
		}
		if (rowNum < 1 || rowNum > array.length)
		{
			LogUtil.w(TAG, "setImageDataArray error: rowNum < 1 || rowNum > array.length");
			return;
		}
		mImageDataTable = new FileData[rowNum][];
		final int colNum = array.length / rowNum;
		for (int i = 0; i < rowNum; i++)
		{
			int start = i * colNum;
			if (i == rowNum - 1)
			{
				int num = array.length - start;
				mImageDataTable[i] = new FileData[num];
			}
			else
			{
				mImageDataTable[i] = new FileData[colNum];
			}
			for (int j = 0; j < mImageDataTable[i].length; j++)
			{
				mImageDataTable[i][j] = array[start + j];
			}
		}
		mCurrentRowFrame = -1;
		mCurrentColFrame = -1;
		nextColFrame();
	}

	public void setImageDataTable(FileData[][] table)
	{
		mImageDataTable = table;
		mCurrentRowFrame = -1;
		mCurrentColFrame = -1;
		nextColFrame();
	}

	public void setRowFrameSequenceReverse(boolean reverse)
	{
		mRowFrameSequenceReverse = reverse;
	}

	public void setColFrameSequenceReverse(boolean reverse)
	{
		mColFrameSequenceReverse = reverse;
	}

	public void setFrameSequenceReverse(boolean reverse)
	{
		setRowFrameSequenceReverse(reverse);
		setColFrameSequenceReverse(reverse);
	}

	public void setRowFrameLoop(boolean loop)
	{
		mRowFrameLoop = loop;
	}

	public void setColFrameLoop(boolean loop)
	{
		mColFrameLoop = loop;
	}

	public void setFrameLoop(boolean loop)
	{
		setRowFrameLoop(loop);
		setColFrameLoop(loop);
	}

	public int getRowCount()
	{
		if (mImageDataTable == null)
			return 0;
		return mImageDataTable.length;
	}

	public int getColCount(int rowIndex)
	{
		if (mImageDataTable == null || mImageDataTable.length == 0)
			return 0;
		if (rowIndex < 0 || rowIndex >= mImageDataTable.length)
			return 0;
		return mImageDataTable[rowIndex].length;
	}

	public int getCurrentColCount()
	{
		return getColCount(mCurrentRowFrame);
	}

	public int getCurrentRowFrame()
	{
		return mCurrentRowFrame;
	}

	public int getCurrentColFrame()
	{
		return mCurrentColFrame;
	}

	private void updateCurrentFrame()
	{
		LogUtil.v(TAG, "updateCurrentFrame: " + mCurrentRowFrame + ", " + mCurrentColFrame);
		FileData data = mImageDataTable[mCurrentRowFrame][mCurrentColFrame];
		DecodeMode mode = DecodeMode.FIT_WIDTH;
		if (getWidth() != 0)
			mode = DecodeMode.FIT_WIDTH;
		else if (getHeight() != 0)
			mode = DecodeMode.FIT_HEIGHT;
		setImageDataSource(data, mode);
		startImageLoad(false);
	}

	private boolean checkBefore()
	{
		if (mLoadStatus == LoadStatus.LOADING)
		{
			LogUtil.w(TAG, "checkBefore error: mLoadStatus == LoadStatus.LOADING");
			return true;
		}
		if (mImageDataTable == null || mImageDataTable.length == 0)
		{
			LogUtil.w(TAG, "checkBefore error: mImageDataTable == null || mImageDataTable.length == 0");
			return true;
		}
		return false;
	}

	private void ensureCurrentRowFrame()
	{
		if (mCurrentRowFrame < 0 || mCurrentRowFrame >= mImageDataTable.length)
			mCurrentRowFrame = 0;
	}

	private boolean checkRowFrame(int rowFrame)
	{
		if (rowFrame < 0 || rowFrame >= mImageDataTable.length)
		{
			LogUtil.w(TAG, "checkRowFrame error: " + rowFrame + " < 0 || " + rowFrame + " >= mImageDataTable.length");
			return true;
		}
		if (mImageDataTable[rowFrame] == null || mImageDataTable[rowFrame].length == 0)
		{
			LogUtil.w(TAG, "checkRowFrame error: mImageDataTable[" + rowFrame + "] == null || mImageDataTable["
					+ rowFrame + "].length == 0");
			return true;
		}
		return false;
	}

	private void ensureCurrentColFrame()
	{
		if (mCurrentColFrame < 0 || mCurrentColFrame >= mImageDataTable[mCurrentRowFrame].length)
			mCurrentColFrame = 0;
	}

	private boolean checkColFrame(int rowFrame, int colFrame)
	{
		if (colFrame < 0 || colFrame >= mImageDataTable[rowFrame].length)
		{
			LogUtil.w(TAG, "checkColFrame error: " + colFrame + " < 0 || " + colFrame + " >= mImageDataTable["
					+ rowFrame + "].length");
			return true;
		}
		if (mImageDataTable[rowFrame][colFrame] == null)
		{
			LogUtil.w(TAG, "checkColFrame error: mImageDataTable[" + rowFrame + "][" + colFrame + "] == null");
			return true;
		}
		return false;
	}

	public void prevColFrame()
	{
		LogUtil.d(TAG, "prevColFrame...");
		if (checkBefore())
			return;
		ensureCurrentRowFrame();
		if (checkRowFrame(mCurrentRowFrame))
			return;
		if (!mColFrameSequenceReverse)
		{
			mCurrentColFrame--;
			if (mCurrentColFrame < 0 || mCurrentColFrame >= mImageDataTable[mCurrentRowFrame].length)
				mCurrentColFrame = mImageDataTable[mCurrentRowFrame].length - 1;
		}
		else
		{
			mCurrentColFrame++;
			if (mCurrentColFrame < 0 || mCurrentColFrame >= mImageDataTable[mCurrentRowFrame].length)
				mCurrentColFrame = 0;
		}
		if (checkColFrame(mCurrentRowFrame, mCurrentColFrame))
			return;
		updateCurrentFrame();
	}

	public void nextColFrame()
	{
		LogUtil.d(TAG, "nextColFrame...");
		if (checkBefore())
			return;
		ensureCurrentRowFrame();
		if (checkRowFrame(mCurrentRowFrame))
			return;
		if (!mColFrameSequenceReverse)
		{
			mCurrentColFrame++;
			if (mCurrentColFrame < 0 || mCurrentColFrame >= mImageDataTable[mCurrentRowFrame].length)
				mCurrentColFrame = 0;
		}
		else
		{
			mCurrentColFrame--;
			if (mCurrentColFrame < 0 || mCurrentColFrame >= mImageDataTable[mCurrentRowFrame].length)
				mCurrentColFrame = mImageDataTable[mCurrentRowFrame].length - 1;
		}
		if (checkColFrame(mCurrentRowFrame, mCurrentColFrame))
			return;
		updateCurrentFrame();
	}

	public void prevRowFrame()
	{
		LogUtil.d(TAG, "prevRowFrame...");
		if (checkBefore())
			return;
		if (!mRowFrameSequenceReverse)
		{
			mCurrentRowFrame--;
			if (mCurrentRowFrame < 0 || mCurrentRowFrame >= mImageDataTable.length)
				if (mRowFrameLoop)
					mCurrentRowFrame = mImageDataTable.length - 1;
				else
					mCurrentRowFrame = 0;
		}
		else
		{
			mCurrentRowFrame++;
			if (mCurrentRowFrame < 0 || mCurrentRowFrame >= mImageDataTable.length)
				if (mRowFrameLoop)
					mCurrentRowFrame = 0;
				else
					mCurrentRowFrame = mImageDataTable.length - 1;
		}
		if (checkRowFrame(mCurrentRowFrame))
			return;
		ensureCurrentColFrame();
		updateCurrentFrame();
	}

	public void nextRowFrame()
	{
		LogUtil.d(TAG, "nextRowFrame...");
		if (checkBefore())
			return;
		if (!mRowFrameSequenceReverse)
		{
			mCurrentRowFrame++;
			if (mCurrentRowFrame < 0 || mCurrentRowFrame >= mImageDataTable.length)
				if (mRowFrameLoop)
					mCurrentRowFrame = 0;
				else
					mCurrentRowFrame = mImageDataTable.length - 1;
		}
		else
		{
			mCurrentRowFrame--;
			if (mCurrentRowFrame < 0 || mCurrentRowFrame >= mImageDataTable.length)
				if (mRowFrameLoop)
					mCurrentRowFrame = mImageDataTable.length - 1;
				else
					mCurrentRowFrame = 0;
		}
		if (checkRowFrame(mCurrentRowFrame))
			return;
		ensureCurrentColFrame();
		updateCurrentFrame();
	}

	public void setCurrentColFrame(int colFrame)
	{
		LogUtil.d(TAG, "setCurrentColFrame: " + colFrame);
		if (checkBefore())
			return;
		ensureCurrentRowFrame();
		if (checkRowFrame(mCurrentRowFrame))
			return;
		if (checkColFrame(mCurrentRowFrame, colFrame))
			return;
		mCurrentColFrame = colFrame;
		updateCurrentFrame();
	}

	public void setCurrentRowFrame(int rowFrame)
	{
		LogUtil.d(TAG, "setCurrentRowFrame: " + rowFrame);
		if (checkBefore())
			return;
		if (checkRowFrame(rowFrame))
			return;
		ensureCurrentColFrame();
		mCurrentRowFrame = rowFrame;
		updateCurrentFrame();
	}

	public void setCurrentFrame(int rowFrame, int colFrame)
	{
		LogUtil.d(TAG, "setCurrentFrame: " + rowFrame + ", " + colFrame);
		if (checkBefore())
			return;
		if (checkRowFrame(rowFrame))
			return;
		if (checkColFrame(rowFrame, colFrame))
			return;
		mCurrentRowFrame = rowFrame;
		mCurrentColFrame = colFrame;
		updateCurrentFrame();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (getDrawable() == null)
			return super.onTouchEvent(event);
		// LogUtil.d(TAG, "onTouchEvent: " + event);
		switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				savedPoint.set(event.getX(), event.getY());
				return true;
			case MotionEvent.ACTION_MOVE:
				float deltaX = event.getX() - savedPoint.x;
				float deltaY = event.getY() - savedPoint.y;
				// LogUtil.v(TAG, "deltaX=" + deltaX + "; deltaY=" + deltaY);
				if (Math.abs(deltaX) >= Math.abs(deltaY))
				{
					if (Math.abs(deltaX) > 20)
					{
						savedPoint.set(event.getX(), event.getY());
						if (deltaX < 0)
							prevColFrame();
						else
							nextColFrame();
					}
				}
				else
				{
					if (Math.abs(deltaY) > 20)
					{
						savedPoint.set(event.getX(), event.getY());
						if (deltaY < 0)
							prevRowFrame();
						else
							nextRowFrame();
					}
				}
				return true;
		}
		return super.onTouchEvent(event);
	}
}
