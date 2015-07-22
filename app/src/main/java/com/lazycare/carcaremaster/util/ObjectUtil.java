package com.lazycare.carcaremaster.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>标    题: 核心框架</p>
 * @version 2.0
 * ObjectUtil
 */
public class ObjectUtil{

	/**
	 * 判断String类型对象是否为空(空串或null)
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if( str == null || str.trim().equals("") || str.trim().equals("null") ) return true;
		return false;
	}

	/**
	 * 判断一个对象数组是否为空(没有成员)
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(Object[] array) {
		if( array == null || array.length < 1 ) return true;
		return false;
	}

	/**
	 * @Title: isEmpty 
	 * @Description: 判断一个list是否为空(没有成员)
	 * @param list
	 * @return
	 * @return boolean
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(List list) {
		if( null == list || "".equals(list) || list.size() < 1 ) return true;
		return false;
	}

	/**
	 * @Title: isEmpty 
	 * @Description: 判断一个map是否为空
	 * @param map
	 * @return
	 * Jun 28, 2014 10:51:24 AM
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Map map) {
		if( null == map || "".equals(map) || map.size() < 1 ) return true;
		return false;
	}

	/**
	 * @Title: isEmptyJson 
	 * @Description: Json是否为空
	 * @param json
	 * @return
	 * Aug 27, 20144:24:08 PM
	 */
	public static boolean isEmptyJson(String json) {
		if( isEmpty(json) ) return true;
		if( json.indexOf("]") - json.indexOf("[") < 1 ) return true;
		return false;
	}

	/**
	 * 获取值为0.00的BigDecimal对象
	 * @return
	 */
	public static BigDecimal getZeroBigDecimal() {
		return new BigDecimal(0);
	}

	/**
	 * @Title: getZeroBigDecimal 
	 * @Description: 获取值为amt的BigDecimal对象
	 * @param amt
	 * @return
	 * Aug 23, 2014 4:27:39 PM
	 */
	public static BigDecimal getZeroBigDecimal(String amt) {
		if( ObjectUtil.isEmpty(amt) ) return new BigDecimal(0);
		return new BigDecimal(amt);
	}

	/**
	 * @Title: getSize 
	 * @Description: 得到list的size
	 * @param list
	 * @return
	 * Aug 23, 2014 4:25:44 PM
	 */
	@SuppressWarnings("rawtypes")
	public static int getSize(List list) {
		return isEmpty(list) ? 0 : list.size();
	}

	/**
	 * @Title: getObject 
	 * @Description: 得到list的item
	 * @param list
	 * @param position
	 * @return
	 * Aug 23, 2014 4:57:28 PM
	 */
	@SuppressWarnings("rawtypes")
	public static Object getObject(List list, int position) {
		return ObjectUtil.isEmpty(list) ? null : list.get(position);
	}
}

