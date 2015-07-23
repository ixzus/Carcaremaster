package com.lazycare.carcaremaster.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import android.annotation.SuppressLint;

/**
 * <p>
 * 标 题: 核心框架
 * </p>
 * <p>
 * 描 述: 日期处理实用类
 * </p>
 * <p>
 * 版 权: Copyright (c) 2010
 * </p>
 * <p>
 * 创建时间: 2010-12-13 上午11:54:42
 * </p>
 * 
 * @author 产品开发部
 * @version 2.0 DateUtil
 */
@SuppressLint({ "UseValueOf", "SimpleDateFormat" })
public class DateUtil {

	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(DateUtil.class);

	/**
	 * 获取yyyyMMdd格式的系统日期，默认取服务器当前日期，如系统参数表内配置了当前工作日，并且DATE_MODE参数配置为1，则取参数日期
	 * 
	 * @return
	 */
	public static String getSysDate() {
		/* 默认取服务器日期 */
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
		String s = simpledateformat.format(Calendar.getInstance().getTime());
		return s;
	}

	/**
	 * 返回当前日期时间字符串<br>
	 * 默认格式:yyyymmddhhmmss
	 * 
	 * @return String 返回当前字符串型日期时间
	 */
	public static BigDecimal getCurrentTimeAsNumber() {
		String returnStr = null;
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		returnStr = f.format(date);
		return new BigDecimal(returnStr);
	}

	/**
	 * 获取kkmmss格式的系统时间
	 * 
	 * @return
	 */
	public static String getSysTime() {
		SimpleDateFormat simpledateformat = new SimpleDateFormat("kkmmss");
		String s = simpledateformat.format(Calendar.getInstance().getTime());
		return s;
	}

	public static String date2Str(Calendar c, String format) {
		if (c == null) {
			return null;
		}
		return date2Str(c.getTime(), format);
	}

	public static String date2Str(Date d, String format) {// yyyy-MM-dd HH:mm:ss
		if (d == null) {
			return null;
		}
		if (format == null || format.length() == 0) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String s = sdf.format(d);
		return s;
	}

	/**
	 * 取年月日中的年
	 * 
	 * @param fdate
	 *            格式：yyyyMMdd或yyyy-MM-dd
	 * @return
	 */
	public static String getYear(String fdate) {
		if (ObjectUtil.isEmpty(fdate))
			return "";
		String cur_date = fdate;
		cur_date = cur_date.substring(0, 4);
		return cur_date;
	}

	/**
	 * 取年月日中的月
	 * 
	 * @param fdate
	 *            格式：yyyyMMdd
	 * @return
	 */
	public static String getMonth(String fdate) {
		if (ObjectUtil.isEmpty(fdate))
			return "";
		String cur_date = fdate;
		cur_date = cur_date.substring(4, 6);
		return cur_date;
	}

	/**
	 * 取年月日中的日
	 * 
	 * @param fdate
	 *            格式：yyyyMMdd
	 * @return
	 */
	public static String getDay(String fdate) {
		if (ObjectUtil.isEmpty(fdate))
			return "";
		String cur_date = fdate;
		cur_date = cur_date.substring(6);
		return cur_date;
	}

	/**
	 * 根据当前年份获取取下拉列表年份集合，从2005年开始至当前年份后3年
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getYear() {
		List vRet = new ArrayList();
		int iCurrYear = Integer.parseInt((getSysDate()).substring(0, 4));
		for (int i = 0; i <= iCurrYear - 2005 + 3; i++) {
			vRet.add(Integer.toString(2005 + i));
		}
		return vRet;
	}

	/**
	 * 根据给定的年份和季度提取对应的季末日期
	 * 
	 * @param ogYear
	 *            年份
	 * @param iValue
	 *            季度
	 * @return
	 */
	public static String getQuarterOfLastDate(String ogYear, int iValue) {
		String strRetDate = "";
		switch (iValue) {
		case 1:
			strRetDate = ogYear + "0331";
			break;
		case 2:
			strRetDate = ogYear + "0630";
			break;
		case 3:
			strRetDate = ogYear + "0930";
			break;
		case 4:
			strRetDate = ogYear + "1231";
			break;
		}
		return strRetDate;
	}

	/**
	 * 根据参数ogdate，得到ogdate这个月的最后一天的日期，例如： getLastDate("200308")=20030831
	 * 参数ogdate必须是6位（yyyyMM）或8位（yyyyMMdd）
	 * 
	 * @param ogdate
	 * @return
	 */
	public static String getMonthLastDate(String ogdate) {
		if (ogdate.length() == 6)
			ogdate = ogdate + "01";
		else
			ogdate = ogdate.substring(0, 6) + "01"; // 把ogdate变成前6位加01的串，如20030805-->20030801
		ogdate = getNextDateByMonth(ogdate, 1);
		ogdate = getNextDateByNum(ogdate, -1);
		return ogdate;
	}

	/**
	 * 根据参数ogdate，得到ogdate这个月的最后一个工作日，例如：
	 * getMonthLastDateNoWeekend("200308")=20030829
	 * 参数ogdate必须是6位（yyyyMM）或8位（yyyyMMdd）
	 * 
	 * @param ogdate
	 * @return
	 */
	public static String getMonthLastWorkDate(String ogdate) {
		String sDate = getMonthLastDate(ogdate);
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
		Date date = simpledateformat.parse(sDate,
				new ParsePosition(0));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int iWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (iWeek == 7)
			sDate = getNextDateByNum(sDate, -1);
		if (iWeek == 1)
			sDate = getNextDateByNum(sDate, -2);
		// calendar.add(2, i);
		date = calendar.getTime();
		return sDate;
	}

	/**
	 * 得到输入日期+i天以后的日期
	 * 
	 * @param s
	 *            yyyyMMdd格式日期
	 * @param i
	 *            可以是负数
	 * @return
	 */
	public static String getNextDateByNum(String s, int i) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
		Date date = simpledateformat.parse(s, new ParsePosition(0));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(5, i);
		date = calendar.getTime();
		s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 得到输入日期+i月以后的日期
	 * 
	 * @param s
	 *            yyyyMMdd格式日期
	 * @param i
	 *            可以是负数
	 * @return
	 */
	public static String getNextDateByMonth(String s, int i) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
		Date date = simpledateformat.parse(s, new ParsePosition(0));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(2, i);
		date = calendar.getTime();
		s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 得到输入日期+i年以后的日期
	 * 
	 * @param s
	 *            yyyyMMdd格式日期
	 * @param i
	 *            可以是负数
	 * @return
	 */
	public static String getNextDateByYear(String s, int i) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
		Date date = simpledateformat.parse(s, new ParsePosition(0));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, i);
		date = calendar.getTime();
		s = simpledateformat.format(date);
		return s;
	}

	/**
	 * 判断参数字符串是否为yyyyMMdd格式的日期
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isDate(String date) {
		if (date.length() != 8)
			return false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			dateFormat.parse(date);
		} catch (Exception e) {
			return false;
		}
		String year = date.substring(0, 4);
		int y = Integer.parseInt(year);
		if (y < 1975 || y > 2100)
			return false;
		String month = date.substring(4, 6);
		int m = Integer.parseInt(month);
		if (m < 1 || m > 12)
			return false;
		String day = date.substring(6);
		int d = Integer.parseInt(day);
		if (d < 1)
			return false;
		String lastday = getMonthLastDate(date);
		int ld = Integer.parseInt(lastday.substring(6));
		if (d > ld)
			return false;// 大于本月最后一天，返回假
		return true;
	}

	/**
	 * 判断参数字符串是否为kkmmss格式的时间
	 * 
	 * @param time
	 * @return
	 */
	public static boolean isTime(String time) {
		if (time.length() != 6)
			return false;
		String hour = time.substring(0, 2);
		int h = Integer.parseInt(hour);
		if (h < 0 || h > 24)
			return false;
		String miet = time.substring(2, 4);
		int m = Integer.parseInt(miet);
		if (m < 0 || m > 59)
			return false;
		if (h == 24 && m != 0)
			return false;
		String sd = time.substring(4);
		int s = Integer.parseInt(sd);
		if (s < 0 || s > 59)
			return false;
		if (h == 24 && s != 0)
			return false;
		return true;
	}

	/**
	 * 判断日期是否符合格式要求
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isInPattern(String date, String pattern) {
		if (date == null)
			return false;
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		try {
			dateFormat.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 把YYYY-MM-DD格式日期字符串格式化成yyyyMMdd或yyyyMM格式
	 * 
	 * @param date
	 *            YYYY-MM-DD格式日期字符串
	 * @return
	 */
	public static String format2DB(String date) {
		if (ObjectUtil.isEmpty(date))
			return "";
		return date.replaceAll("-", "");
	}

	/**
	 * 把kk:mm:ss或kk:mm格式时间字符串格式化成kkmmss或kkmm格式
	 * 
	 * @param time
	 *            kk:mm:ss或kk:mm格式时间字符串
	 * @return
	 */
	public static String formatTime2DB(String time) {
		if (ObjectUtil.isEmpty(time))
			return "";
		return time.replaceAll(":", "");
	}

	/**
	 * 把yyyyMMdd格式日期字符串格式化成YYYY-MM-DD格式
	 * 
	 * @param date
	 *            yyyyMMdd格式日期字符串
	 * @return
	 */
	public static String formatFromDB(String date) {
		if (ObjectUtil.isEmpty(date))
			return "";
		StringBuffer buf = new StringBuffer(date);
		return buf.insert(6, '-').insert(4, '-').toString();
	}

	/**
	 * @Title: formatFromDBTwo
	 * @Description: 把yyyyMM格式日期字符串格式化成YYYY-MM格式
	 * @param date
	 * @return Sep 15, 2014 8:07:34 PM
	 */
	public static String formatFromDBTwo(String date) {
		if (ObjectUtil.isEmpty(date))
			return "";
		StringBuffer buf = new StringBuffer(date);
		return buf.insert(4, '-').toString();
	}

	/**
	 * 把kkmmss格式时间字符串格式化成kk:mm:ss或kk:mm格式
	 * 
	 * @param time
	 *            kkmmss格式时间字符串
	 * @return
	 */
	public static String formatTimeFromDB(String time) {
		if (ObjectUtil.isEmpty(time))
			return "";
		StringBuffer buf = new StringBuffer(time);
		if (time.length() == 4)
			return buf.insert(2, ':').toString();
		return buf.insert(2, ':').insert(5, ':').toString();
	}

	/**
	 * 把kkmm格式时间字符串格式化成kk:mm格式
	 * 
	 * @param time
	 *            kkmm格式时间字符串
	 * @return
	 */
	public static String formatTimeFourFromDB(String time) {
		if (ObjectUtil.isEmpty(time))
			return "";
		StringBuffer buf = new StringBuffer(time);
		return buf.insert(2, ':').toString();
	}

	/**
	 * 把yyyyMMdd格式日期字符串格式化成YYYY-MM-DD格式,把kkmmss格式时间字符串格式化成kk:mm:ss格式,中间加空格后拼接
	 * 
	 * @param date
	 *            yyyyMMdd格式日期字符串
	 * @param time
	 *            kkmmss格式时间字符串
	 * @return
	 */
	public static String formatDateTimeFromDB(String date, String time) {
		if (ObjectUtil.isEmpty(date) || date.length() < 8)
			date = "        ";
		if (ObjectUtil.isEmpty(time) || date.length() < 6)
			time = "      ";
		StringBuffer buf = new StringBuffer(date);
		buf.insert(6, '-').insert(4, '-');
		StringBuffer buf1 = new StringBuffer(time);
		buf1.insert(2, ':').insert(5, ':');
		return buf.toString() + " " + buf1.toString();
	}

	/**
	 * 把yyyyMMdd格式日期字符串格式化成YYYY-MM-DD格式,把kkmmss格式时间字符串格式化成kk:mm:ss或kk：mm格式,
	 * 中间加空格后拼接
	 * 
	 * @param date
	 *            yyyyMMdd格式日期字符串
	 * @param time
	 *            kkmmss格式时间字符串
	 * @return
	 */
	public static String formatDateTime(String date, String time) {
		if (ObjectUtil.isEmpty(date) && ObjectUtil.isEmpty(time))
			return "";
		if (ObjectUtil.isEmpty(date))
			return formatTimeFromDB(time);
		if (ObjectUtil.isEmpty(time))
			return formatTimeFromDB(date);
		return formatFromDB(date) + " " + formatTimeFromDB(time);
	}

	/**
	 * 得到"yyyy年MM月dd日"格式日期
	 * 
	 * @param fdate
	 *            yyyyMMdd格式日期
	 * @return
	 */
	public static String getCNDate(String fdate) {
		if (ObjectUtil.isEmpty(fdate))
			return "";
		String cur_date = fdate;
		cur_date = cur_date.substring(0, 4) + "年" + cur_date.substring(4, 6)
				+ "月" + cur_date.substring(6) + "日";
		return cur_date;
	}

	/**
	 * 得到"yyyy年MM月"格式日期
	 * 
	 * @param fdate
	 *            yyyyMMdd格式日期
	 * @return
	 */
	public static String getCNDateYM(String fdate) {
		if (ObjectUtil.isEmpty(fdate))
			return "";
		String cur_date = fdate;
		cur_date = cur_date.substring(0, 4) + "年" + cur_date.substring(4, 6)
				+ "月";
		return cur_date;
	}

	/**
	 * 得到"kk时mm分ss秒"格式时间
	 * 
	 * @param ftime
	 *            kkmmss格式时间
	 * @return
	 */
	public static String getCNTime(String ftime) {
		if (ObjectUtil.isEmpty(ftime)) {
			return "";
		}
		String cur_time = ftime;
		cur_time = cur_time.substring(0, 2) + "时" + cur_time.substring(2, 4)
				+ "分" + cur_time.substring(4) + "秒";
		return cur_time;
	}

	/**
	 * @Title: getCNFourTime
	 * @Description: 得到"kk时mm分"格式时间
	 * @param ftime
	 * @return Jul 7, 2014 3:54:22 PM
	 */
	public static String getCNFourTime(String ftime) {
		if (ObjectUtil.isEmpty(ftime)) {
			return "";
		}
		String cur_time = ftime;
		cur_time = cur_time.substring(0, 2) + "时" + cur_time.substring(2, 4)
				+ "分";
		return cur_time;
	}

	/**
	 * 计算两个日期相差的天数
	 * 
	 * @param startDate
	 *            格式：yyyy-MM-dd
	 * @param endDate
	 *            格式：yyyy-MM-dd
	 * @return 返回两日期相差的天数
	 */
	public static int getDatePeriod(String startDate, String endDate) {
		String[] date1 = startDate.split("-");
		String[] date2 = endDate.split("-");
		GregorianCalendar gc1 = new GregorianCalendar(
				Integer.parseInt(date1[0]), Integer.parseInt(date1[1]),
				Integer.parseInt(date1[2]));
		GregorianCalendar gc2 = new GregorianCalendar(
				Integer.parseInt(date2[0]), Integer.parseInt(date2[1]),
				Integer.parseInt(date2[2]));
		long longDate1 = gc1.getTimeInMillis();
		long longDate2 = gc2.getTimeInMillis();
		long period = longDate2 - longDate1;
		period /= 24 * 60 * 60 * 1000;
		return (int) period;
	}

	/**
	 * 计算两个日期相差的天数
	 * 
	 * @param startDate
	 *            格式：yyyyMMdd
	 * @param endDate
	 *            格式：yyyyMMdd
	 * @return 返回两日期相差的天数
	 */
	public static int dateMargin(String startDate, String endDate) {
		String d1 = format2DB(startDate);
		String d2 = format2DB(endDate);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(df.parse(d1, new ParsePosition(0)));
		Date end = df.parse(d2, new ParsePosition(0));
		int margin = 0;
		int step = startDate.compareTo(endDate) > 0 ? -1 : 1;
		while (calendar.getTime().compareTo(end) != 0) {
			calendar.add(Calendar.DATE, step);
			margin += step;
		}
		return margin;
	}

	/**
	 * 计算两个时间的差（单位：秒）
	 * 
	 * @param end_dt
	 *            格式：yyyyMMddkkmmss
	 * @param  begin_dt
	 *            格式：yyyyMMddkkmmss
	 * @return 差（秒）
	 */
	public static int getSecsDiff(String begin_dt, String end_dt) {
		if (begin_dt == null || end_dt == null)
			return 0;
		if (begin_dt.length() == 8)
			begin_dt = begin_dt + "000000";
		if (begin_dt.length() == 6)
			begin_dt = getSysDate() + begin_dt;
		if (end_dt.length() == 8)
			end_dt = end_dt + "000000";
		if (end_dt.length() == 6)
			end_dt = getSysDate() + end_dt;
		int iBYYYY = Integer.parseInt(begin_dt.substring(0, 4));
		int iBMM = Integer.parseInt(begin_dt.substring(4, 6));
		int iBDD = Integer.parseInt(begin_dt.substring(6, 8));
		int iBhh = Integer.parseInt(begin_dt.substring(8, 10));
		int iBmm = Integer.parseInt(begin_dt.substring(10, 12));
		int iBss = Integer.parseInt(begin_dt.substring(12, 14));
		int iEYYYY = Integer.parseInt(end_dt.substring(0, 4));
		int iEMM = Integer.parseInt(end_dt.substring(4, 6));
		int iEDD = Integer.parseInt(end_dt.substring(6, 8));
		int iEhh = Integer.parseInt(end_dt.substring(8, 10));
		int iEmm = Integer.parseInt(end_dt.substring(10, 12));
		int iEss = Integer.parseInt(end_dt.substring(12, 14));
		Calendar BeginDate = new GregorianCalendar(iBYYYY, iBMM, iBDD, iBhh,
				iBmm, iBss);
		Calendar EndDate = new GregorianCalendar(iEYYYY, iEMM, iEDD, iEhh,
				iEmm, iEss);
		long lBegin = BeginDate.getTime().getTime();
		long lEnd = EndDate.getTime().getTime();
		// long lDiff = (lEnd > lBegin) ? (lEnd - lBegin) : (lBegin - lEnd);
		long lDiff = lBegin - lEnd;
		BeginDate = null;
		EndDate = null;
		return (int) (lDiff / 1000);
	}

	/**
	 * 根据日期字符串获得java.util.Date对象
	 * 
	 * @param String类型的日期
	 * @param 输入日期的格式
	 * @return Date对象
	 * */
	public static Date getDateByString(String dateByString, String pattern)
			throws Exception {
		Date date = null;
		if (dateByString == null || dateByString.trim().equals(""))
			return null;
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			date = format.parse(dateByString);
		} catch (Exception e) {
			String error = "输入的日期格式不正确，请输入" + pattern + "格式的日期";
			throw new Exception(error, e);
		}
		return date;
	}

	/**
	 * 根据java.util.Date对象获得指定格式的日期字符串
	 * 
	 * @param Date对象
	 * @param 输出日期的格式
	 * @return String类型的日期
	 * */
	public static String getStringByDate(Date date, String pattern) {
		if (date == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/**
	 * 根据工作日期获得上一结息日,如果工作日本身为结息日返回上季度结息日
	 * 
	 * @param workDate
	 *            yyyyMMdd形式日期字符串
	 * @param cutday
	 *            分摊日期，通常为21
	 * @return
	 */
	public static String getLastIntDate(String workDate, int cutday) {
		String retDate = "";
		/**
		 * 结息月＝当前月日/((（当前月份－1）/3*3+3)*100+分摊日)*3+（当前月份－1）/3*3+3-3
		 * */
		int flag = (Integer.parseInt(getMonth(workDate)) - 1) / 3 * 3 + 3;
		int month = Integer.parseInt(getMonth(workDate) + getDay(workDate))
				/ ((flag) * 100 + cutday) * 3 + flag - 3;
		int year = (month == 0) ? Integer.parseInt(getYear(workDate)) - 1
				: Integer.parseInt(getYear(workDate));
		month = (month == 0) ? 12 : month;
		retDate = year
				+ StringUtil.fillString(new Integer(month).toString(), '0', 2)
				+ "20";
		return retDate;
	}

	/**
	 * @Title: getSysDateAndTime
	 * @Description: 得到中文的日期和时间
	 * @return Sep 2, 2014 4:35:24 PM
	 */
	public static String getSysCNDateAndTime() {
		return getCNDate(getSysDate()) + "\u3000" + getCNTime(getSysTime());
	}

	/**
	 * @Title: isOutDate
	 * @Description: 判断是否逾期
	 * @param enddate
	 * @param endtime
	 * @param date
	 * @return
	 */
	public static boolean isOutDate(String enddate, String endtime, String date) {
		// 判断是否超过最后上报时间
		if (ObjectUtil.isEmpty(enddate)) {
			enddate = (Integer.parseInt(DateUtil.getSysDate()) + 10) + "";
		}
		if (ObjectUtil.isEmpty(endtime)) {
			endtime = "00";
		}
		String endTime = enddate + endtime + "0000";
		String sysTime = date + "000000";
		int iidate = DateUtil.getSecsDiff(sysTime, endTime);
		// 逾期
		if (iidate > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @Title: isOutDate
	 * @Description: 判断是否逾期
	 * @param enddate
	 * @param date
	 * @return
	 */
	public static boolean isOutDate(String enddate, String date) {
		// 判断是否超过最后上报时间
		int dateTime = DateUtil.getDatePeriod(date, enddate);
		// 所选时间比现在小
		if (dateTime > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @Title: isOutSysTime
	 * @Description: 是否比系统时间大
	 * @param mTime
	 * @return Jul 15, 2014 5:41:23 PM
	 */
	public static boolean isOutSysTime(String mDate, String mTime) {
		if (ObjectUtil.isEmpty(mDate))
			mDate = "00000000";
		else
			mDate = mDate.trim(); // 消除空格
		if (ObjectUtil.isEmpty(mTime))
			mTime = "000000";
		else
			mTime = mTime.trim(); // 消除空格
		if (mTime.length() == 2)
			mTime = mTime + "0000";
		if (mTime.length() == 4)
			mTime = mTime + "00";
		String mTemp = mDate + mTime;
		if ((Long.valueOf(mTemp) - Long.valueOf(getSysDate() + getSysTime())) > 0)
			return true;
		else
			return false;
	}

	/**
	 * @Title: isFastDoubleClick
	 * @Description:Config.LAST_CLICK_TIME是否是快速点击(0.3秒)
	 * @return Sep 2, 2014 4:50:06 PM
	 */
	public static boolean isFastDoubleClick() {
		if (isFastDoubleClick(Config.LAST_CLICK_TIME))
			return true;
		Config.LAST_CLICK_TIME = System.currentTimeMillis();
		return false;
	}

	/**
	 * @Title: isFastDoubleClick
	 * @Description: 是否是快速点击(0.3秒)
	 * @param lastClick
	 * @return Sep 2, 2014 4:50:06 PM
	 */
	public static boolean isFastDoubleClick(Long lastClick) {
		return isFastDoubleClick(lastClick, 200);
	}

	/**
	 * @Title: isFastDoubleClick
	 * @Description: 是否快速点击
	 * @param lastClick
	 * @param time
	 * @return Sep 17, 2014 6:11:20 PM
	 */
	public static boolean isFastDoubleClick(Long lastClick, int time) {
		if (System.currentTimeMillis() - lastClick <= time)
			return true;
		return false;
	}

	/**
	 * @Title: getSysDifftime
	 * @Description: 系统时间差
	 * @return Oct 11, 2014 3:54:22 PM
	 */
	public static long getSysDifftime(String sys_date, String sys_time,
			String now_date, String now_time) {
		return getSysDifftime(sys_date + sys_time, now_date + now_time);
	}

	/**
	 * @Title: getSysDifftime
	 * @Description: 系统时间差
	 * @return Oct 11, 2014 3:54:22 PM
	 */
	public static long getSysDifftime(String sys_time, String now_time) {
		if (ObjectUtil.isEmpty(sys_time) || ObjectUtil.isEmpty(now_time)
				|| sys_time.length() < now_time.length()) // 两个时间能为空，且系统时间的长度不能小于被比较的
			return 0;
		if (sys_time.length() == 8) // 只有系统日期
			sys_time = sys_time + "000000";
		if (sys_time.length() == 6) // 只有系统时间
			sys_time = "00000000" + sys_time;
		if (now_time.length() == 8)
			now_time = now_time + "000000";
		if (now_time.length() == 6)
			now_time = "00000000" + now_time;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		try {
			long sys = sdf.parse(sys_time).getTime();
			long now = sdf.parse(now_time).getTime();
			return sys - now;
		} catch (ParseException e) {
			android.util.Log.d("BUG", e.toString());
			return 0;
		}// 毫秒
	}

	/**
	 * @Title: getTiemAgoCN
	 * @Description: 将毫秒转成时间
	 * @param time
	 * @return Oct 11, 2014 4:05:14 PM
	 */
	public static String getTiemAgoCN(long time) {
		if (0 >= time)
			return "";
		time = time / 1000; // 转换成秒
		if (60 > time) // 一分钟内
			return "刚刚";
		time = time / 60; // 分钟
		if (60 > time) // 一小时内
			return time + "分钟前";
		time = time / 60; // 小时
		if (24 > time) // 一天内
			return time + "小时前";
		time = time / 24; // 天
		return time + "天前";
	}

	/**
	 * 将YYYYMMDD 转化为 MM-DD
	 * 
	 * @param dateString
	 * @return
	 */
	public static String getDateForMouth(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat simpledateformat = new SimpleDateFormat("MM-dd");
		String s = "";
		Date newDate = null;
		try {
			newDate = dateFormat.parse(dateString);
			s = simpledateformat.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
			s = "";
		}
		return s;
	}
}
