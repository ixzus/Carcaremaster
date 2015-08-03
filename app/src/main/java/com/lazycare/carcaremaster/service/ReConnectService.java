package com.lazycare.carcaremaster.service;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.XmppConnectionManager;

/**
 * IM重连服务
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ReConnectService extends Service {
    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private final IBinder rebinder = new MyReBinder();
    private Dialog dialog;

    public class MyReBinder extends Binder {
        /* 返回service服务，方便activity中得到 */
        public ReConnectService getService() {
            return ReConnectService.this;
        }
    }

    @Override
    public void onCreate() {
        context = this;
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(reConnectionBroadcastReceiver, mFilter);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(reConnectionBroadcastReceiver);
        super.onDestroy();
    }

    BroadcastReceiver reConnectionBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.d("gmyboy", "网络状态已经改变");
                connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                final XMPPConnection connection = XmppConnectionManager
                        .getInstance().getConnection();
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    if (!connection.isConnected()) {
                        //发送正在连接状态
                        sendInentAndPre(Config.RECONNECT_STATE_ING);
                        new Thread() {
                            public void run() {
                                Looper.prepare();
                                reConnect(connection);
                                Looper.loop();
                            }
                        }.start();
                    } else {
                        sendInentAndPre(Config.RECONNECT_STATE_SUCCESS);
//						Toast.makeText(context, "您已上线!", Toast.LENGTH_LONG)
//								.show();
                    }
                } else {
                    sendInentAndPre(Config.RECONNECT_STATE_FAIL);
                    Toast.makeText(context, "网络断开,您已离线!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        }

    };

    /**
     * 递归重连，直连上为止.
     *
     * @param connection
     */
    public void reConnect(XMPPConnection connection) {
        try {

            connection.connect();
            if (connection.isConnected()) {
                Presence presence = new Presence(Presence.Type.available);
                connection.sendPacket(presence);
//				Toast.makeText(context, "您已上线!", Toast.LENGTH_LONG).show();
            }
        } catch (XMPPException e) {
            Log.e("gmyboy", "XMPP连接失败!", e);
            reConnect(connection);
        }
    }

    private void sendInentAndPre(int isSuccess) {
        Intent intent = new Intent();
        SharedPreferences preference = getSharedPreferences(Config.LOGIN_SET, 0);
        // 保存在线连接信息
        preference.edit().putInt(Config.IS_ONLINE, isSuccess).commit();
        intent.setAction(Config.ACTION_RECONNECT_STATE);
        intent.putExtra(Config.RECONNECT_STATE, isSuccess);
        sendBroadcast(intent);
    }
}
