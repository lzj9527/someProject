package android.extend.widget.pull;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.extend.widget.pull.BasePullLoading.State;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

/**
 * 定义了下拉刷新和上拉加载更多的功能
 * 
 * @author Li Hong
 * @since 2013-7-29
 * @param <T>
 */
public abstract class BasePullView<T extends View> extends LinearLayout
{
	/**
	 * 定义了下拉刷新和上拉加载更多的接口。
	 * 
	 * @author Li Hong
	 * @since 2013-7-29
	 */
	public interface OnPullActionListener
	{

		/**
		 * 下拉松手后会被调用
		 * 
		 * @param pullView
		 */
		void onPullToRefresh(BasePullView<?> pullView);

		/**
		 * 加载更多时会被调用或上拉时调用
		 * 
		 * @param pullView
		 */
		void onPullToLoad(BasePullView<?> pullView);

		/**
		 * 用于判断是否还有更多数据可加载
		 */
		boolean hasMoreData();
	}

	public final String TAG = getClass().getSimpleName();

	/** 回滚的时间 */
	private static final int SCROLL_DURATION = 150;
	/** 阻尼系数 */
	private static final float OFFSET_RADIO = 2.5f;
	/** */
	private boolean mHorizontalLayout = false;
	/** 上一次移动的点 */
	private float mLastMotionValue = -1;
	/** 下拉刷新和加载更多的监听器 */
	private OnPullActionListener mPullActionListener;
	/** 下拉刷新的布局 */
	private BasePullLoading mHeaderLayout;
	/** 上拉加载更多的布局 */
	private BasePullLoading mFooterLayout;
	/** HeaderView的高度 */
	private int mHeaderSize;
	/** FooterView的高度 */
	private int mFooterSize;
	/** 下拉刷新是否可用 */
	private boolean mPullRefreshEnabled = true;
	/** 上拉加载是否可用 */
	private boolean mPullLoadEnabled = true;
	/** 判断滑动到底部加载是否可用 */
	private boolean mScrollLoadEnabled = false;
	/** 是否截断touch事件 */
	private boolean mInterceptEventEnable = true;
	/** 表示是否消费了touch事件，如果是，则不调用父类的onTouchEvent方法 */
	private boolean mIsHandledTouchEvent = false;
	/** 移动点的保护范围值 */
	private int mTouchSlop;
	/** 下拉的状态 */
	private State mPullRefreshState = State.NONE;
	/** 上拉的状态 */
	private State mPullLoadState = State.NONE;
	/** 可以下拉刷新的View */
	T mPullContentView;
	/** 平滑滚动的Runnable */
	private SmoothScrollRunnable mSmoothScrollRunnable;

	// /**可刷新View的包装布局*/
	// private FrameLayout mRefreshableViewWrapper;

	/**
	 * 构造方法
	 * 
	 * @param context context
	 */
	public BasePullView(Context context)
	{
		super(context);
		init(context, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param context context
	 * @param attrs attrs
	 */
	public BasePullView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
	}

	/**
	 * 构造方法
	 * 
	 * @param context context
	 * @param attrs attrs
	 * @param defStyle defStyle
	 */
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public BasePullView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@Override
	public boolean isInEditMode()
	{
		return true;
	}

	/**
	 * 初始化
	 * 
	 * @param context context
	 */
	private void init(Context context, AttributeSet attrs)
	{
		mHorizontalLayout = isHorizontalLayout();
		if (!mHorizontalLayout)
			setOrientation(LinearLayout.VERTICAL);
		else
			setOrientation(LinearLayout.HORIZONTAL);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		mHeaderLayout = createHeaderLoadingLayout(context, attrs);
		mFooterLayout = createFooterLoadingLayout(context, attrs);
		mPullContentView = createPullConentView(context, attrs);

		if (null == mPullContentView)
		{
			throw new NullPointerException("Refreshable view can not be null.");
		}

		addPullContentView(context, mPullContentView);
		addHeaderAndFooter(context);

		// 得到Header的高度，这个高度需要用这种方式得到，在onLayout方法里面得到的高度始终是0
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				getViewTreeObserver().removeOnGlobalLayoutListener(this);
				refreshLoadingViewsSize();
			}
		});

		onInit(context, attrs);
	}

	protected void onInit(Context context, AttributeSet attrs)
	{
	}

	/**
	 * 初始化padding，我们根据header和footer的高度来设置top padding和bottom padding
	 */
	private void refreshLoadingViewsSize()
	{
		// 得到header和footer的内容高度，它将会作为拖动刷新的一个临界值，
		// 如果拖动距离大于这个高度，然后再松开手，就会触发刷新操作
		int headerSize = (null != mHeaderLayout) ? mHeaderLayout.getContentSize() : 0;
		int footerSize = (null != mFooterLayout) ? mFooterLayout.getContentSize() : 0;

		if (headerSize < 0)
		{
			headerSize = 0;
		}

		if (footerSize < 0)
		{
			footerSize = 0;
		}

		mHeaderSize = headerSize;
		mFooterSize = footerSize;

		// 这里得到Header和Footer的高度，设置的padding的top和bottom就应该是header和footer的高度
		// 因为header和footer是完全看不见的
		if (!mHorizontalLayout)
		{
			headerSize = (null != mHeaderLayout) ? mHeaderLayout.getMeasuredHeight() : 0;
			footerSize = (null != mFooterLayout) ? mFooterLayout.getMeasuredHeight() : 0;
		}
		else
		{
			headerSize = (null != mHeaderLayout) ? mHeaderLayout.getMeasuredWidth() : 0;
			footerSize = (null != mFooterLayout) ? mFooterLayout.getMeasuredWidth() : 0;
		}
		if (0 == footerSize)
		{
			footerSize = mFooterSize;
		}
		// LogUtil.v(TAG, this + " refreshLoadingViewsSize: " + headerSize + "; " + footerSize);

		int pLeft = getPaddingLeft();
		int pTop = getPaddingTop();
		int pRight = getPaddingRight();
		int pBottom = getPaddingBottom();
		// LogUtil.v(TAG, this + " refreshLoadingViewsSize: " + pLeft + "; " + pTop + "; " + pRight + "; " + pBottom);

		if (!mHorizontalLayout)
		{
			pTop = -headerSize;
			pBottom = -footerSize;
		}
		else
		{
			pLeft = -headerSize;
			pRight = -footerSize;
		}

		setPadding(pLeft, pTop, pRight, pBottom);
	}

	@Override
	protected final void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		// LogUtil.v(TAG, this + " onSizeChanged: " + w + "; " + h + "; " + oldw + "; " + oldh);

		// We need to update the header/footer when our size changes
		refreshLoadingViewsSize();

		// 设置刷新View的大小
		refreshPullContentViewSize(w, h);

		/**
		 * As we're currently in a Layout Pass, we need to schedule another one
		 * to layout any changes we've made here
		 */
		post(new Runnable()
		{
			@Override
			public void run()
			{
				requestLayout();
			}
		});
	}

	@Override
	public void setOrientation(int orientation)
	{
		if (!mHorizontalLayout && LinearLayout.VERTICAL != orientation)
		{
			throw new IllegalArgumentException("This class only supports VERTICAL orientation.");
		}
		else if (mHorizontalLayout && LinearLayout.HORIZONTAL != orientation)
		{
			throw new IllegalArgumentException("This class only supports HORIZONTAL orientation.");
		}

		// Only support vertical orientation
		super.setOrientation(orientation);
	}

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event)
	{
		if (!isInterceptTouchEventEnabled())
		{
			return false;
		}

		if (!isPullLoadEnabled() && !isPullRefreshEnabled())
		{
			return false;
		}

		final int action = event.getAction();
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
		{
			mIsHandledTouchEvent = false;
			return false;
		}

		if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent)
		{
			return true;
		}

		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
				if (!mHorizontalLayout)
					mLastMotionValue = event.getY();
				else
					mLastMotionValue = event.getX();
				mIsHandledTouchEvent = false;
				break;

			case MotionEvent.ACTION_MOVE:
				final float deltaValue = (!mHorizontalLayout ? event.getY() : event.getX()) - mLastMotionValue;
				final float absDiff = Math.abs(deltaValue);
				// 这里有三个条件：
				// 1，位移差大于mTouchSlop，这是为了防止快速拖动引发刷新
				// 2，isPullRefreshing()，如果当前正在下拉刷新的话，是允许向上滑动，并把刷新的HeaderView挤上去
				// 3，isPullLoading()，理由与第2条相同
				if (absDiff > mTouchSlop || isPullRefreshing() || isPullLoading())
				{
					if (!mHorizontalLayout)
						mLastMotionValue = event.getY();
					else
						mLastMotionValue = event.getX();
					// 第一个显示出来，Header已经显示或拉下
					if (isPullRefreshEnabled() && isReadyForPullRefresh())
					{
						// 1，Math.abs(getScrollY()) > 0：表示当前滑动的偏移量的绝对值大于0，表示当前HeaderView滑出来了或完全
						// 不可见，存在这样一种case，当正在刷新时并且RefreshableView已经滑到顶部，向上滑动，那么我们期望的结果是
						// 依然能向上滑动，直到HeaderView完全不可见
						// 2，deltaY > 0.5f：表示下拉的值大于0.5f
						mIsHandledTouchEvent = (Math.abs(getScrollValue()) > 0 || deltaValue > 0.5f);
						// 如果截断事件，我们则仍然把这个事件交给刷新View去处理，
						// 典型的情况是让ListView/GridView将按下Child的Selector隐藏
						if (mIsHandledTouchEvent)
						{
							mPullContentView.onTouchEvent(event);
						}
					}
					else if (isPullLoadEnabled() && isReadyForPullLoad())
					{
						// 原理如上
						mIsHandledTouchEvent = (Math.abs(getScrollValue()) > 0 || deltaValue < -0.5f);
					}
				}
				break;

			default:
				break;
		}

		return mIsHandledTouchEvent;
	}

	@Override
	public final boolean onTouchEvent(MotionEvent event)
	{
		boolean handled = false;
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if (!mHorizontalLayout)
					mLastMotionValue = event.getY();
				else
					mLastMotionValue = event.getX();
				mIsHandledTouchEvent = false;
				break;

			case MotionEvent.ACTION_MOVE:
				final float deltaValue = (!mHorizontalLayout ? event.getY() : event.getX()) - mLastMotionValue;
				if (!mHorizontalLayout)
					mLastMotionValue = event.getY();
				else
					mLastMotionValue = event.getX();
				if (isPullRefreshEnabled() && isReadyForPullRefresh())
				{
					pullHeaderLayout(deltaValue / OFFSET_RADIO);
					handled = true;
				}
				else if (isPullLoadEnabled() && isReadyForPullLoad())
				{
					pullFooterLayout(deltaValue / OFFSET_RADIO);
					handled = true;
				}
				else
				{
					mIsHandledTouchEvent = false;
				}
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (mIsHandledTouchEvent)
				{
					mIsHandledTouchEvent = false;
					// 当第一个显示出来时
					if (isReadyForPullRefresh())
					{
						// 调用刷新
						if (mPullRefreshEnabled && (mPullRefreshState == State.RELEASE_TO_REFRESH))
						{
							startRefreshing();
							handled = true;
						}
						resetHeaderLayout();
					}
					else if (isReadyForPullLoad())
					{
						// 加载更多
						if (isPullLoadEnabled() && (mPullLoadState == State.RELEASE_TO_REFRESH))
						{
							startLoading();
							handled = true;
						}
						resetFooterLayout();
					}
				}
				break;

			default:
				break;
		}

		return handled;
	}

	/**
	 * 设置当前下拉刷新是否可用
	 * 
	 * @param pullRefreshEnabled true表示可用，false表示不可用
	 */
	public void setPullRefreshEnabled(boolean pullRefreshEnabled)
	{
		mPullRefreshEnabled = pullRefreshEnabled;
	}

	/**
	 * 设置当前上拉加载更多是否可用
	 * 
	 * @param pullLoadEnabled true表示可用，false表示不可用
	 */
	public void setPullLoadEnabled(boolean pullLoadEnabled)
	{
		mPullLoadEnabled = pullLoadEnabled;
	}

	/**
	 * 滑动到底部是否自动加载更多数据
	 * 
	 * @param scrollLoadEnabled 如果这个值为true的话，那么上拉加载更多的功能将会禁用
	 */
	public void setScrollAutoLoadEnabled(boolean scrollLoadEnabled)
	{
		mScrollLoadEnabled = scrollLoadEnabled;
	}

	/**
	 * 判断当前下拉刷新是否可用
	 * 
	 * @return true如果可用，false不可用
	 */
	public boolean isPullRefreshEnabled()
	{
		return mPullRefreshEnabled && (null != mHeaderLayout);
	}

	/**
	 * 判断上拉加载是否可用
	 * 
	 * @return true可用，false不可用
	 */
	public boolean isPullLoadEnabled()
	{
		return mPullLoadEnabled && (null != mFooterLayout);
	}

	/**
	 * 滑动到底部加载是否可用
	 * 
	 * @return true可用，否则不可用
	 */
	public boolean isScrollAutoLoadEnabled()
	{
		return mScrollLoadEnabled;
	}

	public void setOnPullActionListener(OnPullActionListener pullActionListener)
	{
		mPullActionListener = pullActionListener;
	}

	/**
	 * 结束下拉刷新
	 */
	public void onPullRefreshComplete()
	{
		if (isPullRefreshing())
		{
			mPullRefreshState = State.RESET;
			onStateChanged(State.RESET, true);

			// 回滚动有一个时间，我们在回滚完成后再设置状态为normal
			// 在将LoadingLayout的状态设置为normal之前，我们应该禁止
			// 截断Touch事件，因为设里有一个post状态，如果有post的Runnable
			// 未被执行时，用户再一次发起下拉刷新，如果正在刷新时，这个Runnable
			// 再次被执行到，那么就会把正在刷新的状态改为正常状态，这就不符合期望
			postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					setInterceptTouchEventEnabled(true);
					mHeaderLayout.setState(State.RESET);
				}
			}, getSmoothScrollDuration());

			resetHeaderLayout();
			setInterceptTouchEventEnabled(false);
		}
	}

	/**
	 * 结束上拉加载更多
	 */
	public void onPullLoadComplete()
	{
		if (isPullLoading())
		{
			mPullLoadState = State.RESET;
			onStateChanged(State.RESET, false);

			postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					setInterceptTouchEventEnabled(true);
					mFooterLayout.setState(State.RESET);
				}
			}, getSmoothScrollDuration());

			resetFooterLayout();
			setInterceptTouchEventEnabled(false);
		}
	}

	public T getPullContentView()
	{
		return mPullContentView;
	}

	/**
	 * 得到Header布局对象
	 * 
	 * @return Header布局对象
	 */
	public BasePullLoading getHeaderLoadingLayout()
	{
		return mHeaderLayout;
	}

	/**
	 * 得到Footer布局对象
	 * 
	 * @return Footer布局对象
	 */
	public BasePullLoading getFooterLoadingLayout()
	{
		return mFooterLayout;
	}

	/**
	 * 设置最后更新的时间文本
	 * 
	 * @param label 文本
	 */
	public void setLastUpdatedLabel(CharSequence label)
	{
		if (null != mHeaderLayout)
		{
			mHeaderLayout.setLastUpdatedLabel(label);
		}

		if (null != mFooterLayout)
		{
			mFooterLayout.setLastUpdatedLabel(label);
		}
	}

	/**
	 * 设置是否有更多数据的标志
	 * 
	 * @param hasMoreData true表示还有更多的数据，false表示没有更多数据了
	 */
	public void setHasMoreData(boolean hasMoreData)
	{
		if (!hasMoreData)
		{
			if (null != mFooterLayout)
			{
				mFooterLayout.setState(State.NO_MORE_DATA);
			}
		}
		else
		{
			if (null != mFooterLayout)
			{
				mFooterLayout.resetState();
			}
		}
	}

	/**
	 * 表示是否还有更多数据
	 * 
	 * @return true表示还有更多数据
	 */
	public boolean hasMoreData()
	{
		if ((null != mFooterLayout) && (mFooterLayout.getState() == State.NO_MORE_DATA))
		{
			return false;
		}

		return true;
	}

	/**
	 * 开始刷新，通常用于调用者主动刷新，典型的情况是进入界面，开始主动刷新，这个刷新并不是由用户拉动引起的
	 * 
	 * @param smoothScroll 表示是否有平滑滚动，true表示平滑滚动，false表示无平滑滚动
	 * @param delayMillis 延迟时间
	 */
	public void doPullRefreshing(final boolean smoothScroll, final long delayMillis)
	{
		postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				int newScrollValue = -mHeaderSize;
				int duration = smoothScroll ? SCROLL_DURATION : 0;

				startRefreshing();
				smoothScrollTo(newScrollValue, duration, 0);
			}
		}, delayMillis);
	}

	public void doPullRefreshing()
	{
		doPullRefreshing(true, 100);
	}

	/**
	 * 由子类实现，是否水平布局
	 * 
	 * @return true 水平拉动布局 false 垂直拉动布局
	 * */
	protected abstract boolean isHorizontalLayout();

	/**
	 * 创建可以刷新的View
	 * 
	 * @param context context
	 * @param attrs 属性
	 * @return View
	 */
	protected abstract T createPullConentView(Context context, AttributeSet attrs);

	/**
	 * 判断刷新的View是否滑动到顶部
	 * 
	 * @return true表示已经滑动到顶部，否则false
	 */
	protected abstract boolean isReadyForPullRefresh();

	/**
	 * 判断刷新的View是否滑动到底
	 * 
	 * @return true表示已经滑动到底部，否则false
	 */
	protected abstract boolean isReadyForPullLoad();

	/**
	 * 创建Header的布局
	 * 
	 * @param context context
	 * @param attrs 属性
	 * @return LoadingLayout对象
	 */
	protected BasePullLoading createHeaderLoadingLayout(Context context, AttributeSet attrs)
	{
		if (!mHorizontalLayout)
			return new HeaderLoading(context);
		else
			return new HorizontalHeaderLoading(context);
	}

	/**
	 * 创建Footer的布局
	 * 
	 * @param context context
	 * @param attrs 属性
	 * @return LoadingLayout对象
	 */
	protected BasePullLoading createFooterLoadingLayout(Context context, AttributeSet attrs)
	{
		if (!mHorizontalLayout)
			return new FooterLoading(context);
		else
			return new HorizontalFooterLoading(context);
	}

	/**
	 * 得到平滑滚动的时间，派生类可以重写这个方法来控件滚动时间
	 * 
	 * @return 返回值时间为毫秒
	 */
	protected long getSmoothScrollDuration()
	{
		return SCROLL_DURATION;
	}

	/**
	 * 计算刷新View的大小
	 * 
	 * @param width 当前容器的宽度
	 * @param height 当前容器的宽度
	 */
	protected void refreshPullContentViewSize(int width, int height)
	{
		// if (null != mRefreshableViewWrapper) {
		// LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRefreshableViewWrapper.getLayoutParams();
		// if (lp.height != height) {
		// lp.height = height;
		// mRefreshableViewWrapper.requestLayout();
		// }
		// }
		if (null != mPullContentView)
		{
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mPullContentView.getLayoutParams();
			// LogUtil.v(TAG, this + " refreshPullContentViewSize: " + width + "; " + height + "; " + lp.width + "; " +
			// lp.height);
			if (!mHorizontalLayout)
			{
				if (lp.height != height)
				{
					lp.height = height;
					mPullContentView.requestLayout();
				}
			}
			else
			{
				if (lp.width != width)
				{
					lp.width = width;
					mPullContentView.requestLayout();
				}
			}
		}
	}

	/**
	 * 将刷新View添加到当前容器中
	 * 
	 * @param context context
	 * @param refreshableView 可以刷新的View
	 */
	protected void addPullContentView(Context context, T pullContentView)
	{
		int width = ViewGroup.LayoutParams.MATCH_PARENT;
		int height = ViewGroup.LayoutParams.MATCH_PARENT;

		// 创建一个包装容器
		// mRefreshableViewWrapper = new FrameLayout(context);
		// mRefreshableViewWrapper.addView(refreshableView, width, height);

		// 这里把Refresh view的高度设置为一个很小的值，它的高度最终会在onSizeChanged()方法中设置为MATCH_PARENT
		// 这样做的原因是，如果此是它的height是MATCH_PARENT，那么footer得到的高度就是0，所以，我们先设置高度很小
		// 我们就可以得到header和footer的正常高度，当onSizeChanged后，Refresh view的高度又会变为正常。
		if (!mHorizontalLayout)
			height = 10;
		else
			width = 10;
		mPullContentView.setBackgroundColor(Color.TRANSPARENT);
		addView(mPullContentView, new LinearLayout.LayoutParams(width, height));
	}

	/**
	 * 添加Header和Footer
	 * 
	 * @param context context
	 */
	protected void addHeaderAndFooter(Context context)
	{
		LinearLayout.LayoutParams params;
		if (!mHorizontalLayout)
			params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		else
			params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.MATCH_PARENT);

		final BasePullLoading headerLayout = mHeaderLayout;
		final BasePullLoading footerLayout = mFooterLayout;

		if (null != headerLayout)
		{
			if (this == headerLayout.getParent())
			{
				removeView(headerLayout.getView());
			}

			addView(headerLayout.getView(), 0, params);
		}

		if (null != footerLayout)
		{
			if (this == footerLayout.getParent())
			{
				removeView(footerLayout.getView());
			}

			addView(footerLayout.getView(), -1, params);
		}
	}

	/**
	 * 拉动Header Layout时调用
	 * 
	 * @param delta 移动的距离
	 */
	protected void pullHeaderLayout(float delta)
	{
		// 向上滑动，并且当前scrollY为0时，不滑动
		int oldScrollValue = getScrollValue();
		if (delta < 0 && (oldScrollValue - delta) >= 0)
		{
			setScrollTo(0, 0);
			return;
		}

		// 向下滑动布局
		if (!mHorizontalLayout)
			setScrollBy(0, -(int)delta);
		else
			setScrollBy(-(int)delta, 0);

		if (null != mHeaderLayout && 0 != mHeaderSize)
		{
			float scale = Math.abs(getScrollValue()) / (float)mHeaderSize;
			mHeaderLayout.onPull(scale);
		}

		// 未处于刷新状态，更新箭头
		int scrollValue = Math.abs(getScrollValue());
		if (isPullRefreshEnabled() && !isPullRefreshing())
		{
			if (scrollValue > mHeaderSize)
			{
				mPullRefreshState = State.RELEASE_TO_REFRESH;
			}
			else
			{
				mPullRefreshState = State.PULL_TO_REFRESH;
			}

			mHeaderLayout.setState(mPullRefreshState);
			onStateChanged(mPullRefreshState, true);
		}
	}

	/**
	 * 拉Footer时调用
	 * 
	 * @param delta 移动的距离
	 */
	protected void pullFooterLayout(float delta)
	{
		int oldScrollValue = getScrollValue();
		if (delta > 0 && (oldScrollValue - delta) <= 0)
		{
			setScrollTo(0, 0);
			return;
		}

		if (!mHorizontalLayout)
			setScrollBy(0, -(int)delta);
		else
			setScrollBy(-(int)delta, 0);

		if (null != mFooterLayout && 0 != mFooterSize)
		{
			float scale = Math.abs(getScrollValue()) / (float)mFooterSize;
			mFooterLayout.onPull(scale);
		}

		int scrollValue = Math.abs(getScrollValue());
		if (isPullLoadEnabled() && !isPullLoading())
		{
			if (scrollValue > mFooterSize)
			{
				mPullLoadState = State.RELEASE_TO_REFRESH;
			}
			else
			{
				mPullLoadState = State.PULL_TO_REFRESH;
			}

			mFooterLayout.setState(mPullLoadState);
			onStateChanged(mPullLoadState, false);
		}
	}

	/**
	 * 重置header
	 */
	protected void resetHeaderLayout()
	{
		final int scrollValue = Math.abs(getScrollValue());
		final boolean refreshing = isPullRefreshing();

		if (refreshing && scrollValue <= mHeaderSize)
		{
			smoothScrollTo(0);
			return;
		}

		if (refreshing)
		{
			smoothScrollTo(-mHeaderSize);
		}
		else
		{
			smoothScrollTo(0);
		}
	}

	/**
	 * 重置footer
	 */
	protected void resetFooterLayout()
	{
		int scrollValue = Math.abs(getScrollValue());
		boolean isPullLoading = isPullLoading();

		if (isPullLoading && scrollValue <= mFooterSize)
		{
			smoothScrollTo(0);
			return;
		}

		if (isPullLoading)
		{
			smoothScrollTo(mFooterSize);
		}
		else
		{
			smoothScrollTo(0);
		}
	}

	/**
	 * 判断是否正在下拉刷新
	 * 
	 * @return true正在刷新，否则false
	 */
	protected boolean isPullRefreshing()
	{
		return (mPullRefreshState == State.REFRESHING);
	}

	/**
	 * 是否正的上拉加载更多
	 * 
	 * @return true正在加载更多，否则false
	 */
	protected boolean isPullLoading()
	{
		return (mPullLoadState == State.REFRESHING);
	}

	/**
	 * 开始刷新，当下拉松开后被调用
	 */
	protected void startRefreshing()
	{
		// 如果正在刷新
		if (isPullRefreshing())
		{
			return;
		}

		mPullRefreshState = State.REFRESHING;
		onStateChanged(State.REFRESHING, true);

		if (null != mHeaderLayout)
		{
			mHeaderLayout.setState(State.REFRESHING);
		}

		if (null != mPullActionListener)
		{
			// 因为滚动回原始位置的时间是200，我们需要等回滚完后才执行刷新回调
			postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					mPullActionListener.onPullToRefresh(BasePullView.this);
				}
			}, getSmoothScrollDuration());
		}
	}

	/**
	 * 开始加载更多，上拉松开后调用
	 */
	protected void startLoading()
	{
		// 如果正在加载
		if (isPullLoading())
		{
			return;
		}

		mPullLoadState = State.REFRESHING;
		onStateChanged(State.REFRESHING, false);

		if (null != mFooterLayout)
		{
			mFooterLayout.setState(State.REFRESHING);
		}

		if (null != mPullActionListener)
		{
			// 因为滚动回原始位置的时间是200，我们需要等回滚完后才执行加载回调
			postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					mPullActionListener.onPullToLoad(BasePullView.this);
				}
			}, getSmoothScrollDuration());
		}
	}

	/**
	 * 当状态发生变化时调用
	 * 
	 * @param state 状态
	 * @param isPullDown 是否向下
	 */
	protected void onStateChanged(State state, boolean isPullDown)
	{

	}

	/**
	 * 设置滚动位置
	 * 
	 * @param x 滚动到的x位置
	 * @param y 滚动到的y位置
	 */
	private void setScrollTo(int x, int y)
	{
		scrollTo(x, y);
	}

	/**
	 * 设置滚动的偏移
	 * 
	 * @param x 滚动x位置
	 * @param y 滚动y位置
	 */
	private void setScrollBy(int x, int y)
	{
		scrollBy(x, y);
	}

	/**
	 * 得到当前滚动值
	 * 
	 * @return 滚动值
	 */
	private int getScrollValue()
	{
		if (!mHorizontalLayout)
			return getScrollY();
		else
			return getScrollX();
	}

	/**
	 * 平滑滚动
	 * 
	 * @param newScrollValue 滚动的值
	 */
	private void smoothScrollTo(int newScrollValue)
	{
		smoothScrollTo(newScrollValue, getSmoothScrollDuration(), 0);
	}

	/**
	 * 平滑滚动
	 * 
	 * @param newScrollValue 滚动的值
	 * @param duration 滚动时候
	 * @param delayMillis 延迟时间，0代表不延迟
	 */
	private void smoothScrollTo(int newScrollValue, long duration, long delayMillis)
	{
		if (null != mSmoothScrollRunnable)
		{
			mSmoothScrollRunnable.stop();
		}

		int oldScrollValue = this.getScrollValue();
		boolean post = (oldScrollValue != newScrollValue);
		if (post)
		{
			mSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration);
		}

		if (post)
		{
			if (delayMillis > 0)
			{
				postDelayed(mSmoothScrollRunnable, delayMillis);
			}
			else
			{
				post(mSmoothScrollRunnable);
			}
		}
	}

	/**
	 * 设置是否截断touch事件
	 * 
	 * @param enabled true截断，false不截断
	 */
	private void setInterceptTouchEventEnabled(boolean enabled)
	{
		mInterceptEventEnable = enabled;
	}

	/**
	 * 标志是否截断touch事件
	 * 
	 * @return true截断，false不截断
	 */
	private boolean isInterceptTouchEventEnabled()
	{
		return mInterceptEventEnable;
	}

	/**
	 * 实现了平滑滚动的Runnable
	 * 
	 * @author Li Hong
	 * @since 2013-8-22
	 */
	final class SmoothScrollRunnable implements Runnable
	{
		/** 动画效果 */
		private final Interpolator mInterpolator;
		/** 结束Y */
		private final int mScrollToValue;
		/** 开始Y */
		private final int mScrollFromValue;
		/** 滑动时间 */
		private final long mDuration;
		/** 是否继续运行 */
		private boolean mContinueRunning = true;
		/** 开始时刻 */
		private long mStartTime = -1;
		/** 当前Y */
		private int mCurrentValue = -1;

		/**
		 * 构造方法
		 * 
		 * @param fromY 开始Y
		 * @param toY 结束Y
		 * @param duration 动画时间
		 */
		public SmoothScrollRunnable(int fromValue, int toValue, long duration)
		{
			mScrollFromValue = fromValue;
			mScrollToValue = toValue;
			mDuration = duration;
			mInterpolator = new DecelerateInterpolator();
		}

		@Override
		public void run()
		{
			/**
			 * If the duration is 0, we scroll the view to target y directly.
			 */
			if (mDuration <= 0)
			{
				if (!mHorizontalLayout)
					setScrollTo(0, mScrollToValue);
				else
					setScrollTo(mScrollToValue, 0);
				return;
			}

			/**
			 * Only set mStartTime if this is the first time we're starting,
			 * else actually calculate the Y delta
			 */
			if (mStartTime == -1)
			{
				mStartTime = System.currentTimeMillis();
			}
			else
			{

				/**
				 * We do do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				final long oneSecond = 1000; // SUPPRESS CHECKSTYLE
				long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime)) / mDuration;
				normalizedTime = Math.max(Math.min(normalizedTime, oneSecond), 0);

				final int deltaY = Math.round((mScrollFromValue - mScrollToValue)
						* mInterpolator.getInterpolation(normalizedTime / (float)oneSecond));
				mCurrentValue = mScrollFromValue - deltaY;

				if (!mHorizontalLayout)
					setScrollTo(0, mCurrentValue);
				else
					setScrollTo(mCurrentValue, 0);
			}

			// If we're not at the target Y, keep going...
			if (mContinueRunning && mScrollToValue != mCurrentValue)
			{
				BasePullView.this.postDelayed(this, 16);// SUPPRESS CHECKSTYLE
			}
		}

		/**
		 * 停止滑动
		 */
		public void stop()
		{
			mContinueRunning = false;
			removeCallbacks(this);
		}
	}
}
