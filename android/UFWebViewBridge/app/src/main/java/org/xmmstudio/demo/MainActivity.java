package org.xmmstudio.demo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.xmmstudio.ufwebviewbridge.WebViewBridge;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        final WebViewBridge webViewBridge = new WebViewBridge(webView);
        webViewBridge.registerApiHandler("log", new WebViewBridge.WebViewBridgeApiHandler() {
            @Override
            public Object call(JsonElement paramElem) {
                Log.e("testtest", "web bridge log-: " + paramElem.getAsString());
                return null;
            }
        });
        webViewBridge.registerApiHandler("hello", new WebViewBridge.WebViewBridgeApiHandler() {
            @Override
            public Object call(JsonElement paramElem) {
                Log.e("testtest", "hello: " + new Gson().toJson(paramElem));
                return paramElem;
            }
        });

//        webView.loadUrl("file:///android_asset/index.html");
        webView.loadUrl("http://192.168.1.140:3000/index.html");
        webView.setWebViewClient(new WebViewBridge.WebViewBridgeClient() {

        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("testtest", "on refresh");
//                webView.reload();
                swipeRefreshLayout.setRefreshing(false);

//                webView.loadUrl("javascript:<script>console.log('aaaa')</script>");
//                webView.loadDataWithBaseURL(null, "<script>console.log('aaaa')</script>", "text/html", "utf-8", null);

                List params = new ArrayList();
                params.add(1.0f);
                params.add(2);
                params.add(1.2f);
                webViewBridge.callJSApi("abc", params, new WebViewBridge.WebViewBridgeJSApiCallback() {
                    @Override
                    public void done(JsonElement paramElem) {
                        Log.e("testtest", "abc callback: " + paramElem);
                    }
                });

//                webView.loadUrl("javascript:console.log('hello -refresh')");
//                webView.loadUrl("javascript:" + webViewBridge.getJSBridgeAsset());
            }
        });

//        webView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("testtest", "start call jsapi");
////                webView.loadUrl("javascript:window.bridge.nativeCall('abc', 'hello world')");
//                webViewBridge.callJSApi("abc", "hallo--abc", new WebViewBridge.WebViewBridgeJSApiCallback() {
//                    @Override
//                    public void done(JsonElement paramElem) {
//                        Log.e("testtest", "abc callback: " + paramElem);
//                    }
//                });
//            }
//        }, 2000);
    }
}
