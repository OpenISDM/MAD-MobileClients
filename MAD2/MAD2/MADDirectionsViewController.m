//
//  MADDirectionsViewController.m
//  MAD2
//
//  Created by Derrick Cheng on 8/9/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import "MADDirectionsViewController.h"

@implementation MADDirectionsViewController

@synthesize mapURL=_mapURL;

-(id) initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self)
    {
        [self setHidesBottomBarWhenPushed:YES];
    }
    return self;
}

-(void) viewDidLoad
{
    if (_mapURL !=nil){
        NSURL * url = [NSURL URLWithString:_mapURL];
        NSURLRequest * req = [NSURLRequest requestWithURL:url];
        [((UIWebView * ) [self.view.subviews objectAtIndex:0]) loadRequest:req]; 
    }
}



@end
