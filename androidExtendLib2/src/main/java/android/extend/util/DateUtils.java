package android.extend.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils
{
	public static final String TAG = DateUtils.class.getSimpleName();

	public static final String DEFAULT_DATEFORMAT = "yyyyMMdd_kkmmss";

	public static String getMonthEnString(int month)
	{
		switch (month)
		{
			case Calendar.JANUARY:
				return "JANUARY";
			case Calendar.FEBRUARY:
				return "FEBRUARY";
			case Calendar.MARCH:
				return "MARCH";
			case Calendar.APRIL:
				return "APRIL";
			case Calendar.MAY:
				return "MAY";
			case Calendar.JUNE:
				return "JUNE";
			case Calendar.JULY:
				return "JULY";
			case Calendar.AUGUST:
				return "AUGUST";
			case Calendar.SEPTEMBER:
				return "SEPTEMBER";
			case Calendar.OCTOBER:
				return "OCTOBER";
			case Calendar.NOVEMBER:
				return "NOVEMBER";
			case Calendar.DECEMBER:
				return "DECEMBER";
			default:
				// throw new IllegalArgumentException("unknown month " + month);
				LogUtil.w(TAG, "unknown month " + month);
				return "";
		}
	}

	public static String getMonthEnShortString(int month)
	{
		switch (month)
		{
			case Calendar.JANUARY:
				return "Jan.";
			case Calendar.FEBRUARY:
				return "Feb.";
			case Calendar.MARCH:
				return "Mar.";
			case Calendar.APRIL:
				return "Apr.";
			case Calendar.MAY:
				return "May.";
			case Calendar.JUNE:
				return "Jun.";
			case Calendar.JULY:
				return "Jul.";
			case Calendar.AUGUST:
				return "Aug.";
			case Calendar.SEPTEMBER:
				return "Sept.";
			case Calendar.OCTOBER:
				return "Oct.";
			case Calendar.NOVEMBER:
				return "Nov.";
			case Calendar.DECEMBER:
				return "Dec.";
			default:
				// throw new IllegalArgumentException("unknown month " + month);
				LogUtil.w(TAG, "unknown month " + month);
				return "";
		}
	}

	public static String getWeekDayEnString(int weekday)
	{
		switch (weekday)
		{
			case Calendar.SUNDAY:
				return "SUNDAY";
			case Calendar.MONDAY:
				return "MONDAY";
			case Calendar.TUESDAY:
				return "TUESDAY";
			case Calendar.WEDNESDAY:
				return "WEDNESDAY";
			case Calendar.THURSDAY:
				return "THURSDAY";
			case Calendar.FRIDAY:
				return "FRIDAY";
			case Calendar.SATURDAY:
				return "SATURDAY";
			default:
				// throw new IllegalArgumentException("unknown weekday " + weekday);
				LogUtil.w(TAG, "unknown weekday " + weekday);
				return "";
		}
	}

	public static String getCurrentDateFormatString(String pattern)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}

	public static String getDateFormatString(long milliseconds, String pattern)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date(milliseconds));
	}

	public static Date parseDateFormat(String formatString, String pattern) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(formatString);
	}
}
