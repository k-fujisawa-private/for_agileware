package com.example.hatsutc;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{
	private final static int MENU_ID_SETUP	= 0;
	private final static int MENU_ID_PAYSLIP = 1;

	private final static int REQCODE_ED	= 1;
	private final static int REQCODE_ES = 2;

	private LinearLayout[] mWeeks = new LinearLayout[6];
	
	private TextView mTotalWTFW;
	private TextView mTotalWTEW;
	private TextView mTotalWTFH;
	private TextView mTotalWTLH;

	private int	mOffset	= 0;
	
	private int mTodayY	= 0;
	private int mTodayM	= 0;
	private int mTodayD	= 0;
	
	private int mCurrY	= 0;
	private int mCurrM	= 0;
	private int mCurrD	= 0;

	private int mTargetM = 0;
	private int mTargetD = 0;

	private MonthInfo mMonth = new MonthInfo();

	private Contract mCont	= new Contract();
	
	private SQLiteDB SQLDB = new SQLiteDB(this);

	// 曜日別文字色配列取得
	TypedArray mTextColors;
	// 曜日別背景配列取得
	TypedArray mSelectBackgrounds;

	private Common format = new Common();

	// コンストラクタ
	public MainActivity()
	{
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

//		//タイトルバーを非表示にする（setContentViewの前にすること）
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activitymain);

		mWeeks[0] = (LinearLayout) findViewById(R.id.listMonthWeek1);
		mWeeks[1] = (LinearLayout) findViewById(R.id.listMonthWeek2);
		mWeeks[2] = (LinearLayout) findViewById(R.id.listMonthWeek3);
		mWeeks[3] = (LinearLayout) findViewById(R.id.listMonthWeek4);
		mWeeks[4] = (LinearLayout) findViewById(R.id.listMonthWeek5);
		mWeeks[5] = (LinearLayout) findViewById(R.id.listMonthWeek6);
		
		// 曜日別文字色配列取得
		mTextColors = getResources().obtainTypedArray(R.array.text_colors);
		// 曜日別背景配列取得
		mSelectBackgrounds = getResources().obtainTypedArray(R.array.select_backgrounds);

		// 合計時間表示欄生成
		createTotalLayout();
		// 曜日タイトル生成
		createDayOfWeekTitleView();
		
		Calendar curr	= Calendar.getInstance();
		
		mTodayY	= curr.get(Calendar.YEAR);
		mTodayM	= curr.get(Calendar.MONTH);
		mTodayD	= curr.get(Calendar.DATE);

		if (mTodayD > 15) mTodayM++;

		SQLDB.Open();
		
		SQLDB.res = getResources();
		SQLDB.sp = PreferenceManager.getDefaultSharedPreferences(this);

		// 源泉所得税情報の更新
		SQLDB.UpdateIncomeTaxs();

		// カレンダーの特殊情報(祝日、振替出勤など)を更新
		SQLDB.UpdateHolidays();
		
		SQLDB.Close();

		// カレンダー画面生成
		buildCalendar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(Menu.NONE, MENU_ID_PAYSLIP, Menu.NONE, R.string.lbl_payslip);
		menu.add(Menu.NONE, MENU_ID_SETUP, Menu.NONE, R.string.action_settings);
		
		return true;
	}

	// オプションメニューアイテムが選択された時に呼び出されます
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean ret = true;
		switch (item.getItemId())
		{
		default:
			ret = super.onOptionsItemSelected(item);
			break;
		case MENU_ID_SETUP:
			ShowSetupActivity();
			break;
		case MENU_ID_PAYSLIP:
			ShowPaySlip();
			break;
		}

		return ret;
	}
	@Override
	public void onClick(View v)
	{
		DayInfo info = (DayInfo)v.getTag();

		Intent intent = new Intent(this, DateActivity.class);

		intent.putExtra("year", info.Year());
		intent.putExtra("month", info.Month());
		intent.putExtra("date", info.Date());
		intent.putExtra("dayOfWeek", info.DayOfWeek());

		mTargetM = info.Month();
		mTargetD = info.Date();

		startActivityForResult(intent, REQCODE_ED);
	}

	//
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (REQCODE_ED == requestCode && RESULT_OK == resultCode)
		{
			buildCalendar();
			String m = Common.DateTitle(mTargetM + 1, mTargetD) +
					"の時間が変更されました。";
			Toast.makeText(this, m, Toast.LENGTH_LONG).show();
		}
		if (REQCODE_ES == requestCode && RESULT_OK == resultCode)
		{
			SQLDB.Open();

			// 契約内容取得
			mCont.Clear();
			SQLDB.SelectContract(mCurrY, mCurrM, mCont);
			
			SQLDB.Close();
		}
	}
	
	// 先月ボタンクリック
	public void BtnPrevOnClick(View v)
	{
		mOffset--;
		buildCalendar();		
	}
	
	// 次月ボタンクリック
	public void BtnNextOnClick(View v)
	{
		mOffset++;
		buildCalendar();
	}

	/*
	 * 
	 */
	private void ShowSetupActivity()
	{
		Intent intent = new Intent(this, SetupActivity.class);

		intent.putExtra("year", mCurrY);
		intent.putExtra("month", mCurrM);

		startActivityForResult(intent, REQCODE_ES);
	}
	/*
	 * 給与明細ダイアログ表示
	 */
	private void ShowPaySlip()
	{
		DlgPaySlip dlg = new DlgPaySlip(this);
		dlg.setTitle(R.string.lbl_payslip);
		
		PaySlipValues val = new PaySlipValues();

		// 時間別給与額設定
		val.WagesFixedWD = (int)Math.round(mMonth.GetFixedWeekdayWorkTime() * mCont.Hour100);
		val.WagesExtraWD = (int)Math.round(mMonth.GetExtraWeekdayWorkTime() * mCont.Hour125);
		val.WagesFixedHD = (int)Math.round(mMonth.GetFixedHolidayWorkTime() * mCont.Hour125);
		val.WagesLegalHD = (int)Math.round(mMonth.GetLegalHolidayWorkTime() * mCont.Hour135);

		// 交通費設定
		val.TrafficExpenses = mMonth.GetNumberOfWorkdays() * mCont.Fare;

		// 仮払金設定
		val.PrePayment = mMonth.GetNumberOfPrePayment() * mCont.PrePay;
		
		val.CalcWages();

		SQLDB.Open();
		val.IncomeTax = SQLDB.SelectIncomeTax(val.NetTaxableAmount,	mCurrY, mCont.Familys);
		SQLDB.Close();

		val.CalcDeduction();

		dlg.Values	= val;
		
		dlg.show();
	}
	
	/*
	 * 
	 */
	private void createTotalLayout()
	{
		mTotalWTFW 	= createTotalView(R.id.rowWTTotal1, R.string.lbl_time_fixed_weekday);
		mTotalWTEW 	= createTotalView(R.id.rowWTTotal2, R.string.lbl_time_extra_weekday);
		mTotalWTFH	= createTotalView(R.id.rowWTTotal1, R.string.lbl_time_fixed_holiday);
		mTotalWTLH	= createTotalView(R.id.rowWTTotal2, R.string.lbl_time_legal_holiday);
	}
	/*
	 * 
	 */
	private TextView createTotalView(int rowId, int lblId)
	{
		View child = getLayoutInflater().inflate(R.layout.totalinfo, null);
		
		String lbl = getResources().getString(lblId) +
					getResources().getString(R.string.lbl_time);
		
		((TextView)child.findViewById(R.id.lblTitle)).setText(lbl);
		
		((TableRow)findViewById(rowId)).addView(child);
		
		return (TextView)child.findViewById(R.id.lblValue);
	}
	
	/*
	 * 曜日タイトル生成
	 */
	private void createDayOfWeekTitleView()
	{
		LinearLayout list = (LinearLayout) findViewById(R.id.listWeekNames);
		
		list.removeAllViews();

		String [] weeks = getResources().getStringArray(R.array.day_of_week_names);

		for (int i = 0; i < weeks.length; i++)
		{
			// 曜日のレイアウトを生成
			View child = getLayoutInflater().inflate(R.layout.calendartitle, null);
			
			// 曜日を設定するTextViewのインスタンスを取得
			TextView text = (TextView) child.findViewById(R.id.lblMonth);
		
			// 曜日の文字を設定
			text.setText((i < weeks.length - 1)?weeks[i+1]:weeks[0]);

			// 文字の色を設定
			if (i == 6)
			{
				text.setTextColor(getResources().getColor(R.color.holiday_text));
				text.setBackgroundResource(R.drawable.bg_light_red);
			}
			else if (i == 5)
			{
				text.setTextColor(getResources().getColor(R.color.saturday_text));
				text.setBackgroundResource(R.drawable.bg_light_blue);
			}
			else
			{
				text.setTextColor(getResources().getColor(R.color.weekday_text));
				text.setBackgroundResource(R.drawable.bg_light_green);
			}
			
			list.addView(child);
		}
	}
	/*
	 * カレンダー画面生成
	 */
	private void buildCalendar()
	{
		Calendar curr = Calendar.getInstance();
		curr.clear();
		
		curr.set(mTodayY, mTodayM, 1);

		curr.add(Calendar.MONTH, mOffset);

		// タイトル設定
		setMonthTitle(curr);
		
		mCurrY	= curr.get(Calendar.YEAR);
		mCurrM	= curr.get(Calendar.MONTH);
		mCurrD	= curr.get(Calendar.DATE);
		
		curr.add(Calendar.MONTH, -1);
		curr.set(Calendar.DATE, 16);
		
		mWeeks[0].removeAllViews();
		mWeeks[1].removeAllViews();
		mWeeks[2].removeAllViews();
		mWeeks[3].removeAllViews();
		mWeeks[4].removeAllViews();
		mWeeks[5].removeAllViews();

		SQLDB.Open();

		// 日毎生成
		createDayOfMonthView(curr);

		// 契約内容取得
		mCont.Clear();
		int r = SQLDB.SelectContract(mCurrY, mCurrM, mCont);
		
		SQLDB.Close();
		
		mMonth.Update();
		
		mTotalWTFW.setText(Double.toString(mMonth.GetFixedWeekdayWorkTime()));
		mTotalWTEW.setText(Double.toString(mMonth.GetExtraWeekdayWorkTime()));
		mTotalWTFH.setText(Double.toString(mMonth.GetFixedHolidayWorkTime()));
		mTotalWTLH.setText(Double.toString(mMonth.GetLegalHolidayWorkTime()));
		
		if (0 == r)
		{
			ShowSetupActivity();
		}
	}
	
	/*
	 * タイトル設定
	 * 
	 * @param calendar
	 */
	private void setMonthTitle(Calendar curr)
	{
		((TextView)findViewById(R.id.lblMonth)).setText(format.MonthTitle(curr));
	}
	
	/*
	 * カレンダー生成
	 */
	private void createDayOfMonthView(Calendar curr)
	{
		// 表示月の最終日を取得
		int maxdate = curr.getActualMaximum(Calendar.DATE);
		int rc = 0;
		int cc = 0;

		int prevc = curr.get(Calendar.DAY_OF_WEEK);
		int nextc = 0;

		if (prevc == Calendar.SUNDAY) 	prevc = 6;
		else							prevc -= 2;
	
		if ((prevc + maxdate) % 7 > 0) nextc = 7 - (prevc + maxdate) % 7;

		curr.add(Calendar.DATE, 0 - prevc);

		// 対象月前の日毎のViewを作成
		for (int i = 0; i < prevc; i++)
		{
			View child = createIgnoreDateView(curr);

			mWeeks[rc].addView(child);
			
			cc++;
			
			curr.add(Calendar.DATE, 1);
		}
		
		// 対象月の日毎のViewを作成
		for (int i = 0; i < maxdate; i++)
		{
			View child = createTargetDateView(i, curr);
			
			mWeeks[rc].addView(child);
			
			cc++;

			if (cc == 7) {
				rc++;
				cc = 0;
			}
			
			curr.add(Calendar.DATE, 1);			
		}
		
		// 対象月後の日毎のViewを作成
		for (int i = 0; i < nextc; i++)
		{
			View child = createIgnoreDateView(curr);
			
			mWeeks[rc].addView(child);
			
			cc++;
			
			curr.add(Calendar.DATE, 1);
		}
	}
	
	/*
	 * 
	 */
	private View createTargetDateView(int idx, Calendar curr)
	{
		View child = getLayoutInflater().inflate(R.layout.calendardate, null);
		
		TextView txtDate = (TextView)child.findViewById(R.id.lblDate);
		
		txtDate.setText(format.DateTitle(curr));

		DayInfo info = mMonth.GetDayInfo(idx);

		info.Clear();
		
		info.SetDate(curr.get(Calendar.YEAR),
					 curr.get(Calendar.MONTH),
					 curr.get(Calendar.DATE),
					 curr.get(Calendar.DAY_OF_WEEK));

		SQLDB.SelectDate(info);
		
		// 日付の色の設定
		txtDate.setTextColor(mTextColors.getColor(info.IsTextColorType, 0));
	
		// 背景の設定
		child.setBackgroundResource(mSelectBackgrounds.getResourceId(info.IsBackgroundType, 0));
		
		TextView txtTime100 = (TextView)child.findViewById(R.id.lblTimeA);
		TextView txtTime125 = (TextView)child.findViewById(R.id.lblTimeB);
		TextView txtTime135 = (TextView)child.findViewById(R.id.lblTimeC);
		
		txtTime100.setVisibility(View.INVISIBLE);
		txtTime125.setVisibility(View.INVISIBLE);
		txtTime135.setVisibility(View.INVISIBLE);

		if (info.IsValidComeTime() && info.IsValidLeftTime())
		{
			info.Calculate();

			if (info.IsWorkTimeWeekday())
			{
				//
				txtTime100.setText(Double.toString(info.GetFixedWeekdayWorkTime()));
				txtTime100.setVisibility(View.VISIBLE);
				if (info.GetExtraWeekdayWorkTime10() > 0)
				{
					txtTime125.setText(Double.toString(info.GetExtraWeekdayWorkTime()));
					txtTime125.setVisibility(View.VISIBLE);
				}
			}
			else if (info.IsWorkTimeSaturday())
			{
				txtTime125.setText(Double.toString(info.GetFixedHolidayWorkTime()));
				txtTime125.setVisibility(View.VISIBLE);
			}
			else if (info.IsWorkTimeHoliday())
			{
				txtTime135.setText(Double.toString(info.GetLegalHolidayWorkTime()));
				txtTime135.setVisibility(View.VISIBLE);
			}
		}
		
		// ボタンクリック時用にTagに設定
		child.setTag(info);
		
		child.setOnClickListener(this);
		
		return child;
	}
	/*
	 * 
	 */
	private View createIgnoreDateView(Calendar curr)
	{
		View child = getLayoutInflater().inflate(R.layout.calendardate, null);

		child.setBackgroundResource(R.drawable.bg_light_grey);

		TextView txtDate = (TextView)child.findViewById(R.id.lblDate);
		
		txtDate.setText(format.DateTitle(curr));
		txtDate.setTextColor(getResources().getColor(R.color.ignoreday_text));
		
		child.findViewById(R.id.lblTimeA).setVisibility(View.INVISIBLE);
		child.findViewById(R.id.lblTimeB).setVisibility(View.INVISIBLE);
		child.findViewById(R.id.lblTimeC).setVisibility(View.INVISIBLE);
		
		return child;
	}
}
