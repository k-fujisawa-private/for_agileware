package com.example.hatsutc;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Common
{
	private SimpleDateFormat CalendarMonthTitleFormat = new SimpleDateFormat("yyyy”NMMŒŽ“x", Locale.JAPAN);
	private SimpleDateFormat CalendarDateTitleFormat = new SimpleDateFormat("MM/dd", Locale.JAPAN);

	public String MonthTitle(Calendar curr)
	{
		return CalendarMonthTitleFormat.format(curr.getTime());
	}
	
	public String DateTitle(Calendar curr)
	{
		return CalendarDateTitleFormat.format(curr.getTime());
	}
	
	public static String DateTitle(int month, int date)
	{
		return String.format(Locale.JAPAN, "%02d/%02d", month, date);
	}
	
	public static String FullDate(int year, int month, int date)
	{
		return String.format(Locale.JAPAN, "%04d%02d%02d", year, month, date);
	}
	
	public static String FullDate(int year, int month, int date, String dayOfWeek)
	{
		return String.format(Locale.JAPAN, "%04d/%02d/%02d (%s)",
				year,
				month,
				date,
				dayOfWeek);
	}
	
	public static String FullMonth(int year, int month)
	{
		return String.format(Locale.JAPAN, "%4d%02d", year, month);
	}
	public static String Int2Place(int value)
	{
		return String.format(Locale.JAPANESE, "%02d", value);
	}
	
	public static String Money(int value)
	{
		return String.format(Locale.JAPANESE, "%1$,3d", value);
	}
}
