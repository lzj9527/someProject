package com.shiyou.tryapp2;

import android.text.TextUtils;

public class Config
{
	public static final String TAG = "Config";

	public static final String CLIENT = "android";
	public static final int CHANNEL = 1;// 平台 1：tryapp平台，2：91助手，3：安智市场，4：应用宝
	public static final int PLATFORM = 3;// 1：AndriodPad 2：iso 3:AndriodBoss
	public static final String ServicePhone = "020-39991116";// 客服电话

	// public static final String BaseUrl = "http://zsmt.tryapp.cn";// 正式版域名
//	public static final String BaseUrl = "http://www.zsa888.com";  //测试域名
	public static final String BaseUrl = "http://www.zsmtvip.com";
//	public static final String BaseUrl = "http://lex.zsmt.com";
	public static final String BaseWebUrl = "file:///android_asset";// Web页面BaseUrl
	// public static final String BaseWebUrl = "http://weixintest.tryapp.cn/addons/ewei_shop/template";

	public static final String LoadGoodsListUrl = BaseUrl
			+ "/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=goods&p=goods_list";
	public static final String LoadGoodsDetailUrl = BaseUrl
			+ "/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=goods&p=goods";// &id=14";
	public static final String LoadGoodsErpUrl = BaseUrl
			+ "/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=goods&p=goods_erp";// &id=6";
	// 添加购物车
	public static final String AppendShoppingcartUrl = BaseUrl
			+ "/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=member&p=member_cart&act=add";// &key=f7f53b265afd3c7dab12c3ec43366c89&id=14&erpid=&size=30";
	// 门店列表
	public static final String GetShopListUrl = BaseUrl
			+ "/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=member&p=member_address&act=member";// &key=f7f53b265afd3c7dab12c3ec43366c89;
	// 获取购物车列表
	public static final String LoadShoppingcartListUrl = BaseUrl
			+ "/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=member&p=member_cart&psize=999999";// &key=a70bc36843d53df512b0323fe87fb252";
	// 检查更新
	public static final String CheckVersionUrl = BaseUrl + "/app/index.php?i=2&c=entry&do=update_version&m=test";// &id=6&key=111&channel=2&versionCode=";
	
	//特价裸石接口
	public static final String GiaBargainUrl = BaseUrl + "/app/index.php?i=2&c=entry&m=ewei_shop&v=pad&do=goods&p=gia_bargain"; //&key=;

	// 登录 http://weixintest.tryapp.cn/addons/ewei_shop/template/app/default/shop/index.html
	// public static final String WebLogin = BaseUrl + "/addons/ewei_shop/template/app/default/shop/index.html";
	public static final String WebLogin = BaseWebUrl + "/app/default/shop/index.html";
	// 产品分类http://weixintest.tryapp.cn/addons/ewei_shop/template/app/default/shop/category.html
	// public static final String WebProductCategory = BaseUrl
	// + "/addons/ewei_shop/template/app/default/shop/category.html";
	public static final String WebProductCategory = BaseWebUrl + "/app/default/shop/category.html";
	// 购物车http://weixintest.tryapp.cn/addons/ewei_shop/template/app/default/shop/cart.html
	// public static final String WebBossShoppingCart = BaseUrl +
	// "/addons/ewei_shop/template/app/default/shop/cart.html";
	public static final String WebBossShoppingCart = BaseWebUrl + "/app/default/shop/cart.html";
	// 订单http://weixintest.tryapp.cn/addons/ewei_shop/template/app/default/shop/order.html
	// public static final String WebBossOrder = BaseUrl + "/addons/ewei_shop/template/app/default/shop/order.html";
	public static final String WebBossOrder = BaseWebUrl + "/app/default/shop/order.html";
	// 我的 http://weixintest.tryapp.cn/addons/ewei_shop/template/app/default/shop/user.html
	// public static final String WebBossMy = BaseUrl + "/addons/ewei_shop/template/app/default/shop/user.html";
	public static final String WebBossMy = BaseWebUrl + "/app/default/shop/user.html";
	// 全球选钻http://weixintest.tryapp.cn/addons/ewei_shop/api/select.htm
	// public static final String WebBossRings = BaseUrl + "/addons/ewei_shop/api/select.htm";
	public static final String WebBossRings = BaseWebUrl + "/app/default/api/select.html";
	// 搜索http://weixintest.tryapp.cn/addons/ewei_shop/template/app/default/shop/search.html
	// public static final String WebBossSearch = BaseUrl + "/addons/ewei_shop/template/app/default/shop/search.html";
	public static final String WebBossSearch = BaseWebUrl + "/app/default/shop/search.html";
	// 退出登录
	// public static final String WebLoginOut = BaseUrl + "/addons/ewei_shop/template/app/default/shop/logout.html";
	public static final String WebLoginOut = BaseWebUrl + "/app/default/shop/logout.html";

	/** 上传分享图片 */
	public static final String UploadShareImageUrl = "http://file.tryapp.cn/mobile/index.php?act=upload_file&op=up_share_img";
	// share_img multipart/form-data
	

	/**
	 * 试戴类型定义
	 */
	public static final int Type_Unknown = -1;
	public static final int Type_Combine = 0x01;// 搭配
	public static final int Type_Tops = 0x10;// 上衣
	public static final int Type_Tops_Longsleeve = 0x11;// 上衣长袖
	public static final int Type_Tops_Shortsleeve = 0x12;// 上衣短袖
	public static final int Type_Tops_Sleeveless = 0x13;// 上衣无袖
	public static final int Type_Tops_Batsleeve = 0x14;// 上衣蝙蝠袖
	public static final int Type_Coat = 0x20;// 外套
	public static final int Type_Coat_Longsleeve = 0x21;// 外套长袖
	public static final int Type_Coat_Shortsleeve = 0x22;// 外套短袖
	public static final int Type_Coat_Sleeveless = 0x23;// 外套无袖
	public static final int Type_Coat_Batsleeve = 0x24;// 外套蝙蝠袖
	public static final int Type_Dress = 0x30;// 连衣裙
	public static final int Type_Dress_Longsleeve = 0x31;// 连衣裙长袖
	public static final int Type_Dress_Shortsleeve = 0x32;// 连衣裙短袖
	public static final int Type_Dress_Sleeveless = 0x33;// 连衣裙无袖
	public static final int Type_Dress_Batsleeve = 0x34;// 连衣裙蝙蝠袖
	public static final int Type_Skirt = 0x40;// 裙子
	public static final int Type_Trousers = 0x50;// 裤子
	public static final int Type_Trousers_Long = 0x51;// 长裤
	public static final int Type_Trousers_Short = 0x52;// 短裤
	public static final int Type_Wedding = 0x60;// 婚装
	public static final int Type_Wedding_Longsleeve = 0x61;// 婚装长袖
	public static final int Type_Wedding_Shortsleeve = 0x62;// 婚装短袖
	public static final int Type_Wedding_Sleeveless = 0x63;// 婚装无袖
	public static final int Type_Wedding_Batsleeve = 0x64;// 婚装蝙蝠袖
	public static final int Type_Jumpsuits = 0x70;// 连体裤
	public static final int Type_Jumpsuits_Longsleeve_Longtrousers = 0x71;// 连体裤长袖长裤
	public static final int Type_Jumpsuits_Shortsleeve_Longtrousers = 0x72;// 连体裤短袖长裤
	public static final int Type_Jumpsuits_Sleeveless_Longtrousers = 0x73;// 连体裤无袖长裤
	public static final int Type_Jumpsuits_Batsleeve_Longtrousers = 0x74;// 连体裤蝙蝠袖长裤
	public static final int Type_Jumpsuits_Longsleeve_Shorttrousers = 0x75;// 连体裤长袖短裤
	public static final int Type_Jumpsuits_Shortsleeve_Shorttrousers = 0x76;// 连体裤短袖短裤
	public static final int Type_Jumpsuits_Sleeveless_Shorttrousers = 0x77;// 连体裤无袖短裤
	public static final int Type_Jumpsuits_Batsleeve_Shorttrousers = 0x78;// 连体裤蝙蝠袖短裤
	public static final int Type_Underclothes = 0x80;// 内衣
	public static final int Type_Necklace = 0x100;// 项链
	public static final int Type_Bag = 0x110;// 包包
	public static final int Type_Bag_Back = 0x111;// 背包
	public static final int Type_Bag_Cross = 0x112;// 斜挎包
	public static final int Type_Bag_Shoulder = 0x113;// 单肩包
	public static final int Type_Bag_Hand = 0x114;// 手提包
	public static final int Type_Ornament = 0x120;// 饰品
	public static final int Type_Earring = 0x121;// 耳环
	public static final int Type_Headwear = 0x122;// 发饰
	public static final int Type_Ring = 0x123;// 戒指
	public static final int Type_CoupleRing_Male = 0x124;// 情侣对戒男戒
	public static final int Type_CoupleRing_FeMale = 0x125;// 情侣对戒女戒
	public static final int Type_Wristlet = 0x126;// 腕饰
	public static final int Type_Caps = 0x127;// 帽子
	public static final int Type_Periwig = 0x128;// 假发
	public static final int Type_Glasses = 0x129;// 眼镜
	public static final int Type_Watch = 0x130;// 手表
	public static final int Type_Tuinga = 0x131;// 发饰头冠
	public static final int Type_TravelCase = 0x140;// 旅行箱

	public static final int IK_Unknown = -1;
	public static final int IK_None = 0x00;
	public static final int IK_Sleeve_Long = 0x01;// 长袖
	public static final int IK_Sleeve_Short = 0x02;// 短袖
	public static final int IK_Sleeve_Bat = 0x03;// 蝙蝠袖
	public static final int IK_Sleeve_None = 0x04;// 无袖
	public static final int IK_Trousers_Long = 0x05;// 长裤
	public static final int IK_Trousers_Short = 0x06;// 短裤
	public static final int IK_Skirt = 0x07;// 裙子
	public static final int IK_Necklace = 0x10;// 项链
	public static final int IK_Jumpsuits_Longsleeve_Longtrousers = 0x11;// 连体裤长袖长裤
	public static final int IK_Jumpsuits_Shortsleeve_Longtrousers = 0x12;// 连体裤短袖长裤
	public static final int IK_Jumpsuits_Sleeveless_Longtrousers = 0x13;// 连体裤无袖长裤
	public static final int IK_Jumpsuits_Batsleeve_Longtrousers = 0x14;// 连体裤蝙蝠袖长裤
	public static final int IK_Jumpsuits_Longsleeve_Shorttrousers = 0x15;// 连体裤长袖短裤
	public static final int IK_Jumpsuits_Shortsleeve_Shorttrousers = 0x16;// 连体裤短袖短裤
	public static final int IK_Jumpsuits_Sleeveless_Shorttrousers = 0x17;// 连体裤无袖短裤
	public static final int IK_Jumpsuits_Batsleeve_Shorttrousers = 0x18;// 连体裤蝙蝠袖短裤

	public static final String Face_Front = "正面";// 正面
	public static final String Face_Side = "侧面";// 侧面
	public static final String Face_45 = "45度";// 45度
	public static final String Face_Right_Front = "右正面";// 右正面
	public static final String Face_Right_Side = "右侧面";// 右侧面
	public static final String Face_Right_45 = "右45度";// 右45度

	public static final int getTryonType(String name)
	{
		if (TextUtils.isEmpty(name))
			return Type_Unknown;
		if (name.equals("上衣长袖"))
			return Type_Tops_Longsleeve;
		if (name.equals("上衣短袖"))
			return Type_Tops_Shortsleeve;
		if (name.equals("上衣无袖"))
			return Type_Tops_Sleeveless;
		if (name.equals("上衣蝙蝠袖"))
			return Type_Tops_Batsleeve;
		if (name.equals("外套长袖"))
			return Type_Coat_Longsleeve;
		if (name.equals("外套短袖"))
			return Type_Coat_Shortsleeve;
		if (name.equals("外套无袖"))
			return Type_Coat_Sleeveless;
		if (name.equals("外套蝙蝠袖"))
			return Type_Coat_Batsleeve;
		if (name.equals("连衣裙长袖"))
			return Type_Dress_Longsleeve;
		if (name.equals("连衣裙短袖"))
			return Type_Dress_Shortsleeve;
		if (name.equals("连衣裙无袖"))
			return Type_Dress_Sleeveless;
		if (name.equals("连衣裙蝙蝠袖"))
			return Type_Dress_Batsleeve;
		if (name.equals("婚装长袖"))
			return Type_Wedding_Longsleeve;
		if (name.equals("婚装短袖"))
			return Type_Wedding_Shortsleeve;
		if (name.equals("婚装无袖"))
			return Type_Wedding_Sleeveless;
		if (name.equals("婚装蝙蝠袖"))
			return Type_Wedding_Batsleeve;
		if (name.equals("裙子"))
			return Type_Skirt;
		if (name.equals("裤子长裤"))
			return Type_Trousers_Long;
		if (name.equals("裤子短裤"))
			return Type_Trousers_Short;
		if (name.equals("连体裤长袖长裤"))
			return Type_Jumpsuits_Longsleeve_Longtrousers;
		if (name.equals("连体裤短袖长裤"))
			return Type_Jumpsuits_Shortsleeve_Longtrousers;
		if (name.equals("连体裤无袖长裤"))
			return Type_Jumpsuits_Sleeveless_Longtrousers;
		if (name.equals("连体裤蝙蝠袖长裤"))
			return Type_Jumpsuits_Batsleeve_Longtrousers;
		if (name.equals("连体裤长袖短裤"))
			return Type_Jumpsuits_Longsleeve_Shorttrousers;
		if (name.equals("连体裤短袖短裤"))
			return Type_Jumpsuits_Shortsleeve_Shorttrousers;
		if (name.equals("连体裤无袖短裤"))
			return Type_Jumpsuits_Sleeveless_Shorttrousers;
		if (name.equals("连体裤蝙蝠袖短裤"))
			return Type_Jumpsuits_Batsleeve_Shorttrousers;
		if (name.equals("内衣"))
			return Type_Underclothes;
		if (name.equals("包包背包"))
			return Type_Bag_Back;
		if (name.equals("包包斜挎包"))
			return Type_Bag_Cross;
		if (name.equals("包包单肩包"))
			return Type_Bag_Shoulder;
		if (name.equals("包包手提包"))
			return Type_Bag_Hand;
		if (name.equals("项链"))
			return Type_Necklace;
		if (name.equals("耳环"))
			return Type_Earring;
		if (name.equals("发饰"))
			return Type_Headwear;
		if (name.equals("戒指"))
			return Type_Ring;
		if (name.equals("腕饰"))
			return Type_Wristlet;
		if (name.equals("帽子"))
			return Type_Caps;
		if (name.equals("假发"))
			return Type_Periwig;
		if (name.equals("眼镜"))
			return Type_Glasses;
		if (name.equals("手表"))
			return Type_Watch;
		if (name.equals("发饰头冠"))
			return Type_Tuinga;
		if (name.equals("旅行箱"))
			return Type_TravelCase;
		return Type_Unknown;
	}

	// public static final int getTryonType(ProductItem.ModelClass modelClass)
	// {
	// if (modelClass == null)
	// return Type_Unknown;
	// return getTryonType(modelClass.model_class_name);
	// }

	public static final String Category_Combine = "搭配";
	public static final String Category_Tops = "上衣";
	public static final String Category_Coat = "外套";
	public static final String Category_Dress = "连衣裙";
	public static final String Category_Skirt = "裙子";
	public static final String Category_Suits = "套装";
	public static final String Category_Trousers = "裤子";
	public static final String Category_Bag = "包包";
	public static final String Category_Ornament = "配饰";

	public static final int getTryonCategory(String name)
	{
		if (TextUtils.isEmpty(name))
			return Type_Unknown;
		if (name.equals(Category_Combine))
			return Type_Combine;
		if (name.equals(Category_Tops))
			return Type_Tops;
		if (name.equals(Category_Coat))
			return Type_Coat;
		if (name.equals(Category_Suits))
			return Type_Jumpsuits;
		if (name.equals(Category_Skirt))
			return Type_Dress;
		if (name.equals(Category_Dress))
			return Type_Dress;
		if (name.equals(Category_Trousers))
			return Type_Trousers;
		if (name.equals(Category_Bag))
			return Type_Bag;
		if (name.equals(Category_Ornament))
			return Type_Ornament;
		return Type_Unknown;
	}

	// public static final int getTryonCategory(ProductItem.TryClass tryClass)
	// {
	// if (tryClass == null)
	// return Type_Unknown;
	// return getTryonCategory(tryClass.try_class_name);
	// }

	public static int getMainType(int type)
	{
		if (type >= Type_Tops && type < Type_Coat)
			return Type_Tops;
		if (type >= Type_Coat && type < Type_Dress)
			return Type_Coat;
		if (type >= Type_Dress && type < Type_Skirt)
			return Type_Dress;
		if (type > Type_Wedding && type < Type_Jumpsuits)
			return Type_Wedding;
		if (type >= Type_Trousers && type < Type_Necklace)
			return Type_Trousers;
		if (type > Type_Jumpsuits && type < 0x130)
			return Type_Jumpsuits;
		if (type >= Type_Bag && type < Type_Ornament)
			return Type_Bag;
		if (type >= Type_Ornament && type < Type_Underclothes)
			return Type_Ornament;
		return type;
	}

	public static int getIKOfType(int type)
	{
		switch (type)
		{
			case Type_Tops_Longsleeve:
			case Type_Coat_Longsleeve:
			case Type_Dress_Longsleeve:
			case Type_Wedding_Longsleeve:
				return IK_Sleeve_Long;
			case Type_Tops_Shortsleeve:
			case Type_Coat_Shortsleeve:
			case Type_Dress_Shortsleeve:
			case Type_Wedding_Shortsleeve:
				return IK_Sleeve_Short;
			case Type_Tops_Batsleeve:
			case Type_Coat_Batsleeve:
			case Type_Dress_Batsleeve:
			case Type_Wedding_Batsleeve:
				return IK_Sleeve_Bat;
			case Type_Tops_Sleeveless:
			case Type_Coat_Sleeveless:
			case Type_Dress_Sleeveless:
			case Type_Wedding_Sleeveless:
				return IK_Sleeve_None;
			case Type_Trousers_Long:
				return IK_Trousers_Long;
			case Type_Trousers_Short:
				return IK_Trousers_Short;
			case Type_Jumpsuits_Longsleeve_Longtrousers:
				return IK_Jumpsuits_Longsleeve_Longtrousers;
			case Type_Jumpsuits_Shortsleeve_Longtrousers:
				return IK_Jumpsuits_Shortsleeve_Longtrousers;
			case Type_Jumpsuits_Batsleeve_Longtrousers:
				return IK_Jumpsuits_Batsleeve_Longtrousers;
			case Type_Jumpsuits_Sleeveless_Longtrousers:
				return IK_Jumpsuits_Sleeveless_Longtrousers;
			case Type_Jumpsuits_Longsleeve_Shorttrousers:
				return IK_Jumpsuits_Longsleeve_Shorttrousers;
			case Type_Jumpsuits_Shortsleeve_Shorttrousers:
				return IK_Jumpsuits_Shortsleeve_Shorttrousers;
			case Type_Jumpsuits_Batsleeve_Shorttrousers:
				return IK_Jumpsuits_Batsleeve_Shorttrousers;
			case Type_Jumpsuits_Sleeveless_Shorttrousers:
				return IK_Jumpsuits_Sleeveless_Shorttrousers;
			case Type_Skirt:
				return IK_Skirt;
			case Type_Necklace:
				return IK_Necklace;
		}
		return IK_Unknown;
	}

	public static boolean isNeedReplace(int type, int targetType)
	{
		if (type == targetType)
			return true;
		int mainType = getMainType(type);
		int targetMainType = getMainType(targetType);
		switch (targetMainType)
		{
			case Type_Tops:
				if (mainType == Type_Tops)
					return true;
				break;
			case Type_Coat:
			case Type_Wedding:
				if (mainType == Type_Coat || mainType == Type_Wedding)
					return true;
				break;
			case Type_Trousers:
			case Type_Skirt:
				if (mainType == Type_Trousers || mainType == Type_Skirt)
					return true;
				break;
			case Type_Dress:
			case Type_Jumpsuits:
				if (mainType == Type_Dress || mainType == Type_Jumpsuits)
					return true;
				break;
		}
		return false;
	}

	public static final String getTryonTypeText(int type)
	{
		switch (type)
		{
			case Type_Combine:
				return Category_Combine;
			case Type_Tops:
				return Category_Tops;
			case Type_Coat:
				return Category_Coat;
			case Type_Dress:
				return Category_Dress;
			case Type_Wedding:
				return "婚装";
			case Type_Skirt:
				return Category_Skirt;
			case Type_Trousers:
				return Category_Trousers;
			case Type_Jumpsuits:
				return Category_Suits;
			case Type_Necklace:
				return "项链";
			case Type_Bag:
				return Category_Bag;
			case Type_Ornament:
				return Category_Ornament;
		}
		return "全部";
	}

	public static boolean isClothesType(int type)
	{
		switch (getMainType(type))
		{
			case Type_Tops:
			case Type_Coat:
			case Type_Dress:
			case Type_Wedding:
			case Type_Trousers:
			case Type_Jumpsuits:
			case Type_Skirt:
				return true;
		}
		return false;
	}

	// public static CategoryItem findCategoryItem(ProductCategoryInfo categoryInfo, String categoryName)
	// {
	// if (categoryInfo != null)
	// {
	// return findCategoryItem(categoryInfo.class_list, categoryName);
	// }
	// return null;
	// }

	// public static CategoryItem findCategoryItem(CategoryItem[] categoryList, String categoryName)
	// {
	// if (categoryList != null && categoryList.length > 0)
	// {
	// for (CategoryItem item : categoryList)
	// {
	// if (item.gc_name.contains(categoryName))
	// {
	// return item;
	// }
	// }
	// }
	// return null;
	// }

	// public static CategoryItem[] findCategoryItems(ProductCategoryInfo categoryInfo, String categoryName)
	// {
	// if (categoryInfo != null)
	// {
	// return findCategoryItems(categoryInfo.class_list, categoryName);
	// }
	// return null;
	// }

	// public static CategoryItem[] findCategoryItems(CategoryItem[] categoryList, String categoryName)
	// {
	// if (categoryList != null && categoryList.length > 0)
	// {
	// List<CategoryItem> list = new ArrayList<CategoryItem>();
	// for (CategoryItem item : categoryList)
	// {
	// if (item.gc_name.contains(categoryName))
	// {
	// list.add(item);
	// }
	// }
	// CategoryItem[] items = new CategoryItem[list.size()];
	// items = list.toArray(items);
	// return items;
	// }
	// return null;
	// }

	public static final int HistoryDB_Version = 2;
	public static final int SearchDB_Version = 2;
	public static final int FileDownloadDB_Version = 2;
	public static final int CombineDB_Viersion = 4;
	public static final int SystemModelDB_Version = 6;
	public static final int SystemBackgroundDB_Version = 3;
	public static final int PhotoModelDB_Version = 3;
}
