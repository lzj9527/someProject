package android.extend.widget.pull;

import android.content.Context;
import android.extend.widget.pull.BasePullLoading.State;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.GridView;

/**
 * 这个类实现了GridView下拉刷新，上加载更多和滑到底部自动加载
 * 
 * @author Li Hong
 * @since 2013-8-15
 */
public class PullGridView extends BasePullView<GridView> implements OnScrollListener {
    
    /**GridView*/
    private GridView mGridView;
    /**用于滑到底部自动加载的Footer*/
    private BasePullLoading mFooterLayout;
    /**滚动的监听器*/
    private OnScrollListener mScrollListener;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public PullGridView(Context context) {
        super(context);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public PullGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public PullGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
	protected void onInit(Context context, AttributeSet attrs)
	{
    	setPullLoadEnabled(false);
	}

	@Override
    protected boolean isHorizontalLayout() {
    	return false;
    }
    
    @Override
    protected GridView createPullConentView(Context context, AttributeSet attrs) {
        GridView gridView = new GridView(context);
        mGridView = gridView;
        gridView.setOnScrollListener(this);
        
        return gridView;
    }
    
    @Override
    public void setHasMoreData(boolean hasMoreData) {
    	super.setHasMoreData(hasMoreData);
        if (null != mFooterLayout) {
            if (!hasMoreData) {
                mFooterLayout.setState(State.NO_MORE_DATA);
            }
        }
    }

    /**
     * 设置滑动的监听器
     * 
     * @param l 监听器
     */
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }
    
    @Override
    protected boolean isReadyForPullLoad() {
        return isLastItemVisible();
    }

    @Override
    protected boolean isReadyForPullRefresh() {
        return isFirstItemVisible();
    }

    @Override
    protected void startLoading() {
        super.startLoading();
        
        
        if (null != mFooterLayout) {
            mFooterLayout.setState(State.REFRESHING);
        }
    }
    
    @Override
    public void onPullLoadComplete() {
        super.onPullLoadComplete();
        
        if (null != mFooterLayout) {
            mFooterLayout.setState(State.RESET);
        }
    }
    
    @Override
    public void setScrollAutoLoadEnabled(boolean scrollLoadEnabled) {
        super.setScrollAutoLoadEnabled(scrollLoadEnabled);
        
        if (scrollLoadEnabled) {
        	// 设置Footer
            if (null == mFooterLayout) {
                mFooterLayout = new FooterLoading(getContext());
            }
            
            //mGridView.removeFooterView(mFooterLayout);
            //mGridView.addFooterView(mFooterLayout, null, false);
        } else {
            if (null != mFooterLayout) {
                //mGridView.removeFooterView(mFooterLayout);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isScrollAutoLoadEnabled() && hasMoreData()) {
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE 
                    || scrollState == OnScrollListener.SCROLL_STATE_FLING) {
                if (isReadyForPullLoad()) {
                    startLoading();
                }
            }
        }
        
        if (null != mScrollListener) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (null != mScrollListener) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
    
    @Override
    public boolean hasMoreData() {
        if ((null != mFooterLayout) && (mFooterLayout.getState() == State.NO_MORE_DATA)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 判断第一个child是否完全显示出来
     * 
     * @return true完全显示出来，否则false
     */
    private boolean isFirstItemVisible() {
        final Adapter adapter = mGridView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        }

        int mostTop = (mGridView.getChildCount() > 0) ? mGridView.getChildAt(0).getTop() : 0;
        if (mostTop >= 0) {
            return true;
        }

        return false;
    }

    /**
     * 判断最后一个child是否完全显示出来
     * 
     * @return true完全显示出来，否则false
     */
    private boolean isLastItemVisible() {
        final Adapter adapter = mGridView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        }

        final int lastItemPosition = adapter.getCount() - 1;
        final int lastVisiblePosition = mGridView.getLastVisiblePosition();

        /**
         * This check should really just be: lastVisiblePosition == lastItemPosition, but ListView
         * internally uses a FooterView which messes the positions up. For me we'll just subtract
         * one to account for it and rely on the inner condition which checks getBottom().
         */
        if (lastVisiblePosition >= lastItemPosition - 1) {
            final int childIndex = lastVisiblePosition - mGridView.getFirstVisiblePosition();
            final int childCount = mGridView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = mGridView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= mGridView.getBottom();
            }
        }

        return false;
    }
}
