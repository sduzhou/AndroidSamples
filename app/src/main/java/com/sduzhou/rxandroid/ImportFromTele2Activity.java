package com.sduzhou.rxandroid;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import sduzhou.com.samples.R;

public class ImportFromTele2Activity extends AppCompatActivity {

    @BindView(R.id.web_view)
    WebView webView;

    private String initUrl = "http://login.189.cn/web/login";
    private String home189Url = "http://www.189.cn/dqmh/my189/initMy189home.do";
    private static String eInvoiceHomeUrl = "";
    private MyHandler handler;

    private boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_from_qq);
        handler = new MyHandler();
        initViews();
        initEvents();
    }

    protected void initViews() {
        ButterKnife.bind(this);
    }

    protected void initEvents() {
        removeAllCookie();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "callback");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.loadUrl(initUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.i("OverrideUrlLoading url=", url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                handlecss(url);
                Log.i("onPageFinished url=", url);
                CookieSyncManager.getInstance().sync();
                String cookie = CookieManager.getInstance().getCookie(url);
                if (cookie != null) {
                    Log.i("onPageFinished cookie=", cookie);
                    if(cookie.contains("isLogin=logined") && !isFinished){
                        isFinished = true;
                        view.loadUrl(home189Url);
                    }
                }
                if(url.equals(home189Url)){
                    webView.loadUrl("javascript:var einv = document.getElementById('20000566');");
                    webView.loadUrl("javascript:window.callback.processHTML(einv.getAttribute('href'));");
                    webView.loadUrl("javascript:einv.click();");
                    handler.sendEmptyMessageDelayed(100,1000);
                }
            }


            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && Uri.parse(url).getScheme() != null) {
                    String scheme = Uri.parse(url).getScheme().trim();

                    if ((scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) && !getMimeType(url)) {
                        Log.e("ajax url2",url);
                        WebResourceResponse response = super.shouldInterceptRequest(view, url);
                        if(response != null){
                            Log.e("ajax url2 reponse",response.getResponseHeaders().toString());
                        }

                        return response;
                    }
                }

                return super.shouldInterceptRequest(view, url);
            }


        });
    }

    public static boolean getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if(type != null){
                Log.e("type",type);
                return type.contains("js") || type.contains("css") || type.contains("image") ;
            }
        }
        return false;
    }



    private void removeAllCookie() {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(webView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();

        cookieManager.removeAllCookie();
        cookieSyncManager.sync();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }

            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();

            try {
                webView.destroy();
            } catch (Throwable ex) {

            }
        }
        super.onDestroy();
    }

    private void handlecss(String url)
    {
        if(TextUtils.equals(initUrl,url))
            webView.loadUrl("javascript:"
                            +"document.getElementsByClassName('imgBox')[0].style.display='none'; "
                             +"document.getElementById('divMain').style.width = '450px'; "
                            +"document.getElementById('divMain').style.marginLeft = 0; "
                            +"document.getElementById('divNormalLogin').style.marginLeft = 0; "
            );
    }

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        public void processHTML(String html)
        {
            Log.e("callback",html);
            String subs[] = html.split("toStUrl=");
            if(subs != null && subs.length > 1){
                eInvoiceHomeUrl = subs[1].substring(0,subs[1].indexOf("/iframe"));
                System.out.println(eInvoiceHomeUrl);
            }

        }
    }

    static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String cookie = CookieManager.getInstance().getCookie(eInvoiceHomeUrl);
            if(cookie.contains("JSESSIONID_") && cookie.contains("WT_FPC")){
                Log.e("final",cookie);
            }else{
                Log.e("not final",cookie);
                sendEmptyMessageDelayed(100,1000);
            }
        }
    }
}

