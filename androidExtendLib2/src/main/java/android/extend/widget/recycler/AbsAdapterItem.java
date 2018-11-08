package android.extend.widget.recycler;

import android.extend.widget.recycler.BaseRecyclerAdapter.BaseViewHolder;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbsAdapterItem
{
	BaseRecyclerAdapter mAttachedAdapter;

	public BaseRecyclerAdapter getAttachedAdapter()
	{
		return mAttachedAdapter;
	}

	public abstract View onCreateView(ViewGroup parent, int position);

	public abstract void onBindView(BaseViewHolder holder, View view, int position);

	public abstract void onViewAttachedToWindow(BaseViewHolder holder, View view);

	public void onViewDetachedFromWindow(BaseViewHolder holder, View view)
	{
	}

	public void onViewRecycled(BaseViewHolder holder, View view)
	{
	}
}
