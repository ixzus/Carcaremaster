package com.lazycare.carcaremaster;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lazycare.carcaremaster.util.Config;

/**
 * 用户个人信息
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ProfileActivity extends BaseActivity {
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_profile);

    }

    @Override
    public void setActionBarOption() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("个人信息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initView() {
        id = getSharePreferences().getString(Config.ID, "0");

    }

}
