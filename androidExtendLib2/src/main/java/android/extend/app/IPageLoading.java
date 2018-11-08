package android.extend.app;

import java.util.List;

import android.extend.widget.adapter.AbsAdapterItem;

/**
 * 分页加载接口
 * */
public interface IPageLoading<T extends AbsAdapterItem>
{
	/**
	 * 读取当前页数据
	 * */
	void onPageLoadStart(int pageNumber);

	void onPageLoadFinish(List<T> itemList, boolean success);

	int getMaxPageNumber();
}
