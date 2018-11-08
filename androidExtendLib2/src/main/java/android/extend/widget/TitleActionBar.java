package android.extend.widget;

import android.content.Context;
import android.content.Intent;
import android.extend.util.ResourceUtil;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class TitleActionBar extends ExtendFrameLayout implements ITitleAction
{
	private View mTitleView;
	private View mLeftButton;
	private View mRightButton;

	public TitleActionBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		ensureViews();
	}

	public TitleActionBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		ensureViews();
	}

	public TitleActionBar(Context context)
	{
		super(context);
		ensureViews();
	}

	private void ensureViews()
	{
		if (mTitleView == null)
		{
			mTitleView = findViewById(android.R.id.title);
		}
		if (mTitleView == null)
		{
			mTitleView = createTitleView();
			addView(mTitleView, 0);
		}
		if (mLeftButton == null)
		{
			int id = ResourceUtil.getId(getContext(), "left_button");
			mLeftButton = findViewById(id);
		}
		if (mLeftButton == null)
		{
			mLeftButton = createLeftButton();
			addView(mLeftButton);
		}
		if (mRightButton == null)
		{
			int id = ResourceUtil.getId(getContext(), "right_button");
			mRightButton = findViewById(id);
		}
		if (mRightButton == null)
		{
			mRightButton = createRightButton();
			addView(mRightButton);
		}
	}

	private View createTitleView()
	{
		TextView view = new TextView(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		view.setLayoutParams(params);
		view.setId(android.R.id.title);
		return view;
	}

	private View createLeftButton()
	{
		Button button = new Button(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT
				| Gravity.CENTER_VERTICAL);
		button.setLayoutParams(params);
		int id = ResourceUtil.getId(getContext(), "left_button");
		button.setId(id);
		button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		return button;
	}

	private View createRightButton()
	{
		Button button = new Button(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT
				| Gravity.CENTER_VERTICAL);
		button.setLayoutParams(params);
		int id = ResourceUtil.getId(getContext(), "right_button");
		button.setId(id);
		button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		return button;
	}

	// private void setTitleViewImpl(View view)
	// {
	// if (view == null || mTitleView == view)
	// {
	// return;
	// }
	// if (mTitleView != null)
	// {
	// removeView(mTitleView);
	// }
	// mTitleView = view;
	// if (mTitleView != null)
	// {
	// mTitleView.setId(R.id.title);
	// addView(mTitleView, 0);
	// }
	// }

	@Override
	public void setTitle(CharSequence text)
	{
		// ensureViews();
		if (mTitleView != null && mTitleView instanceof TextView)
		{
			((TextView)mTitleView).setText(text);
		}
	}

	@Override
	public void setTitle(int textResID)
	{
		setTitle(getResources().getText(textResID));
	}

	@Override
	public void setTitleColor(int textColor)
	{
		// ensureViews();
		if (mTitleView != null && mTitleView instanceof TextView)
		{
			((TextView)mTitleView).setTextColor(textColor);
		}
	}

	@Override
	public void setTitleSize(float textSize)
	{
		// ensureViews();
		if (mTitleView != null && mTitleView instanceof TextView)
		{
			((TextView)mTitleView).setTextSize(textSize);
		}
	}

	@Override
	public void setTitleImage(int imageResID)
	{
		// ensureViews();
		if (mTitleView != null)
		{
			if (mTitleView instanceof TextView)
			{
				((TextView)mTitleView).setText("");
			}
			mTitleView.setBackgroundResource(imageResID);
		}
	}

	private void setButtonImage(View view, int resID)
	{
		if (resID > 0)
		{
			if (view instanceof ImageView)
			{
				((ImageView)view).setImageResource(resID);
			}
			else
			{
				view.setBackgroundResource(resID);
			}
		}
	}

	@Override
	public void setLeftButton(int resID, OnClickListener listener)
	{
		// ensureViews();
		if (mLeftButton != null)
		{
			setButtonImage(mLeftButton, resID);
			if (listener != null)
			{
				mLeftButton.setOnClickListener(listener);
			}
		}
	}

	@Override
	public void setLeftButton(int resID, final Intent intent)
	{
		// ensureViews();
		if (mLeftButton != null)
		{
			setButtonImage(mLeftButton, resID);
			if (intent != null)
			{
				mLeftButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						getContext().startActivity(intent);
					}
				});
			}
		}
	}

	@Override
	public void setRightButton(int resID, OnClickListener listener)
	{
		// ensureViews();
		if (mRightButton != null)
		{
			setButtonImage(mRightButton, resID);
			if (listener != null)
			{
				mRightButton.setOnClickListener(listener);
			}
		}
	}

	@Override
	public void setRightButton(int resID, final Intent intent)
	{
		// ensureViews();
		if (mRightButton != null)
		{
			setButtonImage(mRightButton, resID);
			if (intent != null)
			{
				mRightButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						getContext().startActivity(intent);
					}
				});
			}
		}
	}

	@Override
	public void setLeftButtonVisible(boolean visible)
	{
		// ensureViews();
		if (mLeftButton != null)
		{
			mLeftButton.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void setRightButtonVisible(boolean visible)
	{
		// ensureViews();
		if (mRightButton != null)
		{
			mRightButton.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}
}
