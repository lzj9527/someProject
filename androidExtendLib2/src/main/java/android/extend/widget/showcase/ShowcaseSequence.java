package android.extend.widget.showcase;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.extend.widget.showcase.ShowcaseView.OnShowcaseListener;

public class ShowcaseSequence
{
	private Activity mActivity;
	private List<ShowcaseView> mSequenceList = new ArrayList<ShowcaseView>();

	public ShowcaseSequence(Activity activity)
	{
		mActivity = activity;
	}

	public ShowcaseSequence addSequenceItem(ShowcaseView item)
	{
		mSequenceList.add(item);
		return this;
	}

	private void showNext()
	{
		if (mSequenceList.isEmpty())
			return;
		ShowcaseView item = mSequenceList.remove(0);
		item.addOnShowcaseListener(new OnShowcaseListener()
		{
			@Override
			public void onShow(ShowcaseView showcaseView)
			{
			}

			@Override
			public void onHide(ShowcaseView showcaseView)
			{
			}

			@Override
			public void onDismiss(ShowcaseView showcaseView)
			{
				showNext();
			}

			@Override
			public void onBuild(ShowcaseView showcaseView)
			{
			}
		});
		item.show(mActivity);
	}

	public void start()
	{
		showNext();
	}
}
