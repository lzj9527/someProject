package com.shiyou.tryapp2.app.product;

import java.util.List;

import android.content.Context;
import android.extend.widget.SpinnerPopupWindow;

import com.shiyou.tryapp2.boss.zsa.R;

public class SpinnerWindow extends SpinnerPopupWindow
{
	public SpinnerWindow(Context context, List<String> list)
	{
		super(context, R.layout.spinner_window_layout, R.layout.spinner_item_layout, list);
	}
}
