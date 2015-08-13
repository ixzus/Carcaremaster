package com.lazycare.carcaremaster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.lazycare.carcaremaster.widget.HackyViewPager;
import com.lazycare.carcaremaster.widget.imageview.PhotoView;
import com.lazycare.carcaremaster.widget.imageview.PhotoViewAttacher;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示多张图片
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ImagesShowActivity extends BaseActivity {
    private HackyViewPager viewPager;
    // private int[] res = { R.drawable.login_bg, R.drawable.login_bg,
    // R.drawable.login_bg, R.drawable.login_bg };
    private MyPageAdapter adapter;

    private List<String> res = new ArrayList<>();
    private int position = 0;
    private TextView txt;
    private Button back;
    int old = 0;

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_imagesshow);
    }

    @Override
    public void setActionBarOption() {
        setIsFullScreen(true);
    }

    @Override
    public void initView() {
        viewPager = (HackyViewPager) this.findViewById(R.id.viewpage);
        txt = (TextView) findViewById(R.id.show_txt);
        back = (Button) findViewById(R.id.show_back);
        res = getIntent().getStringArrayListExtra("mlist");
        String pos = getIntent().getStringExtra("pos");
        if (!pos.equals("")) {
            position = Integer.parseInt(pos);
        }

        if (res != null && res.size() != 0) {
            adapter = new MyPageAdapter(res, ImagesShowActivity.this);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
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
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View iv = LayoutInflater.from(context).inflate(
                    R.layout.item_viewpage, null);
            PhotoView img = (PhotoView) iv.findViewById(R.id.imageview);
            if (!res.get(position).equals("")) {
                img.setZoomable(true);
                img.setAllowParentInterceptOnEdge(true);
                img.setImageUri(res.get(position));
            }
            //图片区域内单击之后退出
            img.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
            container.addView(iv, 0);
            return iv;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return false;
    }
}
