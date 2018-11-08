package android.extend.widget.adapter;

import java.util.Collection;

import android.database.DataSetObserver;

public abstract class OnDataSetObserver extends DataSetObserver
{
	public void onDataAdded(int position, AbsAdapterItem item)
	{
	}

	public void onDataAdded(int position, Collection<? extends AbsAdapterItem> itemCollection)
	{
	}

	public void onDataRemoved(int position)
	{
	}

	public void onDataCleared()
	{
	}
}
