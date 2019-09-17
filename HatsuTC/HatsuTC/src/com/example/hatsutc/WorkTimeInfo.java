package com.example.hatsutc;

public class WorkTimeInfo
{
	// �J������
	protected int mFixedWeekdayWT	= 0;
	protected int mExtraWeekdayWT	= 0;
	protected int mFixedHolidayWT	= 0;
	protected int mLegalHolidayWT	= 0;

	// �J�����Ԏ擾 (�����l)
	public double GetFixedWeekdayWorkTime()
	{
		return ConvInt10ToDouble(mFixedWeekdayWT);
	}
	public double GetExtraWeekdayWorkTime()
	{
		return ConvInt10ToDouble(mExtraWeekdayWT);
	}
	public double GetFixedHolidayWorkTime()
	{
		return ConvInt10ToDouble(mFixedHolidayWT);
	}
	public double GetLegalHolidayWorkTime()
	{
		return ConvInt10ToDouble(mLegalHolidayWT);
	}
	
	// �J������x10�{�擾 (�����l)
	public int GetFixedWeekdayWorkTime10()
	{
		return mFixedWeekdayWT;
	}
	public int GetExtraWeekdayWorkTime10()
	{
		return mExtraWeekdayWT;
	}
	public int GetFixedHolidayWorkTime10()
	{
		return mFixedHolidayWT;
	}
	public int GetLegalHolidayWorkTime10()
	{
		return mLegalHolidayWT;
	}

	// 10�{�����l�̏����ϊ�
	private double ConvInt10ToDouble(int val)
	{
		String tmp = Integer.toString(val);

		String n; // ������
		String m; // ������
		
		n = tmp.substring(0, tmp.length() - 1);
		m = tmp.substring(tmp.length() -1);
		
		return Double.parseDouble(n + "." + m);		
	}
}
