package android.extend.util;

import android.content.Context;

/**
 * 资源引用类
 * 
 * 所有对资源的引用都必须使用该类， 否则在Unity中会出现找不到id的情况
 * */
public final class ResourceUtil
{
	public static Object getResourceId(Context context, String name, String type)
	{
		String className = context.getPackageName();
		try
		{
			Class<?> parentClass = Class.forName(className + ".R");
			Class<?> childClass = ReflectHelper.getInnerClass(parentClass, type);
			return ReflectHelper.getStaticFieldValue(childClass, name);
		}
		catch (Exception e)
		{
			LogUtil.w(className, "", e);
		}
		return null;
	}

	public static int getStyleableId(Context context, String name)
	{
		return (Integer)getResourceId(context, name, "styleable");
	}

	public static int[] getStyleableArray(Context context, String name)
	{
		return (int[])getResourceId(context, name, "styleable");
	}

	public static int getLayoutId(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "layout", context.getPackageName());
	}

	public static int getAnimId(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "anim", context.getPackageName());
	}

	public static int getStringId(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "string", context.getPackageName());
	}

	public static int getDrawableId(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
	}

	public static int getStyleId(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "style", context.getPackageName());
	}

	public static int getColorId(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "color", context.getPackageName());
	}

	public static int getDimenId(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "dimen", context.getPackageName());
	}

	public static int getId(Context context, String name)
	{
		return context.getResources().getIdentifier(name, "id", context.getPackageName());
	}

	public static int getPositionId(Context context)
	{
		int id = ResourceUtil.getId(context, "position");
		if (id == 0)
			id = Integer.MAX_VALUE;
		return id;
	}

	public static int getDividerId(Context context)
	{
		int id = ResourceUtil.getId(context, "divider");
		if (id == 0)
			id = Integer.MAX_VALUE - 1;
		return id;
	}

	public static int getItemId(Context context)
	{
		int id = ResourceUtil.getId(context, "item");
		if (id == 0)
			id = Integer.MAX_VALUE - 2;
		return id;
	}

	public static int getRecycledId(Context context)
	{
		int id = ResourceUtil.getId(context, "recycle");
		if (id == 0)
			id = Integer.MAX_VALUE - 3;
		return id;
	}

	public static int getLoadedId(Context context)
	{
		int id = ResourceUtil.getId(context, "load");
		if (id == 0)
			id = Integer.MAX_VALUE - 4;
		return id;
	}
}
