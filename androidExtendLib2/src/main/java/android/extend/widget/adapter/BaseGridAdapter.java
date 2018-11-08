package android.extend.widget.adapter;

import java.util.Collection;

public class BaseGridAdapter<T extends AbsAdapterItem> extends BaseAdapter<T>
{
	@Override
	public void addItem(int position, T item)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void addItems(int position, Collection<T> itemCollection)
	{
		throw new UnsupportedOperationException();
	}
}
