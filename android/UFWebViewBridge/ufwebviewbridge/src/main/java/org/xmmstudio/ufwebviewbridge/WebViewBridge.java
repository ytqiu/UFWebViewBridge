package org.xmmstudio.ufwebviewbridge;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuekong on 2016/11/28.
 */

public class WebViewBridge {
    private static Map<String, WebViewBridgeApiHandler> apiHandlers = new HashMap<>();
    private int jsApiCallId;
    private Map<Integer, WebViewBridgeJSApiCallback> callbacks = new HashMap<>();
    private WebView webView;

    public WebViewBridge(final WebView webView) {
        this.webView = webView;

        webView.addJavascriptInterface(this, "apis");


    }

    private static String getJSBridgeAsset(Context context) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("jsbridge.js")));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null && !line.matches("^\\s*\\/\\/.*")) {
                builder.append(line);
            }

            return builder.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void registerApiHandler(String api, WebViewBridgeApiHandler apiHandler) {
        if (api != null && apiHandler != null && api.trim().length() > 0) {
            apiHandlers.put(api, apiHandler);
        }
    }

    public void callJSApi(final String jsapi, final Object param, final WebViewBridgeJSApiCallback callback) {
        Log.e("testtest", "callJSApi: " + jsapi);
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(String.format("window.bridge.nativeCall('%s', %s)", jsapi, new Gson().toJson(param)), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            if (callback != null) {
                                callback.done(new Gson().fromJson(s, JsonElement.class));
                            }
                        }
                    });
                } else {
                    String jsCallStr = String.format("window.bridge.nativeCall('%s', %s)", jsapi, new Gson().toJson(param));
                    if (callback == null) {
                        webView.loadUrl("javascript:" + jsCallStr);
                    } else {
                        int callId = ++jsApiCallId;
                        callbacks.put(callId, callback);
                        webView.loadUrl(String.format("javascript:apis.jsCallReturn(%d, window.bridge.toNativeParam(%s))", callId, jsCallStr));
                    }
                }
            }
        }, 0);
    }

    public void callJSCallback(long callId, Object param) {
        callJSApi("callback:" + callId, param, null);
    }

    @JavascriptInterface
    public void postMessage(String message) {
        Log.e("testtest", "js post message: " + message);
        JsonElement element = new Gson().fromJson(message, JsonElement.class);
        if (!element.isJsonObject()) {
            return;
        }

        JsonElement apiElem = element.getAsJsonObject().get("api");
        JsonElement callIdElem = element.getAsJsonObject().get("callId");
        JsonElement paramsElem = element.getAsJsonObject().get("params");
        if (apiElem == null || apiElem.isJsonNull() || !apiElem.isJsonPrimitive()
                || paramsElem == null || paramsElem.isJsonNull()) {
            return;
        }

        String api = apiElem.getAsString();
        WebViewBridgeApiHandler apiHandler = apiHandlers.get(api);
        if (apiHandler == null) {
            return;
        }

        Object result = apiHandler.call(paramsElem);
        if (callIdElem != null && !callIdElem.isJsonNull() && callIdElem.isJsonPrimitive()) {
            callJSCallback(callIdElem.getAsLong(), result);
        }
    }

    @JavascriptInterface
    public void jsCallReturn(int callId, String result) {
        JsonElement resultElem = new Gson().fromJson(result, JsonElement.class);
        Log.e("testtest", "jsCallReturn: " + resultElem);
        if (callbacks.containsKey(callId)) {
            WebViewBridgeJSApiCallback jsApiCallback = callbacks.get(callId);
            jsApiCallback.done(resultElem);

            callbacks.remove(jsApiCallback);
        }
    }

    public interface WebViewBridgeApiHandler {
        Object call(JsonElement paramElem);
    }

    public interface WebViewBridgeJSApiCallback {
        void done(JsonElement paramElem);
    }

    public static class WebViewBridgeClient extends WebViewClient {
        @Override
        public void onPageFinished(final WebView view, String url) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        view.evaluateJavascript(WebViewBridge.getJSBridgeAsset(view.getContext()), null);
                    } else {
                        view.loadUrl("javascript:" + WebViewBridge.getJSBridgeAsset(view.getContext()));
                    }
                }
            });
        }
    }
}
