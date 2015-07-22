package com.lazycare.carcaremaster.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lazycare.carcaremaster.data.QuestionClass;
import com.lazycare.carcaremaster.db.DBManager;
import com.lazycare.carcaremaster.db.SQLiteTemplate;
import com.lazycare.carcaremaster.db.SQLiteTemplate.RowMapper;
import com.lazycare.carcaremaster.util.Config;

/**
 * question表统一管理类
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionListServices {
	private static DBManager manager = null;
	private static QuestionListServices services = null;
	private String table_name = "question";

	public QuestionListServices(Context context) {
		super();
		manager = DBManager.getInstance(context, Config.DATABASE_FILE);
	}

	public static QuestionListServices getInstance(Context context) {
		if (services == null) {
			services = new QuestionListServices(context);
		}
		return services;
	}

	/**
	 * 清除表数据
	 * 
	 * @return
	 */
	public boolean clearData() {
		// delete from 表名

		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		if (st.deleteByCondition(table_name, " ", new String[] {}) > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取条数
	 * 
	 * @param loginid
	 * 
	 * @return
	 */
	public int getCount(String type, String loginid) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		// st.execSQL(" select count(*) from question where type = ?", new
		// String[]{type});
		int count = st.getCount(
				" select id from question where type= ? and loginid= ?",
				new String[] { type, loginid });
		Log.d("gmyboy", "count     " + count);
		return count;
	}

	/**
	 * 判断是否有服务器来的更新
	 * 
	 * @return
	 */
	public boolean hasUpdate() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		int count = st.getCount(table_name, new String[] {});
		if (count == 84) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存问题数据
	 * 
	 * @param lstQuestion
	 *            数据源
	 * @param type
	 *            问题类型
	 * @param loginid
	 *            当前登陆师傅ID
	 * @return
	 */
	public boolean savaData(List<QuestionClass> lstQuestion, int type,
			String loginid) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = null;
		QuestionClass question = null;
		String mphotos = "";
		if (lstQuestion == null || lstQuestion.size() == 0) {
			return false;
		} else {
			for (int i = 0; i < lstQuestion.size(); i++) {
				contentValues = new ContentValues();
				question = lstQuestion.get(i);
				contentValues.put("id", question.getId());
				contentValues.put("car", question.getCar());
				contentValues.put("loginid", loginid);
				contentValues.put("type", type);
				contentValues.put("title", question.getTitle());
				contentValues.put("content", question.getContent());
				contentValues.put("audio", question.getAudio());
				List<String> mPhotos = question.getMphotos();
				for (String string : mPhotos) {
					mphotos += string + ";";
				}
				contentValues.put("mphotos", mphotos);
				contentValues.put("add_time", question.getAdd_time());
				contentValues.put("nickname", question.getNickname());
				contentValues.put("mobile", question.getMobile());
				contentValues.put("head", question.getHead());
				contentValues.put("new_answers_count",
						question.getNew_answers_count());
				contentValues.put("answers_count", question.getAnswers_count());
				contentValues.put("mdate", question.getMdate());

				// 插入
				if (!st.isExistsById(table_name, lstQuestion.get(i).getId())) {
					st.insert(table_name, contentValues);
				} else {// 更新
					st.updateById(table_name, lstQuestion.get(i).getId(),
							contentValues);
				}
				contentValues = null;
				question = null;
				mphotos = "";

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
	public List<QuestionClass> getList(String type, int pageNum, int pageSize,
			String loginid) {

		int fromIndex = (pageNum - 1) * pageSize;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<QuestionClass> list = st
				.queryForList(
						new RowMapper<QuestionClass>() {
							@Override
							public QuestionClass mapRow(Cursor cursor, int index) {
								QuestionClass question = new QuestionClass();
								String mphotos = "";
								List<String> mPhoto = new ArrayList<String>();
								question.setId(cursor.getString(cursor
										.getColumnIndex("id")));
								question.setCar(cursor.getString(cursor
										.getColumnIndex("car")));
								question.setType(cursor.getString(cursor
										.getColumnIndex("type")));
								question.setTitle(cursor.getString(cursor
										.getColumnIndex("title")));
								question.setContent(cursor.getString(cursor
										.getColumnIndex("content")));
								question.setAudio(cursor.getString(cursor
										.getColumnIndex("audio")));
								mphotos = cursor.getString(cursor
										.getColumnIndex("mphotos"));
								for (String str : mphotos.split(";")) {
									if (!str.equals("")) {
										mPhoto.add(str);
										Log.d("gmyboy", "str=    " + str);
									}
								}
								question.setAdd_time(cursor.getString(cursor
										.getColumnIndex("add_time")));
								question.setNickname(cursor.getString(cursor
										.getColumnIndex("nickname")));
								question.setMobile(cursor.getString(cursor
										.getColumnIndex("mobile")));
								question.setHead(cursor.getString(cursor
										.getColumnIndex("head")));
								question.setNew_answers_count(cursor.getString(cursor
										.getColumnIndex("new_answers_count")));
								question.setAnswers_count(cursor.getString(cursor
										.getColumnIndex("answers_count")));
								question.setMdate(cursor.getString(cursor
										.getColumnIndex("mdate")));
								return question;
							}
						},
						"select * from question where loginid= ? and type = ? order by id desc limit ? , ? ",
						new String[] { loginid, type, "" + fromIndex,
								"" + pageSize });
		return list;
	}

}
