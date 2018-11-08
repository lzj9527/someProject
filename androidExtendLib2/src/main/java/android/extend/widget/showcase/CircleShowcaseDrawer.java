package android.extend.widget.showcase;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class CircleShowcaseDrawer implements IShowcaseDrawer
{
	private float mCentreX;
	private float mCentreY;
	private float mRadius;

	public CircleShowcaseDrawer(int cx, int cy, int radius)
	{
		mCentreX = cx;
		mCentreY = cy;
		mRadius = radius;
	}

	public CircleShowcaseDrawer(float cx, float cy, float radius)
	{
		mCentreX = cx;
		mCentreY = cy;
		mRadius = radius;
	}

	@Override
	public void draw(Canvas canvas, Paint paint)
	{
		canvas.drawCircle(mCentreX, mCentreY, mRadius, paint);
	}

	@Override
	public RectF getBounds()
	{
		float left = mCentreX - mRadius;
		float top = mCentreY - mRadius;
		float right = mCentreX + mRadius;
		float bottom = mCentreY + mRadius;
		return new RectF(left, top, right, bottom);
	}
}
