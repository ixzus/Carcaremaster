package com.lazycare.carcaremaster.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lazycare.carcaremaster.service.IMChatService;
import com.lazycare.carcaremaster.service.ReConnectService;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 聊天服务
		Intent chatServer = new Intent(context, IMChatService.class);
		context.startService(chatServer);
		// 自动恢复连接服务
		Intent reConnectService = new Intent(context, ReConnectService.class);
		context.startService(reConnectService);
		// 系统消息连接服务
		// Intent imSystemMsgService = new Intent(context,
		// IMSystemMsgService.class);
		// context.startService(imSystemMsgService);
	}
}
