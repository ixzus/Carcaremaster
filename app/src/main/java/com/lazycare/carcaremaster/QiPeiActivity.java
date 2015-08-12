package com.lazycare.carcaremaster;

import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lazycare.carcaremaster.util.NetworkUtil;

/**
 * 源泰收汽配跳转界面
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QiPeiActivity extends BaseActivity {
    private WebView webview;
    private ProgressBar progressbar;

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_qipei);
    }

    @Override
    public void setActionBarOption() {
        // ActionBar bar = getSupportActionBar();
        // bar.hide();
    }

    @Override
    public void initView() {
        webview = (WebView) findViewById(R.id.qp_webview);
        progressbar = new ProgressBar(mContext, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                3));
        webview.addView(progressbar);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        // webview.setScrollBarStyle(0);
        // webview.setScrollBarStyle(33554432);
        webview.setHorizontalScrollBarEnabled(false);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.setInitialScale(70);
        webview.requestFocus();
        webview.setHorizontalScrollbarOverlay(true);
        if (NetworkUtil.isNetworkAvailable(mContext))
            webview.loadUrl("http://wx.sooqp.com");

//            webview.loadUrl("http://www.ssei.cn/TzsbZyAzProjectWEB/pages/weixin/index_ywdt.jsp");
        else {
            webview.loadUrl("file:///android_asset/network_warning.htm");
        }
        webview.setWebViewClient(new WebViewClient() {
            public void onLoadResource(WebView paramWebView, String paramString) {
                super.onLoadResource(paramWebView, paramString);
            }

            public void onPageFinished(WebView paramWebView, String paramString) {
                super.onPageFinished(paramWebView, paramString);
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                progressbar.setVisibility(View.VISIBLE);

            }

            // if ((paramWebView.canGoBack())
            // || (Main.this.back.getVisibility() != 0))
            // return;
            // Main.this.back.setVisibility(8);

            public void onReceivedError(WebView paramWebView, int paramInt,
                                        String paramString1, String paramString2) {
                super.onReceivedError(paramWebView, paramInt, paramString1,
                        paramString2);
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    paramWebView
                            .loadUrl("file:///android_asset/network_warning.htm");
                    progressbar.setVisibility(View.GONE);
                }
            }

            public boolean shouldOverrideUrlLoading(WebView paramWebView,
                                                    String paramString) {
                paramWebView.loadUrl(paramString);
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                } else {
                    if (progressbar.getVisibility() == View.GONE)
                        progressbar.setVisibility(View.VISIBLE);
                    progressbar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            webview.goBack();
            return true;
        } else
            finish();
        return false;
    }
}
