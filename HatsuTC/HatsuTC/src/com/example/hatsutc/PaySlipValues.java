package com.example.hatsutc;

public class PaySlipValues
{
	// ‹‹
	public int PeyByHour100	= 1100;
	public int PeyByHour125 = 1375;
	public int PeyByHour135 = 1485;
	// Œğ’Ê”ï“úŠz
	public int PeyByFare	= 400;
	// ‰¼•¥‚¢“úŠz
	public int PeyByPrePayment	= 5000;

	// ŠÔ•Ê‹‹—^Šz
	public int WagesFixedWD	= 0;
	public int WagesExtraWD	= 0;
	public int WagesFixedHD	= 0;
	public int WagesLegalHD	= 0;
	
	// ‹‹—^‘Šz
	public int WagesAll		= 0;
	
	// ‰ÛÅ‘ÎÛŠz
	public int NetTaxableAmount	= 0;
	
	// Œğ’Ê”ï
	public int TrafficExpenses	= 0;
	
	// ‘x‹‹Šz
	public int GrossWages	= 0;
	
	// ŒÙ—p•ÛŒ¯
	public int UnemploymentInsurance	= 0;
	
	// Œ¹òŠ“¾Å
	public int IncomeTax	= 0;

	// ‰¼•¥‹à
	public int PrePayment	= 0;
	
	// Tœ‘Šz
	public int TotalDeduction	= 0;
	
	// ŒûÀUŠz
	public int DirectDeposit	= 0;
	
	public void CalcWages()
	{
		WagesAll = WagesFixedWD + WagesExtraWD + WagesFixedHD + WagesLegalHD;

		GrossWages = WagesAll + TrafficExpenses;
		
		UnemploymentInsurance = (int)Math.round(GrossWages * 0.005);
		
		NetTaxableAmount = WagesAll - UnemploymentInsurance;
	}
	
	public void CalcDeduction ()
	{
		TotalDeduction	= UnemploymentInsurance + IncomeTax + PrePayment;
		
		DirectDeposit	= GrossWages - TotalDeduction;		
	}
	//
	public void Calc(
			double	weekdaynom,
			double	weekdayext,
			double	saturday,
			double	holiday,
			int		numOfWorkdays,
			int		numOfPrepayments
	)
	{
		WagesFixedWD = (int)Math.round(weekdaynom * PeyByHour100);
		WagesExtraWD = (int)Math.round(weekdayext * PeyByHour125);
		WagesFixedHD = (int)Math.round(saturday * PeyByHour125);
		WagesLegalHD = (int)Math.round(holiday * PeyByHour135);
	
		WagesAll	= WagesFixedWD + WagesExtraWD + WagesFixedHD + WagesLegalHD;

		TrafficExpenses = numOfWorkdays * PeyByFare;
	
		GrossWages = WagesAll + TrafficExpenses;
		
		UnemploymentInsurance = (int)(GrossWages * 0.005);
		
		NetTaxableAmount	= WagesAll - UnemploymentInsurance;
		
		PrePayment		= numOfPrepayments * PeyByPrePayment; 
		
		TotalDeduction	= UnemploymentInsurance + IncomeTax + PrePayment;
		
		DirectDeposit	= GrossWages - TotalDeduction;
	}
}
