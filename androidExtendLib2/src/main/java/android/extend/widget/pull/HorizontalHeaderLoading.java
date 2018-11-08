package android.extend.widget.pull;

import android.content.Context;
import android.extend.util.ResourceUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class HorizontalHeaderLoading extends BasePullLoading {
	/** 旋转动画时间 */
    private static final int ROTATE_ANIM_DURATION = 150;
    /**Header的容器*/
    private RelativeLayout mHeaderContainer;
    /**箭头图片*/
    private ImageView mArrowImageView;
    /**进度条*/
    private ProgressBar mProgressBar;
    /**状态提示TextView*/
    private TextView mHintTextView;
    /**最后更新时间的TextView*/
    private TextView mHeaderTimeView;
    /**最后更新时间的标题*/
    private TextView mHeaderTimeViewTitle;
    /**向上的动画*/
    private Animation mRotateUpAnim;
    /**向下的动画*/
    private Animation mRotateDownAnim;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public HorizontalHeaderLoading(Context context) {
        super(context);
//        init(context);
    }

    /**
     * 初始化
     * 
     * @param context context
     */
    @Override
    protected void init(Context context) {
    	super.init(context);
    	int id = ResourceUtil.getId(getContext(), "pull_to_refresh_header_content");
        mHeaderContainer = (RelativeLayout) findViewById(id);
        id = ResourceUtil.getId(getContext(), "pull_to_refresh_header_arrow");
        mArrowImageView = (ImageView) findViewById(id);
        id = ResourceUtil.getId(getContext(), "pull_to_refresh_header_hint_textview");
        mHintTextView = (TextView) findViewById(id);
        id = ResourceUtil.getId(getContext(), "pull_to_refresh_header_progressbar");
        mProgressBar = (ProgressBar) findViewById(id);
        id = ResourceUtil.getId(getContext(), "pull_to_refresh_header_time");
        mHeaderTimeView = (TextView) findViewById(id);
        id = ResourceUtil.getId(getContext(), "pull_to_refresh_last_update_time_text");
        mHeaderTimeViewTitle = (TextView) findViewById(id);
        
        float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = -180f;     // SUPPRESS CHECKSTYLE
        // 初始化旋转动画
        mRotateUpAnim = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(toDegree, 0.0f, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
    	// 如果最后更新的时间的文本是空的话，隐藏前面的标题
        mHeaderTimeViewTitle.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE : View.VISIBLE);
        mHeaderTimeView.setText(label);
    }

    @Override
    public int getContentSize() {
        if (null != mHeaderContainer) {
            return mHeaderContainer.getWidth();
        }
        
        return (int) (getView().getResources().getDisplayMetrics().density * 60);
    }
    
    @Override
    protected View createLoadingView(Context context) {
    	int layout = ResourceUtil.getLayoutId(getContext(), "pull_to_refresh_horizontalheader");
        View container = LayoutInflater.from(context).inflate(layout, null);
        return container;
    }
    
    @Override
    protected void onStateChanged(State curState, State oldState) {
        mArrowImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        mArrowImageView.clearAnimation();
        int string = ResourceUtil.getStringId(getContext(), "pull_to_refresh_horizontal_hint_normal");
        mHintTextView.setText(string);
    }

    @Override
    protected void onPullToRefresh() {
        if (State.RELEASE_TO_REFRESH == getPreState()) {
            mArrowImageView.clearAnimation();
            mArrowImageView.startAnimation(mRotateDownAnim);
        }        
        int string = ResourceUtil.getStringId(getContext(), "pull_to_refresh_horizontal_hint_normal");
        mHintTextView.setText(string);
    }

    @Override
    protected void onReleaseToRefresh() {
        mArrowImageView.clearAnimation();
        mArrowImageView.startAnimation(mRotateUpAnim);
        int string = ResourceUtil.getStringId(getContext(), "pull_to_refresh_hint_ready");
        mHintTextView.setText(string);
    }

    @Override
    protected void onRefreshing() {
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        int string = ResourceUtil.getStringId(getContext(), "pull_to_refresh_hint_loading");
        mHintTextView.setText(string);
    }
}
