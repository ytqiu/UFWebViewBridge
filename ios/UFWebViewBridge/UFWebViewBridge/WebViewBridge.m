//
//  WebViewBridge.m
//  TestWKWebView
//
//  Created by yuekong on 2016/11/25.
//  Copyright © 2016年 org.xmmstudio. All rights reserved.
//

#import "WebViewBridge.h"
#import <objc/runtime.h>

@interface WKWebView (WebViewBridgeMessageHandler) <WKScriptMessageHandler>
@end

@implementation WKWebView (WebViewBridge)

- (void)bridge_setup:(WebViewBridgeSetupBlock)setupBlock {
    [self.configuration.userContentController addScriptMessageHandler:self name:@"apis"];
    [self.configuration.userContentController addUserScript:[[WKUserScript alloc] initWithSource:[[NSString alloc] initWithData:[NSData dataWithContentsOfURL:[[NSBundle bundleWithPath:[[NSBundle mainBundle] pathForResource:@"ufwebviewbridge" ofType:@"bundle"]] URLForResource:@"jsbridge" withExtension:@"js"]] encoding:NSUTF8StringEncoding] injectionTime:WKUserScriptInjectionTimeAtDocumentStart forMainFrameOnly:NO]];
    
    __weak id wself = self;
    [self __bridge_apis][@"log"] = ^(WKWebView *webView, NSString *message, WebViewNativeAPIReturnBlock returnBlock) {
        NSLog(@"web.bridge.log: %@", message);
    };
    [self __bridge_apis][@"checkApi"] = ^(WKWebView *webView, NSString *api, WebViewNativeAPIReturnBlock returnBlock) {
        returnBlock(@([wself __bridge_apiExist:api]));
    };
    
    !setupBlock ?: setupBlock([self __bridge_apis]);
}

- (void)bridge_reigsterNativeAPI:(WebViewNativeAPI)nativeAPI forName:(NSString *)api {
    if (api.length > 0 && nativeAPI) {
        [self __bridge_apis][api] = nativeAPI;
    }
}

- (NSMutableDictionary *)__bridge_apis {
    NSMutableDictionary *apis = objc_getAssociatedObject(self, "__bridge_apis");
    if (!apis) {
        apis = [[NSMutableDictionary alloc] init];
        objc_setAssociatedObject(self, "__bridge_apis", apis, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    
    return apis;
}

- (BOOL)__bridge_apiExist:(NSString *)api {
    return !![self __bridge_apis][@"api"];
}

- (void)bridge_callJSAPI:(NSString *)jsapi params:(id)params callback:(WebViewJSAPICallback)callback {
    [self evaluateJavaScript:[NSString stringWithFormat:@"window.bridge.nativeCall('%@', %@)", jsapi, [self __bridge_toJSObject:params]] completionHandler:callback];
}

- (void)__bridge_executeJSCallback:(NSUInteger)callId params:(NSDictionary *)params {
    [self bridge_callJSAPI:[NSString stringWithFormat:@"callback:%lu", callId] params:params callback:nil];
}

- (id)__bridge_toJSObject:(id)params {
    if (!params) {
        return @"null";
    }
    
    if ([params isKindOfClass:[NSString class]]) {
        return [NSString stringWithFormat:@"'%@'", params];
    }
    
    if ([params isKindOfClass:[NSArray class]] || [params isKindOfClass:[NSDictionary class]]) {
        return [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:params options:0 error:nil] encoding:NSUTF8StringEncoding];
    }
    
    return params;
}

- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    NSDictionary *body = message.body;
    if (![body isKindOfClass:[NSDictionary class]]) {
        return;
    }
    
    NSString *api = body[@"api"];
    NSUInteger callId = [body[@"callId"] unsignedIntegerValue];
    id params = body[@"params"];
    if (api.length <= 0) {
        return;
    }
    
    WebViewNativeAPI nativeAPI = [self __bridge_apis][api];
    if (nativeAPI) {
        __weak id wself = self;
        nativeAPI(message.webView, params, ^(id result) {
            if (callId > 0) {
                [wself __bridge_executeJSCallback:callId params:result];
            }
        });
    }
}

@end
