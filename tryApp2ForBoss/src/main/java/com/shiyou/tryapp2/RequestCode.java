package com.shiyou.tryapp2;

public class RequestCode
{
	/** 自动登录 */
	public static final int auto_login = 0;
	/** 用户注册 */
	public static final int user_register = 1001;
	/** 用户登录 */
	public static final int user_login = 1002;
	/** 用户重置密码 */
	public static final int user_reset_password = 1003;
	/** 用户修改信息 */
	public static final int user_modify_info = 1004;
	/** 版本检查 */
	public static final int user_check_version = 1005;
	/** 用户信息 */
	public static final int user_info = 1006;
	/** 反馈建议 */
	public static final int user_feedback = 1007;
	/** 用户注册协议内容 */
	public static final int user_register_protocol = 1008;
	/** 用户注册验证码 */
	public static final int user_register_verifycode = 1009;
	/** 用户重置密码验证码 */
	public static final int user_reset_password_verifycode = 1010;
	/** 用户关注他人 */
	public static final int user_attention = 1011;
	/** 取消关注 */
	public static final int user_cancel_attention = 1012;
	/** 是否已关注 */
	public static final int user_isattentioned = 1013;
	/** 用户IM Token */
	public static final int user_imtoken = 1014;
	/** 用户退出登录 */
	public static final int user_logout = 1015;

	/** 品牌列表 */
	public static final int brand_list = 2001;
	/** 品牌首页 */
	public static final int brand_homepage = 2002;
	/** 品牌产品列表 */
	public static final int brand_product_list = 2003;
	/** 品牌首页推荐 */
	public static final int brand_main_recommends = 2004;
	/** 品牌标签 */
	public static final int brand_tags = 2005;
	/** 标签品牌列表 */
	public static final int tag_brand_list = 2006;

	/** 产品列表 */
	public static final int product_list = 3001;
	/** 产品信息 */
	public static final int product_info = 3002;
	/** 产品分类 */
	public static final int product_category = 3003;
	/** 产品推荐 */
	public static final int product_recommends = 3004;
	/** 首页产品推荐 */
	public static final int product_main_recommends = 3005;
	/** 产品标签 */
	public static final int product_tags = 3006;
	/** 标签产品列表 */
	public static final int tag_product_list = 3007;
	/** 产品参数 */
	public static final int product_attribute = 3008;
	/** 产品评价 */
	public static final int product_evaluate = 3009;
	/** 产品关联搭配 */
	public static final int product_combines = 3010;
	/** 产品规格信息 */
	public static final int product_specinfo = 3011;
	/** 产品库存 */
	public static final int product_storage = 3012;

	/** 搭配分类 */
	public static final int combine_category = 4001;
	/** 搭配列表 */
	public static final int combine_list = 4002;
	/** 搭配信息 */
	public static final int combine_info = 4003;
	/** 搭配评论 */
	public static final int combine_evaluate = 4004;
	/** 搭配首页推荐 */
	public static final int combine_main_recommends = 4005;
	/** 搭配发布 */
	public static final int combine_publish = 4006;
	/** 发布的搭配列表 */
	public static final int combine_publish_list = 4007;
	/** 搭配收藏 */
	public static final int combine_addcollect = 4008;
	/** 搭配收藏列表 */
	public static final int combine_collect_list = 4009;
	/** 是否已收藏 */
	public static final int combine_iscollected = 4010;
	/** 取消收藏 */
	public static final int combine_collect_cancel = 4011;
	/** 搭配标签 */
	public static final int combine_tags = 4012;
	/** 标签搭配列表 */
	public static final int tag_combine_list = 4013;

	/** 收藏列表 */
	public static final int product_collect_list = 5001;
	/** 添加收藏 */
	public static final int product_collect_add = 5002;
	/** 取消收藏 */
	public static final int product_collect_cancel = 5003;
	/** 是否收藏 */
	public static final int product_iscollected = 5004;

	/** 收藏列表 */
	public static final int store_collect_list = 6001;
	/** 添加收藏 */
	public static final int store_collect_add = 6002;
	/** 取消收藏 */
	public static final int store_collect_cancel = 6003;
	/** 是否收藏 */
	public static final int store_iscollected = 6004;
	/** 店铺信息 */
	public static final int store_info = 6005;
	/** 店铺列表 */
	public static final int store_list = 6006;

	/** 海报素材分类 */
	public static final int poster_category = 7001;
	/** 海报素材信息列表 */
	public static final int poster_material_list = 7002;
	/** 试戴背景列表 */
	public static final int tryon_background_list = 7003;
	/** 试戴模特列表 */
	public static final int tryon_model_list = 7004;

	/** 首页广告推荐 */
	public static final int advertisement_main = 8001;
	/** 试戴广告推荐 */
	public static final int advertisement_tryon = 8002;

	/** 获取搜索关键字 */
	public static final int search_getkeywords = 9001;
	/** 搜索结果 */
	public static final int search_result = 9002;
	/** 地区查询 */
	public static final int area_list = 9003;
	/** 收货地址列表 */
	public static final int address_list = 9004;
	/** 收货地址信息 */
	public static final int address_info = 9005;
	/** 收货地址删除 */
	public static final int address_delete = 9006;
	/** 收货地址添加 */
	public static final int address_add = 9007;
	/** 收货地址修改 */
	public static final int address_modify = 9008;
	/** 设置默认收货地址 */
	public static final int address_default = 9009;

	/** 购买 */
	public static final int shopping_buy = 10001;
	/** 购物车购买 */
	public static final int shoppingcart_buy = 10002;
	/** 添加购物车 */
	public static final int shoppingcart_add = 10003;
	/** 购物车列表 */
	public static final int shoppingcart_list = 10004;
	/** 修改购物车商品数量 */
	public static final int shoppingcart_modify_num = 10005;
	/** 删除购物车 */
	public static final int shoppingcart_delete = 10006;
	/** 确认订单 */
	public static final int shopping_confirm_order = 10007;
	/** 购物车确认订单 */
	public static final int shoppingcart_confirm_order = 10008;
	/** 订单列表 */
	public static final int order_list = 10010;
	/** 取消订单 */
	public static final int order_cancel = 10011;
	/** 快递查询 */
	public static final int deliver_info = 10012;
	/** 确认收货 */
	public static final int order_confirm_over = 10013;
	/** 订单评价 */
	public static final int order_evaluate = 10014;
	/** 发起退款 */
	public static final int refund_start = 10015;
	/** 提交退款 */
	public static final int refund_submit = 10016;

	/** 微店信息 */
	public static final int micro_store_info = 11001;
	/** 微店信息修改 */
	public static final int micro_store_modify = 11002;

	/** 上传分享图片 */
	public static final int upload_share_image = 20001;
	/** 上传搭配图片 */
	public static final int upload_combine_image = 20002;
	/** 上传微店logo图片 */
	public static final int upload_mstore_logo = 20003;
	/** 上传微店背景图片 */
	public static final int upload_mstore_bg = 20004;
	/** 上传商品动作数据 */
	public static final int upload_act_json = 20005;
	/** 读取商品动作数据 */
	public static final int load_act_json = 20006;
	/** 请求同步商品浏览历史 */
	public static final int request_copy_browse = 20007;
}
