package com.lazycare.carcaremaster.service;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lazycare.carcaremaster.db.DBManager;
import com.lazycare.carcaremaster.db.SQLiteTemplate;
import com.lazycare.carcaremaster.db.SQLiteTemplate.RowMapper;
import com.lazycare.carcaremaster.util.Config;

/**
 * worktime表统一管理类
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class WorkTimeServices {
	private static DBManager manager = null;
	private static WorkTimeServices services = null;
	private String table_name = "worktime";

	public WorkTimeServices(Context context) {
		super();
		manager = DBManager.getInstance(context, Config.DATABASE_FILE);
	}

	public static WorkTimeServices getInstance(Context context) {
		if (services == null) {
			services = new WorkTimeServices(context);
		}
		return services;
	}

	public boolean clearData() {
		// delete from 表名

		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		if (st.deleteByCondition(table_name, "", new String[] {}) > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否已有数据
	 * 
	 * @return
	 */
	public boolean hasData() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		int count = st.getCount("select id from worktime", new String[] {});
		if (count == 84) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存数据
	 * 
	 * @param lstQuestion
	 * @param true 1 false 0
	 * @return true 保存成功 false 保存失败
	 */
	public boolean savaData(List<Boolean> list) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = null;
		boolean b = false;
		if (list == null || list.size() == 0) {
			return false;
		} else {

			for (int i = 0; i < list.size(); i++) {// 插入
				contentValues = new ContentValues();
				b = list.get(i);
				contentValues.put("state", b ? "1" : "0");
				if (!st.isExistsById(table_name, String.valueOf(i + 1))) {
					st.insert(table_name, contentValues);
				} else {// 更新
					st.updateById(table_name, String.valueOf(i + 1),
							contentValues);
				}
				contentValues = null;
				b = false;
			}
			return true;

		}
	}

	/**
	 * 获取数据
	 * 
	 * @param type
	 * 
	 * @param pageNum
	 *            第几页
	 * @param pageSize
	 *            要查询记录的条数
	 * @return
	 */
	public List<Boolean> getList() {

		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Boolean> list = st.queryForList(new RowMapper<Boolean>() {
			@Override
			public Boolean mapRow(Cursor cursor, int index) {
				boolean b;
				String str = cursor.getString(cursor.getColumnIndex("state"));
				b = str.equals("0") ? false : true;
				return b;
			}
		}, "select * from worktime order by id asc  ", new String[] {});
		return list;
	}

}
