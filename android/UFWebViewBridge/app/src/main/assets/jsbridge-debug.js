(function () {
    if (window.bridge != null) {
        return
    }
    var nativeCallId = 0;
    var jsapiFuncs = {};
    var userAgent = navigator.userAgent || navigator.vendor || window.opera;
    var isAndroid = /android/i.test(userAgent);
    var isIOS = /iPad|iPhone|iPod/i.test(userAgent) && !window.MSStream;
    var nativeApis = isAndroid ? apis : (isIOS ? window.webkit.messageHandlers.apis : null);
    window.bridge = {
        toNativeParam: function (param) {
            return isIOS ? param : (isAndroid ? JSON.stringify(param) : null)
        },
        nativeCall: function (jsapi, params) {
            console.log('......jsbridge native call: ' + jsapi + " - " + JSON.stringify(params));
            var func = jsapiFuncs[jsapi];
            if (jsapi.indexOf('callback:') == 0) {
                jsapiFuncs[jsapi] = null
            }
            if (func != null) {
                return func(params)
            }
        },
        registerJSApi: function (jsapi, func) {
            jsapiFuncs[jsapi] = func
        },
        callNative: function (api, params, callback) {
            if (callback != null) {
                nativeCallId = nativeCallId + 1;
                jsapiFuncs['callback:' + nativeCallId] = callback;
                nativeApis.postMessage(this.toNativeParam({api: api, params: params, callId: nativeCallId}))
            } else {
                nativeApis.postMessage(this.toNativeParam({api: api, params: params}))
            }
        },
        isNativeApiExist: function (api, callback) {
            this.callNative('checkApi', api, callback)
        },
        log: function (message) {
            this.callNative('log', message)
        },
    }
})();