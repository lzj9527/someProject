package android.extend.widget.showcase;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public interface IShowcaseDrawer
{
	public void draw(Canvas canvas, Paint paint);

	public RectF getBounds();
}
