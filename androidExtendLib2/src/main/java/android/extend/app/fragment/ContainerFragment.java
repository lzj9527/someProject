package android.extend.app.fragment;

import android.extend.app.BaseFragment;
import android.extend.util.ResourceUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContainerFragment extends BaseFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		mLayoutResID = ResourceUtil.getLayoutId(getContext(), "default_fragment_layout");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
