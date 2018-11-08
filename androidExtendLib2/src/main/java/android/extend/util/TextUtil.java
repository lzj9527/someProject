package android.extend.util;

import java.util.List;

import android.text.TextUtils;

public class TextUtil
{
	public static boolean isContainsString(String text, String[] strings)
	{
		if (!TextUtils.isEmpty(text))
		{
			for (String str : strings)
			{
				if (!TextUtils.isEmpty(str) && text.contains(str))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isContainsString(String text, List<String> list)
	{
		if (!TextUtils.isEmpty(text))
		{
			for (String str : list)
			{
				if (!TextUtils.isEmpty(str) && text.contains(str))
				{
					return true;
				}
			}
		}
		return false;
	}

	// sqlite特殊字符转义
	public static String sqliteEscape(String keyWord)
	{		
		keyWord = keyWord.replace("/", "//");
		keyWord = keyWord.replace("'", "''");
		keyWord = keyWord.replace("[", "/[");
		keyWord = keyWord.replace("]", "/]");
		keyWord = keyWord.replace("%", "/%");
		keyWord = keyWord.replace("&", "/&");
		keyWord = keyWord.replace("_", "/_");
		keyWord = keyWord.replace("(", "/(");
		keyWord = keyWord.replace(")", "/)");
		return keyWord;
	}
}
