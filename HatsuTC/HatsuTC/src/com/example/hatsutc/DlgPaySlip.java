package com.example.hatsutc;

import android.app.Dialog;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.widget.TextView;
import android.widget.Button;

public class DlgPaySlip extends Dialog  implements OnClickListener
{
	TextView mWagesFW;
	TextView mWagesEW;
	TextView mWagesFH;
	TextView mWagesLH;
	
	TextView mAllTotal;
	TextView mAllTotalForTax;
	TextView mTransportationExpenses;
	
	TextView mGrossPay;
	
	TextView mUnemploymentInsurance;
	TextView mIncomeTax;
	TextView mSuspensePayment;
	
	TextView mTotalDeducation;
	
	TextView mDirectDeposit;
	
	public PaySlipValues	Values;
	
	public DlgPaySlip(Context context)
	{
		super(context);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(	R.layout.dialogpayslip );
		
		// ビューの取得
		mWagesFW	= (TextView)findViewById(R.id.lblWeekdayNormal);
		mWagesEW	= (TextView)findViewById(R.id.lblWeekdayExtra);
		mWagesFH		= (TextView)findViewById(R.id.lblSaturday);
		mWagesLH		= (TextView)findViewById(R.id.lblHoliday);
		
		mAllTotal		= (TextView)findViewById(R.id.lblAllTotal);
		mAllTotalForTax	= (TextView)findViewById(R.id.lblAllTotalForTax);
		
		mTransportationExpenses	= (TextView)findViewById(R.id.lblTransportationExpenses);
		
		mGrossPay		= (TextView)findViewById(R.id.lblGrossPay);
		
		mUnemploymentInsurance	= (TextView)findViewById(R.id.lblUnemploymentInsurance);
		mIncomeTax				= (TextView)findViewById(R.id.lblIncomeTax);
		mSuspensePayment		= (TextView)findViewById(R.id.lblSuspensePayment);
		
		mTotalDeducation		= (TextView)findViewById(R.id.lblTotalDeducation);
		
		mDirectDeposit			= (TextView)findViewById(R.id.lblDirectDeposit);

//		//OKボタンのリスナー
		((Button)findViewById(R.id.btnOK)).setOnClickListener(this);
	}
	
	@Override
	public void show()
	{
		super.show();

		mWagesFW.setText(Common.Money(Values.WagesFixedWD));
		mWagesEW.setText(Common.Money(Values.WagesExtraWD));
		mWagesFH.setText(Common.Money(Values.WagesFixedHD));
		mWagesLH.setText(Common.Money(Values.WagesLegalHD));
		
		mAllTotal.setText(Common.Money(Values.WagesAll));
		mAllTotalForTax.setText(Common.Money(Values.NetTaxableAmount));
		
		mTransportationExpenses.setText(Common.Money(Values.TrafficExpenses));
		
		mGrossPay.setText(Common.Money(Values.GrossWages));
		
		mUnemploymentInsurance.setText(Common.Money(Values.UnemploymentInsurance));
		mIncomeTax.setText(Common.Money(Values.IncomeTax));
		mSuspensePayment.setText(Common.Money(Values.PrePayment));
		
		mTotalDeducation.setText(Common.Money(Values.TotalDeduction));
		
		mDirectDeposit.setText(Common.Money(Values.DirectDeposit));
	}
	
	@Override
	public void onClick(View v)
	{
		dismiss();
	}
	
	public void SetValues(PaySlipValues val)
	{
	}
}
