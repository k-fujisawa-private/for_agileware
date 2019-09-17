package com.example.hatsutc;

import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SetupActivity extends Activity 
{
	private EditText 	mHour100;
	private TextView 	mHour125;
	private TextView 	mHour135;
	private EditText 	mFare;
	private EditText 	mPrePay;
	private CheckBox 	mExexPre;
	private Spinner		mFamilys;
	
	private EditText	mPeriodY;
	private EditText	mPeriodM;
	
	private SQLiteDB SQLDB = new SQLiteDB(this);

	int		year;
	int 	month;

	private Contract cont	= new Contract();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitysetup);

		Intent intent = getIntent();

		year	= intent.getIntExtra("year", 0);
		month	= intent.getIntExtra("month", 0);

		mHour100	= (EditText)findViewById(R.id.edtHour100);
		mHour125	= (TextView)findViewById(R.id.lblHour125);
		mHour135	= (TextView)findViewById(R.id.lblHour135);
		
		mFare		= (EditText)findViewById(R.id.edtFare);
		
		mPrePay		= (EditText)findViewById(R.id.edtPrePay);
		
		mExexPre	= (CheckBox)findViewById(R.id.chkExecPrePay);
		
		mFamilys	= (Spinner)findViewById(R.id.spnNumOfFamily);
		
		mPeriodY	= (EditText)findViewById(R.id.edtPeriodYear);
		mPeriodM	= (EditText)findViewById(R.id.edtPeriodMonth);
		
		SQLDB.Open();

		if (0 == SQLDB.SelectContract(year, month, cont))
		{
			findViewById(R.id.BtnNG).setVisibility(View.INVISIBLE);
			findViewById(R.id.lblPeriod).setVisibility(View.INVISIBLE);
			findViewById(R.id.tblPeriod).setVisibility(View.INVISIBLE);
			
			SetDefault();
		}

		mHour100.setText(Integer.toString(cont.Hour100));
		mHour125.setText(Integer.toString(cont.Hour125));
		mHour135.setText(Integer.toString(cont.Hour135));
		
		mFare.setText(Integer.toString(cont.Fare));
		
		mPrePay.setText(Integer.toString(cont.PrePay));
		
		mExexPre.setChecked(cont.ExecPrePay);
		
		mFamilys.setSelection(cont.Familys);
		
		if (Integer.MAX_VALUE != cont.Period)
		{
			mPeriodY.setText(Integer.toString(cont.Period / 100));
			mPeriodM.setText(Integer.toString(cont.Period % 100));
		}
		
		mHour100.setOnFocusChangeListener(new View.OnFocusChangeListener(){
    		
    		@Override
    		public void onFocusChange(View v, boolean flag){
    			if(flag == false){
    				CalcExtraWages();
    			}
    		}
    	});

		SQLDB.Close();
	}

	// 決定ボタンクリックリスナー
	public void BtnOkOnClick(View v)
	{
		cont.Hour100	= Integer.parseInt(mHour100.getText().toString());
		cont.Hour125	= Integer.parseInt(mHour125.getText().toString());
		cont.Hour135	= Integer.parseInt(mHour135.getText().toString());
		
		cont.Fare		= Integer.parseInt(mFare.getText().toString());
		
		cont.PrePay		= Integer.parseInt(mPrePay.getText().toString());
		
		cont.ExecPrePay	= mExexPre.isChecked();
		
		cont.Familys	= mFamilys.getSelectedItemPosition();

		cont.Period	= Integer.MAX_VALUE;
		
		if (mPeriodY.getText().length() > 0 && mPeriodM.getText().length() > 0)
		{
			int y = Integer.parseInt(mPeriodY.getText().toString());
			int m = Integer.parseInt(mPeriodM.getText().toString());
			
			cont.Period	= Integer.parseInt(Common.FullMonth(y, m));
		}
		
		SQLDB.Open();
		SQLDB.UpdateContract(cont);
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
	
	/*
	 * 初期設定
	 */
	private void SetDefault()
	{
		cont.Period	= Integer.MAX_VALUE;
		
		cont.Hour100	= 1000;
		cont.Hour125	= 1250;
		cont.Hour135	= 1350;
		
		cont.Fare		= 0;
		
		cont.PrePay		= 5000;
		
		cont.ExecPrePay	= false;
		
		cont.Familys	= 0;
	}
	
	/*
	 * 割り増し時給計算
	 */
	private void CalcExtraWages()
	{
		int h100 = Integer.parseInt(((EditText)findViewById(R.id.edtHour100)).getText().toString());
		
		((TextView)findViewById(R.id.lblHour125)).setText(Integer.toString((int)(h100*1.25)));
		((TextView)findViewById(R.id.lblHour135)).setText(Integer.toString((int)(h100*1.35)));
	}
}
