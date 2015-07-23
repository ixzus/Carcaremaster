package com.lazycare.carcaremaster.util;

import android.os.Environment;

/**
 * 配置信息
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class Config {
    /**
     * 个信推送
     */
    public static final String USERNAME = "username";
    public static int TIME_DAY = 7;
    public static int TIME_HOURS = 12;
    public static final String BASE_PATH = Environment
            .getExternalStorageDirectory().toString() + "/chudongyangche";
    /**
     * 录音文件保存路径
     */
    public static final String RECORD_PATH = BASE_PATH + "/audio/";
    /**
     * 拍照图片保存路径
     */
    public static final String IMG_PATH = BASE_PATH + "/img/";
    /**
     * 微博地址
     */
    public static final String BLOG_SOFT_URL = "";
    // 与数据库相关的常量，根据需要配置
    /**
     * 本地数据库创建文件
     */
    public static final String DATABASE_CHEMA_PATH = BASE_PATH + "/schema/";
    /**
     * 本地数据库名称
     */
    public static final String DATABASE_FILE = "cdyc.db";
    /**
     * 本地数据库版本
     */
    public static final int DATABASE_VERSION = 5;
    // 与数据库相关的常量，根据需要配置,错误提示信息
    /**
     * 您的网络不好，请稍后重试
     */
    public static final String NETWORK_UNCONNECT = "您的网络不好，请稍后重试！";
    /**
     * 网络异常
     */
    public static final String NETWORK_ERROR = "网络异常";
    /**
     * 网络不可用，请使用WiFi/3G/4G网络
     */
    public static final String NETWORK_OFF = "网络不可用，请使用WiFi/3G/4G网络";
    /**
     * 系统正在建设中
     */
    public static final String SYSTEM_CONSTRUCTION = "系统正在建设中......";
    /**
     * 错误信息前缀
     */
    public static final String ERROR = "error^";
    // 几种时间格式根据需要而变
    /**
     * 订单付款的时间期限(秒)
     */
    public static int PEROID = 120;
    /**
     * 系统当前时间
     */
    public static String SYS_TIME = String.valueOf(System.currentTimeMillis());
    /**
     * 服务器时间
     */
    public static String SYS_DATE_TIEM = "";
    /**
     * 日期时间格式:yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_TIME_FOR_MAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期时间格式:yyyy-MM-dd HH:mm
     */
    public static final String DATE_TIME_SHORT_FOR_MAT = "yyyy-MM-dd HH:mm";
    /**
     * 日期格式:yyyy-MM-dd
     */
    public static final String DATE_FOR_MAT = "yyyy-MM-dd";
    // 返回响应设置
    public final static int DEFAULT_ERROR = -1;
    public final static int DEFAULT_SUCCESS = 1;
    public final static int RESULT_USER = 0X1111;
    public final static int REQUESTCODE_ONE = 0X0001;
    public final static int REQUESTCODE_TWO = 0X0002;
    public final static int REQUESTCODE_THREE = 0X0003;
    public final static int REQUESTCODE_PHOTO = 0X1001;
    public final static int REQUESTCODE_CHOOSE = 0X1002;
    public final static int REQUESTCODE_DELETE = 0X1003;
    public final static int WHAT_DEFAULT = 0X0000;
    public final static int WHAT_ONE = 0X0001;
    public final static int WHAT_TWO = 0X0002;
    public final static int WHAT_THREE = 0X0003;
    public final static int WHAT_FOUR = 0X0004;
    public final static int WHAT_MAX = 0X1001;
    // 动画持续时间
    public final static int SHORT_ANIM_DURATION = 180;
    /**
     * 拍照后照片本地的存储路径(需要SD卡)
     */
    public static final String LOCAL_IMAGES_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/hzmb/Images/";
    // 系统标示
    public static String ANDROID_TIME = "ANDROID_TIME";
    public static final String USER_INFO = "USER_INFO";
    public static final String SECT_INFO = "SECT_INFO";
    // 用于图片获取的头部
    /**
     * HTTP头部
     */
    public static final String HTTP = "http://";
    /**
     * HTTPS头部
     */
    public static final String HTTPS = "https://";
    /**
     * SD卡头部
     */
    public static final String FILE = "file://";
    /**
     * CONTENT头部
     */
    public static final String CONTENT = "content://";
    /**
     * assets(程序内部文件)头部
     */
    public static final String ASSETS = "assets://";
    /**
     * drawable(程序内部资源文件)头部
     */
    public static final String DRAWABLE = "drawable://";
    /**
     * 从网络加载标示
     */
    public static final int FLAG_SERVER = 0;
    public static final int FLAG_HTTP = 1;
    public static final int FLAG_HTTPS = 2;
    /**
     * 从本地加载标示
     */
    public static final int FLAG_FILE = 3;
    /**
     * 图片文件标识
     */
    public static final String FILE_IMG_TYPE = "img";
    /**
     * 音频文件标识
     */
    public static final String FILE_AUDIO_TYPE = "audio";
    /**
     * 从content加载
     */
    public static final int FLAG_CONTENT = 4;
    /**
     * 从程序加载标示
     */
    public static final int FLAG_ASSETS = 5;
    /**
     * 从drawable中加载标示
     */
    public static final int FLAG_DRAWABLE = 6;
    // 分页大小
    public static final int PAGE_SIZE = 20;
    public static final String PAGE_SIZE_STR = "20";
    public static final String PAGE_SIZE_STR_10 = "10";
    // 最后一次点击时间
    public volatile static long LAST_CLICK_TIME = 0;
    public static final int REPLY_WENZI = 1;// 1纯文字，2音频 ,3:图片 4:视频
    public static final int REPLY_YINPIN = 2;
    public static final int REPLY_TUPIANI = 3;
    public static final int REPLY_SHIPIN = 4;
    /**
     * 描述冲连接，
     */
    public static final boolean RECONNECT_STATE_SUCCESS = true;
    public static final boolean RECONNECT_STATE_FAIL = false;
    /**
     * 描述冲连接状态的关机子，寄放的intent的关键字
     */
    public static final String RECONNECT_STATE = "reconnect_state";
    /**
     * 是否在线的SharedPreferences名称
     */
    public static final String PREFENCE_USER_STATE = "prefence_user_state";
    public static final String IS_ONLINE = "is_online";
    /**
     * 重连接状态acttion
     */
    public static final String ACTION_RECONNECT_STATE = "action_reconnect_state";
    /**
     * 服务器的配置
     */
    public static final String LOGIN_SET = "eim_login_set";// 登录设置
}