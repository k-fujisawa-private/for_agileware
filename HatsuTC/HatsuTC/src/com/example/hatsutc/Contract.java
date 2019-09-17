package com.example.hatsutc;

public class Contract
{
	public int id		= -1;

	public int Period	= 0;

	public int Hour100 	= 0;
	public int Hour125 	= 0;
	public int Hour135 	= 0;
	
	public int Fare		= 0;
	
	public int PrePay	= 0;
	public boolean ExecPrePay	= false; 

	public int Familys	= 0;
	
	// コンストラクタ
	public Contract() {
		Clear();
	}

	// 初期化関数
	public void Clear()
	{
		id		= -1;

		Period	= 0;
		
		Hour100	= 0;
		Hour125 = 0;
		Hour135 = 0;
		
		Fare	= 0;
		
		PrePay	= 0;
		ExecPrePay	= false;
		
		Familys	= 0;
	}
}
