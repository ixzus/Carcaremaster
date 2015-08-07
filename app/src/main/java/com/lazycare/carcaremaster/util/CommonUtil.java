package com.lazycare.carcaremaster.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import u.aly.cu;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * 通用utils
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class CommonUtil {
    static String TAG = "CommonUtil";

    /**
     * 将结果解码
     *
     * @param dataStr
     * @return
     */
    // public static String decodeUnicode(String dataStr) {
    //
    // final StringBuffer buffer = new StringBuffer();
    // Pattern p=Pattern.compile("\\\\u([\\S]{4})([^\\\\]*)");
    // Matcher match=p.matcher(dataStr);
    // while(match.find())
    // {
    // char letter = (char) Integer.parseInt(match.group(1), 16);
    // buffer.append(new Character(letter).toString());
    // buffer.append(match.group(2));
    // }
    //
    // return buffer.toString();
    // }
    public static String decodeUnicode(String dataStr) {
        String retStr = "";
        try {
            byte[] bs = dataStr.getBytes();
            retStr = new String(bs, "UTF-8");
            // byte[] utf8 = dataStr.getBytes("UTF-8");
            // retStr = new String(utf8,"UTF-8");
            // retStr = utf8.toString();
        } catch (Exception e) {
            retStr = e.getMessage();
        }
        Log.d("retStr", retStr);
        return retStr;
    }

    /**
     * 判断数据是否为空
     *
     * @param dataStr
     * @return
     */
    public static boolean isEmpty(String dataStr) {
        boolean ret = false;
        try {
            if (dataStr.equals("")) {
                ret = true;
            } else
                ret = false;
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 格式化时间
     *
     * @param formatTime
     * @param time
     * @return
     */
    public static String FormatTime(String formatTime, float time) {
        String ftTime = "yyyy年MM月dd日 HH:mm";
        time = time * 1000;
        String afterFormat = new SimpleDateFormat(ftTime)
                .format(new Date((long) time));
        if (afterFormat.length() == 17)
            afterFormat = afterFormat.substring(5, afterFormat.length());

        // SimpleDateFormat formatter = new SimpleDateFormat(formatTime);
        // String afterFormat = formatter.format(time);
        return afterFormat;
    }

    /**
     * dip转为 px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px 转为 dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取工时管理的日期数据
     *
     * @param string 服务器当前时间
     * @return
     */
    public static List<String> getDateList(String string) {
        List<String> lstDate = new ArrayList<String>();
        /** 输出格式: 2006-01-01 00:00:00 */
        String s = "";
        java.text.DateFormat formatm = new SimpleDateFormat(
                "yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Calendar tempc;
        Date date = null;
        try {
            date = formatm.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        Log.d("gmyboy", "11111" + c.getTime());
        for (int i = 0; i < Config.TIME_DAY; i++) {
            tempc = Calendar.getInstance();
            tempc.setTime(date);
            // tempc.set(Calendar.DATE, c.get(Calendar.DATE) + i);
            tempc.add(Calendar.DATE, i);
            s = formatm.format(tempc.getTime());
            lstDate.add(s);
            s = "";
            tempc = null;
        }
        return lstDate;
    }

    /**
     * 判断是否是繁忙时间
     *
     * @param busyTime
     * @param curTime
     * @return
     */
    public static void setInBusy(List<String> busyTime,
                                 HashMap<Integer, Boolean> hmWorkStates) {
        try {
            for (int i = 0; i < 77; i++) {
                hmWorkStates.put(i, false);
                Calendar cur = Calendar.getInstance();
                int day = cur.get(Calendar.DAY_OF_MONTH);
                int hour = i % 11 + 9;
                int dayadd = i / 11;
                cur.set(Calendar.DAY_OF_MONTH, day + 1 + dayadd);
                cur.set(Calendar.HOUR_OF_DAY, hour);
                cur.set(Calendar.MINUTE, 0);
                cur.set(Calendar.SECOND, 0);
                Date curDate = cur.getTime();
                Long a = curDate.getTime() / 1000;
                for (String key : busyTime) {
                    float fStart = Float.parseFloat(key) * 1000;
                    Date dEnd = null;
                    SimpleDateFormat d_full = new SimpleDateFormat(
                            "MM-dd HH:mm:ss");
                    dEnd = new Date((long) fStart);
                    Log.i("gmyboy", a + "==" + dEnd.getTime());
                    if (a == dEnd.getTime()) {
                        hmWorkStates.put(i, true);
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 判断是否是繁忙时间
     *
     * @param string
     * @param busyTime
     * @param curTime
     * @return
     */
    public static String getInBusyTime(String string, List<Boolean> list) {
        String selectTime = "";
        java.text.DateFormat formatm = new SimpleDateFormat(
                "yyyy-MM-dd");
        // 服务器当前时间
        Calendar tempc = Calendar.getInstance();
        Date date = null;
        Log.d("gmyboy", "----------------" + string);
        try {
            date = formatm.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tempc.setTime(date);

        try {
            for (int i = 0; i < Config.TIME_HOURS * Config.TIME_DAY; i++) {
                boolean isSelected = list.get(i);
                if (isSelected == true) {
                    Calendar cur = Calendar.getInstance();

                    int day = tempc.get(Calendar.DAY_OF_MONTH);
                    int month = tempc.get(Calendar.MONTH);
                    int hour = i % Config.TIME_HOURS + 9;// 时间
                    int dayadd = i / Config.TIME_HOURS;
                    Log.d("gmyboy", "--------day--------" + day + "\n" + "--------month--------" + month + "\n" + "--------hour--------" + hour + "\n" + "--------dayadd--------" + dayadd + "\n");
                    cur.set(Calendar.DAY_OF_MONTH, day + dayadd);
                    cur.set(Calendar.MONTH, month);
                    cur.set(Calendar.HOUR_OF_DAY, hour);
                    cur.set(Calendar.MINUTE, 0);
                    cur.set(Calendar.SECOND, 0);
                    // Date curDate = cur.getTime();
                    // java.text.DateFormat formatm = new
                    // java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String ss = String.valueOf(cur.getTimeInMillis() / 1000);
                    selectTime += ss + ",";
                }

            }
            if (selectTime.length() > 1)
                selectTime = selectTime.substring(0, selectTime.length() - 1);
        } catch (Exception e) {
        }
        return selectTime;
    }

    public static void showToast(Context context, String massage) {
        Toast.makeText(context, massage, Toast.LENGTH_SHORT).show();
    }

    public static void showSnack(View view, String message) {
        Snackbar.make(view,message, Snackbar.LENGTH_SHORT)
                .setAction("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }
}
