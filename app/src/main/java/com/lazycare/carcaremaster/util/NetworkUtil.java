package com.lazycare.carcaremaster.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.lazycare.carcaremaster.data.Attachments;

/**
 * Created by Administrator on 13-11-26.
 */
public class NetworkUtil {
    /* 测试环境 */
//    public static String IP = "test.chudongyangche.cn";
    /* 开发环境地址 */
//    public static String IP = "development.chudongyangche.com";
    /* 生产环境地址 */
	 public static String IP = "121.43.235.158/chudongyangche";

    public static String WSDL_URL = "http://" + IP + "/api/index.php";
    /* menu图片显示的地址 */
    public static String WSDL_IMG_URL = "http://" + IP + "/Static/";
    public static String MAIN_UPLOAD = "http://" + IP + "/Uploads/questions";

    /* 测试机 */
    public static final String WXZJ_URL = "http://www.962121.net/hmfmstest/wyfeemp/wyfeemp/commumication/billmsg";
    // 需要可供升级的软件放的位置及相关变量
    public static final String UPDATE_SERVER = WSDL_URL + "/apkVerCode/";
    public static final String UPDATE_VER = "lazycare.json";
    public static final String UPDATE_APKNAME = "lazycare.apk";
    public static final String UPDATE_SAVENAME = "lazycare.apk";

    // 验证码相关接口
    public static String accountSid = "aaf98f894a188342014a237b6ae005c3";
    public static String authToken = "81c9f710c09b4363b9168575c41f1661";
    public static String appid = "8a48b5514da42dc3014dacc64631042e";
    public static String templateid = "21456";
    // 验证手机号码是否有效
    public static String phone = "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm";

    /**
     * 判断网络是否可用
     *
     * @param
   0  * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取网址内容
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String getContent(String url) throws Exception {
        StringBuilder sb = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpParams httpParams = client.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000); // 设置网络超时参数
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        HttpResponse response = client.execute(new HttpGet(url));
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    entity.getContent(), "GBK"), 8192);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
        }
        return sb.toString();
    }

    /**
     * 网络连接
     ********************************************************************************************************/
    public static final int REGISTRATION_TIMEOUT = 15 * 1000;
    private static HttpClient mHttpClient = null;

    /**
     * @param
     * @Title create
     * @Description 创建HTTP
     */
    public static MultipartEntity create() {
        createHttp();
        return new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null,
                Charset.forName("GBK"));
    }

    /**
     * @Title createHttp
     * @Description 创建HTTP
     */
    public static void createHttp() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
                    Charset.forName("GBK"));
            HttpConnectionParams.setConnectionTimeout(params,
                    REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
        }
    }

    /**
     * @param @param  map
     * @param @throws UnsupportedEncodingException
     * @Title put
     * @Description 放入Map<String,String> 参数
     */
    public static MultipartEntity put(MultipartEntity entity,
                                      Map<String, String> map) throws UnsupportedEncodingException {
        if (ObjectUtil.isEmpty(map) || null == entity)
            return entity;
        Log.d("PARAM", "parm:" + map.entrySet().toString());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            StringBody par = new StringBody(
                    ObjectUtil.isEmpty(entry.getValue()) ? ""
                            : entry.getValue(), Charset.forName("GBK"));
            entity.addPart(entry.getKey(), par);
        }
        return entity;
    }

    /**
     * @param @param files
     * @throws IOException
     * @throws FileNotFoundException
     * @Title put
     * @Description 放入图片地址
     */
    public static MultipartEntity put(MultipartEntity entity, Context context,
                                      List<String> files) throws FileNotFoundException, IOException {
        if (ObjectUtil.isEmpty(files) || null == entity)
            return entity;
        for (int i = 0; i < files.size(); i++) {
            byte[] bt = ImageUtil.compressImage(ImageUtil.getSmallBitmap(
                    files.get(i), context));
            if (null == bt)
                continue;
            ByteArrayBody ib = new ByteArrayBody(bt, files.get(i));
            entity.addPart("file" + i, ib);
        }
        return entity;
    }

    /**
     * 将语音文件传位字节数组
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] getAudioContent(String filePath) throws IOException {
        File file = new File(filePath);

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }

    /**
     * @param name
     * @param body
     * @Title putContentBody
     * @Description 放入body
     */
    public static MultipartEntity putContentBody(MultipartEntity entity,
                                                 String name, ContentBody body) {
        entity.addPart(name, body);
        return entity;
    }

    /**
     * @param @param files
     * @throws IOException
     * @throws FileNotFoundException
     * @Title putAttachements
     * @Description 放入图片对象
     */
    public static MultipartEntity putAttachements(MultipartEntity entity,
                                                  Context context, List<Attachments> files)
            throws FileNotFoundException, IOException {
        if (ObjectUtil.isEmpty(files) || null == entity)
            return entity;
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getFiletype().equals(Config.FILE_IMG_TYPE)) {
                byte[] bt = ImageUtil.compressImage(ImageUtil.getSmallBitmap(
                        files.get(i).getFile_path(), context));
                if (null == bt)
                    continue;
                ContentBody ib = new ByteArrayBody(bt, files.get(i)
                        .getFile_path());
                entity.addPart("img" + i, ib);
            } else if (files.get(i).getFiletype()
                    .equals(Config.FILE_AUDIO_TYPE)) {
                byte[] bt = getAudioContent(files.get(i).getFile_path());
                if (null == bt)
                    continue;
                ContentBody ib = new ByteArrayBody(bt, files.get(i)
                        .getFile_path());
                entity.addPart("audio" + i, ib);
            }
        }
        return entity;
    }

    // public static MultipartEntity putAttMap(MultipartEntity entity, Context
    // context, List<Attachments> files,
    // Map<String, byte[]> map) throws FileNotFoundException, IOException {
    // if( ObjectUtil.isEmpty(files) || null == map || null == entity )
    // return entity;
    // for(int i = 0; i < files.size(); i++) {
    // String path = files.get(i).getFile_path();
    // byte[] temp = map.get(path);
    // if( null == temp )
    // temp = ImageUtil.compressImage(ImageUtil.getSmallBitmap(path, context));
    // if( null == temp )
    // continue;
    // ContentBody ib = new ByteArrayBody(temp, path);
    // entity.addPart("file" + i, ib);
    // }
    // return entity;
    // }

    /**
     * @param @param  map
     * @param @param  files
     * @param @throws UnsupportedEncodingException
     * @throws IOException
     * @throws FileNotFoundException
     * @Title put
     * @Description 放入数据和图片信息
     */
    public static MultipartEntity put(MultipartEntity entity, Context context,
                                      Map<String, String> map, List<String> files)
            throws FileNotFoundException, IOException {
        entity = put(entity, map);
        return put(entity, context, files);
    }

    /**
     * @param @param  uri
     * @param @return
     * @Title post
     * @Description 与后台连接
     */
    public static String post(MultipartEntity entity, String uri) {
        return post(entity, uri, WSDL_URL);
    }

    public static String post(MultipartEntity entity, String uri, String http) {
        try {
            HttpPost httpost = new HttpPost(http + uri);
            Log.d("PARAM", "URL:" + http + uri);
            httpost.setEntity(entity);
            HttpResponse response = mHttpClient.execute(httpost);
            if (null != response
                    && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            else
                return ErrorUtil.ERROR + ErrorUtil.NETWORK_UNCONNECT;
        } catch (Exception e) {
            Log.e("Exception", e.toString());
            return ErrorUtil.getMessage(ErrorUtil.NETWORK_UNCONNECT);
        }
    }

    public static String post2(Map<String, String> entity, String uri) {
        return post2(entity, uri, WSDL_URL);
    }

    public static String post2(Map<String, String> entity, String uri, String http) {
        String mURL = http + uri + "?";
        try {
            for (Map.Entry<String, String> entry : entity.entrySet()) {
                mURL += entry.getKey() + "=" + entry.getValue() + "&";
            }
            mURL.substring(0, mURL.length() - 2);
            HttpGet httpost = new HttpGet(mURL);
            Log.d("PARAM", "URL:" + mURL);
            HttpResponse response = mHttpClient.execute(httpost);
            if (null != response
                    && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            else
                return ErrorUtil.ERROR + ErrorUtil.NETWORK_UNCONNECT;
        } catch (Exception e) {
            Log.e("Exception", e.toString());
            return ErrorUtil.getMessage(ErrorUtil.NETWORK_UNCONNECT);
        }
    }
    /*************************************************************************************************/
    /** 调用接口 */
    /**
     * @return
     * @Title interfaceCreateHttp
     * @Description 创建HTTP
     */
    public static List<NameValuePair> interfaceCreateHttp() {
        createHttp();
        return new ArrayList<NameValuePair>();
    }

    /**
     * @return
     * @Title interfacePut
     * @Description 放入数据
     */
    public static List<NameValuePair> interfacePut(List<NameValuePair> list,
                                                   Map<String, String> map) {
        if (ObjectUtil.isEmpty(map))
            return list;
        Log.d("PARAM", "parm::" + map.entrySet().toString());
        for (Map.Entry<String, String> entry : map.entrySet())
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        return list;
    }

    /**
     * @return
     * @Title interfacePutList
     * @Description 放入数据
     */
    public static List<NameValuePair> interfacePutList(
            List<NameValuePair> list, Map<String, List<String>> map) {
        if (ObjectUtil.isEmpty(map))
            return list;
        Log.d("PARAM", "parm::" + map.entrySet().toString());
        for (Map.Entry<String, List<String>> entry : map.entrySet())
            for (String value : entry.getValue())
                list.add(new BasicNameValuePair(entry.getKey(), value));
        return list;
    }

    /**
     * @return
     * @Title interfacePost
     * @Description 提交接口
     */
    public static String interfacePost(List<NameValuePair> list, String uri,
                                       String http) {
        try {
            HttpPost httpost = new HttpPost(http + uri);
            Log.d("PARAM", "URL:" + http + uri);
            httpost.setHeader("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            if (!ObjectUtil.isEmpty(list))
                httpost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
            HttpResponse response = mHttpClient.execute(httpost);
            if (null != response
                    && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                return EntityUtils.toString(response.getEntity());
            } else
                return ErrorUtil.ERROR + ErrorUtil.NETWORK_UNCONNECT;
        } catch (Exception e) {
            Log.e("Exception", e.toString());
            return ErrorUtil.getMessage(ErrorUtil.NETWORK_UNCONNECT);
        }
    }

    /**
     * @return
     * @Title interfacePost
     * @Description 提交接口
     */
    public static String interfacePost(List<NameValuePair> list, String uri) {
        return interfacePost(list, uri, WXZJ_URL);
    }
}
