package android.extend.widget;

import android.view.MotionEvent;
import android.view.View;

public interface DispatchTouchListener
{
	public boolean dispatchTouch(View v, MotionEvent event);
}
