package android.extend.data;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.extend.BasicConfig;
import android.extend.util.LogUtil;
import android.text.TextUtils;

/**
 * BaseData
 * 
 * @category 数据定义类继承该类，可将数据以Json文本形式打印出来
 * @author Lijuhong
 * */
public abstract class BaseData
{
	public void printData(String logTag, int preSpaceNum)
	{
		if (!BasicConfig.DebugMode)
			return;
		String preSpace = "";
		for (int i = 0; i < preSpaceNum; i++)
		{
			preSpace += " ";
		}
		printData(logTag, "", preSpace);
	}

	private void printData(String logTag, String name, String preSpace)
	{
		String nextSpace = preSpace + "  ";
		Class<?> classz = this.getClass();
		Field[] fields = classz.getFields();
		if (TextUtils.isEmpty(name))
			LogUtil.d(logTag, preSpace + classz.getSimpleName() + ":{");
		else
			LogUtil.d(logTag, preSpace + name + ":" + classz.getSimpleName() + ":{");
		for (Field field : fields)
		{
			if (Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			String fieldName = field.getName();
			try
			{
				Object value = field.get(this);
				printObject(logTag, fieldName, value, nextSpace);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		LogUtil.d(logTag, preSpace + "}");
	}

	private void printObject(String logTag, String name, Object value, String preSpace)
	{
		if (value != null)
		{
			if (value instanceof BaseData)
			{
				BaseData data = (BaseData)value;
				data.printData(logTag, name, preSpace);
				return;
			}
			else if (value.getClass().isArray())
			{
				printArray(logTag, name, value, preSpace);
				return;
			}
		}
		LogUtil.i(logTag, preSpace + name + ":" + value);
	}

	private void printArray(String logTag, String arrayName, Object array, String preSpace)
	{
		String nextSpace = preSpace + "  ";
		LogUtil.d(logTag, preSpace + arrayName + ":[");
		int length = Array.getLength(array);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++)
		{
			Object value = Array.get(array, i);
			if (value != null && value instanceof BaseData)
			{
				BaseData data = (BaseData)value;
				data.printData(logTag, "", nextSpace);
			}
			else
			{
				sb.append(value).append(',');
			}
		}
		if (sb.length() > 0)
			LogUtil.i(logTag, nextSpace + sb.toString());
		LogUtil.d(logTag, preSpace + "]");
	}
}
