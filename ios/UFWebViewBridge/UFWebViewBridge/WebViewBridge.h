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
typedef void(^WebViewNativeAPI)(id params, WebViewNativeAPIReturnBlock returnBlock);
typedef void(^WebViewBridgeSetupBlock)(NSMutableDictionary *apis);
typedef void(^WebViewJSAPICallback)(id result, NSError *error);

@interface WKWebView (WebViewBridge)

- (void)bridge_setup:(WebViewBridgeSetupBlock)setupBlock;

- (void)bridge_callJSAPI:(NSString *)jsapi params:(id)params callback:(WebViewJSAPICallback)callback;

@end
