package android.extend.widget;

import java.util.List;

import android.content.Context;
import android.extend.util.ResourceUtil;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SpinnerPopupWindow extends PopupWindow implements OnItemClickListener
{
	public interface OnItemClickListener
	{
		public void onItemClick(int position);
	}

	private Context mContext;
	private int mItemLayoutId;
	private ListView mListView;
	private List<String> mList;
	private Adapter mAdapter;
	private OnItemClickListener mItemClickListener;

	public SpinnerPopupWindow(Context context, View contentView, int itemLayoutId, List<String> list)
	{
		super(context);
		mContext = context;
		mItemLayoutId = itemLayoutId;
		mList = list;
		init(contentView);
	}

	public SpinnerPopupWindow(Context context, int contentViewLayoutId, int itemLayoutId, List<String> list)
	{
		this(context, View.inflate(context, contentViewLayoutId, null), itemLayoutId, list);
	}

	private void init(View contentView)
	{
		if (contentView == null)
			contentView = new ListView(mContext);
		if (contentView instanceof ListView)
			mListView = (ListView)contentView;
		else
		{
			int id = ResourceUtil.getId(mContext, "listView");
			mListView = (ListView)contentView.findViewById(id);
		}

		setContentView(contentView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);

		setFocusable(true);
		setBackgroundDrawable(new ColorDrawable(0x00));

		mAdapter = new Adapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	public ListView getListView()
	{
		return mListView;
	}

	public BaseAdapter getAdapter()
	{
		return mAdapter;
	}

	public void refreshData(List<String> list)
	{
		mList = list;
		mAdapter.notifyDataSetChanged();
	}

	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mItemClickListener = listener;
	}

	@Override
	public void showAsDropDown(View anchor)
	{
		setWidth(anchor.getWidth());
		super.showAsDropDown(anchor);
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff)
	{
		setWidth(anchor.getWidth());
		super.showAsDropDown(anchor, xoff, yoff);
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff, int gravity)
	{
		setWidth(anchor.getWidth());
		super.showAsDropDown(anchor, xoff, yoff, gravity);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		dismiss();
		if (mItemClickListener != null)
			mItemClickListener.onItemClick(position);
	}

	private class Adapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return mList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return mList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder viewHolder;

			if (convertView == null)
			{
				convertView = View.inflate(mContext, mItemLayoutId, null);
				viewHolder = new ViewHolder();
				if (convertView == null)
					convertView = new TextView(mContext);
				if (convertView instanceof TextView)
					viewHolder.mTextView = (TextView)convertView;
				else
				{
					int id = ResourceUtil.getId(mContext, "textView");
					viewHolder.mTextView = (TextView)convertView.findViewById(id);
				}
				convertView.setTag(viewHolder);
				final TextView textView = viewHolder.mTextView;
				textView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
				{
					@Override
					public void onGlobalLayout()
					{
						if (textView.getWidth() == 0)
							return;
						textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						LayoutParams params = textView.getLayoutParams();
						params.width = textView.getWidth();
						textView.setLayoutParams(params);
					}
				});
			}
			else
			{
				viewHolder = (ViewHolder)convertView.getTag();
			}

			String item = (String)getItem(position);
			viewHolder.mTextView.setText(item);

			return convertView;
		}
	}

	private class ViewHolder
	{
		public TextView mTextView;
	}
}
