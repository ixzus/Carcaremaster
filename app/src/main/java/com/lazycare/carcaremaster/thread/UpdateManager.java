package com.lazycare.carcaremaster.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.afollestad.materialdialogs.Theme;
import com.lazycare.carcaremaster.MessageActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.util.Config;

/**
 * 自动更新管理类
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class UpdateManager {
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 下载的url */
    private String updateUrl = "";
    /* 下载保存路径 */
    private String mSavePath;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private Context mContext;

    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    /* 更新进度条 */
    // private ProgressBar mProgress;
    private MaterialDialog dialog;
    private Dialog mDownloadDialog;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:


//                    if (progress < 100) {
//                        RemoteViews contentview = mNotification.contentView;
//                        contentview.setTextViewText(R.id.tv_progress, progress + "%");
//                        contentview.setProgressBar(R.id.progressbar, 100, progress,
//                                false);
//                    }
//                    else {
//                        // 下载完毕后变换通知形式
//                        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
//                        mNotification.contentView = null;
//                        Intent intent = new Intent(mContext, MessageActivity.class);
//
//                        // 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
//                        PendingIntent contentIntent = PendingIntent.getActivity(
//                                mContext, 0, intent,
//                                PendingIntent.FLAG_UPDATE_CURRENT);
//                        mNotification.setLatestEventInfo(mContext, "下载完成",
//                                "文件已下载完毕", contentIntent);
//
//                    }
//                    mNotificationManager.notify(0, mNotification);
                    // 设置进度条位置
                    dialog.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    mNotificationManager.cancel(0);
                    // 安装文件
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UpdateManager(Context context, String url) {
        this.mContext = context;
        this.updateUrl = url;
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 检测软件更新
     *
     * @param versionCode
     */
    public void checkUpdate(String versionCode, int config) {
        if (isUpdate(versionCode)) {
            // 显示提示对话框
            showNoticeDialog(config);
        } else {
            if (config == Config.WHAT_ONE)
                Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 检查软件是否有更新版本
     *
     * @param version
     * @return
     */
    private boolean isUpdate(String version) {
        // 获取当前软件版本
        int versionCode = getVersionCode(mContext);
        int serverCode = Integer.valueOf(version);
        // 版本判断
        if (serverCode > versionCode) {
            return true;
        }
        return false;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(
                    "com.lazycare.carcaremaster", 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog(int config) {
        switch (config) {
            case Config.WHAT_ONE:
                new MaterialDialog.Builder(mContext).content(R.string.dialog_update)
                        .theme(Theme.LIGHT)
                        .positiveText(R.string.action_now)
                        .negativeText(R.string.action_delay)
                        .negativeColorRes(R.color.grey)
                        .positiveColorRes(R.color.olivedrab)
                        .callback(new ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                // 显示下载通知
                                showDownloadDialog();
                                setUpNotification();
                            }
                        }).show();
                break;
            case Config.WHAT_TWO:
                new MaterialDialog.Builder(mContext).title(R.string.dialog_update_auto_title)
                        .content(R.string.dialog_update_auto)
                        .positiveText(R.string.action_now)
                        .negativeText(R.string.action_delay)
                        .neutralText(R.string.action_update_auto)
                        .neutralColorRes(R.color.netur)
                        .negativeColorRes(R.color.grey)
                        .positiveColorRes(R.color.olivedrab)
                        .callback(new ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                // 显示下载通知
                                showDownloadDialog();
                                setUpNotification();
                            }

                            @Override
                            public void onNeutral(MaterialDialog dialog) {
                                //不再启动提醒
                                mContext.getSharedPreferences(Config.USERINFO, 0).edit()
                                        .putString(Config.REMIND_UPDATE, "0")
                                        .commit();
                            }
                        }).show();
                break;
        }

    }

    private void setUpNotification() {
        int icon = R.mipmap.ic_launcher;
        CharSequence tickerText = "开始下载";
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);

        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
                R.layout.notification_download);
        contentView.setTextViewText(R.id.name, "懒人养车最新版 正在下载...");
        // 指定个性化视图
        mNotification.contentView = contentView;

        Intent intent = new Intent(mContext, MessageActivity.class);
        // 下面两句是 在按home后，点击通知栏，返回之前activity 状态;
        // 有下面两句的话，假如service还在后台下载， 在点击程序图片重新进入程序时，直接到下载界面，相当于把程序MAIN 入口改了 - -
        // 是这么理解么。。。
        // intent.setAction(Intent.ACTION_MAIN);
        // intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 指定内容意图
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(0, mNotification);
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        //开启启动提醒
        mContext.getSharedPreferences(Config.USERINFO, 0).edit()
                .putString(Config.REMIND_UPDATE, "1")
                .commit();
        new MaterialDialog.Builder(mContext).title(R.string.dialog_now)
                .contentGravity(GravityEnum.CENTER).progress(false, 100, true)
                .theme(Theme.LIGHT)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog = (MaterialDialog) dialogInterface;
                        // 启动新线程下载软件
                        new downloadApkThread().start();
                    }
                }).show();
    }

    /**
     * 下载文件线程
     *
     * @date 2012-4-26
     * @blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory()
                            + "/";
                    mSavePath = Config.BASE_PATH + "/download/";

                    URL url = new URL(updateUrl);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    //要求http请求不要gzip压缩 否则conn.getContentLength()返回-1
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();
                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "CarCareMaster.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    Log.e("gmyboy", "length=" + length);
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
//                        Message msg = mHandler.obtainMessage();
//                        msg.what = DOWNLOAD;
//                        msg.arg1 = progress;
//                        if (progress >= lastRate + 1) {
//                            mHandler.sendMessage(msg);
//                            lastRate = progress;
//                        }
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            dialog.dismiss();
        }
    }

    ;

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, "CarCareMaster.apk");
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(apkfile.toString())), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}
