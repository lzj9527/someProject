package android.extend.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MenuBar extends FrameLayout
{
	public interface OnMenuListener
	{
		void onMenuSelected(MenuBar menuBar, MenuView menuView, int menuIndex);

		void onMenuUnSelected(MenuBar menuBar, MenuView menuView, int menuIndex);
	}

	public static final String TAG = MenuBar.class.getSimpleName();

	private boolean mHasInitialized = false;
	private int mMenuGroupOrientation = LinearLayout.HORIZONTAL;
	private int mMenuGroupGravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
	private ViewGroup mMenuGroupLayout;
	private List<MenuView> mMenuList = new ArrayList<MenuView>();
	private View mMenuCursor;
	private OnMenuListener mMenuListener;
	private int mCurrentIndex = -1;
	private boolean mAllowRepeatClickMenu = false;
	// private int mSpacingID;
	// private Drawable mMenuSpacingDrawable;
	// private int mMenuSpacingResID;
	// private int mMenuSpacingColor = -1;
	// private int mMenuSpacingWidth;
	// private int mMenuSpacingHeight;

	private View.OnClickListener mClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			int index = mMenuList.indexOf(view);
			setCurrentMenu(index);
		}
	};

	private static final int MSG_CHANGEMENU_INDEX = 0x01;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case MSG_CHANGEMENU_INDEX:
					setCurrentMenu(msg.arg1);
					break;
			}
		}
	};

	public MenuBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public MenuBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public MenuBar(Context context)
	{
		super(context);
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		initialize();
		checkMenuCursor();
	}

	// 这个方法要放到onFinishInflate之后执行，否则通过xml设置的child不会添加进来
	private void initialize()
	{
		if (mHasInitialized)
			return;
		mHasInitialized = true;
		LogUtil.d(TAG, "initialize: " + getChildCount());

		// mSpacingID = ResourceUtil.getId(getContext(), "spacing");
		// if (mSpacingID == 0)
		// mSpacingID = Integer.MAX_VALUE;
		// mMenuItemID = ResourceUtil.getId(getContext(), "menuitem");
		// if (mMenuItemID == 0)
		// mMenuItemID = Integer.MAX_VALUE - 1;

		mCurrentIndex = -1;
		checkMenuViews();
		findMenuCursor();
	}

	@Deprecated
	public void setMenuGroupOrientation(int orientation)
	{
		mMenuGroupOrientation = orientation;
		if (mMenuGroupLayout != null)
			if (mMenuGroupLayout instanceof LinearLayout)
				((LinearLayout)mMenuGroupLayout).setOrientation(orientation);
	}

	@Deprecated
	public void setMenuGroupGravity(int gravity)
	{
		mMenuGroupGravity = gravity;
		if (mMenuGroupLayout != null)
			if (mMenuGroupLayout instanceof LinearLayout)
				((LinearLayout)mMenuGroupLayout).setGravity(gravity);
	}

	public ViewGroup getMenuGroup()
	{
		ensureMenuGroup();
		return mMenuGroupLayout;
	}

	private void ensureMenuGroup()
	{
		if (mMenuGroupLayout == null)
		{
			int id = ResourceUtil.getId(getContext(), "menugroup");
			mMenuGroupLayout = (ViewGroup)findViewById(id);
			if (mMenuGroupLayout == null)
			{
				mMenuGroupLayout = new LinearLayout(getContext());
				mMenuGroupLayout.setId(id);
				((LinearLayout)mMenuGroupLayout).setOrientation(mMenuGroupOrientation);
				if (mMenuGroupGravity != Gravity.NO_GRAVITY)
					((LinearLayout)mMenuGroupLayout).setGravity(mMenuGroupGravity);
				else
				{
					switch (mMenuGroupOrientation)
					{
						case LinearLayout.HORIZONTAL:
							((LinearLayout)mMenuGroupLayout).setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
							break;
						case LinearLayout.VERTICAL:
						default:
							((LinearLayout)mMenuGroupLayout).setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
							break;
					}
				}
				mMenuGroupLayout.setBackgroundColor(Color.TRANSPARENT);
				addView(mMenuGroupLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
		}
	}

	private void checkMenuViews()
	{
		ensureMenuGroup();
		mMenuList.clear();
		int count = mMenuGroupLayout.getChildCount();
		for (int i = count - 1; i >= 0; i--)
		{
			View child = mMenuGroupLayout.getChildAt(i);
			if (child instanceof MenuView)
			{
				mMenuList.add(0, (MenuView)child);
				if (child.isClickable())
				{
					child.setSelected(false);
					child.setFocusable(true);
					child.setOnClickListener(mClickListener);
				}
			}
		}
		int size = mMenuList.size();
		for (int i = 0; i < size; i++)
		{
			MenuView menu = mMenuList.get(i);
			if (mMenuGroupLayout.indexOfChild(menu) == -1)
			{
				mMenuGroupLayout.addView(menu);
				if (menu.isClickable())
				{
					menu.setSelected(false);
					menu.setFocusable(true);
					menu.setOnClickListener(mClickListener);
				}
			}
		}
		// checkAllMenuSpacing();
		// if (!mMenuList.isEmpty() && mCurrentIndex < 0)
		// {
		// setCurrentMenuInternal(0, false);
		// }
	}

	private void findMenuCursor()
	{
		if (mMenuCursor == null)
		{
			int id = ResourceUtil.getId(getContext(), "menucursor");
			mMenuCursor = findViewById(id);
		}
	}

	// public void setMenuCursor(View cursor, boolean alignTop)
	// {
	// if (cursor == mMenuCursor)
	// {
	// return;
	// }
	// if (mMenuCursor != null)
	// {
	// removeView(mMenuCursor);
	// }
	// mMenuCursor = cursor;
	// if (alignTop)
	// {
	// addView(cursor, 0);
	// }
	// else
	// {
	// addView(cursor);
	// }
	// requestInitMenuCursor();
	// }

	public void setOnMenuListener(OnMenuListener listener)
	{
		mMenuListener = listener;
	}

	// public void setMenuSpacing(int width, int height)
	// {
	// if (mMenuSpacingWidth == width && mMenuSpacingHeight == height)
	// return;
	// LogUtil.d(TAG, "setMenuSpacing: " + width + "; " + height);
	// mMenuSpacingWidth = width;
	// mMenuSpacingHeight = height;
	// checkAllMenuSpacing();
	// }

	// public void setMenuSpacingDrawable(Drawable drawable)
	// {
	// if (mMenuSpacingDrawable == drawable)
	// return;
	// mMenuSpacingDrawable = drawable;
	// mMenuSpacingResID = 0;
	// mMenuSpacingColor = -1;
	// checkAllMenuSpacing();
	// }

	// public void setMenuSpacingResID(int drawableResID)
	// {
	// if (mMenuSpacingResID == drawableResID)
	// return;
	// mMenuSpacingResID = drawableResID;
	// mMenuSpacingDrawable = null;
	// mMenuSpacingColor = -1;
	// checkAllMenuSpacing();
	// }

	// public void setMenuSpacingColor(int color)
	// {
	// if (mMenuSpacingColor == color)
	// return;
	// mMenuSpacingColor = color;
	// mMenuSpacingDrawable = null;
	// mMenuSpacingResID = 0;
	// checkAllMenuSpacing();
	// }

	public MenuView addMenu(MenuView menu, ViewGroup.LayoutParams params)
	{
		if (menu == null || mMenuList.contains(menu))
			return menu;
		ensureMenuGroup();
		mMenuList.add(menu);
		if (params == null)
			mMenuGroupLayout.addView(menu);
		else
			mMenuGroupLayout.addView(menu, params);
		if (menu.isClickable())
		{
			menu.setSelected(false);
			menu.setFocusable(true);
			menu.setOnClickListener(mClickListener);
		}
		// if (mCurrentIndex < 0)
		// {
		// setCurrentMenuInternal(0, false);
		// }
		return menu;
	}

	public MenuView addMenu(MenuView menu)
	{
		return addMenu(menu, null);
	}

	public void removeMenu(int index)
	{
		if (index < 0 || index > mMenuList.size() - 1 || mMenuGroupLayout == null)
			return;
		MenuView menu = mMenuList.remove(index);
		mMenuGroupLayout.removeView(menu);
	}

	public void removeAllMenus()
	{
		mMenuList.clear();
		if (mMenuGroupLayout != null)
			mMenuGroupLayout.removeAllViews();
		mCurrentIndex = -1;
	}

	public MenuView getMenu(int index)
	{
		if (index < 0 || index >= mMenuList.size())
			return null;
		return mMenuList.get(index);
	}

	public int getCurrentMenuIdx()
	{
		return mCurrentIndex;
	}

	public MenuView getCurrentMenu()
	{
		return getMenu(mCurrentIndex);
	}

	public int getMenuCount()
	{
		return mMenuList.size();
	}

	private void setCurrentMenuInternal(int index)
	{
		LogUtil.d(TAG, "setCurrentMenuInternal: " + index + "; " + mCurrentIndex + "; " + mAllowRepeatClickMenu);
		if (index == mCurrentIndex)
		{
			if (mAllowRepeatClickMenu)
			{
				MenuView menu = getMenu(index);
				if (menu != null && mMenuListener != null)
				{
					mMenuListener.onMenuSelected(this, menu, index);
					checkMenuCursor();
				}
			}
			return;
		}
		MenuView curMenu = getCurrentMenu();
		if (curMenu != null)
		{
			curMenu.setSelected(false);
			if (mMenuListener != null)
			{
				mMenuListener.onMenuUnSelected(this, curMenu, mCurrentIndex);
			}
		}
		MenuView nextMenu = getMenu(index);
		if (nextMenu != null)
		{
			nextMenu.setSelected(true);
			if (mMenuListener != null)
			{
				mMenuListener.onMenuSelected(this, nextMenu, index);
			}
			if (curMenu != null)
			{
				startMenuCursorMoveAnimation(mCurrentIndex, index);
				mCurrentIndex = index;
			}
			else
			{
				mCurrentIndex = index;
				checkMenuCursor();
			}
		}
		else
		{
			mCurrentIndex = index;
			checkMenuCursor();
		}
	}

	public void setCurrentMenu(int index)
	{
		if (!mHasInitialized)
		{
			mHandler.removeMessages(MSG_CHANGEMENU_INDEX);
			Message msg = mHandler.obtainMessage(MSG_CHANGEMENU_INDEX);
			msg.arg1 = index;
			mHandler.sendMessageDelayed(msg, 100);
			return;
		}
		setCurrentMenuInternal(index);
	}

	public int indexOfMenu(MenuView menu)
	{
		return mMenuList.indexOf(menu);
	}

	public int indexOfMenu(int id)
	{
		int size = mMenuList.size();
		for (int i = 0; i < size; i++)
		{
			MenuView menu = mMenuList.get(i);
			if (menu.getId() == id)
				return i;
		}
		return -1;
	}

	public void setAllowRepeatClickMenu(boolean value)
	{
		mAllowRepeatClickMenu = value;
	}

	private void checkMenuCursor()
	{
		findMenuCursor();
		if (mMenuCursor == null)
			return;
		if (getMenuCount() == 0)
		{
			mMenuCursor.setVisibility(View.GONE);
			return;
		}
		post(new Runnable()
		{
			@Override
			public void run()
			{
				View menu = getMenu(mCurrentIndex);
				if (menu == null)
				{
					mMenuCursor.setVisibility(View.GONE);
					return;
				}
				if (mMenuGroupLayout instanceof LinearLayout)
				{
					int orientation = ((LinearLayout)mMenuGroupLayout).getOrientation();
					switch (orientation)
					{
						case LinearLayout.HORIZONTAL:
							if (menu.getMeasuredWidth() == 0)
							{
								postDelayed(this, 500);
								return;
							}
							break;
						case LinearLayout.VERTICAL:
						default:
							if (menu.getMeasuredHeight() == 0)
							{
								postDelayed(this, 500);
								return;
							}
							break;
					}
					// LogUtil.v(TAG,
					// "checkMenuCursor currentMenu: " + menu + "; " + menu.getLeft() + "; " + menu.getMeasuredWidth());
					if (mMenuCursor.getVisibility() != View.VISIBLE)
					{
						mMenuCursor.setVisibility(View.VISIBLE);
					}
					MarginLayoutParams params;
					if (mMenuCursor.getLayoutParams() != null)
					{
						params = (MarginLayoutParams)mMenuCursor.getLayoutParams();
					}
					else
					{
						params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					}
					switch (orientation)
					{
						case LinearLayout.HORIZONTAL:
							if (params.width != menu.getMeasuredWidth())
							{
								mMenuCursor.clearAnimation();
								params.width = menu.getMeasuredWidth();
								params.leftMargin = menu.getLeft();
								mMenuCursor.setLayoutParams(params);
							}
							break;
						case LinearLayout.VERTICAL:
						default:
							if (params.height != menu.getMeasuredHeight())
							{
								mMenuCursor.clearAnimation();
								params.height = menu.getMeasuredHeight();
								params.topMargin = menu.getTop();
								mMenuCursor.setLayoutParams(params);
							}
							break;
					}
				}
			}
		});
	}

	private void startMenuCursorMoveAnimation(int fromIndex, int toIndex)
	{
		findMenuCursor();
		if (mMenuCursor == null || fromIndex == toIndex)
			return;
		Animation animation;
		if (mMenuGroupLayout instanceof LinearLayout)
		{
			int orientation = ((LinearLayout)mMenuGroupLayout).getOrientation();
			switch (orientation)
			{
				case LinearLayout.HORIZONTAL:
					float fromXDelta = getMenu(fromIndex).getLeft() - mMenuCursor.getLeft();
					float toXDelta = getMenu(toIndex).getLeft() - mMenuCursor.getLeft();
					animation = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);
					break;
				case LinearLayout.VERTICAL:
				default:
					float fromYDelta = getMenu(fromIndex).getTop() - mMenuCursor.getTop();
					float toYDelta = getMenu(toIndex).getTop() - mMenuCursor.getTop();
					animation = new TranslateAnimation(0, 0, fromYDelta, toYDelta);
					break;
			}
			animation.setFillAfter(true);
			animation.setDuration(300);
			if (mMenuCursor.getVisibility() != View.VISIBLE)
				mMenuCursor.setVisibility(View.VISIBLE);
			mMenuCursor.startAnimation(animation);
		}
	}

	// private View createMenuSpacingView()
	// {
	// View spacingView = new View(getContext());
	// spacingView.setId(mSpacingID);
	// LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	// checkMenuSpacingView(spacingView, params);
	// return spacingView;
	// }

	// private void checkMenuSpacingView(View spacingView, LayoutParams params)
	// {
	// if (mMenuSpacingDrawable != null)
	// {
	// spacingView.setBackgroundDrawable(mMenuSpacingDrawable);
	// }
	// else if (mMenuSpacingResID > 0)
	// {
	// spacingView.setBackgroundResource(mMenuSpacingResID);
	// }
	// else if (mMenuSpacingColor != -1)
	// {
	// spacingView.setBackgroundColor(mMenuSpacingColor);
	// }
	// params.width = mMenuSpacingWidth;
	// params.height = mMenuSpacingHeight;
	// spacingView.setLayoutParams(params);
	// }

	// private void checkAllMenuSpacing()
	// {
	// if (mMenuGroupLayout == null)
	// return;
	// for (int i = 1; i < mMenuList.size(); i++)
	// {
	// MenuView menu = mMenuList.get(i);
	// int index = mMenuGroupLayout.indexOfChild(menu);
	// View view = mMenuGroupLayout.getChildAt(index - 1);
	// if (view == null || view.getId() != mSpacingID)
	// {
	// View spacingView = createMenuSpacingView();
	// mMenuGroupLayout.addView(spacingView, index);
	// }
	// else
	// {
	// LayoutParams params = (LayoutParams)view.getLayoutParams();
	// checkMenuSpacingView(view, params);
	// }
	// }
	// }

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	// {
	// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	// }

	// @Override
	// protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	// {
	// super.onLayout(changed, left, top, right, bottom);
	// }
}
