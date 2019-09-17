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
	
	// �o�Ў���
	public int ComeTimeHour = -1;
	public int ComeTimeMinute = -1;

	// �ގЎ���
	public int LeftTimeHour = -1;
	public int LeftTimeMinute = -1;
	
	public boolean	PrePayment = false;

	public String WorkMemo = "";

	private final static int IS_FIXEDWEEKDAY = 0;
	private final static int IS_FIXEDHOLIDAY = 1;
	private final static int IS_LEGAYHOLIDAY = 2;

	// �R���X�g���N�^
	public DayInfo()
	{
	}

	// ������
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

		// �o�Ў���
		ComeTimeHour = -1;
		ComeTimeMinute = -1;

		// �ގЎ���
		LeftTimeHour = -1;
		LeftTimeMinute = -1;

		PrePayment	= false;

		// �Ζ�����
		WorkMemo = "";

		// �J������
		mFixedWeekdayWT	= 0;
		mExtraWeekdayWT	= 0;
		mFixedHolidayWT	= 0;
		mLegalHolidayWT	= 0;
	}
	
	// ���t�ݒ�
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
	
	// ������w��̐ݒ�
	public void SetSpecialdayValue(int type, String note)
	{
		mSpecialType = type;
		mSpecialNote = note;
		
		// ���t�����̐F�̃t���O�ݒ�
		if (TYPE_NATIONAL == mSpecialType)
		{
			IsTextColorType	= IS_LEGAYHOLIDAY;
		}

		// �w�i�̃t���O�ݒ�
		if (TYPE_EXTRA == mSpecialType)
		{
			IsBackgroundType	= IS_FIXEDWEEKDAY;
		}
		else if (TYPE_NATIONAL == mSpecialType || TYPE_COMPANY == mSpecialType)
		{
			IsBackgroundType	= IS_LEGAYHOLIDAY;
		}

		// �J�����Ԃ̃t���O�ݒ�
		if (TYPE_EXTRA == type)
		{
			IsWorkTimeType	= IS_FIXEDWEEKDAY;
		}
		else if ((TYPE_NATIONAL == type || TYPE_COMPANY == type) && mDayOfWeek != Calendar.SUNDAY)
		{
			IsWorkTimeType	= IS_FIXEDHOLIDAY;
		}
		
	}
	
	// �o�Ў��ԗL������
	public boolean IsValidComeTime()
	{
		return (-1 != ComeTimeHour && -1 != ComeTimeMinute);
	}

	// �ގЎ��ԗL������
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
	
	// ���Ԍv�Z
	public void Calculate()
	{
		Calendar c = Calendar.getInstance();
		c.clear();
	
		int wtAM = 0;
		int wtPM = 0;
		int wtOV = 0;

		// �x�e�̋�؂莞�Ԃ��擾
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

		// �ߑO�̘J������
		if (st < lunchS)
		{
			long s = st;
			long e = Math.min(et, lunchS);
			
			wtAM = CalculateWorkTime(s, e);
		}
		
		// �ߌ�̘J������
		if (st < supperS && et > lunchE)
		{
			long s = Math.max(st, lunchE);
			long e = Math.min(et, supperS);
			
			wtPM = CalculateWorkTime(s, e);
		}
		
		// �c�Ƃ̘J������
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
	
	// �~���b���玞��x10�{�l�ɕϊ�
	private int CalculateWorkTime(long start, long end)
	{
		long d = end - start;

		// �~���b�؎̂�
	    d /= 1000;
		// �b�؎̂�
	    d /= 60;

	    // �J������(10�{)
	    // ���������Z�����Ȃ��悤�ɁA�Ώی������_��1�ʂ܂ł𐮐��Ƃ���ׁA
	    //   �{��60(��)�Ŋ���̂�6�Ŋ���A10�{�ň����B
	    return (int)d / 6;
	}
}
