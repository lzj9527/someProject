package android.extend.widget.pull;

import android.content.Context;
import android.extend.util.ResourceUtil;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class RotateHeaderLoading extends BasePullLoading {
	/**旋转动画的时间*/
    static final int ROTATION_ANIMATION_DURATION = 1200;
    /**动画插值*/
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    /**Header的容器*/
    private RelativeLayout mHeaderContainer;
    /**箭头图片*/
    private ImageView mArrowImageView;
    /**状态提示TextView*/
    private TextView mHintTextView;
    /**最后更新时间的TextView*/
    private TextView mHeaderTimeView;
    /**最后更新时间的标题*/
    private TextView mHeaderTimeViewTitle;
    /**旋转的动画*/
    private Animation mRotateAnimation;
    
    private ImageViewRotationHelper mRotationHelper;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public RotateHeaderLoading(Context context) {
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
        id = ResourceUtil.getId(getContext(), "pull_to_refresh_header_time");
        mHeaderTimeView = (TextView) findViewById(id);        
        id = ResourceUtil.getId(getContext(), "pull_to_refresh_last_update_time_text");
        mHeaderTimeViewTitle = (TextView) findViewById(id);
        
        mArrowImageView.setScaleType(ScaleType.CENTER);
        int drawable = ResourceUtil.getDrawableId(getContext(), "rotate_refresh");
        mArrowImageView.setImageResource(drawable);
        
        float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = 720.0f;    // SUPPRESS CHECKSTYLE
        mRotateAnimation = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }
    
    @Override
    protected View createLoadingView(Context context) {
    	int layout = ResourceUtil.getLayoutId(getContext(), "pull_to_refresh_header");
        View container = LayoutInflater.from(context).inflate(layout, null);
        return container;
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
            return mHeaderContainer.getHeight();
        }
        
        return (int) (getView().getResources().getDisplayMetrics().density * 60);
    }
    
    @Override
    protected void onStateChanged(State curState, State oldState) {
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        resetRotation();
        int string = ResourceUtil.getStringId(getContext(), "pull_to_refresh_hint_normal");
        mHintTextView.setText(string);
    }

    @Override
    protected void onReleaseToRefresh() {
    	int string = ResourceUtil.getStringId(getContext(), "pull_to_refresh_hint_ready");
        mHintTextView.setText(string);
    }
    
    @Override
    protected void onPullToRefresh() {
    	int string = ResourceUtil.getStringId(getContext(), "pull_to_refresh_hint_normal");
        mHintTextView.setText(string);
    }
    
    @Override
    protected void onRefreshing() {
        resetRotation();
        mArrowImageView.startAnimation(mRotateAnimation);
        int string = ResourceUtil.getStringId(getContext(), "pull_to_refresh_hint_loading");
        mHintTextView.setText(string);
    }
    
    @Override
    public void onPull(float scale) {
    	if (null == mRotationHelper) {
            mRotationHelper = new ImageViewRotationHelper(mArrowImageView);
        }
    	
        float angle = scale * 180f; // SUPPRESS CHECKSTYLE
        mRotationHelper.setRotation(angle);
    }
    
    /**
     * 重置动画
     */
    private void resetRotation() {
    	if (null == mRotationHelper) {
            mRotationHelper = new ImageViewRotationHelper(mArrowImageView);
        }
    	
        mArrowImageView.clearAnimation();
        mRotationHelper.setRotation(0);
    }
        
    /**
     * The image view rotation helper
     * 
     * @author lihong06
     * @since 2014-5-2
     */
    static class ImageViewRotationHelper {
        /** The imageview */
        private final ImageView mImageView;
        /** The matrix */
        private Matrix mMatrix;
        /** Pivot X */
        private float mRotationPivotX;
        /** Pivot Y */
        private float mRotationPivotY;
        
        /**
         * The constructor method.
         * 
         * @param imageView the image view
         */
        public ImageViewRotationHelper(ImageView imageView) {
            mImageView = imageView;
        }
        
        /**
         * Sets the degrees that the view is rotated around the pivot point. Increasing values
         * result in clockwise rotation.
         *
         * @param rotation The degrees of rotation.
         *
         * @see #getRotation()
         * @see #getPivotX()
         * @see #getPivotY()
         * @see #setRotationX(float)
         * @see #setRotationY(float)
         *
         * @attr ref android.R.styleable#View_rotation
         */
        public void setRotation(float rotation) {
            if (Build.VERSION.SDK_INT > 10) {
                mImageView.setRotation(rotation);
            } else {
                if (null == mMatrix) {
                    mMatrix = new Matrix();
                    
                    // 计算旋转的中心点
                    Drawable imageDrawable = mImageView.getDrawable();
                    if (null != imageDrawable) {
                        mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
                        mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
                    }
                }
                
                mMatrix.setRotate(rotation, mRotationPivotX, mRotationPivotY);
                mImageView.setImageMatrix(mMatrix);
            }
        }
    }
}
