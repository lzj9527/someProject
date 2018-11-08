package android.extend.widget.adapter;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ListAdapter<T extends AbsAdapterItem> extends BaseAdapter<T> implements OnItemClickListener,
		OnItemLongClickListener, OnItemSelectedListener, MultiChoiceModeListener
{
	public ListAdapter(AdapterView<? extends Adapter> adapterView, boolean listenClicks)
	{
		if (adapterView != null && listenClicks)
		{
			adapterView.setOnItemClickListener(this);
			adapterView.setOnItemLongClickListener(this);
			adapterView.setOnItemSelectedListener(this);
			if (adapterView instanceof AbsListView)
			{
				((AbsListView)adapterView).setMultiChoiceModeListener(this);
			}
		}
	}

	public ListAdapter(AdapterView<? extends Adapter> adapterView)
	{
		this(adapterView, true);
	}

	public ListAdapter()
	{
		this(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		AbsAdapterItem item = mItemList.get(position);
		if (item != null)
		{
			item.onItemClick(parent, parent, view, position, id);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		AbsAdapterItem item = mItemList.get(position);
		if (item != null)
		{
			return item.onItemLongClick(parent, parent, view, position, id);
		}
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		AbsAdapterItem item = mItemList.get(position);
		if (item != null)
		{
			item.onItemSelected(parent, parent, view, position, id);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
	{
		AbsAdapterItem item = mItemList.get(position);
		if (item != null)
		{
			item.onItemCheckedStateChanged(position, id, checked);
		}
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		return false;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	{
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode)
	{
	}
}
