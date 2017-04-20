//
//  ViewController.m
//  Demo
//
//  Created by yuekong on 2016/11/28.
//  Copyright © 2016年 org.xmmstudio. All rights reserved.
//

#import "ViewController.h"
#import <UFWebViewBridge/UFWebViewBridge.h>
#import <WebKit/WebKit.h>
#import <MJRefresh/MJRefresh.h>

@interface ViewController ()
@property (nonatomic, strong) WKWebView *webView;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.webView = [[WKWebView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    [self.webView bridge_setup:^(NSMutableDictionary *apis) {
//        apis[@"hello"] = ^(WKWebView *webView, NSDictionary *params, WebViewNativeAPIReturnBlock returnBlock) {
//            NSLog(@"web bridge hello called....: %@", params);
//            returnBlock(params);
//        };
    }];
    [self.webView bridge_reigsterNativeAPI:^(WKWebView *webView, NSDictionary *params, WebViewNativeAPIReturnBlock returnBlock) {
        NSLog(@"web bridge hello called....: %@", params);
        returnBlock(params);
    } forName:@"hello"];
    
    self.webView.scrollView.mj_header = [MJRefreshHeader headerWithRefreshingBlock:^{
        [self.webView reload];
        [self.webView.scrollView.mj_header endRefreshing];
    }];
    
    [self.view addSubview:self.webView];
    
//    [self.webView loadRequest:[NSURLRequest requestWithURL:[[NSBundle bundleWithPath:[[NSBundle mainBundle] pathForResource:@"ufwebviewbridge" ofType:@"bundle"]] URLForResource:@"index" withExtension:@"html"]]];
    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"http://192.168.1.140:3000/test-bridge.html"]]];
    
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0f * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//        [self.webView bridge_callJSAPI:@"abc" params:@[@(1), @(1.1f)] callback:^(id result, NSError *error) {
//            NSLog(@".........call abc: %@ - %@", result, error);
//        }];
//    });
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
