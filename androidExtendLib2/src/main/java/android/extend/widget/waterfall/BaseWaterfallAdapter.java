package android.extend.widget.waterfall;

import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.adapter.BaseGridAdapter;

public class BaseWaterfallAdapter<T extends AbsAdapterItem> extends BaseGridAdapter<T>
{
	@Override
	public void removeItem(int position)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeItem(T item)
	{
		throw new UnsupportedOperationException();
	}

}
