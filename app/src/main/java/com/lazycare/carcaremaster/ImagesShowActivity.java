package com.lazycare.carcaremaster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.lazycare.carcaremaster.image.OpusTypeProcessor;
import com.lazycare.carcaremaster.image.TuoFrescoProcessor;
import com.lazycare.carcaremaster.util.StringUtil;
import com.lazycare.carcaremaster.widget.imageview.GestureImageView;
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
            SimpleDraweeView img = (SimpleDraweeView) iv.findViewById(R.id.imageview);
//            GestureImageView img = (GestureImageView) iv.findViewById(R.id.image);
//            ImageView img=(ImageView)iv.findViewById(R.id.imageview);

//            TuoFrescoProcessor processor = new TuoFrescoProcessor();
//            OpusTypeProcessor opusTypeProcessor = new OpusTypeProcessor(context);
//            opusTypeProcessor.setOpusType(opusInfo.getOpusType());
//            processor.addProcessor(opusTypeProcessor);
            /**
             * 给图片加标识
             */
            Postprocessor redMeshPostprocessor = new BasePostprocessor() {
                @Override
                public String getName() {
                    return "redMeshPostprocessor";
                }

                @Override
                public void process(Bitmap bitmap) {
                    Canvas canvas = new Canvas(bitmap);
                    //对bitmap进行处理

                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);

                    TextPaint textPaint = new TextPaint();
                    textPaint.setAntiAlias(true);
                    textPaint.setTextSize(16.0F);
                    StaticLayout sl = new StaticLayout("GIF", textPaint, bitmap.getWidth() - 8, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    canvas.translate(6, 40);
                    sl.draw(canvas);
                }

            };
            Log.d("gmyboy", "---------------" + res.get(position));
            if (!res.get(position).equals("")) {
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(res.get(position)))
//                            .setResizeOptions(new ResizeOptions(100, 100))//修改图片大小
                        .setAutoRotateEnabled(true)//设置图片智能摆正
                        .setProgressiveRenderingEnabled(true)//设置渐进显示
//                            .setPostprocessor(redMeshPostprocessor)//设置后处理  (设置之后加载图片每次都会有progress，说明每次都要从网络从新加载)
                        .build();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(img.getController())
                        .build();
                img.setController(controller);
            }
            ((ViewPager) container).addView(iv, 0);
            return iv;
        }

    }

}
