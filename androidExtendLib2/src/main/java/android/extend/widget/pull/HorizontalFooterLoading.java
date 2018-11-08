package android.extend.widget.pull;

import android.content.Context;
import android.extend.util.ResourceUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HorizontalFooterLoading extends BasePullLoading
{
	/**进度条*/
    private ProgressBar mProgressBar;
    /** 显示的文本 */
    private TextView mHintView;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public HorizontalFooterLoading(Context context) {
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
    	int id = ResourceUtil.getId(context, "pull_to_load_footer_progressbar");
        mProgressBar = (ProgressBar) findViewById(id);
        id = ResourceUtil.getId(context, "pull_to_load_footer_hint_textview");
        mHintView = (TextView) findViewById(id);
        
        setState(State.RESET);
    }
    
    @Override
    protected View createLoadingView(Context context) {
    	int layout = ResourceUtil.getLayoutId(context, "pull_to_load_horizontalfooter");
        View container = LayoutInflater.from(context).inflate(layout, null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
    }

    @Override
    public int getContentSize() {
    	int id = ResourceUtil.getId(getContext(), "pull_to_load_footer_content");
        View view = findViewById(id);
        if (null != view) {
            return view.getWidth();
        }
        
        return (int) (getView().getResources().getDisplayMetrics().density * 40);
    }
    
    @Override
    protected void onStateChanged(State curState, State oldState) {
        mProgressBar.setVisibility(View.GONE);
        mHintView.setVisibility(View.INVISIBLE);
        
        super.onStateChanged(curState, oldState);
    }
    
    @Override
    protected void onReset() {
    	int string = ResourceUtil.getStringId(getContext(), "pull_to_load_horizontal_hint_normal");
        mHintView.setText(string);
    }

    @Override
    protected void onPullToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
        int string = ResourceUtil.getStringId(getContext(), "pull_to_load_horizontal_hint_normal");
        mHintView.setText(string);
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
        int string = ResourceUtil.getStringId(getContext(), "pull_to_load_hint_ready");
        mHintView.setText(string);
    }

    @Override
    protected void onRefreshing() {
        mProgressBar.setVisibility(View.VISIBLE);
        mHintView.setVisibility(View.VISIBLE);
        int string = ResourceUtil.getStringId(getContext(), "pull_to_load_hint_loading");
        mHintView.setText(string);
    }
    
    @Override
    protected void onNoMoreData() {
        mHintView.setVisibility(View.VISIBLE);
        int string = ResourceUtil.getStringId(getContext(), "pull_to_load_no_more_data");
        mHintView.setText(string);
    }
}
