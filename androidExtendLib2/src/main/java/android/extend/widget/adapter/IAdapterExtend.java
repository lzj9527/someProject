package android.extend.widget.adapter;

import java.util.Collection;

public interface IAdapterExtend<T extends AbsAdapterItem>
{
	public void registerOnDataSetObserver(OnDataSetObserver observer);

	public void unregisterOnDataSetObserver(OnDataSetObserver observer);

	public void addItem(T item);

	public void addItem(int position, T item);

	public void addItems(Collection<T> itemList);

	public void addItems(int position, Collection<T> itemList);

	public void removeItem(int position);

	public void removeItem(T item);

	public void clear();
}
