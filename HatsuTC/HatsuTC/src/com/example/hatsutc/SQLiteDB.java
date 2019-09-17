package com.example.hatsutc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.database.Cursor;

public class SQLiteDB
{
	private final static String DB_NAME	= "HatsuTC.db";
	private final static int	DB_VERSION = 1;

	private final static String	TBL_SPECIALDAYLIST = "specialdaylist";

	private final static String	COL_SDL_DATE	= "date";
	private final static String COL_SDL_TYPE	= "type";
	private final static String COL_SDL_NOTE	= "note";
	
	private final static String TBL_WORKTIMELIST = "worktimelist";
	
	private final static String COL_WTL_DATE	= "date";
	private final static String COL_WTL_COMETIMEHOUR	= "cometimehour";
	private final static String COL_WTL_COMETIMEMINUTE	= "cometimeminute";
	private final static String COL_WTL_LEFTTIMEHOUR	= "lefttimehour";
	private final static String COL_WTL_LEFTTIMEMINUTE	= "lefttimeminute";
	private final static String COL_WTL_PREPAYMENT		= "prepayment";
	private final static String	COL_WTL_MEMO			= "memo";

	private final static String TBL_INCOMETAXLIST = "incometaxlist";
	
	private final static String COL_ITL_YEAR	= "year";
	private final static String COL_ITL_LOWER	= "lower";
	private final static String COL_ITL_UPPER	= "upper";
	private final static String COL_ITL_TAX0	= "tax0";
	private final static String COL_ITL_TAX1	= "tax1";
	private final static String COL_ITL_TAX2	= "tax2";
	private final static String COL_ITL_TAX3	= "tax3";
	private final static String COL_ITL_TAX4	= "tax4";
	private final static String COL_ITL_TAX5	= "tax5";
	private final static String COL_ITL_TAX6	= "tax6";
	private final static String COL_ITL_TAX7	= "tax7";
	
	private final static String TBL_CONTRACTLIST = "contractlist";
	
	private final static String COL_CRL_PERIOD		= "period";
	private final static String COL_CRL_HOUR_100	= "hour100";
	private final static String COL_CRL_HOUR_125	= "hour125";
	private final static String COL_CRL_HOUR_135	= "hour135";
	private final static String COL_CRL_FARE		= "fare";
	private final static String COL_CRL_PREPAY		= "prepay";
	private final static String COL_CRL_EXECPREPAY	= "execprepay";
	private final static String COL_CRL_FAMILYS		= "familys";
	
	
	public SQLiteDatabase db;
	public Resources res;
	public SharedPreferences sp;
	
	protected final Context context;
	protected DBOpenHelper helper;
	
	// コンストラクタ
	public SQLiteDB(Context context)
	{
		this.context = context;
		helper = new DBOpenHelper(this.context);
	}
	
	private class DBOpenHelper extends SQLiteOpenHelper
	{
		// コンストラクタ
		public DBOpenHelper(Context context)
		{
			// ヘルパークラスのコンストラクタの呼び出し
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// 特殊日テーブル作成
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_SPECIALDAYLIST + " (" +
						"id INTEGER PRIMARY KEY NOT NULL, " +
						COL_SDL_DATE + " INTEGER NOT NULL UNIQUE, " +
						COL_SDL_TYPE + " INTEGER NOT NULL DEFAULT -1, " +
						COL_SDL_NOTE + " TEXT)");

			// 出退社時間テーブル作成
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_WORKTIMELIST + " (" +
						"id INTEGER PRIMARY KEY NOT NULL, " +
						COL_WTL_DATE + " INTEGER NOT NULL UNIQUE, " +
						COL_WTL_COMETIMEHOUR + " INTEGER NOT NULL DEFAULT -1, " +
						COL_WTL_COMETIMEMINUTE + " INTEGER NOT NULL DEFAULT -1, " +
						COL_WTL_LEFTTIMEHOUR + " INTEGER NOT NULL DEFAULT -1, " +
						COL_WTL_LEFTTIMEMINUTE + " INTEGER NOT NULL DEFAULT -1, " +
						COL_WTL_PREPAYMENT + " INTEGER NOT NULL DEFAULT 0 CHECK (" + COL_WTL_PREPAYMENT + " IN (0, 1)), " +
						COL_WTL_MEMO + " TEXT)");
			
			// 源泉所得税テーブル作成
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_INCOMETAXLIST + " (" +
						"id INTEGER PRIMARY KEY NOT NULL, " +
						COL_ITL_YEAR + " INTEGER NOT NULL, " +
						COL_ITL_LOWER + " INTEGER NOT NULL, " +
						COL_ITL_UPPER + " INTEGER NOT NULL, " +
						COL_ITL_TAX0 + " INTEGER NOT NULL, " +
						COL_ITL_TAX1 + " INTEGER NOT NULL, " +
						COL_ITL_TAX2 + " INTEGER NOT NULL, " +
						COL_ITL_TAX3 + " INTEGER NOT NULL, " +
						COL_ITL_TAX4 + " INTEGER NOT NULL, " +
						COL_ITL_TAX5 + " INTEGER NOT NULL, " +
						COL_ITL_TAX6 + " INTEGER NOT NULL, " +
						COL_ITL_TAX7 + " INTEGER NOT NULL)");

			// 契約内容テーブル作成
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_CONTRACTLIST + " (" +
						"id INTEGER PRIMARY KEY NOT NULL, " +
						COL_CRL_PERIOD + " INTEGER NOT NULL UNIQUE, " +
						COL_CRL_HOUR_100 + " INTEGER NOT NULL DEFAULT -1, " +
						COL_CRL_HOUR_125 + " INTEGER NOT NULL DEFAULT -1, " +
						COL_CRL_HOUR_135 + " INTEGER NOT NULL DEFAULT -1, " +
						COL_CRL_FARE + " INTEGER NOT NULL DEFAULT -1, " +
						COL_CRL_PREPAY + " INTEGER NOT NULL DEFAULT -1, " +
						COL_CRL_EXECPREPAY + " INTEGER NOT NULL DEFAULT 0 CHECK (" + COL_CRL_EXECPREPAY + " IN (0, 1)), " +
						COL_CRL_FAMILYS + " INTEGER NOT NULL DEFAULT 0)");
		}

		// データベースアップグレードする関数
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
		}
	}

	/*
	 * 
	 */
	public void Open()
	{
		db = helper.getWritableDatabase();
	}
	
	/*
	 * 
	 */
	public void Close()
	{
		helper.close();
	}
	
	/*
	 * 源泉所得税情報更新
	 */
	public void UpdateIncomeTaxs()
	{
		int minVer = sp.getInt("minIncomeTaxsVer", Integer.MAX_VALUE);
		int maxVer = sp.getInt("maxIncomeTaxsVer", Integer.MIN_VALUE);

		if (minVer > 2012 || maxVer < 2012)
		{
			InsertIncomeTaxTransaction(R.array.incometax2012, 2012);
			
			minVer = Math.min(minVer, 2012);
			maxVer = Math.max(maxVer, 2012);
		}

		if (minVer > 2013 || maxVer < 2013)
		{
			InsertIncomeTaxTransaction(R.array.incometax2013, 2013);
			
			minVer = Math.min(minVer, 2013);
			maxVer = Math.max(maxVer, 2013);
		}
		
		Editor editor = sp.edit();
		
		editor.putInt("minIncomeTaxsVer", minVer);
		editor.putInt("maxIncomeTaxsVer", maxVer);

		editor.commit();
	}
	/*
	 * 特殊日情報更新
	 */
	public void UpdateHolidays()
	{
		int minVer = sp.getInt("minSpecialsVer", Integer.MAX_VALUE);
		int maxVer = sp.getInt("maxSpecialsVer", Integer.MIN_VALUE);

		// 2012年特殊日情報作成
		if (minVer > 2012 || maxVer < 2012)
		{
			InsertSpecialdayTransaction(R.array.specialdays2012);

			minVer = Math.min(minVer, 2012);
			maxVer = Math.max(maxVer, 2012);
		}
		
		// 2013年特殊日情報更新
		if (minVer > 2013 || maxVer < 2013)
		{
			InsertSpecialdayTransaction(R.array.specialdays2013);

			minVer = Math.min(minVer, 2013);
			maxVer = Math.max(maxVer, 2013);
		}
		
		Editor editor = sp.edit();
		
		editor.putInt("minSpecialsVer", minVer);
		editor.putInt("maxSpecialsVer", maxVer);

		editor.commit();
	}
	
	/*
	 * 契約内容取得関数
	 */
	public int SelectContract(int year, int month, Contract cont)
	{
		
		String sql = "SELECT * FROM " + TBL_CONTRACTLIST +
					" WHERE " + COL_CRL_PERIOD + " >= " + Common.FullMonth(year, month + 1) +
					" ORDER BY " + COL_CRL_PERIOD;
		
		int cnt = 0;
		
		Cursor c = db.rawQuery(sql, null);
		
		cnt = c.getCount();
		
		if (cnt > 0)
		{
			c.moveToFirst();
			
			int id_id		= c.getColumnIndex("id");
			
			int id_period	= c.getColumnIndex(COL_CRL_PERIOD);
			int id_hour100	= c.getColumnIndex(COL_CRL_HOUR_100);
			int id_hour125	= c.getColumnIndex(COL_CRL_HOUR_125);
			int id_hour135 	= c.getColumnIndex(COL_CRL_HOUR_135);
			int id_fare		= c.getColumnIndex(COL_CRL_FARE);
			int id_prepay	= c.getColumnIndex(COL_CRL_PREPAY);
			int id_execpre	= c.getColumnIndex(COL_CRL_EXECPREPAY);
			int id_family	= c.getColumnIndex(COL_CRL_FAMILYS);
			
			cont.id			= c.getInt(id_id);
			cont.Period		= c.getInt(id_period);
			cont.Hour100	= c.getInt(id_hour100);
			cont.Hour125	= c.getInt(id_hour125);
			cont.Hour135	= c.getInt(id_hour135);
			cont.Fare		= c.getInt(id_fare);
			cont.PrePay		= c.getInt(id_prepay);
			cont.ExecPrePay	= (1 == c.getInt(id_execpre));
			cont.Familys	= c.getInt(id_family);
		}
		
		c.close();
		
		return cnt;
	}

	/*
	 * 契約内容更新
	 */
	public void UpdateContract(Contract cont)
	{
		String sql;

		if (-1 == cont.id)
		{
			sql = "INSERT INTO " + TBL_CONTRACTLIST + " (" +
					COL_CRL_PERIOD + ", " +
					COL_CRL_HOUR_100 + ", " +
					COL_CRL_HOUR_125 + ", " +
					COL_CRL_HOUR_135 + ", " +
					COL_CRL_FARE + ", " +
					COL_CRL_PREPAY + ", " +
					COL_CRL_EXECPREPAY + ", " +
					COL_CRL_FAMILYS + ") VALUES (" +
					Integer.toString(cont.Period) + ", " +
					Integer.toString(cont.Hour100) + ", " +
					Integer.toString(cont.Hour125) + ", " +
					Integer.toString(cont.Hour135) + ", " +
					Integer.toString(cont.Fare) + ", " +
					Integer.toString(cont.PrePay) + ", " +
					(cont.ExecPrePay?"1":"0") + ", " +
					Integer.toString(cont.Familys) + ")";
		}
		else
		{
			sql = "UPDATE " + TBL_CONTRACTLIST + " SET " +
					COL_CRL_PERIOD + " = " + Integer.toString(cont.Period) + ", " +
					COL_CRL_HOUR_100 + " = " + Integer.toString(cont.Hour100) + ", " +
					COL_CRL_HOUR_125 + " = " + Integer.toString(cont.Hour125) + ", " +
					COL_CRL_HOUR_135 + " = " + Integer.toString(cont.Hour135) + ", " +
					COL_CRL_FARE + " = " + Integer.toString(cont.Fare) + ", " +
					COL_CRL_PREPAY + " = " + Integer.toString(cont.PrePay) + ", " +
					COL_CRL_EXECPREPAY + " = " + (cont.ExecPrePay?"1":"0") + ", " +
					COL_CRL_FAMILYS + " =" + Integer.toString(cont.Familys) +
					" WHERE id = " + Integer.toString(cont.id);
		}
		
		db.execSQL(sql);
	}
	// 源泉所得税取得
	public int SelectIncomeTax(int wages, int year, int family)
	{
		if (family > 7) family = 7;
		
		String col = "tax" + Integer.toString(family);

		String sql = "SELECT " + col +
					 " FROM " + TBL_INCOMETAXLIST +
					 " WHERE " + COL_ITL_YEAR + " = " + Integer.toString(year) +
					 " AND " + COL_ITL_LOWER + " <= " + Integer.toString(wages) +
					 " AND " + COL_ITL_UPPER + " >= " + Integer.toString(wages);

		Cursor c = db.rawQuery(sql, null);

		int tax = -1;

		if (1 == c.getCount())
		{
			c.moveToFirst();
	
			int id	= c.getColumnIndex(col);
			
			tax = c.getInt(id);
		}
			
		c.close();

		return tax;
	}
	// 指定日のデータ取得
	public void SelectDate(DayInfo info)
	{
		String date = Common.FullDate(info.Year(), info.Month() + 1, info.Date());

		SelectWorkTimeRecord(info, date);
		
		SelectSpecialDayRecord(info, date);
	}
	
	// 指定日出退社時間更新
	public void UpdateWorkTime(DayInfo info)
	{
		String date = Common.FullDate(info.Year(), info.Month() + 1, info.Date());

		if (info.ExistsWorkTimesRecord)
		{
			if (info.IsValidComeTime() || info.IsValidLeftTime())
			{
				UpdateWorkTimeRecord(info, date);
			}
			else
			{
				DeleteWorkTimeRecord(info, date);
			}
		}
		else
		{
			InsertWorkTimeRecord(info, date);
		}
		
	}
	
	// 特殊日リストの追加
	private void InsertSpecialdayTransaction(int resId)
	{
		TypedArray year = res.obtainTypedArray(resId);   

		//トランザクション開始
		db.beginTransaction();
		try {
		    SQLiteStatement stmt = db.compileStatement("INSERT INTO " + TBL_SPECIALDAYLIST + " (" +
		    											COL_SDL_DATE + ", " +
		    											COL_SDL_TYPE + ", " +
		    											COL_SDL_NOTE + ") VALUES (?, ?, ?);");

			for (int i = 0; i < year.length(); i++)
			{
				int r	= year.getResourceId(i, 0);
				
				// 数値型配列とフリー型配列として取得
				int[] ints		= res.getIntArray(r);

				stmt.bindLong(1, ints[0]);
				stmt.bindLong(2, ints[1]);

				TypedArray rIds	= res.obtainTypedArray(r);
				
				int sr  = rIds.getResourceId(2, 0);

				rIds.recycle();

				stmt.bindString(3, res.getString(sr));
		        //実行   
		        stmt.execute();
			}

		    //コミット
		    db.setTransactionSuccessful();   
		} finally {
		    //トランザクション終了   
		    db.endTransaction();   
		}
		
		year.recycle();
	}
	
	// 源泉所得税リストを追加
	private void InsertIncomeTaxTransaction(int resId, int year)
	{
		TypedArray list = res.obtainTypedArray(resId);   

		//トランザクション開始
		db.beginTransaction();
		try {
		    SQLiteStatement stmt =
		    		db.compileStatement("INSERT INTO " + TBL_INCOMETAXLIST + " (" +
		    							COL_ITL_YEAR + ", " +
		    							COL_ITL_LOWER + ", " +
		    							COL_ITL_UPPER + ", " +
		    							COL_ITL_TAX0 + ", " +
		    							COL_ITL_TAX1 + ", " +
		    							COL_ITL_TAX2 + ", " +
		    							COL_ITL_TAX3 + ", " +
		    							COL_ITL_TAX4 + ", " +
		    							COL_ITL_TAX5 + ", " +
		    							COL_ITL_TAX6 + ", " +
		    							COL_ITL_TAX7 + ") " +
		    							"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			for (int i = 0; i < list.length(); i++)
			{
				int r	= list.getResourceId(i, 0);
				
				// 数値型配列とフリー型配列として取得
				int[] vals	= res.getIntArray(r);

				stmt.bindLong(1, year);
				stmt.bindLong(2, vals[0]);
				stmt.bindLong(3, vals[1]);
				stmt.bindLong(4, vals[2]);
				stmt.bindLong(5, vals[3]);
				stmt.bindLong(6, vals[4]);
				stmt.bindLong(7, vals[5]);
				stmt.bindLong(8, vals[6]);
				stmt.bindLong(9, vals[7]);
				stmt.bindLong(10, vals[8]);
				stmt.bindLong(11, vals[9]);

				//実行   
		        stmt.execute();
			}

		    //コミット
		    db.setTransactionSuccessful();   
		} finally {
		    //トランザクション終了   
		    db.endTransaction();   
		}
		
		list.recycle();
	}
	
	// 特殊日情報を取得
	private void SelectSpecialDayRecord(DayInfo info, String date)
	{
		String sql = "SELECT * FROM " + TBL_SPECIALDAYLIST + " WHERE "
					+ COL_SDL_DATE + " = " + date;

		Cursor c = db.rawQuery(sql, null);
		
		if (1 == c.getCount())
		{
			c.moveToFirst();
	
			int id_type	= c.getColumnIndex(COL_SDL_TYPE);
			int id_note = c.getColumnIndex(COL_SDL_NOTE);
			
			info.SetSpecialdayValue(c.getInt(id_type), c.getString(id_note));
			
			info.ExistsSpecialsRecord = true;
		}
			
		c.close();
	}
	
	// 勤務時間を取得
	private void SelectWorkTimeRecord(DayInfo info, String date)
	{
		String sql = "SELECT * FROM " + TBL_WORKTIMELIST + " WHERE " +
					 COL_WTL_DATE + " = " + date;
		
		Cursor c = db.rawQuery(sql, null);
		
		if (1 == c.getCount())
		{
			c.moveToFirst();

			int id_cth = c.getColumnIndex(COL_WTL_COMETIMEHOUR);
			int id_ctm = c.getColumnIndex(COL_WTL_COMETIMEMINUTE);
			int id_lth = c.getColumnIndex(COL_WTL_LEFTTIMEHOUR);
			int id_ltm = c.getColumnIndex(COL_WTL_LEFTTIMEMINUTE);
			int id_ppm = c.getColumnIndex(COL_WTL_PREPAYMENT);
			int id_mem = c.getColumnIndex(COL_WTL_MEMO);
			
			info.ComeTimeHour	= c.getInt(id_cth);
			info.ComeTimeMinute	= c.getInt(id_ctm);
			info.LeftTimeHour	= c.getInt(id_lth);
			info.LeftTimeMinute	= c.getInt(id_ltm);
			info.PrePayment		= (1 == c.getInt(id_ppm));
			info.WorkMemo		= c.getString(id_mem);
			
			info.ExistsWorkTimesRecord	= true;
		}
		
		c.close();
	}

	/*
	 * 勤務時間を更新
	 */
	private void UpdateWorkTimeRecord(DayInfo info, String date)
	{
		ContentValues vals = new ContentValues();
	
		vals.put(COL_WTL_COMETIMEHOUR, info.ComeTimeHour);
	    vals.put(COL_WTL_COMETIMEMINUTE, info.ComeTimeMinute);
	    vals.put(COL_WTL_LEFTTIMEHOUR, info.LeftTimeHour);
	    vals.put(COL_WTL_LEFTTIMEMINUTE, info.LeftTimeMinute);
	    vals.put(COL_WTL_PREPAYMENT, (info.PrePayment)?1:0);
	    vals.put("memo", info.WorkMemo);

	    db.update(TBL_WORKTIMELIST, vals, COL_WTL_DATE + "=" + date, null);
	}
	
	/*
	 * 勤務時間を削除
	 */
	private void DeleteWorkTimeRecord(DayInfo info, String date)
	{
		db.delete(TBL_WORKTIMELIST, COL_WTL_DATE + "=" + date, null);
	}
	/*
	 * 勤務時間を新規挿入
	 */
	private void InsertWorkTimeRecord(DayInfo info, String date)
	{
		ContentValues vals = new ContentValues();

	    vals.put(COL_WTL_DATE, Integer.parseInt(date));

	    vals.put(COL_WTL_COMETIMEHOUR, info.ComeTimeHour);
	    vals.put(COL_WTL_COMETIMEMINUTE, info.ComeTimeMinute);
	    vals.put(COL_WTL_LEFTTIMEHOUR, info.LeftTimeHour);
	    vals.put(COL_WTL_LEFTTIMEMINUTE, info.LeftTimeMinute);
	    vals.put(COL_WTL_PREPAYMENT, (info.PrePayment)?1:0);
	    vals.put(COL_WTL_MEMO, info.WorkMemo);

	    db.insert(TBL_WORKTIMELIST, null, vals);
	}
}