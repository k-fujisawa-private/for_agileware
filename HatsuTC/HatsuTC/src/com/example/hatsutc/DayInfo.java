package com.example.hatsutc;

import java.util.Calendar;

public class DayInfo extends WorkTimeInfo
{
	private int mCurrY = -1;
	private int mCurrM = -1;
	private int mCurrD = -1;
	
	private int mDayOfWeek = -1;

	public boolean ExistsSpecialsRecord = false;
	public boolean ExistsWorkTimesRecord = false;

	private final static int TYPE_NATIONAL	= 1;
	private final static int TYPE_COMPANY	= 2;
	private final static int TYPE_EXTRA		= 0;

	private int		mSpecialType = -1;
	private String 	mSpecialNote = "";

	public int IsTextColorType	= IS_FIXEDWEEKDAY;
	public int IsBackgroundType	= IS_FIXEDWEEKDAY;
	public int IsWorkTimeType	= IS_FIXEDWEEKDAY;
	
	// 出社時間
	public int ComeTimeHour = -1;
	public int ComeTimeMinute = -1;

	// 退社時間
	public int LeftTimeHour = -1;
	public int LeftTimeMinute = -1;
	
	public boolean	PrePayment = false;

	public String WorkMemo = "";

	private final static int IS_FIXEDWEEKDAY = 0;
	private final static int IS_FIXEDHOLIDAY = 1;
	private final static int IS_LEGAYHOLIDAY = 2;

	// コンストラクタ
	public DayInfo()
	{
	}

	// 初期化
	public void Clear()
	{
		mCurrY = -1;
		mCurrM = -1;
		mCurrD = -1;
		
		mDayOfWeek = -1;
		
		ExistsSpecialsRecord = false;
		ExistsWorkTimesRecord = false;
		
		mSpecialType	= -1;
		mSpecialNote	= "";
		
		IsTextColorType		= IS_FIXEDWEEKDAY;
		IsBackgroundType	= IS_FIXEDWEEKDAY;
		IsWorkTimeType		= IS_FIXEDWEEKDAY;

		// 出社時間
		ComeTimeHour = -1;
		ComeTimeMinute = -1;

		// 退社時間
		LeftTimeHour = -1;
		LeftTimeMinute = -1;

		PrePayment	= false;

		// 勤務メモ
		WorkMemo = "";

		// 労働時間
		mFixedWeekdayWT	= 0;
		mExtraWeekdayWT	= 0;
		mFixedHolidayWT	= 0;
		mLegalHolidayWT	= 0;
	}
	
	// 日付設定
	public void SetDate(int year, int month, int date, int dayOfWeek)
	{
		mCurrY		= year;
		mCurrM		= month;
		mCurrD		= date;
		mDayOfWeek	= dayOfWeek;
		
		if (Calendar.SUNDAY == mDayOfWeek)
		{
			IsTextColorType		= IS_LEGAYHOLIDAY;
			IsBackgroundType	= IS_LEGAYHOLIDAY;
			IsWorkTimeType		= IS_LEGAYHOLIDAY;
		}
		else if (Calendar.SATURDAY == mDayOfWeek)
		{
			IsTextColorType		= IS_FIXEDHOLIDAY;
			IsBackgroundType	= IS_FIXEDHOLIDAY;
			IsWorkTimeType		= IS_FIXEDHOLIDAY;			
		}
		else
		{
			IsTextColorType		= IS_FIXEDWEEKDAY;
			IsBackgroundType	= IS_FIXEDWEEKDAY;
			IsWorkTimeType		= IS_FIXEDWEEKDAY;						
		}
	}
	
	public int Year()
	{
		return mCurrY;
	}
	
	public int Month()
	{
		return mCurrM;
	}
	public int Date()
	{
		return mCurrD;
	}
	public int DayOfWeek()
	{
		return mDayOfWeek;
	}
	public String SpecialNote()
	{
		return mSpecialNote;
	}
	
	// 特殊日指定の設定
	public void SetSpecialdayValue(int type, String note)
	{
		mSpecialType = type;
		mSpecialNote = note;
		
		// 日付文字の色のフラグ設定
		if (TYPE_NATIONAL == mSpecialType)
		{
			IsTextColorType	= IS_LEGAYHOLIDAY;
		}

		// 背景のフラグ設定
		if (TYPE_EXTRA == mSpecialType)
		{
			IsBackgroundType	= IS_FIXEDWEEKDAY;
		}
		else if (TYPE_NATIONAL == mSpecialType || TYPE_COMPANY == mSpecialType)
		{
			IsBackgroundType	= IS_LEGAYHOLIDAY;
		}

		// 労働時間のフラグ設定
		if (TYPE_EXTRA == type)
		{
			IsWorkTimeType	= IS_FIXEDWEEKDAY;
		}
		else if ((TYPE_NATIONAL == type || TYPE_COMPANY == type) && mDayOfWeek != Calendar.SUNDAY)
		{
			IsWorkTimeType	= IS_FIXEDHOLIDAY;
		}
		
	}
	
	// 出社時間有効判定
	public boolean IsValidComeTime()
	{
		return (-1 != ComeTimeHour && -1 != ComeTimeMinute);
	}

	// 退社時間有効判定
	public boolean IsValidLeftTime()
	{
		return (-1 != LeftTimeHour && -1 != LeftTimeMinute);
	}

	public boolean IsWorkTimeWeekday()
	{
		return (IS_FIXEDWEEKDAY == IsWorkTimeType);
	}
	public boolean IsWorkTimeSaturday()
	{
		return (IS_FIXEDHOLIDAY == IsWorkTimeType);
	}
	public boolean IsWorkTimeHoliday()
	{
		return (IS_LEGAYHOLIDAY == IsWorkTimeType);
	}
	
	// 時間計算
	public void Calculate()
	{
		Calendar c = Calendar.getInstance();
		c.clear();
	
		int wtAM = 0;
		int wtPM = 0;
		int wtOV = 0;

		// 休憩の区切り時間を取得
		c.set(mCurrY, mCurrM, mCurrD, 12, 0, 0);
		long lunchS = c.getTimeInMillis();
		
		c.set(mCurrY, mCurrM, mCurrD, 12, 45, 0);
		long lunchE = c.getTimeInMillis();
		
		c.set(mCurrY, mCurrM, mCurrD, 17, 30, 0);
		long supperS = c.getTimeInMillis();
		
		c.set(mCurrY, mCurrM, mCurrD, 17, 40, 0);
		long supperE = c.getTimeInMillis();

		c.set(mCurrY, mCurrM, mCurrD, ComeTimeHour, ComeTimeMinute, 0);
		long st = c.getTimeInMillis();

		c.set(mCurrY, mCurrM, mCurrD, LeftTimeHour, LeftTimeMinute, 0);
		long et = c.getTimeInMillis();

		// 午前の労働時間
		if (st < lunchS)
		{
			long s = st;
			long e = Math.min(et, lunchS);
			
			wtAM = CalculateWorkTime(s, e);
		}
		
		// 午後の労働時間
		if (st < supperS && et > lunchE)
		{
			long s = Math.max(st, lunchE);
			long e = Math.min(et, supperS);
			
			wtPM = CalculateWorkTime(s, e);
		}
		
		// 残業の労働時間
		if (et > supperE)
		{
			long s = Math.max(st, supperE);
			long e = et;
			
			wtOV = CalculateWorkTime(s, e);
		}
		
		if (IS_FIXEDWEEKDAY == IsWorkTimeType)
		{
			mFixedWeekdayWT	= wtAM + wtPM;
			mExtraWeekdayWT	= wtOV;
		}
		else if (IS_FIXEDHOLIDAY == IsWorkTimeType)
		{
			mFixedHolidayWT	= wtAM + wtPM + wtOV;
		}
		else if (IS_LEGAYHOLIDAY == IsWorkTimeType)
		{
			mLegalHolidayWT	= wtAM + wtPM + wtOV;
		}
	}
	
	// ミリ秒から時間x10倍値に変換
	private int CalculateWorkTime(long start, long end)
	{
		long d = end - start;

		// ミリ秒切捨て
	    d /= 1000;
		// 秒切捨て
	    d /= 60;

	    // 労働時間(10倍)
	    // ※少数演算をしないように、対象桁小数点第1位までを整数とする為、
	    //   本来60(分)で割るのを6で割り、10倍で扱う。
	    return (int)d / 6;
	}
}
