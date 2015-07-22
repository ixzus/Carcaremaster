package com.lazycare.carcaremaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lazycare.carcaremaster.util.Configuration;

/**
 * 欢迎页
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class SplashActivity extends BaseActivity {
    private String username = "";
    private String id = "";
    private String pwd = "";

    private Class mClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


    }

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void setActionBarOption() {

    }

    @Override
    public void initView() {
        // 从share中拿到id
        id = getSharePreferences().getString(Configuration.ID, "");
        username = getSharePreferences().getString(Configuration.USERNAME, "");
        pwd = getSharePreferences().getString(Configuration.PWD, "111111");
//        if (id.equals("") || username.equals("") || pwd.equals("")) {
//            mClass = LoginActivity.class;
//        } else {
//            mClass = MainActivity.class;
//        }
        mClass = LoginActivity.class;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, mClass);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 2000);
    }

}
