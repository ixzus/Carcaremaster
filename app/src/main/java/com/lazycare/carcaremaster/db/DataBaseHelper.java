package com.lazycare.carcaremaster.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * SQLite数据库的帮助类 该类属于扩展类,主要承担数据库初始化和版本升级使用,其他核心全由核心父类完成
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class DataBaseHelper extends SDCardSQLiteOpenHelper {

	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE [worktime] ([id] integer not null primary key , [state] NVARCHAR );");
		// 问题
		db.execSQL("CREATE TABLE [question] ([id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,[loginid] INTEGER , [car] NVARCHAR, [type] NVARCHAR, [title] NVARCHAR, [content] NVARCHAR, [audio] NVARCHAR, [photos] NVARCHAR, [mphotos] NVARCHAR, [add_time] NVARCHAR, [nickname] NVARCHAR, [mobile] NVARCHAR, [head] NVARCHAR, [new_answers_count] NVARCHAR, [answers_count] NVARCHAR, [mdate] NVARCHAR);");
		// 预约
		// db.execSQL("CREATE TABLE [im_notice]  ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, [type] INTEGER, [title] NVARCHAR, [content] NVARCHAR, [notice_from] NVARCHAR, [notice_to] NVARCHAR, [notice_time] TEXT, [status] INTEGER);");
		db.execSQL("CREATE TABLE [message] ([id] integer not null primary key , [content] NVARCHAR , [title] NVARCHAR , [description] NVARCHAR , [add_time] NVARCHAR );");

	}

	/**
	 * 换掉版本号的时候删除本地表
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS [worktime];");
			// 问题
			db.execSQL("DROP TABLE IF EXISTS [question];");
			// 预约
			// db.execSQL("CREATE TABLE [im_notice]  ([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, [type] INTEGER, [title] NVARCHAR, [content] NVARCHAR, [notice_from] NVARCHAR, [notice_to] NVARCHAR, [notice_time] TEXT, [status] INTEGER);");
			db.execSQL("DROP TABLE IF EXISTS [message];");
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
}
