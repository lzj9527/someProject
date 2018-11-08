package android.extend.widget.pull;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.webkit.WebView;

/**
 * 封装了WebView的下拉刷新
 * 
 * @author Li Hong
 * @since 2013-8-22
 */
public class PullWebView extends BasePullView<WebView> {
    
	/**
     * 构造方法
     * 
     * @param context context
     */
    public PullWebView(Context context) {
        super(context);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public PullWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public PullWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean isHorizontalLayout() {
    	return false;
    }
    
    @Override
    protected WebView createPullConentView(Context context, AttributeSet attrs) {
        WebView webView = new WebView(context);
        return webView;
    }

    @Override
    protected boolean isReadyForPullRefresh() {
        return mPullContentView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullLoad() {
        float exactContentHeight = (float) Math.floor(mPullContentView.getContentHeight() * mPullContentView.getScale());
        return mPullContentView.getScrollY() >= (exactContentHeight - mPullContentView.getHeight());
    }
}
