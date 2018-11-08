package android.extend.app;

import android.app.Activity;
import android.extend.util.ResourceUtil;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FrameUIWrapper implements IFrameUIWrapper
{
	private Activity mActivity;
	private FrameLayout mRootView;
	private LinearLayout mContentContainer;
	private View mContentView;
	private View mLoadingIndicator;
	private View mTitleBar;
	// private ITitleAction mTitleAction;
	private View mSubTitleBar;
	private View mBottomBar;

	// private View mLeftVerticalBar;
	// private View mRightVerticalBar;

	private int mTitleBarId;
	private int mSubTitleBarId;
	private int mBottomBarId;
	private int mLoadingIndicatorId;

	private FrameUIWrapper(Activity activity)
	{
		if (activity == null)
		{
			throw new NullPointerException();
		}
		mActivity = activity;
		makeIds();
	}

	public FrameUIWrapper(Activity activity, int rootLayout)
	{
		this(activity);
		setLayoutView(rootLayout);
	}

	public FrameUIWrapper(Activity activity, View rooView)
	{
		this(activity);
		setLayoutView(rooView);
	}

	private void makeIds()
	{
		mLoadingIndicatorId = ResourceUtil.getId(mActivity, "loading_indicator");
		mTitleBarId = ResourceUtil.getId(mActivity, "titlebar");
		mSubTitleBarId = ResourceUtil.getId(mActivity, "sub_titlebar");
		mBottomBarId = ResourceUtil.getId(mActivity, "bottombar");
	}

	private void setLayoutView(int rootLayout)
	{
		View layoutView = View.inflate(mActivity, rootLayout, null);
		setLayoutView(layoutView);
	}

	private void setLayoutView(View rootView)
	{
		if (rootView == null)
		{
			rootView = new FrameLayout(mActivity);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			rootView.setLayoutParams(params);
		}
		mRootView = (FrameLayout)rootView;
		int id = ResourceUtil.getId(mActivity, "content_container");
		mContentContainer = (LinearLayout)rootView.findViewById(id);
		if (mContentContainer == null)
		{
			mContentContainer = new LinearLayout(mActivity);
			mContentContainer.setOrientation(LinearLayout.VERTICAL);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			mRootView.addView(mContentContainer, 0, params);
		}
		id = ResourceUtil.getId(mActivity, "content");
		mContentView = rootView.findViewById(id);
		mLoadingIndicator = rootView.findViewById(mLoadingIndicatorId);
		mTitleBar = rootView.findViewById(mTitleBarId);
		// if (mTitleBar != null && mTitleBar instanceof ITitleAction)
		// {
		// mTitleAction = (ITitleAction)mTitleBar;
		// }
		mSubTitleBar = rootView.findViewById(mSubTitleBarId);
		mBottomBar = rootView.findViewById(mBottomBarId);
		// id = ResourceUtil.getId(mActivity, "left_verticalbar");
		// mLeftVerticalBar = layoutView.findViewById(id);
		// id = ResourceUtil.getId(mActivity, "right_verticalbar");
		// mRightVerticalBar = layoutView.findViewById(id);
		hideLoadingIndicator();
	}

	public View findViewById(int id)
	{
		return mRootView.findViewById(id);
	}

	public View getRootView()
	{
		return mRootView;
	}

	public View getContentLayout()
	{
		return mContentContainer;
	}

	@Override
	public void setBackgroundColor(int color)
	{
		mContentContainer.setBackgroundColor(color);
	}

	@Override
	public void setBackgroundResource(int resid)
	{
		mContentContainer.setBackgroundResource(resid);
	}

	@Override
	public void setBackgroundDrawable(Drawable background)
	{
		mContentContainer.setBackgroundDrawable(background);
	}

	@Override
	public void setContentView(int layoutResID)
	{
		setContentView(View.inflate(mActivity, layoutResID, null), null);
	}

	@Override
	public void setContentView(View view)
	{
		setContentView(view, null);
	}

	@Override
	public void setContentView(final View view, final LayoutParams params)
	{
		if (mContentView == view)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				setContentViewImpl(view, params);
			}
		});
	}

	private void setContentViewImpl(View view, LayoutParams params)
	{
		if (mContentView != null)
		{
			mContentContainer.removeView(mContentView);
		}
		mContentView = view;
		if (view != null)
		{
			if (params == null)
			{
				params = view.getLayoutParams();
			}
			LinearLayout.LayoutParams linear_params;
			if (params != null)
			{
				linear_params = new LinearLayout.LayoutParams(params);
				linear_params.width = LayoutParams.MATCH_PARENT;
				linear_params.height = 0;
			}
			else
			{
				linear_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
			}
			linear_params.weight = 1;
			int index = 0;
			if (mTitleBar != null)
			{
				index++;
			}
			if (mSubTitleBar != null)
			{
				index++;
			}
			mContentContainer.addView(view, index, linear_params);
		}
		onContentViewChanged();
	}

	@Override
	public View getContentView()
	{
		return mContentView;
	}

	@Override
	public void setLoadingIndicator(int layoutResID)
	{
		setLoadingIndicator(View.inflate(mActivity, layoutResID, null));
	}

	@Override
	public void setLoadingIndicator(final View view)
	{
		if (mLoadingIndicator == view)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				setLoadingIndicatorImpl(view);
			}
		});
	}

	private void setLoadingIndicatorImpl(View view)
	{
		if (mLoadingIndicator != null)
		{
			mRootView.removeView(mLoadingIndicator);
		}
		mLoadingIndicator = view;
		if (view == null)
		{
			return;
		}
		view.setId(mLoadingIndicatorId);
		LayoutParams params = view.getLayoutParams();
		FrameLayout.LayoutParams frame_params;
		if (params != null)
		{
			frame_params = new FrameLayout.LayoutParams(params);
			frame_params.width = LayoutParams.WRAP_CONTENT;
			frame_params.height = LayoutParams.WRAP_CONTENT;
			frame_params.gravity = Gravity.CENTER;
		}
		else
		{
			frame_params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					Gravity.CENTER);
		}
		view.setVisibility(View.GONE);
		mRootView.addView(view, frame_params);
		hideLoadingIndicator();
	}

	@Override
	public void showLoadingIndicator()
	{
		showLoadingIndicator(null);
	}

	@Override
	public void showLoadingIndicator(final String message)
	{
		if (mLoadingIndicator == null)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				int id = ResourceUtil.getId(mActivity, "message");
				TextView tv = (TextView)mLoadingIndicator.findViewById(id);
				if (tv != null)
				{
					if (!TextUtils.isEmpty(message))
					{
						tv.setText(message);
					}
					else
					{
						int resid = ResourceUtil.getStringId(mActivity, "loading_indicator_text");
						tv.setText(resid);
					}
				}
				mLoadingIndicator.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void hideLoadingIndicator()
	{
		if (mLoadingIndicator == null)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mLoadingIndicator.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void setTitleBar(int layoutResID)
	{
		setTitleBar(View.inflate(mActivity, layoutResID, null));
	}

	@Override
	public void setTitleBar(final View view)
	{
		if (mTitleBar == view)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				setTitleBarViewImpl(view);
			}
		});
	}

	private void setTitleBarViewImpl(View view)
	{
		if (mTitleBar != null)
		{
			mContentContainer.removeView(mTitleBar);
		}
		mTitleBar = view;
		if (view == null)
		{
			return;
		}
		view.setId(mTitleBarId);
		LayoutParams params = view.getLayoutParams();
		LinearLayout.LayoutParams linear_params;
		if (params != null)
		{
			linear_params = new LinearLayout.LayoutParams(params);
			linear_params.width = LayoutParams.MATCH_PARENT;
			linear_params.height = LayoutParams.WRAP_CONTENT;
		}
		else
		{
			linear_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		mContentContainer.addView(view, 0, linear_params);
		onTitleBarChanged();
	}

	@Override
	public View getTitleBar()
	{
		return mTitleBar;
	}

	@Override
	public void showTitleBar()
	{
		if (mTitleBar == null)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mTitleBar.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void hideTitleBar()
	{
		if (mTitleBar == null)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mTitleBar.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void setSubTitleBar(int layoutResID)
	{
		setSubTitleBar(View.inflate(mActivity, layoutResID, null));
	}

	@Override
	public void setSubTitleBar(final View view)
	{
		if (mSubTitleBar == view)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				setSubTitleBarViewImpl(view);
			}
		});
	}

	private void setSubTitleBarViewImpl(View view)
	{
		if (mSubTitleBar != null)
		{
			mContentContainer.removeView(mSubTitleBar);
		}
		mSubTitleBar = view;
		if (view == null)
		{
			return;
		}
		view.setId(mSubTitleBarId);
		LayoutParams params = view.getLayoutParams();
		LinearLayout.LayoutParams linear_params;
		if (params != null)
		{
			linear_params = new LinearLayout.LayoutParams(params);
			linear_params.width = LayoutParams.MATCH_PARENT;
			linear_params.height = LayoutParams.WRAP_CONTENT;
		}
		else
		{
			linear_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		if (mTitleBar == null)
		{
			mContentContainer.addView(view, 0, linear_params);
		}
		else
		{
			mContentContainer.addView(view, 1, linear_params);
		}
		onSubTitleBarChanged();
	}

	@Override
	public View getSubTitleBar()
	{
		return mSubTitleBar;
	}

	@Override
	public void showSubTitleBar()
	{
		if (mSubTitleBar == null)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mSubTitleBar.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void hideSubTitleBar()
	{
		if (mSubTitleBar == null)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mSubTitleBar.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void setBottomBar(int layoutResID)
	{
		setBottomBar(View.inflate(mActivity, layoutResID, null));
	}

	@Override
	public void setBottomBar(final View view)
	{
		if (mBottomBar == view)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				setBottomBarViewImpl(view);
			}
		});
	}

	private void setBottomBarViewImpl(View view)
	{
		if (mBottomBar != null)
		{
			mContentContainer.removeView(mBottomBar);
		}
		mBottomBar = view;
		if (view == null)
		{
			return;
		}
		view.setBottom(mBottomBarId);
		LayoutParams params = view.getLayoutParams();
		LinearLayout.LayoutParams linear_params;
		if (params != null)
		{
			linear_params = new LinearLayout.LayoutParams(params);
			linear_params.width = LayoutParams.MATCH_PARENT;
			linear_params.height = LayoutParams.WRAP_CONTENT;
		}
		else
		{
			linear_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		mContentContainer.addView(view, linear_params);
		onBottomBarChanged();
	}

	@Override
	public View getBottomBar()
	{
		return mBottomBar;
	}

	@Override
	public void showBottomBar()
	{
		if (mBottomBar == null)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mBottomBar.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void hideBottomBar()
	{
		if (mBottomBar == null)
		{
			return;
		}
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mBottomBar.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onTitleBarChanged()
	{
		if (mActivity instanceof IFrameUIWrapper)
		{
			((IFrameUIWrapper)mActivity).onTitleBarChanged();
		}
	}

	@Override
	public void onSubTitleBarChanged()
	{
		if (mActivity instanceof IFrameUIWrapper)
		{
			((IFrameUIWrapper)mActivity).onSubTitleBarChanged();
		}
	}

	@Override
	public void onContentViewChanged()
	{
		if (mActivity instanceof IFrameUIWrapper)
		{
			((IFrameUIWrapper)mActivity).onContentViewChanged();
		}
		else
		{
			mActivity.onContentChanged();
		}
	}

	@Override
	public void onBottomBarChanged()
	{
		if (mActivity instanceof IFrameUIWrapper)
		{
			((IFrameUIWrapper)mActivity).onBottomBarChanged();
		}
	}
}
