package com.example.hatsutc;

public class MonthInfo extends WorkTimeInfo
{
	private DayInfo[] mDays = new DayInfo[31];
	
	private int mNumOfWorkdays = 0;
	private int mNumOfPrePayment = 0;
	
	private int	mMaxDays = 0;
	
	public MonthInfo()
	{
		for (int i = 0; i < 31; i++)
		{
			mDays[i] = new DayInfo();
		}
	}
	
	public DayInfo GetDayInfo(int index)
	{
		mMaxDays	= index;

		return mDays[index];
	}
	
	public void Update()
	{
		mFixedWeekdayWT		= 0;
		mExtraWeekdayWT		= 0;
		mFixedHolidayWT			= 0;
		mLegalHolidayWT			= 0;

		mNumOfWorkdays		= 0;
		mNumOfPrePayment	= 0;
		
		for (int i = 0; i <= mMaxDays; i++)
		{
			mFixedWeekdayWT	+= mDays[i].GetFixedWeekdayWorkTime10();
			mExtraWeekdayWT	+= mDays[i].GetExtraWeekdayWorkTime10();
			mFixedHolidayWT		+= mDays[i].GetFixedHolidayWorkTime10();
			mLegalHolidayWT		+= mDays[i].GetLegalHolidayWorkTime10();
			
			if (mDays[i].IsValidComeTime() && mDays[i].IsValidLeftTime())
			{
				mNumOfWorkdays++;
			}
			
			if (mDays[i].PrePayment)
			{
				mNumOfPrePayment++;
			}
		}
	}
	
	public int GetNumberOfWorkdays()
	{
		return mNumOfWorkdays;
	}
	
	public int GetNumberOfPrePayment()
	{
		return mNumOfPrePayment;
	}
}
