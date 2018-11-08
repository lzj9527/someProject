package com.shiyou.tryapp2.data;

import android.extend.data.BaseData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TryonPoseData extends BaseData
{
	public int recordId = -1;
	public String actId;
	public UnityImageInfo BACKGROUND_IMAGE;
	public UnityImageInfo PERSON_IMAGE;
	public UnityImageInfo TOP_IMAGE;
	public UnityImageInfo LIMBS_IMAGE;
	public UnityImageInfo HAIR_IMAGE;
	public UnityModelInfo[] modelList;
	public UnityModelInfo commonClothesModel;// 通用的衣服模型数据

	public static String toJson(TryonPoseData info)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.toJson(info);
	}

	public static TryonPoseData fromJson(String json)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.fromJson(json, TryonPoseData.class);
	}
}
