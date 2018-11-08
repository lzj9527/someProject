package android.extend.widget.pull;

import android.content.Context;
import android.extend.util.LogUtil;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * 下拉刷新和上拉加载更多的界面接口
 * 
 * @author Li Hong
 * @since 2013-8-16
 */
public abstract class BasePullLoading {
    /**
     * 当前的状态
     */
    public enum State {
        
        /**
         * Initial state
         */
        NONE,
        
        /**
         * When the UI is in a state which means that user is not interacting
         * with the Pull-to-Refresh function.
         */
        RESET,
        
        /**
         * When the UI is being pulled by the user, but has not been pulled far
         * enough so that it refreshes when released.
         */
        PULL_TO_REFRESH,
        
        /**
         * When the UI is being pulled by the user, and <strong>has</strong>
         * been pulled far enough so that it will refresh when released.
         */
        RELEASE_TO_REFRESH,
        
        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        REFRESHING,
        
        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        @Deprecated
        LOADING,
        
        /**
         * No more data
         */
        NO_MORE_DATA,
    }
	
    public final String TAG = getClass().getSimpleName();
    
    /**Context*/
    private Context mContext;
    /**loadingView*/
    private View mLoadingView;
    /**当前的状态*/
    private State mCurState = State.NONE;
    /**前一个状态*/
    private State mPreState = State.NONE;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public BasePullLoading(Context context) {
//        this(context, null);
        mContext = context;
        init(context);
    }
   
    /**
     * 初始化
     * 
     * @param context context
     */
    protected void init(Context context) {
        mLoadingView = createLoadingView(context);
        if (null == mLoadingView) {
            throw new NullPointerException("Loading view can not be null.");
        }
        
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT, 
//                LayoutParams.WRAP_CONTENT);
//        addView(mContainer, params);
    }

    public Context getContext() {
    	return mContext;
    }
    
    public View getView() {
    	return mLoadingView;
    }
    
    public View findViewById(int id) {
    	return mLoadingView.findViewById(id);
    }
    
    public int getVisibility() {
    	return mLoadingView.getVisibility();
    }
    
    public int getMeasuredWidth() {
    	return mLoadingView.getMeasuredWidth();
    }
    
    public int getMeasuredHeight() {
    	return mLoadingView.getMeasuredHeight();
    }
    
    public ViewParent getParent() {
    	return mLoadingView.getParent();
    }
    
    /**
     * 显示或隐藏这个布局
     * 
     * @param show flag
     */
    public void show(boolean show) {
        // If is showing, do nothing.
        if (show == (View.VISIBLE == getVisibility())) {
            return;
        }
        
        ViewGroup.LayoutParams params = mLoadingView.getLayoutParams();
        if (null != params) {
            if (show) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                params.height = 0;
            }
            mLoadingView.requestLayout();
            mLoadingView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }
    
    /**
     * 设置最后更新的时间文本
     * 
     * @param label 文本
     */
    public void setLastUpdatedLabel(CharSequence label) {
        
    }
    
    /**
     * 设置加载中的图片
     * 
     * @param drawable 图片
     */
    public void setLoadingDrawable(Drawable drawable) {
        
    }

    /**
     * 设置拉动的文本，典型的是“下拉可以刷新”
     * 
     * @param pullLabel 拉动的文本
     */
    public void setPullLabel(CharSequence pullLabel) {
        
    }

    /**
     * 设置正在刷新的文本，典型的是“正在刷新”
     * 
     * @param refreshingLabel 刷新文本
     */
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        
    }

    /**
     * 设置释放的文本，典型的是“松开可以刷新”
     * 
     * @param releaseLabel 释放文本
     */
    public void setReleaseLabel(CharSequence releaseLabel) {
        
    }

    /**
     * 设置当前状态，派生类应该根据这个状态的变化来改变View的变化
     * 
     * @param state 状态
     */
    public void setState(State state) {    	
        if (mCurState != state && mCurState != State.NO_MORE_DATA) {
        	LogUtil.v(TAG, "setState: " + state + " " + mCurState + " " + mPreState);
        	mPreState = mCurState;
            mCurState = state;
            onStateChanged(state, mPreState);
        }
    }
    
    /**
     * 得到当前的状态
     *  
     * @return 状态
     */
    public State getState() {
        return mCurState;
    }

    /**
     * 重置所有状态
     * */
    public void resetState() {
    	mCurState = State.NONE;
    	mPreState = State.NONE;
    }
    
    /**
     * 在拉动时调用
     * 
     * @param scale 拉动的比例
     */
    public void onPull(float scale) {
        
    }
    
    /**
     * 得到前一个状态
     * 
     * @return 状态
     */
    protected State getPreState() {
        return mPreState;
    }
    
    /**
     * 当状态改变时调用
     * 
     * @param curState 当前状态
     * @param oldState 老的状态
     */
    protected void onStateChanged(State curState, State oldState) {
        switch (curState) {
        case RESET:
            onReset();
            break;
            
        case RELEASE_TO_REFRESH:
            onReleaseToRefresh();
            break;
            
        case PULL_TO_REFRESH:
            onPullToRefresh();
            break;
            
        case REFRESHING:
            onRefreshing();
            break;
            
        case NO_MORE_DATA:
            onNoMoreData();
            break;
            
        default:
            break;
        }
    }
    
    /**
     * 当状态设置为{@link State#RESET}时调用
     */
    protected void onReset() {
        
    }
    
    /**
     * 当状态设置为{@link State#PULL_TO_REFRESH}时调用
     */
    protected void onPullToRefresh() {
        
    }
    
    /**
     * 当状态设置为{@link State#RELEASE_TO_REFRESH}时调用
     */
    protected void onReleaseToRefresh() {
        
    }
    
    /**
     * 当状态设置为{@link State#REFRESHING}时调用
     */
    protected void onRefreshing() {
        
    }
    
    /**
     * 当状态设置为{@link State#NO_MORE_DATA}时调用
     */
    protected void onNoMoreData() {
        
    }
    
    /**
     * 得到当前Layout的内容大小，它将作为一个刷新的临界点
     * 
     * @return 高度
     */
    public abstract int getContentSize();
    
    /**
     * 创建Loading的View
     * 
     * @param context context
     * @param attrs attrs
     * @return Loading的View
     */
    protected abstract View createLoadingView(Context context);    
}
