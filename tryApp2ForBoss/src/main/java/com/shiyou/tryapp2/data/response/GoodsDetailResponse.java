package com.shiyou.tryapp2.data.response;

import android.extend.data.BaseData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shiyou.tryapp2.data.FileInfo;
import com.shiyou.tryapp2.data.ImageInfo;

public class GoodsDetailResponse extends BaseResponse
{
	public GoodsDetail datas;

	public static class GoodsDetail extends BaseData
	{
		public String id;
		public String title;
		public String sku;// 货号
		public String goodssn;
		public String marketprice;
		public String tagname;// 分类标记
		public ImageInfo thumb;// 主图
		public ImageInfo[] thumb_url;// 相册
		public ParamItem[] param;// 商品属性
		public ErpGoods[] erp;
		public int count;
		public FileInfo model_info;// 模型文件

		public static String toJson(GoodsDetail info)
		{
			GsonBuilder gb = new GsonBuilder();
			Gson gson = gb.create();
			return gson.toJson(info);
		}

		public static GoodsDetail fromJson(String json)
		{
			GsonBuilder gb = new GsonBuilder();
			Gson gson = gb.create();
			return gson.fromJson(json, GoodsDetail.class);
		}
	}

	public static class ParamItem extends BaseData
	{
		public String id;
		public String title;
		public String value;
	}

	public static class ErpGoods extends BaseData
	{
		public String erpid;// 货号
		public String p1;// 金重
		public String p2;// 主石净度
		public String p3;// 主石颜色
		public String p4;// 材质
		public String p5;// 价格
		public String p6;// 主石数量
		public String p7;// 主石重量
		public String p8;// 副石数量
		public String p9;// 副石重量
		public String p10;// 副石
		public String p128;// 手寸
		public String zs;// 证书
	}
}
