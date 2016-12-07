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
typedef void(^WebViewNativeAPI)(WKWebView *webView, id params, WebViewNativeAPIReturnBlock returnBlock);
typedef void(^WebViewBridgeSetupBlock)(NSMutableDictionary *apis);
typedef void(^WebViewJSAPICallback)(id result, NSError *error);

@interface WKWebView (WebViewBridge)

- (void)bridge_injectJS:(NSString *)jsCode;

- (void)bridge_setup:(WebViewBridgeSetupBlock)setupBlock;

- (void)bridge_reigsterNativeAPI:(WebViewNativeAPI)nativeAPI forName:(NSString *)api;

- (void)bridge_registerJSPlugin:(NSString *)plugin;

- (void)bridge_callJSAPI:(NSString *)jsapi params:(id)params callback:(WebViewJSAPICallback)callback;

@end
