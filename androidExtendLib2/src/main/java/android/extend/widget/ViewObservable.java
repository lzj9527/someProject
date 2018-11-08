package android.extend.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.view.View;

public class ViewObservable
{
	public interface IViewObservable
	{
		public void registerObserver(OnViewObserver observer);

		public void unregisterObserver(OnViewObserver observer);
	}

	public interface OnViewObserver
	{
		public void onViewSizeChanged(View view, int w, int h, int oldw, int oldh);

		public void onViewLayout(View view, boolean changed, int left, int top, int right, int bottom);

		public void onViewMeasure(View view, int widthMeasureSpec, int heightMeasureSpec);
	}

	private View mView;
	private List<OnViewObserver> mObservers = Collections.synchronizedList(new ArrayList<OnViewObserver>());

	public ViewObservable(View view)
	{
		if (view == null)
		{
			throw new NullPointerException();
		}
		mView = view;
	}

	public void registerObserver(OnViewObserver observer)
	{
		if (observer == null)
		{
			return;
		}
		if (!mObservers.contains(observer))
			mObservers.add(observer);
	}

	public void unregisterObserver(OnViewObserver observer)
	{
		if (observer == null)
		{
			return;
		}
		mObservers.remove(observer);
	}

	public void clear()
	{
		mObservers.clear();
	}

	public void notifyOnLayout(boolean changed, int left, int top, int right, int bottom)
	{
		synchronized (mObservers)
		{
			for (OnViewObserver observer : mObservers)
			{
				observer.onViewLayout(mView, changed, left, top, right, bottom);
			}
		}
	}

	public void notifyOnSizeChanged(int w, int h, int oldw, int oldh)
	{
		synchronized (mObservers)
		{
			for (OnViewObserver observer : mObservers)
			{
				observer.onViewSizeChanged(mView, w, h, oldw, oldh);
			}
		}
	}

	public void notifyOnMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		synchronized (mObservers)
		{
			for (OnViewObserver observer : mObservers)
			{
				observer.onViewMeasure(mView, widthMeasureSpec, heightMeasureSpec);
			}
		}
	}
}
