package com.shiyou.tryapp2;

import java.util.ArrayList;
import java.util.List;

public class Define
{
	public static final String Scene_CombineTryOn = "CombineTryOn";
	public static final String Scene_3DShow = "3DShow";

	public static final String UI_SPLASH = "splash";
	public static final String UI_MAIN_UI = "main_ui";
	public static final String UI_DETAIL_UI = "detail_ui";
	public static final String UI_TRYON_UI = "tryon_ui";

	public static final String STATE_NORMAL = "NORMAL";
	public static final String STATE_ACTION = "ACTION";
	public static final String STATE_IK = "IK";
	public static final String STATE_TONING = "TONING";
	public static final String STATE_ERASURE = "ERASURE";
	public static final String STATE_SETTING = "SETTING";
	public static final String STATE_3DSHOW = "SHOW3DPLAY";

	public static final String ERASURE_STATE_NORMAL = "NORMAL";
	public static final String ERASURE_STATE_EDITOR = "EDITOR";
	public static final String ERASURE_STATE_REMOVE = "REMOVE";

	public static final String TAG_BACKGROUND_IMAGE = "BACKGROUND_IMAGE";
	public static final String TAG_PERSON_IMAGE = "PERSON_IMAGE";
	// public static final String TAG_TOP_IMAGE = "TOP_IMAGE";
	public static final String TAG_LIMBS_IMAGE = "LIMBS_IMAGE";
	public static final String TAG_HAIR_IMAGE = "HAIR_IMAGE";

	public static final String MATERIAL_WHITE_KGOLD = "K白";
	public static final String MATERIAL_RED_KGOLD = "K红";
	public static final String MATERIAL_YELLOW_KGOLD = "K黄";

	public static final String attr_alpha = "透明度";
	public static final String attr_brightness = "亮度";
	public static final String attr_saturation = "饱和度";
	public static final String attr_contrast = "对比度";
	public static final String attr_scale = "缩放";
	public static final String attr_z_rotate = "水平旋转";
	public static final String attr_y_rotate = "纵向旋转";
	public static final String attr_x_rotate = "横向旋转";
	public static final String attr_body3_rotate = "弯腰3";
	public static final String attr_body2_rotate = "弯腰2";
	public static final String attr_body2_length = "腰长2";
	public static final String attr_body_rotate = "弯腰";
	public static final String attr_body_length = "腰长";
	public static final String attr_neck_width = "颈宽";
	public static final String attr_neck_height = "颈高";
	public static final String attr_shoulder_l_width = "左肩宽";
	public static final String attr_shoulder_r_width = "右肩宽";
	public static final String attr_shoulder_l_height = "左肩高";
	public static final String attr_shoulder_r_height = "右肩高";
	public static final String attr_shoulder_l_rotate = "左肩旋转";
	public static final String attr_shoulder_r_rotate = "右肩旋转";
	public static final String attr_chest_width = "胸围";
	public static final String attr_waist_width = "腰围";
	public static final String attr_nates_l_width = "左臀围";
	public static final String attr_nates_r_width = "右臀围";
	public static final String attr_upperarm_l_rotate = "左臂旋转";
	public static final String attr_upperarm_r_rotate = "右臂旋转";
	public static final String attr_upperarm_l_length = "左上臂长";
	public static final String attr_upperarm_r_length = "右上臂长";
	public static final String attr_forearm_l_rotate = "左前臂旋转";
	public static final String attr_forearm_r_rotate = "右前臂旋转";
	public static final String attr_forearm_l_length = "左前臂长";
	public static final String attr_forearm_r_length = "右前臂长";
	public static final String attr_arm_l_width = "左臂宽";
	public static final String attr_arm_r_width = "右臂宽";
	public static final String attr_forearm_l_width = "左前臂宽";
	public static final String attr_forearm_r_width = "右前臂宽";
	public static final String attr_wrist_l_width = "左袖口宽";
	public static final String attr_wrist_r_width = "右袖口宽";
	public static final String attr_thigh_l_width = "左腿宽";
	public static final String attr_thigh_r_width = "右腿宽";
	public static final String attr_crus_l_width = "左小腿宽";
	public static final String attr_crus_r_width = "右小腿宽";
	public static final String attr_foot_l_width = "左裤脚宽";
	public static final String attr_foot_r_width = "右裤脚宽";
	public static final String attr_thigh_l_rotate = "左腿旋转";
	public static final String attr_thigh_r_rotate = "右腿旋转";
	public static final String attr_thigh_l_length = "左大腿长度";
	public static final String attr_thigh_r_length = "右大腿长度";
	public static final String attr_crus_l_rotate = "左小腿旋转";
	public static final String attr_crus_r_rotate = "右小腿旋转";
	public static final String attr_crus_l_length = "左小腿长度";
	public static final String attr_crus_r_length = "右小腿长度";
	public static final String attr_necklace_width = "项链开口宽";

	public static final String TAG_RING = "one";
	public static final String TAG_COUPLE = "two";

	public static final String TAGNAME_WOMAN = "女戒";
	public static final String TAGNAME_MAN = "男戒";
	public static final String TAGNAME_PENDANT = "吊坠";

	public static final int REQ_LOGIN = 0x01;
	public static final int REQ_CAMERA_FROM_TRYON = 0x02;
	public static final int REQ_CAMERA_FROM_FIRSTTRYON = 0x03;
	public static final int REQ_ALBUM = 0x4;
	public static final int REQ_COMBINE_PUBLISH = 0x05;
	public static final int REQ_CAMERA_FROM_PHOTOMODEL = 0x06;
	public static final int REQ_PAYMENT = 0x07;
	public static final int REQ_SYSTEM_CAMERA = 0x08;
	public static final int REQ_SYSTEM_CROP = 0x09;
	public static final int REQ_SYSTEM_MODEL_UPDATE = 0x10;
	public static final int REQ_BACKGROUND_UPDATE = 0x11;
	public static final int REQ_PHOTO_MODEL_UPDATE = 0x12;
	public static final int REQ_LOGIN_FROM_MY = 0x13;
	public static final int REQ_LOGIN_FROM_COLLECT = 0x14;

	public static final String Name_RequestCode = "requestCode";
	public static final String Name_TryonType = "tryonType";
	public static final String Name_MaskIndex = "maskIndex";
	public static final String Name_ID = "id";
	public static final String Name_Path = "path";
	public static final String Name_Album = "album";

	public static final String Event_Register = "register";
	public static final String Event_Login = "login";

	private static List<String> mMaterialList = null;

	public static final List<String> getMaterialList()
	{
		if (mMaterialList == null)
		{
			mMaterialList = new ArrayList<String>();
			mMaterialList.add("18K白");
			mMaterialList.add("18K黄");
			mMaterialList.add("18K红");
			mMaterialList.add("Pt950");
		}
		return mMaterialList;
	}

	private static List<String> mMaleRingsizeList = null;
	private static List<String> mFemaleRingsizeList = null;

	public static final List<String> getMaleRingsizeList()
	{
		if (mMaleRingsizeList == null)
		{
			mMaleRingsizeList = new ArrayList<String>();
			mMaleRingsizeList.add("12号");
			mMaleRingsizeList.add("13号");
			mMaleRingsizeList.add("14号");
			mMaleRingsizeList.add("15号");
			mMaleRingsizeList.add("16号");
			mMaleRingsizeList.add("17号");
			mMaleRingsizeList.add("18号");
			mMaleRingsizeList.add("19号");
			mMaleRingsizeList.add("20号");
			mMaleRingsizeList.add("21号");
			mMaleRingsizeList.add("22号");
			mMaleRingsizeList.add("23号");
			mMaleRingsizeList.add("24号");
			mMaleRingsizeList.add("25号");
		}
		return mMaleRingsizeList;
	}

	public static final List<String> getFemaleRingsizeList()
	{
		if (mFemaleRingsizeList == null)
		{
			mFemaleRingsizeList = new ArrayList<String>();
			mFemaleRingsizeList.add("7号");
			mFemaleRingsizeList.add("8号");
			mFemaleRingsizeList.add("9号");
			mFemaleRingsizeList.add("10号");
			mFemaleRingsizeList.add("11号");
			mFemaleRingsizeList.add("12号");
			mFemaleRingsizeList.add("13号");
			mFemaleRingsizeList.add("14号");
			mFemaleRingsizeList.add("15号");
			mFemaleRingsizeList.add("16号");
		}
		return mFemaleRingsizeList;
	}
}
