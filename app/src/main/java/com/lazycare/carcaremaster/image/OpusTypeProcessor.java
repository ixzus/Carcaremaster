package com.lazycare.carcaremaster.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * 图片的预渲染
 */
public class OpusTypeProcessor implements ProcessorInterface {
    private Paint paint;
    private Context mContext;

    public OpusTypeProcessor(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        paint = new Paint();
    }

    @Override
    public void process(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        //对bitmap进行处理

        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);

        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(16.0F);
        StaticLayout sl= new StaticLayout("GIF", textPaint, bitmap.getWidth()-8, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.translate(6, 40);
        sl.draw(canvas);


    }
}