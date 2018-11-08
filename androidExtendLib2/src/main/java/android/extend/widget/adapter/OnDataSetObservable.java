package android.extend.widget.adapter;

import java.util.Collection;

import android.database.Observable;

public class OnDataSetObservable<T extends AbsAdapterItem> extends Observable<OnDataSetObserver>
{
	/**
	 * Invokes onChanged on each observer. Called when the data set being observed has
	 * changed, and which when read contains the new state of the data.
	 */
	public void notifyChanged()
	{
		synchronized (mObservers)
		{
			// since onChanged() is implemented by the app, it could do anything, including
			// removing itself from {@link mObservers} - and that could cause problems if
			// an iterator is used on the ArrayList {@link mObservers}.
			// to avoid such problems, just march thru the list in the reverse order.
			for (int i = mObservers.size() - 1; i >= 0; i--)
			{
				mObservers.get(i).onChanged();
			}
		}
	}

	/**
	 * Invokes onInvalidated on each observer. Called when the data set being monitored
	 * has changed such that it is no longer valid.
	 */
	public void notifyInvalidated()
	{
		synchronized (mObservers)
		{
			for (int i = mObservers.size() - 1; i >= 0; i--)
			{
				mObservers.get(i).onInvalidated();
			}
		}
	}

	public void notifyDataAdded(int position, T item)
	{
		synchronized (mObservers)
		{
			for (int i = mObservers.size() - 1; i >= 0; i--)
			{
				mObservers.get(i).onDataAdded(position, item);
			}
		}
	}

	public void notifyDataAdded(int position, Collection<T> itemCollection)
	{
		synchronized (mObservers)
		{
			for (int i = mObservers.size() - 1; i >= 0; i--)
			{
				mObservers.get(i).onDataAdded(position, itemCollection);
			}
		}
	}

	public void notifyDataRemoved(int position)
	{
		synchronized (mObservers)
		{
			for (int i = mObservers.size() - 1; i >= 0; i--)
			{
				mObservers.get(i).onDataRemoved(position);
			}
		}
	}

	public void notifyDataCleared()
	{
		synchronized (mObservers)
		{
			for (int i = mObservers.size() - 1; i >= 0; i--)
			{
				mObservers.get(i).onDataCleared();
			}
		}
	}
}
