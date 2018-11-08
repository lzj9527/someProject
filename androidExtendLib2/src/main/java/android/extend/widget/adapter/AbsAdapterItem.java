package android.extend.widget.adapter;

import android.view.View;
import android.view.ViewGroup;

public abstract class AbsAdapterItem
{
	public abstract View onCreateView(int position, ViewGroup parent);

	public abstract void onUpdateView(View view, int position, ViewGroup parent);

	public abstract void onLoadViewResource(View view, int position, ViewGroup parent);

	public abstract void onRecycleViewResource(View view, int position, ViewGroup parent);

	public boolean isEnabled()
	{
		return true;
	}

	public void onItemClick(View adapterView, ViewGroup parent, View view, int position, long id)
	{
	}

	public boolean onItemLongClick(View adapterView, ViewGroup parent, View view, int position, long id)
	{
		return false;
	}

	public void onItemSelected(View adapterView, ViewGroup parent, View view, int position, long id)
	{
	}

	public void onItemUnSelected(View adapterView, ViewGroup parent, View view, int position, long id)
	{
	}

	public void onItemCheckedStateChanged(int position, long id, boolean checked)
	{
	}
}
