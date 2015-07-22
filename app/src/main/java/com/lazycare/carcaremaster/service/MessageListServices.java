package com.lazycare.carcaremaster.service;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lazycare.carcaremaster.data.QuestionClass;
import com.lazycare.carcaremaster.db.DBManager;
import com.lazycare.carcaremaster.db.SQLiteTemplate;
import com.lazycare.carcaremaster.db.SQLiteTemplate.RowMapper;

/**
 * message表统一管理类
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MessageListServices {
	private static DBManager manager = null;
	private static MessageListServices services = null;
	private String table_name = "question";

	public MessageListServices(Context context) {
		super();
		manager = DBManager.getInstance(context, "database");
	}

	public static MessageListServices getInstance(Context context) {
		if (services == null) {
			services = new MessageListServices(context);
		}
		return services;
	}

	/**
	 * 保存问题数据
	 * 
	 * @param lstQuestion
	 * @param type
	 * @return
	 */
	public boolean savaData(List<QuestionClass> lstQuestion, int type) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = null;
		QuestionClass question = null;
		if (lstQuestion == null || lstQuestion.size() == 0) {
			return false;
		} else {

			for (int i = 0; i < lstQuestion.size(); i++) {
				// 根据数据存不存在判断是否插入
				if (!st.isExistsById(table_name, lstQuestion.get(i).getId())) {
					contentValues = new ContentValues();
					question = lstQuestion.get(i);
					contentValues.put("id", question.getId());
					contentValues.put("car", question.getCar());
					contentValues.put("type", type);
					contentValues.put("title", question.getTitle());
					contentValues.put("content", question.getContent());
					contentValues.put("audio", question.getAudio());
					// contentValues.put("photos", question.getPhotos());
					contentValues.put("add_time", question.getAdd_time());
					contentValues.put("nickname", question.getNickname());
					contentValues.put("mobile", question.getMobile());
					contentValues.put("head", question.getHead());
					contentValues.put("new_answers_count",
							question.getNew_answers_count());
					contentValues.put("answers_count",
							question.getAnswers_count());
					contentValues.put("mdate", question.getMdate());
					st.insert(table_name, contentValues);
					contentValues = null;
					question = null;
				}
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
	public List<QuestionClass> getList(String type, int pageNum, int pageSize) {

		int fromIndex = (pageNum - 1) * pageSize;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<QuestionClass> list = st
				.queryForList(
						new RowMapper<QuestionClass>() {
							@Override
							public QuestionClass mapRow(Cursor cursor, int index) {
								QuestionClass question = new QuestionClass();
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
								// question.setPhotos(cursor.getString(cursor
								// .getColumnIndex("photos")));
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
						"select * from question where type = ? order by id desc limit ? , ? ",
						new String[] { type, "" + fromIndex, "" + pageSize });
		return list;
	}

}
