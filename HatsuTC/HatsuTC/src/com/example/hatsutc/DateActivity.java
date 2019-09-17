package com.example.hatsutc;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.SubMenu;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

public class DateActivity extends Activity
{
	private final static int MENU_CLEAR			= 0;

	private final static int MENU_FIXTIME		= 10;
	private final static int MENU_FIXTIME_77_00 = 11;
	private final static int MENU_FIXTIME_77_05 = 12;
	private final static int MENU_FIXTIME_77_10 = 13;
	private final static int MENU_FIXTIME_77_15 = 14;
	private final static int MENU_FIXTIME_77_20 = 15;
	private final static int MENU_FIXTIME_77_25 = 16;
	private final static int MENU_FIXTIME_77_30	= 17;
	
	private DayInfo info = new DayInfo();
	
	private SQLiteDB SQLDB = new SQLiteDB(this);
	
	private Contract cont = new Contract();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitydate);
		
		Intent intent = getIntent();
	
		info.SetDate(
				intent.getIntExtra("year", -1),
				intent.getIntExtra("month", -1),
				intent.getIntExtra("date", -1),
				intent.getIntExtra("dayOfWeek", -1));

		SQLDB.res = getResources();
		SQLDB.sp = PreferenceManager.getDefaultSharedPreferences(this);

		SQLDB.Open();
		
		SQLDB.SelectDate(info);

		SQLDB.SelectContract(info.Year(), info.Month() + 1, cont);

		if (! info.ExistsWorkTimesRecord)
		{
			info.PrePayment = cont.ExecPrePay;
		}
		
		((CheckBox)findViewById(R.id.chkPrePayment)).setChecked(info.PrePayment);

		setDateInfo();

		showComeTimeValue();
		showLeftTimeValue();

		showWorkTime();
		
		SQLDB.Close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		SubMenu sub = menu.addSubMenu(Menu.NONE, MENU_FIXTIME, Menu.NONE, R.string.menu_fixtime);
		
		sub.add(Menu.NONE, MENU_FIXTIME_77_00, Menu.NONE, R.string.menu_fixtime_77_00);
		sub.add(Menu.NONE, MENU_FIXTIME_77_05, Menu.NONE, R.string.menu_fixtime_77_05);
		sub.add(Menu.NONE, MENU_FIXTIME_77_10, Menu.NONE, R.string.menu_fixtime_77_10);
		sub.add(Menu.NONE, MENU_FIXTIME_77_15, Menu.NONE, R.string.menu_fixtime_77_15);
		sub.add(Menu.NONE, MENU_FIXTIME_77_20, Menu.NONE, R.string.menu_fixtime_77_20);
		sub.add(Menu.NONE, MENU_FIXTIME_77_25, Menu.NONE, R.string.menu_fixtime_77_25);
		sub.add(Menu.NONE, MENU_FIXTIME_77_30, Menu.NONE, R.string.menu_fixtime_77_30);

		menu.add(Menu.NONE, MENU_CLEAR, Menu.NONE, R.string.menu_time_clear);

		return true;
	}

	// オプションメニューアイテムが選択された時に呼び出されます
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case MENU_CLEAR:
			SetTime(-1, -1, -1, -1);
			return true;
		case MENU_FIXTIME_77_00:
			SetTime(9, 0, 17, 30);
			return true;
		case MENU_FIXTIME_77_05:
			SetTime(9, 0, 18, 10);
			return true;
		case MENU_FIXTIME_77_10:
			SetTime(9, 0, 18, 40);
			return true;
		case MENU_FIXTIME_77_15:
			SetTime(9, 0, 19, 10);
			return true;
		case MENU_FIXTIME_77_20:
			SetTime(9, 0, 19, 40);
			return true;
		case MENU_FIXTIME_77_25:
			SetTime(9, 0, 20, 10);
			return true;
		case MENU_FIXTIME_77_30:
			SetTime(9, 0, 20, 40);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	// 出社時間設定ボタンクリックリスナー
	public void EditSTimeOnClick(View v)
	{
		int h = 9;
		int m = 0;

		if (info.IsValidComeTime())
		{
			h = info.ComeTimeHour;
			m = info.ComeTimeMinute;
		}
		
		TimePickerDialog dlg = new TimePickerDialog(
				this,
				varSTimeSetListener,
				h,
				m,
				true);
		dlg.setTitle(getResources().getString(R.string.lbl_stime));

		dlg.show();
	}

	// 出社時間設定ダイアログ設定ボタンクリックリスナー
	private TimePickerDialog.OnTimeSetListener varSTimeSetListener
		= new TimePickerDialog.OnTimeSetListener()
		{
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute)
			{
				info.ComeTimeHour = hourOfDay;
				info.ComeTimeMinute = minute;
				
				showComeTimeValue();

				showWorkTime();
			}
		};

	// 退社時間設定ボタンクリックリスナー
	public void EditETimeOnClick(View v)
	{
		int h = 17;
		int m = 30;
		
		if (info.IsValidLeftTime())
		{
			h = info.LeftTimeHour;
			m = info.LeftTimeMinute;
		}

		TimePickerDialog dlg = new TimePickerDialog(
				this,
				varETimeSetListener,
				h,
				m,
				true);
		dlg.setTitle(getResources().getString(R.string.lbl_etime));
		
		dlg.show();
	}
	
	// 退社時間設定ダイアログ設定ボタンクリックリスナー
	private TimePickerDialog.OnTimeSetListener varETimeSetListener
		= new TimePickerDialog.OnTimeSetListener()
		{
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute)
			{
				info.LeftTimeHour = hourOfDay;
				info.LeftTimeMinute = minute;
	
				showLeftTimeValue();

				showWorkTime();
			}
		};
		
	public void ChkPrePaymentOnClick(View v)
	{
		info.PrePayment = ((CheckBox)v).isChecked();
	}
		
	// 決定ボタンクリックリスナー
	public void BtnOkOnClick(View v)
	{
		SQLDB.Open();
		SQLDB.UpdateWorkTime(info);
		SQLDB.Close();
		
		setResult(RESULT_OK, getIntent());

		finish();
	}
	
	// 取消ボタンクリックリスナー
	public void BtnNgOnClick(View v)
	{
		setResult(0, getIntent());

		finish();
	}
	
	private void setDateInfo()
	{
		String [] strs = getResources().getStringArray(R.array.day_of_week_names);
		// 勤務形態文字列取得
		String[] types = getResources().getStringArray(R.array.worktype);

		// 曜日別文字色配列取得
		TypedArray txtclrs = getResources().obtainTypedArray(R.array.text_colors);
		// 曜日別背景配列取得
		TypedArray sbgclrs = getResources().obtainTypedArray(R.array.unselect_backgrounds);

		// 日付表示
		TextView date = (TextView)findViewById(R.id.lblDate);
		
		date.setText(Common.FullDate(info.Year(), info.Month() + 1, info.Date(), strs[info.DayOfWeek() - 1]));
		date.setTextColor(txtclrs.getColor(info.IsTextColorType, 0));

		// 特記事項(祝日名など)表示
		TextView note = (TextView)findViewById(R.id.lblNote);
		
		note.setText(info.SpecialNote());
		note.setTextColor(txtclrs.getColor(info.IsTextColorType, 0));

		// 勤務形態表示
		TextView type = (TextView)findViewById(R.id.lblType);

		if (info.IsWorkTimeHoliday()) 		type.setText(types[2]);
		else if (info.IsWorkTimeSaturday())	type.setText(types[1]);
		else								type.setText(types[0]);

		// 背景の設定
		findViewById(R.id.tblDate).setBackgroundResource(sbgclrs.getResourceId(info.IsBackgroundType, 0));
		
		txtclrs.recycle();
		sbgclrs.recycle();
	}
	
	// 固定時間設定
	private void SetTime(int comehour, int comeminute, int lefthour, int leftminute)
	{
		info.ComeTimeHour	= comehour;
		info.ComeTimeMinute = comeminute;
		info.LeftTimeHour   = lefthour;
		info.LeftTimeMinute	= leftminute;
		
		showComeTimeValue();
		showLeftTimeValue();
		
		showWorkTime();
	}
	
	// 出社時間表示
	private void showComeTimeValue()
	{
		String t = getResources().getString(R.string.default_lbl_notime);
		
		if (info.IsValidComeTime())
		{
			t = Common.Int2Place(info.ComeTimeHour) +
					getResources().getString(R.string.time_sep) +
					Common.Int2Place(info.ComeTimeMinute);
		}
		
		((TextView)findViewById(R.id.lblComeTime)).setText(t);
	}
	
	// 退社時間表示
	private void showLeftTimeValue()
	{
		String t = getResources().getString(R.string.default_lbl_notime);
		
		if (info.IsValidLeftTime())
		{
			t = Common.Int2Place(info.LeftTimeHour) +
					getResources().getString(R.string.time_sep) +
					Common.Int2Place(info.LeftTimeMinute);
		}
		
		((TextView)findViewById(R.id.lblLeftTime)).setText(t);
	}
	
	// 労働時間表示
	private void showWorkTime()
	{
		String fw = getResources().getString(R.string.default_float_zero);
		String ew = getResources().getString(R.string.default_float_zero);
		String fh = getResources().getString(R.string.default_float_zero);
		String lh = getResources().getString(R.string.default_float_zero);
		
		if (info.IsValidComeTime() && info.IsValidLeftTime())
		{
			info.Calculate();
			
			fw = Double.toString(info.GetFixedWeekdayWorkTime());
			ew = Double.toString(info.GetExtraWeekdayWorkTime());
			fh = Double.toString(info.GetFixedHolidayWorkTime());
			lh = Double.toString(info.GetLegalHolidayWorkTime());
		}

		((TextView)findViewById(R.id.lblFixedWeekday)).setText(fw);
		((TextView)findViewById(R.id.lblExtraWeekday)).setText(ew);
		((TextView)findViewById(R.id.lblFixedHoliday)).setText(fh);
		((TextView)findViewById(R.id.lblLegalHoliday)).setText(lh);
	}
}
