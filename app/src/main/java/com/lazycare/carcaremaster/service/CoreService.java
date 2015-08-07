package com.lazycare.carcaremaster.service;

import java.util.Collection;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lazycare.carcaremaster.AppointmentListActivity;
import com.lazycare.carcaremaster.MainActivity;
import com.lazycare.carcaremaster.QuestionsListActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.IMMessage;
import com.lazycare.carcaremaster.data.LoginConfig;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.XmppConnectionManager;

/**
 * IM服务
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class CoreService extends Service {

    private NotificationManager notificationManager;
    private static long date = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        initChatManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setPriority(Notification.PRIORITY_MIN);
//        Notification notification=builder.build();
        //保证service运行在前台(GC是优先级会很高，不会被优先GC)
        Notification notification = new Notification(R.mipmap.ic_launcher, "重连",
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, "通知", "重连", pendingIntent);
        startForeground(123, notification);
        //足够多资源的时候，就会重新开启service
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /* 初始化xmpp连接 */
    private void initChatManager() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        XMPPConnection conn = XmppConnectionManager.getInstance().getConnection();
        conn.addPacketListener(pListener, new MessageTypeFilter(Message.Type.chat));
        //重连监听
        conn.addConnectionListener(new ConnectionListener() {
            @Override
            public void connectionClosed() {
                Log.d("gmyboy", "connectionClosed");
                //关闭本服务
                onDestroy();
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.d("gmyboy", "connectionClosedOnError");
                onDestroy();
            }

            @Override
            public void reconnectingIn(int i) {
                Log.d("gmyboy", "reconnectingIn");
            }

            @Override
            public void reconnectionSuccessful() {
                Log.d("gmyboy", "reconnectionSuccessful");
                //重连成功
                sendInentAndPre(Config.RECONNECT_STATE_SUCCESS);
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Log.d("gmyboy", "reconnectionFailed");
            }
        });
    }

    PacketListener pListener = new PacketListener() {

        @Override
        public void processPacket(Packet arg0) {
            Message message = (Message) arg0;
            if (message != null && message.getBody() != null && !message.getBody().equals("null")) {
                IMMessage msg = new IMMessage();
                String time = (String) message.getProperty(IMMessage.KEY_TIME);
                msg.setTime(time);
                msg.setContent(message.getBody());
                if (Message.Type.error == message.getType()) {
                    msg.setType(IMMessage.ERROR);
                } else {
                    msg.setType(IMMessage.SUCCESS);
                }
                String from = message.getFrom().split("/")[0];
                msg.setFromSubJid(from);
                Log.d("gmyboy", "-------message--------" + message.getBody());
                String[] lstMsg = message.getBody().split("\\/-");

                if (lstMsg != null && lstMsg.length != 0) {
                    if (lstMsg[1].contains(Constant.MY_NEWS_TXT)) {
                        setQuestionNotiType(R.drawable.ic_launcher, "收到新消息", lstMsg[0], QuestionsListActivity.class, 2);
                        sendBroad(msg);
                    } else if (lstMsg[1].contains(Constant.MY_NEWS_AUDIO)) {
                        setQuestionNotiType(R.drawable.ic_launcher, "收到新消息", "音频", QuestionsListActivity.class, 2);
                        sendBroad(msg);
                    } else if (lstMsg[1].contains(Constant.MY_NEWS_IMG)) {
                        setQuestionNotiType(R.drawable.ic_launcher, "收到新消息", "图片", QuestionsListActivity.class, 2);
                        sendBroad(msg);
                    } else if (lstMsg[1].contains(Constant.MY_NEWS_QUESTION)) {
                        setQuestionNotiType(R.drawable.ic_launcher, "有新的抢单问题来啦", "", QuestionsListActivity.class, 0);
                        sendQuestionBroad(Constant.NEW_QUESTION_ACTION);
                    } else if (lstMsg[1].contains(Constant.MY_NEWS_QUESTION_AT)) {
                        setQuestionNotiType(R.drawable.ic_launcher, "有新的@我问题来啦", "", QuestionsListActivity.class, 1);
                        sendQuestionBroad(Constant.NEW_QUESTION_ACTION);
                    } else if (lstMsg[1].contains(Constant.MY_NEWS_APPOINTMENT)) {
                        setQuestionNotiType(R.drawable.ic_launcher, "有新的订单来啦", "", AppointmentListActivity.class, 1);
                        sendQuestionBroad(Constant.NEW_APPOINTMENT);
                    } else {

                    }
                }

            }

        }

    };

    private void sendQuestionBroad(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void setQuestionNotiType(int iconId, String contentTitle, String contentText, Class activity, int type) {
        long noticedate = System.currentTimeMillis();
        /*
         * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
        Intent notifyIntent = new Intent(this, activity);

        notifyIntent.putExtra("type", type);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);// 即时更新
        /* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
        // 点击自动消失
        myNoti.flags = Notification.FLAG_AUTO_CANCEL;
        /* 设置statusbar显示的icon */
        myNoti.icon = iconId;
        /* 设置statusbar显示的文字信息 */
        myNoti.tickerText = contentTitle;
        // 控制多条消息连续发来时只有第一个有声音震动提示
        if (noticedate - date < 250) {// 时间差小于4S
            /* 设置notification发生时同时不发出默认声音 */
            // myNoti.defaults = Notification.DEFAULT_SOUND;
            // myNoti.vibrate = new long[] { 0, 200, 100, 200, 100, 200 };
        } else {
            /* 设置notification发生时同时发出默认声音 */
            myNoti.defaults = Notification.DEFAULT_SOUND;
            myNoti.vibrate = new long[]{0, 200, 100, 200, 100, 200};
        }

        date = noticedate;
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(this, contentTitle, contentText, appIntent);
		/* 送出Notification */
        notificationManager.notify((int) System.currentTimeMillis(), myNoti);
    }

    /**
     * 判断自己的应用是否已经退出
     *
     * @param packageName
     * @return
     */
    public boolean checkBrowser(String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 发送即时消息
     *
     * @param msg
     */
    private void sendBroad(IMMessage msg) {
        Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
        intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
        sendBroadcast(intent);
    }

    /**
     * 发送连接状态广播
     *
     * @param isSuccess
     */
    private void sendInentAndPre(int isSuccess) {
        Intent intent = new Intent();
        SharedPreferences preference = getSharedPreferences(Config.LOGIN_SET, 0);
        // 保存在线连接信息
        preference.edit().putInt(Config.IS_ONLINE, isSuccess).commit();
        intent.setAction(Config.ACTION_RECONNECT_STATE);
        intent.putExtra(Config.RECONNECT_STATE, isSuccess);
        sendBroadcast(intent);
    }

    /**
     * 发出Notification
     *
     * @param iconId       图标
     * @param contentTitle 标题
     * @param contentText  你内容
     * @param activity
     * @author shimiso
     * @update 2012-5-14 下午12:01:55
     */
    private void setNotiType(int iconId, String contentTitle,
                             String contentText, Class activity, int type) {
		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
        Intent notifyIntent = new Intent(this, activity);
        notifyIntent.putExtra("type", type);
        // notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(this, 0,
                notifyIntent, 0);

		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
        // 点击自动消失
        myNoti.flags = Notification.FLAG_AUTO_CANCEL;
		/* 设置statusbar显示的icon */
        myNoti.icon = iconId;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = contentTitle;
		/* 设置notification发生时同时发出默认声音 */
        myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(this, contentTitle, contentText, appIntent);
		/* 送出Notification */
        notificationManager.notify(0, myNoti);
    }

}
