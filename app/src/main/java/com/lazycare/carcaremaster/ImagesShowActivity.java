package com.lazycare.carcaremaster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lazycare.carcaremaster.util.StringUtil;
import com.squareup.picasso.Picasso;

/**
 * 显示多张图片
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ImagesShowActivity extends BaseActivity {
    private ViewPager viewPager;
    // private int[] res = { R.drawable.login_bg, R.drawable.login_bg,
    // R.drawable.login_bg, R.drawable.login_bg };
    private MyPageAdapter adapter;

    private List<String> res = new ArrayList<String>();
    private int position = 0;
    private TextView txt;
    private Button back;
    //	private String type = "";
    int old = 0;

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_imagesshow);
    }

    @Override
    public void setActionBarOption() {
        // TODO Auto-generated method stub

    }

    @Override
    public void initView() {
        viewPager = (ViewPager) this.findViewById(R.id.viewpage);
        txt = (TextView) findViewById(R.id.show_txt);
        back = (Button) findViewById(R.id.show_back);
        res = getIntent().getStringArrayListExtra("mlist");
        String pos = getIntent().getStringExtra("pos");
//		type = getIntent().getStringExtra("type");
        if (!pos.equals("")) {
            position = Integer.parseInt(pos);
        }

        if (res != null && res.size() != 0) {
            adapter = new MyPageAdapter(res, ImagesShowActivity.this);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        txt.setText(position + 1 + "/" + res.size());
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                txt.setText(arg0 + 1 + "/" + res.size());
                old = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
//				if (old == 0) {
//					finish();
//				}
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    public class MyPageAdapter extends PagerAdapter {
        private List<String> res;
        private Context context;

        public MyPageAdapter(List<String> res, Context context) {
            super();
            this.res = res;
            this.context = context;
        }

        @Override
        public int getCount() {
            return res.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (View) arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View iv = LayoutInflater.from(context).inflate(
                    R.layout.item_viewpage, null);

            ImageView img = (ImageView) iv.findViewById(R.id.list_viewpage_img);
            if (!res.get(position).equals("")) {
                //判断是不是url
                if (StringUtil.isHttp(res.get(position))) {
                    Picasso.with(ImagesShowActivity.this)
                            .load(res.get(position)).centerCrop()
                            .resize(1000, 1000).into(img);
                } else {
                    Picasso.with(ImagesShowActivity.this)
                            .load(new File(res.get(position))).centerCrop()
                            .resize(1000, 1000).into(img);
                }
            }

            ((ViewPager) container).addView(iv, 0);
            return iv;
        }

    }

}
