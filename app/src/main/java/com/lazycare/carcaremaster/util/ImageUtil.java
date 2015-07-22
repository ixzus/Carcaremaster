package com.lazycare.carcaremaster.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

/**
 * @author LiuSiQing
 * @Package com.mngwyhoucarcaremaster.util
 * @Title ImageUtil
 * @Description 图片工具类
 * @Time 2014年11月25日下午12:01:06
 */
public final class ImageUtil {
    public static final int UNCONSTRAINED = -1;

    /*
     * 获得设置信息
     */
    public static Options getOptions(String path) {
        Options options = new Options();
        options.inJustDecodeBounds = true;// 只描边，不读取数据
        BitmapFactory.decodeFile(path, options);
        return options;
    }

    /**
     * 获得图像 加载大图片工具类：解决android加载大图片时报OOM异常
     * 解决原理：先设置缩放选项，再读取缩放的图片数据到内存，规避了内存引起的OOM
     *
     * @param path
     * @param options
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap getBitmapByPath(String path, Options options,
                                         int screenWidth, int screenHeight) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        FileInputStream in = null;
        in = new FileInputStream(file);
        if (options != null) {
            Rect r = getScreenRegion(screenWidth, screenHeight);
            int w = r.width();
            int h = r.height();
            int maxSize = w > h ? w : h;
            int inSimpleSize = computeSampleSize(options, maxSize, w * h);
            options.inSampleSize = inSimpleSize; // 设置缩放比例
            options.inJustDecodeBounds = false;
        }
        Bitmap b = BitmapFactory.decodeStream(in, null, options);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 图片需要旋转的时候旋转图片
        int degree = ImageUtil.readPictureDegree(path);
        b = ImageUtil.rotaingImageView(degree, b);
        return b;
    }

    private static Rect getScreenRegion(int width, int height) {
        return new Rect(0, 0, width, height);
    }

    /**
     * 获取需要进行缩放的比例，即options.inSampleSize
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
                .ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
                .min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED)
                && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 旋转图片
     *
     * @param source
     * @return
     * @version:v1.0
     * @author:lanyj
     * @date:2014-7-8 上午11:58:22
     */
    public static Bitmap changeRoate(Bitmap source, boolean isHeadCamera) {
        int orientation = 90;
        Bitmap bMapRotate = null;
        if (source.getHeight() < source.getWidth()) {
            orientation = 90;
            if (isHeadCamera)
                orientation = -90;
        } else {
            orientation = 0;
        }
        if (orientation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            bMapRotate = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                    source.getHeight(), matrix, true);
        } else {
            return source;
        }
        return bMapRotate;
    }

    /**
     * 读取本地的图片得到缩略图，如图片需要旋转则旋转。
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getLocalThumbImg(String path, float width,
                                          float height, String imageType) {
        Options newOpts = new Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > width) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / width);
        } else if (w < h && h > height) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / height);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(path, newOpts);
        bitmap = compressImage(bitmap, 10, imageType);// 压缩好比例大小后再进行质量压缩
        int degree = readPictureDegree(path);
        bitmap = rotaingImageView(degree, bitmap);
        return bitmap;
    }

    /**
     * 图片质量压缩
     *
     * @param image
     * @return
     * @size 图片大小（kb）
     */
    public static Bitmap compressImage(Bitmap image, int size, String imageType) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (imageType.equalsIgnoreCase("png")) {
                image.compress(CompressFormat.PNG, 100, baos);
            } else {
                image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            }
            int options = 100;
            while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                if (imageType.equalsIgnoreCase("png")) {
                    image.compress(CompressFormat.PNG, options, baos);
                } else {
                    image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                }
                options -= 10;// 每次都减少10
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(
                    baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取图片路径
     */
    public static String getPicPathFromUri(Uri uri, Activity activity) {
        String value = uri.getPath();

        if (value.startsWith("/external")) {
            String[] proj = {Media.DATA};
            Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return value;
        }
    }

    /**
     * @param path      路径
     * @param photoName 名称
     * @return Jul 20, 2014 10:11:28 PM
     * @Title: getPhoto
     * @Description: 通过路径和名称得到图片
     */
    public static Bitmap getPhoto(String path, String photoName) {
        Bitmap photoBitmap;
        if (!path.endsWith("/"))
            photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName
                    + ".jpg");
        else
            photoBitmap = BitmapFactory.decodeFile(path + photoName + ".jpg");
        if (photoBitmap == null)
            return null;
        else
            return photoBitmap;
    }

    /**
     * @param filePath 路径
     * @return Jul 20, 2014 10:11:08 PM
     * @Title: getPhoto
     * @Description: 通过路径得到图片
     */
    public static Bitmap getPhoto(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    /**
     * 依据要得到的文件宽度来获得Bitmap文件
     *
     * @param filepath
     * @param filewidth
     * @return
     */
    public static Bitmap decodeBitmap(String filepath, int filewidth) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        // 通过这个bitmap获取图片的宽和高&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
        if (bitmap == null)
            System.out.println("bitmap为空");
        float realWidth = options.outWidth;
        float realHeight = options.outHeight;
        // 计算缩放&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / filewidth);
        if (scale <= 0)
            scale = 1;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        // 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的
        bitmap = BitmapFactory.decodeFile(filepath, options);
        return bitmap;
    }

    /**
     * @param filepath
     * @param filewidth
     * @return Jul 23, 2014 3:53:36 PM
     * @Title: decodeSmallBitmap
     * @Description: 得到bitmap
     */
    public static Bitmap decodeSmallBitmap(String filepath, int filewidth) {
        Options options = new Options();
        options.inJustDecodeBounds = true; // 通过这个bitmap获取图片的宽和高,现在的bitmap并不是实际存在的
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
        if (bitmap == null)
            Log.d("Exception", "bitmap为空");
        float realWidth = options.outWidth;
        float realHeight = options.outHeight;
        int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / filewidth); // 计算缩放
        // int scale = (int)realWidth / filewidth;
        if (scale <= 0)
            scale = 1;
        options.inSampleSize = scale;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        bitmap = BitmapFactory.decodeFile(filepath, options); // 注意这次要把options.inJustDecodeBounds设为false,这次图片是要读取出来的
        return bitmap;
    }

    /**
     * @param drawable
     * @return Sep 4, 2014 4:36:44 PM
     * @Title: drawableToBitmap
     * @Description: drawable转Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * @param bitmap
     * @return Sep 4, 2014 4:36:32 PM
     * @Title: bitmapToDrawable
     * @Description: bitmap转Drawable
     */
    @SuppressWarnings("deprecation")
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * @param inputStream
     * @return
     * @throws Exception Sep 4, 2014 3:58:56 PM
     * @Title: inputStreamToBitmap
     * @Description: inputStream转Bitmap
     */
    public static Bitmap inputStreamToBitmap(InputStream inputStream)
            throws Exception {
        return BitmapFactory.decodeStream(inputStream);
    }

    /**
     * @param byteArray
     * @return Sep 4, 2014 3:58:45 PM
     * @Title: byteToBitmap
     * @Description: byte转Bitmap
     */
    public static Bitmap byteToBitmap(byte[] byteArray) {
        if (null != byteArray && byteArray.length != 0)
            return BitmapFactory
                    .decodeByteArray(byteArray, 0, byteArray.length);
        else
            return null;
    }

    /**
     * @param byteArray
     * @return Sep 4, 2014 3:58:35 PM
     * @Title: byteToDrawable
     * @Description: byte转Drawable
     */
    public static Drawable byteToDrawable(byte[] byteArray) {
        ByteArrayInputStream ins = null;
        if (byteArray != null)
            ins = new ByteArrayInputStream(byteArray);
        return Drawable.createFromStream(ins, null);
    }

    /**
     * @param bm
     * @return Sep 4, 2014 3:58:21 PM
     * @Title: bitmapToBytes
     * @Description: bitmap转Bytes
     */
    public static byte[] bitmapToBytes(Bitmap bm) {
        byte[] bytes = null;
        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(CompressFormat.JPEG, 100, baos);
            bytes = baos.toByteArray();
        }
        return bytes;
    }

    /**
     * @param path
     * @return Jul 22, 2014 6:40:31 PM
     * @Title: bitmapToBytes
     * @Description: 通过path得到bitmap转Bytes
     */
    public static byte[] bitmapToBytes(String path) {
        byte[] bytes = null;
        Bitmap bitmap = getPhoto(path);
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 100, baos);
            bytes = baos.toByteArray();
        }
        return bytes;
    }

    /**
     * @param drawable
     * @return Sep 4, 2014 3:57:46 PM
     * @Title: drawableToBytes
     * @Description: Drawable转byte[]
     */
    public static byte[] drawableToBytes(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        byte[] bytes = bitmapToBytes(bitmap);
        ;
        return bytes;
    }

    /**
     * @param base64
     * @return
     * @throws IOException Sep 4, 2014 3:56:56 PM
     * @Title: base64ToBytes
     * @Description: 转成base64byte[]
     */
    public static byte[] base64ToBytes(String base64) throws IOException {
        return Base64.decode(base64);
    }

    /**
     * @param bytes
     * @return Sep 4, 2014 3:57:24 PM
     * @Title: bytesTobase64
     * @Description: 转成base64String
     */
//    public static String bytesTobase64(byte[] bytes) {
//        return Base64.encode(bytes);
//    }

    /**
     * @param bitmap
     * @param size
     * @return Jul 23, 2014 7:09:16 PM
     * @Title: getImageByte
     * @Description: 得到图片的byte[]
     */
    public static byte[] getImageByte(Bitmap bitmap, int size) {
        if (null == bitmap)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos); // 将图片按100%压缩为JPEG，并赋值给baos
        int options = 100 * 1024 * size / baos.toByteArray().length;
        if (options > 100)
            options = 100;
        if (baos.toByteArray().length / 1024 > size) {
            baos.reset();
            bitmap.compress(CompressFormat.JPEG, options, baos);
        }
        return baos.toByteArray();
    }

    /**
     * @param bitmap
     * @return Sep 4, 2014 3:55:19 PM
     * @Title: createReflectionImageWithOrigin
     * @Description: 创建反射视图
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
                h / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
                + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * @param bitmap
     * @param roundPx
     * @return Sep 4, 2014 3:42:25 PM
     * @Title: getRoundedCornerBitmap
     * @Description: 得到圆角图片
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        return getRoundedCornerBitmap(bitmap, roundPx, roundPx);
    }

    /**
     * @param bitmap
     * @param roundPx
     * @param roundPy
     * @return Sep 4, 2014 3:54:43 PM
     * @Title: getRoundedCornerBitmap
     * @Description: 得到圆角图片
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx,
                                                float roundPy) {
        if (null == bitmap)
            return bitmap;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPy, paint); // roundPx,roundPy如果分别为宽和高的1/2时为圆形
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * @param bitmap
     * @param width
     * @param height
     * @return Sep 4, 2014 3:42:43 PM
     * @Title: zoomBitmap
     * @Description: 调整bitmap的宽和高
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null) {
            return bitmap;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * @param
     * @param
     * @return Sep 9, 2014 4:34:42 PM
     * @Title: zoomDrawable
     * @Description: 按宽的比列调整图片
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width) {
        if (bitmap == null)
            return bitmap;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        if (scaleWidth > 1)
            scaleWidth = 1;
        matrix.postScale(scaleWidth, scaleWidth);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * @param drawable
     * @param w
     * @param h
     * @return Sep 4, 2014 3:43:07 PM
     * @Title: zoomDrawable
     * @Description: 调整Drawable的宽和高
     */
    @SuppressWarnings("deprecation")
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        matrix.postScale(sx, sy);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(newbmp);
    }

    /**
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     * @Title calculateInSampleSize
     * @Description 计算压缩比列
     */
    public static int calculateInSampleSize(Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据路径获得突破并按480*800压缩返回bitmap用于显示
     *
     * @param
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 根据路径获得突破并按480*800压缩返回bitmap用于显示
     *
     * @param
     * @return
     * @throws IOException
     */
    public static Bitmap getSmallBitmap(String filePath, Context context) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            final Options options = new Options();
            options.inJustDecodeBounds = true;
            if (filePath.indexOf(Config.CONTENT) != -1) {
                is = context.getContentResolver().openInputStream(
                        Uri.parse(filePath));
                BitmapFactory.decodeStream(is, null, options);
            } else
                BitmapFactory.decodeFile(filePath, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 480, 800);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            if (filePath.indexOf(Config.CONTENT) != -1) {
                bitmap = BitmapFactory.decodeStream(
                        context.getContentResolver().openInputStream(
                                Uri.parse(filePath)), null, options);
            } else
                bitmap = BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                Log.e("Exception", e.toString());
            }
        }
        return bitmap;
    }

    /**
     * @param path
     * @param photoName
     * @return Jul 20, 2014 8:19:26 PM
     * @Title: getShortPhotoFromSDCard
     * @Description: 得到压缩过的图片
     */
    public static Bitmap getShortPhotoFromSDCard(String path, String photoName) {
        String filePath = path + photoName + ".jpg";
        Options bitmapOptions = new Options();
        /* 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 */
        int degree = readPictureDegree(new File(filePath).getAbsolutePath());
        bitmapOptions.inJustDecodeBounds = true;
        // 将保存在本地的图片取出并缩小后显示在界面上
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bitmapOptions);
        int be = (int) (bitmapOptions.outHeight / (float) 195);
        if (be < 1)
            be = 1;
        bitmapOptions.inSampleSize = be;
        bitmapOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, bitmapOptions);
        return rotaingImageView(degree, bitmap);
    }

    /**
     * @param bitmap 图片
     * @param size   限制大小
     * @return Jul 22, 2014 11:19:17 AM
     * @Title: getPercentCompress
     * @Description: 按比例缩小
     */
    public static Bitmap getPercentCompress(Bitmap bitmap, int size) {
        if (null == bitmap)
            return bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos); // 将图片按100%压缩为JPEG，并赋值给baos
        int options = 100 * 1024 * size / baos.toByteArray().length;
        if (options > 100)
            options = 100;
        if (baos.toByteArray().length / 1024 > size) {
            baos.reset();
            bitmap.compress(CompressFormat.JPEG, options, baos);
        }
        bitmap.recycle();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray()); // 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(bais, null, null); // 把ByteArrayInputStream数据生成图片并返回
    }

    /**
     * @param bitmap 图片
     * @param size   限制大小
     * @return Jul 22, 2014 11:19:36 AM
     * @Title: getPercentCompressWhile
     * @Description: 按比例缩小
     */
    public static Bitmap getPercentCompressWhile(Bitmap bitmap, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos); // 将图片按100%压缩为JPEG，并赋值给baos
        int options = 100;
        int length = -1;
        while (baos.toByteArray().length / 1024 > size) {
            if (baos.toByteArray().length == length || options == 0)
                break; // 防止无限循环
            length = baos.toByteArray().length;
            baos.reset(); // 重置baos即清空baos
            options -= 30; // 每次都减少30
            if (options < 0)
                options = 0;
            bitmap.compress(CompressFormat.JPEG, options, baos); // 这里压缩options%，把压缩后的数据存放到baos中
        }
        bitmap.recycle();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray()); // 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(bais, null, null); // 把ByteArrayInputStream数据生成图片并返回
    }

    /**
     * 质量压缩图片
     *
     * @param
     * @return
     */
    public static byte[] compressImage(String srcPath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bt = null;
        try {
            Options newOpts = new Options();
            newOpts.inJustDecodeBounds = false;
            Bitmap image = BitmapFactory.decodeFile(srcPath, newOpts);
            if (image == null)
                return bt;
            image.compress(CompressFormat.JPEG, 100, baos); // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 150) {
                options -= 10;
                if (options <= 0) {
                    options = 0;
                    break;
                }
                baos.reset();
                image.compress(CompressFormat.JPEG, options, baos);
            }
            bt = baos.toByteArray();
            image.recycle();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != baos)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bt;
    }

    /**
     * 质量压缩图片
     *
     * @param
     * @return
     */
    public static byte[] compressImage(Bitmap bitmap) {
        if (null == bitmap)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bt = null;
        try {
            Options newOpts = new Options();
            newOpts.inJustDecodeBounds = false;
            bitmap.compress(CompressFormat.JPEG, 100, baos); // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 150) {
                options -= 15;
                if (options <= 0) {
                    options = 0;
                    break;
                }
                baos.reset();
                bitmap.compress(CompressFormat.JPEG, options, baos);
            }
            bt = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != baos)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bt;
    }

    /**
     * 图片旋转
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    /**
     * @param path
     * @return Sep 4, 2014 3:52:02 PM
     * @Title: readPictureDegree
     * @Description: 旋转图片
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * @return Sep 4, 2014 3:47:52 PM
     * @Title: checkSDCardAvailable
     * @Description: SD卡是否存在
     */
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * @return Aug 1, 2014 4:16:36 PM
     * @throws IOException
     * @Title: getBasePath
     * @Description: 得到文件路径
     */
    public static String getBasePath() {
        if (checkSDCardAvailable()) // 如果有sd卡
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/carcaremaster/images/";
        else
            return Environment.getRootDirectory().getAbsolutePath()
                    + "/carcaremaster/images/";
    }

    /**
     * @param end
     * @return Aug 1, 2014 4:16:27 PM
     * @Title: getPath
     * @Description: 得到文件路径
     */
    public static String getPath(String end) {
        if (null != end && end.endsWith("/"))
            return getBasePath() + end;
        else
            return getBasePath() + end + "/";
    }

    /**
     * Get image from SD card by path and the name of image
     *
     * @param
     * @return
     */
    public static boolean findPhotoFromSDCard(String path, String photoName) {
        boolean flag = false;
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (dir.exists()) {
                File folders = new File(path);
                File photoFile[] = folders.listFiles();
                for (int i = 0; i < photoFile.length; i++) {
                    String fileName = photoFile[i].getName().split("\\.")[0];
                    if (fileName.equals(photoName))
                        flag = true;
                }
            } else
                flag = false;
        } else
            flag = false;
        return flag;
    }

    /**
     * 保存至sd卡 Save image to the SD card
     *
     * @param photoBitmap
     * @param photoName
     * @param path
     */
    public static void savePhotoToSDCard(Bitmap photoBitmap, String path,
                                         String photoName, Context context) {
        if (checkSDCardAvailable())
            savePhoto(photoBitmap, path, photoName, context);
    }

    /**
     * @param photoBitmap
     * @param path
     * @param photoName
     * @param context     Aug 1, 2014 4:10:05 PM
     * @Title: savePhoto
     * @Description: 保存文件
     */
    public static boolean savePhoto(Bitmap photoBitmap, String path,
                                    String photoName, Context context) {
        File dir = new File(path);
        String url = path + photoName + ".jpg";
        if (!dir.exists())
            dir.mkdirs();
        File photoFile = new File(url);
        FileOutputStream fileOutputStream = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            photoBitmap.compress(CompressFormat.JPEG, 100, out);// 100%取出图片放入输出流中
            byte[] bytes = out.toByteArray();// 转换
            fileOutputStream = new FileOutputStream(photoFile);
            if (photoBitmap != null) {
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
            }
            sendIntent(context, url); // 刷新系统相册
            return true;
        } catch (Exception e) {
            photoFile.delete();
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (null != fileOutputStream)
                    fileOutputStream.close();
                photoBitmap.recycle(); // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of
                // memory异常
                System.gc();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param bitmap
     * @param path
     * @param photoName
     * @param context
     * @Title savePhotoZoom
     * @Description 缩小保存
     */
    public static void savePhotoZoom(Bitmap bitmap, String path,
                                     String photoName, Context context) {
        File dir = new File(path);
        String url = path + photoName + ".jpg";
        if (!dir.exists())
            dir.mkdirs();
        File photoFile = new File(url);
        FileOutputStream fileOutputStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            bitmap.compress(CompressFormat.JPEG, 100, out);// 100k
            int options = 100; // 100%
            while (out.toByteArray().length / 1024 > 150) {
                options -= 10;
                if (options <= 0) {
                    options = 0;
                    break;
                }
                out.reset();
                bitmap.compress(CompressFormat.JPEG, options, out);
            }
            byte[] bytes = out.toByteArray();// 转换
            fileOutputStream = new FileOutputStream(photoFile);
            if (bytes != null) {
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
            }
            sendIntent(context, url); // 刷新系统相册
        } catch (FileNotFoundException e) {
            photoFile.delete();
            e.printStackTrace();
        } catch (IOException e) {
            photoFile.delete();
            e.printStackTrace();
        } finally {
            try {
                if (null != fileOutputStream)
                    fileOutputStream.close();
                if (null != out)
                    out.reset();
                bitmap.recycle();
                System.gc();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param context
     * @param url     Sep 4, 2014 3:50:54 PM
     * @Title: sendIntent
     * @Description: 将url中的图片推送给图库
     */
    public static void sendIntent(Context context, String url) {
        sendIntent(context, Uri.fromFile(new File(url)));
    }

    /**
     * @param context
     * @param uri
     * @Title sendIntent
     * @Description 将uri中的图片推送给图库
     */
    public static void sendIntent(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * Delete the image from SD card 删除路径下所有文件
     *
     * @param
     * @param path    file:///sdcard/temp.jpg
     */
    public static void deleteAllPhoto(String path) {
        if (checkSDCardAvailable()) {
            File folder = new File(path);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++)
                files[i].delete();
        }
    }

    /**
     * @param path
     * @param fileName Jul 21, 2014 10:41:20 AM
     * @Title: deletePhotoAtPathAndName
     * @Description: 根据路径和文件名删除文件
     */
    public static void deletePhotoAtPathAndName(String path, String fileName) {
        if (checkSDCardAvailable()) {
            File folder = new File(path);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i].getName());
                if (files[i].getName().equals(fileName))
                    files[i].delete();
            }
        }
    }

    /**
     * @param path Jul 21, 2014 10:40:55 AM
     * @Title: deletePhotosAtPath
     * @Description: 删除文件或文件夹
     */
    public static void deletePhotosAtPath(String path) {
        File file = new File(path);
        if (file.exists())
            file.delete();
    }

    /**
     * @param activity
     * @param path
     * @return Sep 25, 2014 11:05:20 AM
     * @Title: saveImageFileName
     * @Description: 保存图片并返回文件名
     */
    public static String saveImageFileName(Activity activity, String path) {
        String filePath = path + "image.jpg";
        String fileName = DateUtil.getSysDate() + DateUtil.getSysTime();
        Options bitmapOptions = new Options();
        File file = new File(filePath);
        bitmapOptions.inJustDecodeBounds = false;
        int degree = ImageUtil.readPictureDegree(file.getAbsolutePath()); // 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bitmapOptions);
        int scale = (int) (bitmapOptions.outHeight / (float) 250);
        if (scale <= 0) {
            scale = 1;
        }
        bitmapOptions.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, bitmapOptions);
        boolean save_status = ImageUtil.savePhoto(
                ImageUtil.rotaingImageView(degree, bitmap), path, fileName,
                activity); // 先保存到sd卡上
        if (save_status)
            return fileName;
        else
            return null;
    }

    /**
     * @param activity
     * @param
     * @return Sep 25, 2014 11:08:57 AM
     * @Title: getFilePath
     * @Description: 获得文件路径
     */
    public static String getFilePath(Activity activity, Uri uri) {
        Cursor cursor = activity.getContentResolver()
                .query(uri, new String[]{Media.DATA},
                        null, null, null);
        String path = null;
        if (cursor.moveToFirst())
            path = cursor.getString(cursor
                    .getColumnIndexOrThrow(Media.DATA));
        cursor.close();
        return path;
    }

    /**
     * @param activity
     * @return
     * @Title getFilePath
     * @Description 获得最新的地址
     */
    public static String getFilePath(Activity activity) {
        Cursor cursor = activity.getContentResolver().query(
                Media.EXTERNAL_CONTENT_URI, null, null, null,
                Media.DATE_MODIFIED + " desc");
        if (null == cursor)
            return null;
        if (cursor.moveToNext())
            return cursor.getString(cursor.getColumnIndex(Media.DATA)); // _data：文件的绝对路径
        // Media.DATA='_data'
        return null;
    }

    /**
     * @param url
     * @return
     * @throws Exception Sep 11, 2014 10:28:23 AM
     * @Title: getPhoneInternet
     * @Description: 获取网络图片，需要上网权限
     */
    public static Bitmap getPhoneInternet(String url) throws Exception {
        return inputStreamToBitmap(new URL(url).openStream());
    }

}
