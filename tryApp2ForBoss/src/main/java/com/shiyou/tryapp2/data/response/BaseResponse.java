package com.shiyou.tryapp2.data.response;

import android.extend.data.BaseData;

public class BaseResponse extends BaseData
{
	public static final int RESULT_OK = 0;

	public int code;
	public int resultCode = -1;
	public String error;
}
