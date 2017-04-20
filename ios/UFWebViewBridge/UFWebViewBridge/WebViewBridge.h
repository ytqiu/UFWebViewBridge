//
//  WebViewBridge.h
//  TestWKWebView
//
//  Created by yuekong on 2016/11/25.
//  Copyright © 2016年 org.xmmstudio. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>

typedef void(^WebViewNativeAPIReturnBlock)(id result);
typedef void(^WebViewNativeDefaultAPI)(WKWebView *webView, NSString *api, id params, NSUInteger callId);
typedef void(^WebViewNativeAPI)(WKWebView *webView, id params, WebViewNativeAPIReturnBlock returnBlock);
typedef void(^WebViewBridgeSetupBlock)(NSMutableDictionary *apis);
typedef void(^WebViewJSAPICallback)(id result, NSError *error);

@interface WKWebView (WebViewBridge)

- (void)bridge_injectJS:(NSString *)jsCode;

- (void)bridge_setup:(WebViewBridgeSetupBlock)setupBlock;

- (void)bridge_registerNativeDefaultAPI:(WebViewNativeDefaultAPI)defaultAPI;

- (void)bridge_registerNativeAPI:(WebViewNativeAPI)nativeAPI forName:(NSString *)api;

- (void)bridge_registerJSPlugin:(NSString *)plugin;

- (void)bridge_executeJSCallback:(NSUInteger)callId params:(id)params;

- (void)bridge_callJSAPI:(NSString *)jsapi params:(id)params callback:(WebViewJSAPICallback)callback;

@end
