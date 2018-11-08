package android.extend.app;

import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public abstract class AbsTabActivity extends BaseTabActivity implements TabHost.OnTabChangeListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// int layout = ResourceUtil.getLayoutId(this, "default_tab_content");
		// super.setContentView(layout);
	}

	@Override
	public void onContentChanged()
	{
		super.onContentChanged();
		initTabContent(getTabHost());
	}

	protected abstract void initTabContent(TabHost tabHost);

	public int getCurrentTab()
	{
		return getTabHost().getCurrentTab();
	}

	public String getCurrentTabTag()
	{
		return getTabHost().getCurrentTabTag();
	}
}
