package com.shiyou.tryapp2.data.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse.GoodsDetail;

import android.extend.data.BaseData;

public class GiaBargainResponse extends BaseResponse{

	public GiaBargain datas;
	
	public static class GiaBargain extends BaseData
	{
		
		public String sn; 	   //货号
		public String certno;  //证书号
		public String style;   //形状
		public String weight;  //克拉重
		public String color;   //颜色
		public String clarity; //净度
		public String cut;     //切工
		public String polish;  //颜色
		public String symmetry;//对称
		public String coffee;  //咖色
		public String milk;    //奶色
		public String fluorescence;//荧光
		public String price;   //价格
		public String image;   //证书图
		public int status;     //状态
		
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
	
	
	
}
