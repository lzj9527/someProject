package com.shiyou.tryapp2.data.response;

import com.shiyou.tryapp2.data.PageInfo;

import android.extend.data.BaseData;

public class ShoppingcartListResponse extends BaseResponse
{
	public PageInfo pageInfo;
	public List datas;

	public static class List extends BaseData
	{
		public Item[] list;
	}

	public static class Item extends BaseData
	{
		public String id;
		public String goodsid;
	}
}
