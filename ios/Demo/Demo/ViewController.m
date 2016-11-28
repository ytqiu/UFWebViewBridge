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

@interface ViewController ()
@property (nonatomic, strong) WKWebView *webView;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.webView = [[WKWebView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    [self.webView bridge_setup:^(NSMutableDictionary *apis) {
        apis[@"hello"] = (id)^(NSDictionary *params) {
            NSLog(@"web bridge hello called....: %@", params);
            return params;
        };
    }];
    
    [self.view addSubview:self.webView];
    
    [self.webView loadRequest:[NSURLRequest requestWithURL:[[NSBundle bundleWithPath:[[NSBundle mainBundle] pathForResource:@"ufwebviewbridge" ofType:@"bundle"]] URLForResource:@"index" withExtension:@"html"]]];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0f * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self.webView bridge_callJSAPI:@"abc" params:@[@(1), @(1.1f)] callback:^(id result, NSError *error) {
            NSLog(@".........call abc: %@ - %@", result, error);
        }];
    });
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
