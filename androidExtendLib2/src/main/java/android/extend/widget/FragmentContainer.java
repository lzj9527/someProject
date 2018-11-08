package android.extend.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 该类是用作Fragment容器之用
 * 
 * 为了避免TouchEvent事件向下传递，该类捕获了dispatchTouchEvent
 * */
public class FragmentContainer extends ExtendFrameLayout
{
	public FragmentContainer(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public FragmentContainer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public FragmentContainer(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		setInterceptTouchEventToDownward(true);
	}
}
