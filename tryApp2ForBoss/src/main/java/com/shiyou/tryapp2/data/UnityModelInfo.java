package com.shiyou.tryapp2.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UnityModelInfo extends FileInfo
{
	public UnityModelInfo()
	{
	}

	public UnityModelInfo(FileInfo tempFile)
	{
		super(tempFile);
	}

	public String id;// 商品id，唯一标识
	public String typeName;// 商品试戴类型Name
	public int type;// 模型类型
	// public String faceTag;// 面向标记
	public int weight;// 重量，单位分
	// public String replaceId;// 替换商品id，只在执行替换操作时使用
	// public boolean useAnimation = true;// 是否使用动画

	/**
	 * 以下是试戴相关属性，从保存的试戴数据中读取
	 * */
	public byte needReadRecord;

	public int layer;

	public void copyFrom(UnityModelInfo other)
	{
		if (other != null)
		{
			copyFrom(((FileInfo)other));
			id = other.id;
			type = other.type;
			// faceTag = other.faceTag;
			// replaceId = other.replaceId;
			// useAnimation = other.useAnimation;
			needReadRecord = other.needReadRecord;
			layer = other.layer;
		}
	}

	public double[] xPosition;
	public double[] yPosition;

	// public double[] imageAlphaAmount;
	// public double[] imageBrightnessAmount;
	// public double[] imageContrastAmount;
	// public double[] imageSaturationAmount;

	public double[] scaleSliderValue;
	public double[] rotateZSliderValue;
	public double[] rotateYSliderValue;
	public double[] rotateXSliderValue;

	// public double[] body3RotateSliderValue;
	// public double[] body2RotateSliderValue;
	// public double[] bodyRotateSliderValue;
	// public double[] body2LengthSliderValue;
	// public double[] bodyLengthSliderValue;
	// public double[] neckWidthSliderValue;
	// public double[] neckHeightSliderValue;
	// public double[] neckRotateSliderValue;
	// public double[] chestWidthSliderValue;
	// public double[] waistWidthSliderValue;
	// public double[] natesLWidthSliderValue;
	// public double[] natesRWidthSliderValue;
	// public double[] shoulderLWidthSliderValue;
	// public double[] shoulderRWidthSliderValue;
	// public double[] shoulderLHeightSliderValue;
	// public double[] shoulderRHeightSliderValue;
	// public double[] shoulderLRotateSliderValue;
	// public double[] shoulderRRotateSliderValue;
	// public double[] upperarmLRotateSliderValue;
	// public double[] upperarmRRotateSliderValue;
	// public double[] upperarmLLengthSliderValue;
	// public double[] upperarmRLengthSliderValue;
	// public double[] forearmLLengthSliderValue;
	// public double[] forearmRLengthSliderValue;
	// public double[] forearmLRotateSliderValue;
	// public double[] forearmRRotateSliderValue;
	// public double[] armLWidthSliderValue;
	// public double[] armRWidthSliderValue;
	// public double[] forearmLWidthSliderValue;
	// public double[] forearmRWidthSliderValue;
	// public double[] wristLWidthSliderValue;
	// public double[] wristRWidthSliderValue;
	// public double[] thighLWidthSliderValue;
	// public double[] thighRWidthSliderValue;
	// public double[] crusLWidthSliderValue;
	// public double[] crusRWidthSliderValue;
	// public double[] footLWidthSliderValue;
	// public double[] footRWidthSliderValue;
	// public double[] thighLRotateSliderValue;
	// public double[] thighRRotateSliderValue;
	// public double[] crusLRotateSliderValue;
	// public double[] crusRRotateSliderValue;
	// public double[] thighLLengthSliderValue;
	// public double[] thighRLengthSliderValue;
	// public double[] crusLLengthSliderValue;
	// public double[] crusRLengthSliderValue;
	// public double[] necklaceWidthSliderValue;
	// public boolean[] ikLeftArmDepthAbove;
	// public boolean[] ikRightArmDepthAbove;
	// public boolean[] ikLeftLegDepthAbove;
	// public boolean[] ikRightLegDepthAbove;

	// old version define
	// public double[] upperarmLLenghtSliderValue;
	// public double[] upperarmRLenghtSliderValue;
	// public double[] forearmLLenghtSliderValue;
	// public double[] forearmRLenghtSliderValue;

	// public bool[] ikLeftDepthAbove;
	// public bool[] ikRightDepthAbove;
	// public double[] upperarmLRotate;
	// public double[] upperarmRRotate;
	// public double[] forearmLRotate;
	// public double[] forearmRRotate;

	public static String toJson(UnityModelInfo info)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.toJson(info);
	}

	public static UnityModelInfo fromJson(String json)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		UnityModelInfo info = gson.fromJson(json, UnityModelInfo.class);
		// old version compatible
		// if (info.upperarmLLengthSliderValue == null)
		// info.upperarmLLengthSliderValue = info.upperarmLLenghtSliderValue;
		// if (info.upperarmRLengthSliderValue == null)
		// info.upperarmRLengthSliderValue = info.upperarmRLenghtSliderValue;
		// if (info.forearmLLengthSliderValue == null)
		// info.forearmLLengthSliderValue = info.forearmLLenghtSliderValue;
		// if (info.forearmRLengthSliderValue == null)
		// info.forearmRLengthSliderValue = info.forearmRLenghtSliderValue;
		return info;
	}
}
