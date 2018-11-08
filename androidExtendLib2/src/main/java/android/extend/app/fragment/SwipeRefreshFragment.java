package android.extend.app.fragment;

import android.extend.app.BaseFragment;
import android.extend.util.ResourceUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public abstract class SwipeRefreshFragment extends BaseFragment implements OnRefreshListener
{
	protected SwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (view != null)
		{
			if (view instanceof SwipeRefreshLayout)
			{
				mSwipeRefreshLayout = (SwipeRefreshLayout)view;
			}
			else
			{
				int id = ResourceUtil.getId(getContext(), "swipe_refresh");
				mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(id);
			}
			if (mSwipeRefreshLayout == null)
			{
				mSwipeRefreshLayout = new SwipeRefreshLayout(getContext());
				int id = ResourceUtil.getId(getContext(), "swipe_refresh");
				mSwipeRefreshLayout.setId(id);
				mSwipeRefreshLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
				mSwipeRefreshLayout.addView(view);
				view = mSwipeRefreshLayout;
			}
		}
		if (mSwipeRefreshLayout != null)
		{
			mSwipeRefreshLayout
					.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
							android.R.color.holo_orange_light, android.R.color.holo_green_light);
			mSwipeRefreshLayout.setOnRefreshListener(this);
		}
		return view;
	}

	public void setRefreshing(boolean refreshing)
	{
		if (mSwipeRefreshLayout != null)
			mSwipeRefreshLayout.setRefreshing(refreshing);
	}
}
