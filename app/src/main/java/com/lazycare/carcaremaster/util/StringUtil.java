package com.lazycare.carcaremaster.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import android.annotation.SuppressLint;

/**
 * <p>标    题: 核心框架</p>
 * <p>描    述: 字符串处理实用类</p>
 * <p>版    权: Copyright (c) 2010</p>
 * <p>创建时间: 2010-12-13 下午12:32:42</p>
 *
 * @author 产品开发部
 * @version 2.0
 *          StringUtil
 */
@SuppressLint("DefaultLocale")
public final class StringUtil {

    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(StringUtil.class);

    public static long genEventCode() {
        return System.currentTimeMillis();
    }

    /**
     * 把参数word的第一个字母大写
     *
     * @param word
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static final String upperFirstWord(String word) {
        String firstWord = word.substring(0, 1).toUpperCase();
        return firstWord + word.substring(1);
    }

    public static final String upperWord(String word) {
        return word.toUpperCase();
    }

    /**
     * @param word
     * @return Jun 23, 2014 1:33:41 PM
     * @Title: lowerFirstWord
     * @Description: 把参数word的第一个字母小写
     */
    @SuppressLint("DefaultLocale")
    public static final String lowerFirstWord(String word) {
        String firstWord = word.substring(0, 1).toLowerCase();
        return firstWord + word.substring(1);
    }

    /**
     * @param strbyte
     * @param destEncoding 目标编码
     * @return Jun 23, 2014 1:33:53 PM
     * @Title: encodeByte
     * @Description: 将字节数组转换为指定编码的字符串
     */
    public static String encodeByte(byte[] strbyte, String destEncoding) {
        String ret = null;
        try {
            ret = new String(strbyte, destEncoding);
        } catch (Exception e) {
            ret = "byte to " + destEncoding + " error. str=[" + strbyte + "]";
        }
        return (ret);
    }

    /**
     * 将字节数组转换为指定编码的字符串
     *
     * @param str
     * @param srcEncoding  原字符串编码
     * @param destEncoding 目标编码
     * @return
     */
    public static String encodeStr(String str, String srcEncoding, String destEncoding) {
        String ret = null;
        try {
            ret = new String(str.getBytes(srcEncoding), destEncoding);
        } catch (Exception e) {
            ret = srcEncoding + " to " + destEncoding + " error. str=" + str;
        }
        return (ret);
    }

    /**
     * ISO-8859-1转UTF-8
     *
     * @param str
     * @return
     */
    public static String ISO2UTF8(String str) {
        String ret = null;
        try {
            ret = new String(str.getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
            ret = "ISO2UTF8 error. str=" + str;
        }
        return (ret);
    }

    /**
     * UTF-8转ISO-8859-1
     *
     * @param str
     * @return
     */
    public static String UTF82ISO(String str) {
        String ret = null;
        try {
            ret = new String(str.getBytes("UTF-8"), "ISO-8859-1");
        } catch (Exception e) {
            ret = "UTF82ISO error. str=" + str;
        }
        return (ret);
    }

    /**
     * ISO-8859-1转GBK
     *
     * @param str
     * @return
     */
    public static String ISO2GBK(String str) {
        String ret = null;
        try {
            ret = new String(str.getBytes("ISO-8859-1"), "GBK");
        } catch (Exception e) {
            ret = "ISO2GBK error. str=" + str;
        }
        return (ret);
    }

    /**
     * GBK转ISO-8859-1
     *
     * @param str
     * @return
     */
    public static String GBK2ISO(String str) {
        String ret = null;
        try {
            ret = new String(str.getBytes("GBK"), "ISO-8859-1");
        } catch (Exception e) {
            ret = "GBK2ISO error. str=" + str;
        }
        return (ret);
    }

    /**
     * 把源串sOld中为sPartten的字符串用sReplace替换，
     *
     * @param sOld     源字符串
     * @param sPartten 要替换的字符串
     * @param sReplace 替换成的字符串
     * @return
     */
    public static String replace(String sOld, String sPartten, String sReplace) {
        if (sOld == null)
            return null;
        if (sPartten == null)
            return sOld;
        if (sReplace == null)
            sReplace = "";
        StringBuffer sBuffer = new StringBuffer();
        int isOldLen = sOld.length();
        int isParttenLen = sPartten.length();
        int iIndex = -1;
        int iHead = 0;
        while ((iIndex = sOld.indexOf(sPartten, iHead)) > -1) {
            sBuffer.append(sOld.substring(iHead, iIndex)).append(sReplace);
            iHead = iIndex + isParttenLen;
        }
        sBuffer.append(sOld.substring(iHead, isOldLen));
        return sBuffer.toString();
    }

    /**
     * 把输入值格式化保留两位小数，如price=4535.234095
     * 则此函数返回为4535.23
     *
     * @param price
     * @return
     */
    public static String format2Decimal(double price) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.CHINESE);
        java.text.DecimalFormat df = (java.text.DecimalFormat) nf;
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        String pattern = "#0.00";
        df.applyPattern(pattern);
        df.setDecimalSeparatorAlwaysShown(true);
        return df.format(price);
    }

    /**
     * 把输入值格式化保留两位小数，如price=4535.234095
     * 则此函数返回为4535.23
     *
     * @param price
     * @return
     */
    public static String format2Decimal(String price) {
        double d = 0.0;
        if (ObjectUtil.isEmpty(price))
            d = 0.0;
        else
            try {
                d = Double.parseDouble(price);
            } catch (NumberFormatException ex) {
                d = 0.0;
            } catch (Exception ex) {
                d = 0.0;
            }
        return format2Decimal(d);
    }

//	/**
//	 * 将金额转换成千分位格式
//	 * @param price
//	 * @return
//	 */
//	public static String format2Amt(String price) {
//		double d = 0.0;
//		if( ObjectUtil.isEmpty(price) )
//			d = 0.0;
//		else
//			try {
//				d = Double.parseDouble(price);
//			} catch(java.lang.NumberFormatException ex) {
//				d = 0.0;
//			} catch(java.lang.Exception ex) {
//				d = 0.0;
//			}
//		return MoneyUtil.toAmtString(format2Decimal(d));
//	}

//	/**
//	 * 将金额转换成千分位后添加人民币符号与"元"
//	 * @param price
//	 * @return
//	 */
//	public static String format2AmtWithYuan(String price) {
//		return "￥" + format2Amt(price) + "元";
//	}

    /**
     * 按分隔符号读出字符串的内容
     *
     * @param strlist 含有分隔符号的字符串
     * @param ken     分隔符号
     * @return 列表
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final List parseStringToArrayList(String strlist, String ken) {
        StringTokenizer st = new StringTokenizer(strlist, ken);
        if (strlist == null || strlist.equals("") || st.countTokens() <= 0)
            return new ArrayList();
        int size = st.countTokens();
        List strv = new ArrayList();
        for (int i = 0; i < size; i++) {
            String nextstr = st.nextToken();
            if (!nextstr.equals(""))
                strv.add(nextstr);
        }
        return strv;
    }

    /**
     * 把源串str以delim为分割符号，分成一个String数组返回，如
     * str="aaa,bbb,ccc" , delim=","
     * 则此函数返回为
     * String[0]="aaa"
     * String[1]="bbb"
     * String[2]="ccc"
     *
     * @param str   待处理字符串
     * @param delim 分隔符
     * @return
     */
    public static String[] split(String str, String delim) {
        if (ObjectUtil.isEmpty(delim)) {
            String[] strReturn = new String[1];
            strReturn[0] = str;
        }
        StringTokenizer st = new StringTokenizer(str, delim);
        int size = st.countTokens();
        if (size < 0)
            return null;
        String[] strReturn = new String[size];
        int i = 0;
        while (st.hasMoreTokens()) {
            strReturn[i++] = st.nextToken();
        }
        return strReturn;
    }

    /**
     * 把源串str以delim为分割符号，分成一个String数组返回，<br>
     * 如 str="aaa,,bbb,ccc" , delim=","<br>
     * 则此函数返回为 String[0]="aaa"<br>
     * String[1]="" <br>
     * String[2]="bbb"<br>
     * String[3]="ccc"
     *
     * @param str   待处理字符串
     * @param delim 分割符
     * @return 如果传入的字符为空，返回数组string[0]=""
     */
    public static String[] splitInNull(String str, String delim) {
        if (ObjectUtil.isEmpty(delim)) {
            String[] strReturn = new String[1];
            strReturn[0] = str;
            return strReturn;
        }
        if (ObjectUtil.isEmpty(str)) {
            String[] strReturn = new String[1];
            strReturn[0] = str;
            return strReturn;
        }
        StringTokenizer st = new StringTokenizer(str, delim, true);
        int i = st.countTokens();
        StringBuffer tempString = new StringBuffer();
        for (int j = 0; j < i; j++)
            tempString.append(" " + st.nextToken() + " ");
        st = new StringTokenizer(tempString.toString(), delim);
        i = st.countTokens();
        String[] tempArray = new String[i];
        for (int j = 0; j < i; j++)
            tempArray[j] = st.nextToken().trim();
        return tempArray;
    }

    /**
     * 将传入的字符串数组转换成字符串，转换之后的格式：'xxx','xxx',...,'xxx'
     *
     * @param stringArray
     * @return
     */
    public static String Array2String(String stringArray[]) {
        try {
            String StringResult = "";
            if (ObjectUtil.isEmpty(stringArray)) {
                return StringResult;
            }
            int size = stringArray.length;
            for (int i = 0; i < size; i++) {
                if (ObjectUtil.isEmpty(stringArray[i]))
                    continue;
                StringResult += "'" + stringArray[i] + "',";
            }
            StringResult = StringResult.substring(0, StringResult.length() - 1);
            return StringResult;
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 将传入的字符串数组转换成指定分隔符的字符串
     * 以delim为分隔，如 Array2String(array[],"-")，则返回串是'-'分隔的
     *
     * @param stringArray
     * @param delim       分割符
     * @return
     */
    public static String Array2String(String stringArray[], String delim) {
        try {
            String StringResult = "";
            if (ObjectUtil.isEmpty(stringArray)) {
                return StringResult;
            }
            int size = stringArray.length;
            if (size == 1) {
                return stringArray[0];
            }
            for (int i = 0; i < size - 1; i++) {
                if (ObjectUtil.isEmpty(stringArray[i]))
                    continue;
                StringResult += stringArray[i] + delim;
            }
            StringResult += stringArray[size - 1];
            return StringResult;
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 将字符串格式化成 HTML 代码输出
     *
     * @param string 要格式化的字符串
     * @return 格式化后的字符串
     */
    public static String toHtml(String string) {
        if (string == null)
            return "";
        string = string.replaceAll("&", "&amp;");
        string = string.replaceAll("\"", "&quot;");
        string = string.replaceAll("<", "&lt;");
        string = string.replaceAll(">", "&gt;");
        // string = string.replaceAll("\r\n", "\n");
        string = string.replaceAll("\n", "<br>\n");
        string = string.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
        string = string.replaceAll(" ", "&nbsp;");
        return string;
    }

    /**
     * 将sql内的'转换成''
     *
     * @param sql
     * @return
     */
    public static String toSql(String sql) {
        if (sql == null)
            return "";
        sql = sql.replaceAll("'", "''");
        return sql;
    }

    /**
     * 将sql内的''转换成'
     *
     * @param sql
     * @return
     */
    public static String fromSql(String sql) {
        if (sql == null)
            return "";
        sql = sql.replaceAll("''", "'");
        return sql;
    }

    /**
     * 得到一对象toString的值，如果是null则返回""
     *
     * @param obj
     * @return
     */
    public static String getString(Object obj) {
        if (null == obj || ObjectUtil.isEmpty(obj.toString()))
            return "";
        else
            return obj.toString();
    }

    /**
     * 得到一对象toString的值，如果是null则返回默认值，如果默认值为null则返回""
     *
     * @param obj
     * @param defaultObj 默认值
     * @return
     */
    public static String getString(Object obj, Object defaultObj) {
        if (null == obj || ObjectUtil.isEmpty(obj.toString()))
            if (null == defaultObj)
                return "";
            else
                return defaultObj.toString();
        else
            return obj.toString();
    }

    /**
     * 从字符串首位开始获取指定字符串的指定长度的子串，不考虑中文情况造成的字符串长度变化问题
     *
     * @param sourceString 源字符串
     * @param maxLength    截取最大长度
     * @return 剪切后的结果，如果源字符串为null，返回空串
     */
    public static String subString(String sourceString, int maxLength) {
        String innerSourceString = sourceString;
        if (null == sourceString)
            innerSourceString = ""; // 如果为null，返回空串
        String endString = "";
        int trueLength = innerSourceString.length();
        if (trueLength > maxLength)
            endString = innerSourceString.substring(0, maxLength); // 实际长度大于需要的长度
        else
            endString = innerSourceString;
        return endString;
    }

    /**
     * 按字符长度截取字段串，超过最大字符串的追加"..."符号，注输入的截取参数长度已包含省略号，实际截取内容长度比参数值少1个字符
     * String str = "我是一只小小小小鸟";
     * System.out.println(StringUtil.subStringAppendDots(str,5));
     * 输出结果是"我是一只...";
     *
     * @param sourceString
     * @param maxLength
     * @return
     */
    public static String subStringAppendDots(String sourceString, int maxLength) {
        String str = null;
        if (sourceString.length() > maxLength)
            str = subString(sourceString, maxLength - 1) + "...";
        else
            str = sourceString;
        return str;
    }

    /**
     * 按字节长度截取字段串，超过最大字符串的追加"..."符号，注输入的截取参数长度已包含省略号，实际截取内容长度比参数值少2-4个字节
     *
     * @param sourceString
     * @param maxLength
     * @return
     */
    public static String subStringByBytesAppendDots(String sourceString, int maxLength) {
        String str = null;
        if (getStrLenByBytes(sourceString) > maxLength)
            str = subStringByBytes(sourceString, maxLength - 3) + "...";
        else
            str = sourceString;
        return str;
    }

    /**
     * 按字符长度从尾部截取字段串，超过最大字符串的替换为"..."符号，注输入的截取参数长度已包含省略号，实际截取内容长度比参数值少1个字符
     * String str = "我是一只小小小小鸟";
     * System.out.println(StringUtil.subStringByBytesBeginWithDots(str,5));
     * 输出结果是"...小小小鸟";
     *
     * @param sourceString
     * @param maxLength
     * @return
     */
    public static String subStringByBytesBeginWithDots(String sourceString, int maxLength) {
        String str = null;
        if (getStrLenByBytes(sourceString) > maxLength)
            str = "..." + subStringByBytes(sourceString, (getStrLenByBytes(sourceString) - maxLength + 3), maxLength);
        else
            str = sourceString;
        return str;
    }

    /**
     * 从字符串首位开始获取指定字符串的指定长度的子串，使用byte形式获取字符串长度避免中文情况造成的字符串长度变化问题
     * 并去除截取后尾部由于半个中文字符造成的乱码
     *
     * @param sourceString
     * @param maxLength    截取总长度
     * @return 剪切后的结果，如果源字符串为null，返回空串
     */
    public static String subStringByBytes(String sourceString, int maxLength) {
        return subStringByBytes(sourceString, 0, maxLength);
    }

    /**
     * 从指定偏移位置开始获取指定字符串的指定长度的子串，使用byte形式获取字符串长度避免中文情况造成的字符串长度变化问题
     * 并去除截取后首尾由于半个中文字符造成的乱码
     *
     * @param sourceString
     * @param offset       偏移位置
     * @param maxLength    截取总长度
     * @return 剪切后的结果，如果源字符串为null，返回空串
     */
    public static String subStringByBytes(String sourceString, int offset, int maxLength) {
        if (maxLength <= 0)
            return "";
        if (offset < 0)
            offset = 0;
        String retString = "";
        byte[] sourceByte = sourceString.getBytes();
        int trueLength = sourceByte.length;
        int beginIndex = 0;
        int trueoffset = 0;
        int trueMaxLength = maxLength;
        boolean bChineseFirstHalf = false;
        /* 获取计算后的偏移量,与开始索引 */
        if (trueLength > offset) {
            for (int i = 0; i < offset; i++) {
                trueoffset++;// 实际偏移量
                if (sourceByte[i] > 0) {// 当前位置不是中文
                    bChineseFirstHalf = false;
                    beginIndex++;// 开始指针向后移动
                } else if (sourceByte[i] < 0 && !bChineseFirstHalf) {// 当前位置为中文字首
                    bChineseFirstHalf = true;
                    beginIndex++;// 开始指针向后移动
                } else {// 当前位置为中文字尾
                    bChineseFirstHalf = false;
                    if (i == offset - 1)
                        trueoffset++;// 当前位置为中文字尾实际偏移量再向后移动，偏移位置永远在中文字首后半角字符处
                    if (trueoffset >= trueLength)
                        return "";
                }
            }
        } else
            return "";
		/* 获取结束索引 */
        int endIndex = 0;
        if (trueoffset > offset)
            trueMaxLength--; // 偏移位置向后，则实际截取长度减少
        bChineseFirstHalf = false;
        if (trueLength <= (trueMaxLength + trueoffset))
            endIndex = sourceString.length();
        else {
            for (int i = 0; i < (trueMaxLength + trueoffset); i++) {
                if (sourceByte[i] > 0) {// 当前位置不是中文
                    bChineseFirstHalf = false;
                    endIndex++;// 结束指针向后移动
                } else if (sourceByte[i] < 0 && !bChineseFirstHalf)
                    bChineseFirstHalf = true; // 当前位置为中文字首
                else {// 当前位置为中文字尾
                    bChineseFirstHalf = false;
                    endIndex++;// 结束指针向后移动
                }
            }
            if (endIndex > sourceString.length())
                endIndex = sourceString.length();
        }
        retString = sourceString.substring(beginIndex, endIndex);
        return retString;
    }

    /**
     * 按照所设字符集，返回字符串的长度，使用byte形式获取字符串长度避免中文情况造成的字符串长度变化问题
     *
     * @param str      判断字符串
     * @param encoding 编码设定
     * @return 此编码的长度
     */
    public static int getStrLenByBytes(String str, String encoding) {
        int retInt = -1;
        try {
            if (ObjectUtil.isEmpty(str))
                retInt = 0;
            else {
                byte[] byArr = str.getBytes(encoding);
                retInt = byArr.length;
            }
        } catch (UnsupportedEncodingException e) {
        }
        return retInt;
    }

    /**
     * 按照系统字符集，返回字符串的长度，使用byte形式获取字符串长度避免中文情况造成的字符串长度变化问题
     *
     * @param str 判断字符串
     * @return 此编码的长度
     */
    public static int getStrLenByBytes(String str) {
        return getStrLenByBytes(str, System.getProperty("file.encoding"));
    }

    /**
     * 根据给定的位数，用给定的字符前补足字符串的位数，不考虑中文情况造成的字符串长度变化问题
     *
     * @param strValue 源字条串
     * @param ch       要补的字符
     * @param iSign    补足后的长度
     * @return
     */
    public static String fillString(String strValue, char ch, int iSign) {
        try {
            StringBuffer strTemp = new StringBuffer();
            int iDifference = iSign - strValue.length();
            if (iDifference <= 0)
                return strValue;
            for (int i = 0; i < iDifference; i++)
                strTemp.append(ch);
            strTemp.append(strValue);
            return strTemp.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 根据给定的位数，用给定的字符后补足字符串的位数，不考虑中文情况造成的字符串长度变化问题
     *
     * @param strValue 源字条串
     * @param ch       要补的字符
     * @param iSign    补足后的长度
     * @return
     */
    public static String fillStringOnTail(String strValue, char ch, int iSign) {
        try {
            StringBuffer strTemp = new StringBuffer();
            strTemp.append(strValue);
            int iDifference = iSign - strValue.length();
            if (iDifference <= 0)
                return strValue;
            for (int i = 0; i < iDifference; i++)
                strTemp.append(ch);
            return strTemp.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 根据给定的位数，用给定的字符前补足字符串的位数，使用byte形式获取字符串长度避免中文情况造成的字符串长度变化问题
     *
     * @param strValue 源字条串
     * @param ch       要补的字符
     * @param iSign    补足后的长度
     * @return
     */
    public static String fillStringByBytes(String strValue, char ch, int iSign) {
        try {
            StringBuffer strTemp = new StringBuffer();
            int iDifference = iSign - strValue.getBytes().length;
            if (iDifference <= 0)
                return strValue;
            for (int i = 0; i < iDifference; i++)
                strTemp.append(ch);
            strTemp.append(strValue);
            return strTemp.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 根据给定的位数，用给定的字符后补足字符串的位数，使用byte形式获取字符串长度避免中文情况造成的字符串长度变化问题
     *
     * @param strValue 源字条串
     * @param ch       要补的字符
     * @param iSign    补足后的长度
     * @return
     */
    public static String fillStringByBytesOnTail(String strValue, char ch, int iSign) {
        try {
            StringBuffer strTemp = new StringBuffer();
            strTemp.append(strValue);
            int iDifference = iSign - strValue.getBytes().length;
            if (iDifference <= 0)
                return strValue;
            for (int i = 0; i < iDifference; i++)
                strTemp.append(ch);
            return strTemp.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 比较两个字符串是否相等（任意一个可以为Null）
     *
     * @param str1 判断对象String1
     * @param str2 判断对象String2
     * @return 相等返回true，反之为false
     */
    public static boolean equals(String str1, String str2) {
        return str1 != null ? str1.equals(str2) : str2 == null;
    }

    /**
     * @param str1 str1 判断对象String1
     * @param str2 str2 判断对象String2
     * @return 相等返回true，反之为false
     * Jun 11, 2014 10:06:33 AM
     * @Title: equalsIgnoreCase
     * @Description: 比较两个字符串是否相等（忽略大小写,任意一个可以为Null）
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null ? str1.equalsIgnoreCase(str2) : str2 == null;
    }

    /**
     * 判断字符串中是否包含所需判断的字符
     *
     * @param str        基础字符串
     * @param searchChar 判断用字符
     * @return 包含时返回true，反之为false
     */
    public static boolean contains(String str, char searchChar) {
        if (ObjectUtil.isEmpty(str))
            return false;
        else
            return str.indexOf(searchChar) >= 0;
    }

    /**
     * 判断字符串中是否包含所需判断的字符串
     *
     * @param str        基础字符串
     * @param searchChar 判断用字符串
     * @return 包含时返回true，反之为false
     */
    public static boolean contains(String str, String searchStr) {
        if (ObjectUtil.isEmpty(str) || ObjectUtil.isEmpty(searchStr))
            return false;
        else
            return str.indexOf(searchStr) >= 0;
    }

    /**
     * 比较两个字符串中的值是否相等，如果相等则反回第三个参数的值
     *
     * @param data
     * @param str
     * @return
     */
    public static String equals(Object data, String data1, String str) {
        if (getString(data).equals(data1))
            return str;
        return "";
    }

    /**
     * 比较两个字符串中的值是否相等，如果相等则反回第三个参数的值
     *
     * @param data
     * @param str
     * @return
     */
    public static String equals(String data, String data1, String str) {
        if (getString(data).equals(data1))
            return str;
        return "";
    }

    /**
     * ASCII码转String
     *
     * @param str
     * @return
     */
    public static String ascii2Native(String str) {
        StringBuilder sb = new StringBuilder();
        int begin = 0;
        int index = str.indexOf("%u");
        while (index != -1) {
            sb.append(str.substring(begin, index));
            sb.append(ascii2Char(str.substring(index, index + 6)));
            begin = index + 6;
            index = str.indexOf("%u", begin);
        }
        sb.append(str.substring(begin));
        return sb.toString();
    }

    private static char ascii2Char(String str) {
        if (str.length() != 6)
            throw new IllegalArgumentException("Ascii string of a native character must be 6 character.");
        if (!"%u".equals(str.substring(0, 2)))
            throw new IllegalArgumentException("Ascii string of a native character must start with \"\\u\".");
        String tmp = str.substring(2, 4);
        int code = Integer.parseInt(tmp, 16) << 8;
        tmp = str.substring(4, 6);
        code += Integer.parseInt(tmp, 16);
        return (char) code;
    }

    public static String toChinese(String sDigit) {
        String sChinese = sDigit;
        sChinese = "";
        if (sDigit.charAt(0) >= '0' && sDigit.charAt(0) <= '9') {
            switch (sDigit.charAt(0)) {
                case '1': {
                    if (sDigit.charAt(1) == '0') {
                        sChinese = sChinese + "十";
                        break;
                    }
                    sChinese = sChinese + "一";
                    break;
                }
                case '2': {
                    sChinese = sChinese + "二";
                    break;
                }
                case '3': {
                    sChinese = sChinese + "三";
                    break;
                }
                case '4': {
                    sChinese = sChinese + "四";
                    break;
                }
                case '5': {
                    sChinese = sChinese + "五";
                    break;
                }
                case '6': {
                    sChinese = sChinese + "六";
                    break;
                }
                case '7': {
                    sChinese = sChinese + "七";
                    break;
                }
                case '8': {
                    sChinese = sChinese + "八";
                    break;
                }
                case '9': {
                    sChinese = sChinese + "九";
                    break;
                }
                case '0': {
                    sChinese = sChinese + "0";
                    break;
                }
            }
        }
        return sChinese;
    }

    /**
     * @param str
     * @param def
     * @return Aug 30, 2014 2:35:55 PM
     * @Title: isEmpty
     * @Description: 如果str为空则返回def
     */
    public static String isEmpty(String str, String def) {
        return ObjectUtil.isEmpty(str) ? def : str;
    }

    /**
     * 判断字符串是不是url
     *
     * @param str
     * @return
     */
    public static boolean isHttp(String str) {
        return str.startsWith("http:");
    }

    /**
     * 判断String是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches())
            return false;
        return true;
    }

    /**
     * 判断一个字符串是不是数字(包括负数或1-20)
     *
     * @param str
     * @return
     */
    protected static boolean isNumeric2(String str) {
        char[] charArr = str.toCharArray();
        int index = str.indexOf("-"); // 负号的索引
        for (int i = 0; i < charArr.length; i++)
            if (i == index)
                continue;
            else {
                if (charArr[i] >= '0' && charArr[i] <= '9')
                    continue;
                else
                    return false;
            }
        return true;
    }

    /**
     * @param mobiles
     * @return Sep 25, 2014 2:50:33 PM
     * @Title: isMobileNO
     * @Description: 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        if (ObjectUtil.isEmpty(mobiles) || mobiles.length() < 11)
            return false;
        String regex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return mobiles.matches(regex);
    }

    /**
     * @param email
     * @return Sep 25, 2014 2:50:39 PM
     * @Title: isEmail
     * @Description: 验证邮箱格式
     */
    public static boolean isEmail(String email) {
        if (ObjectUtil.isEmpty(email) || email.length() < 7)
            return false;
        String regex = "^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";
        // String regex =
        // "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return email.matches(regex);
    }

    /**
     * @param str
     * @return Sep 4, 2014 6:13:22 PM
     * @Title: strSpecial
     * @Description: 特殊字符
     */
    public static String strSpecial(String str) {
        String regEx = "[/\\:*?<>|\"\n\t]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    /**
     * @param str
     * @return Sep 4, 2014 6:13:22 PM
     * @Title: strSpecial
     * @Description: 去除特殊字符不包括回车和TAB
     */
    public static String reSpecial(String str) {
        String regEx = "[/\\:*?<>|\"]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    public static String telPhone(String phone) {
        if (ObjectUtil.isEmpty(phone))
            return phone;
        if (phone.trim().startsWith("+86"))
            return phone;
        return "+86" + phone;
    }

    /**
     * @param phone
     * @return
     * @Title replacePhone
     * @Description 替换电话
     */
    public static String replacePhone(String phone) {
        if (!isMobileNO(phone))
            return "";
        String replace = phone.substring(3, phone.length() - 4);
        return phone.replace(replace, "****");
    }

    public static void main(String[] args) {
        String phone = "12345678901";
        System.out.println(phone);
        System.out.println(replacePhone(phone));
    }
}