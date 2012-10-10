//
//  MADLocationMapPoint.m
//  MAD2
//
//  Created by Derrick Cheng on 7/27/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import "MADLocationMapPoint.h"

@implementation MADLocationMapPoint
@synthesize coordinate = _coordinate;
@synthesize title = _title;
@synthesize mLocation = _mLocation;

-(id) initWithCoordinate:(CLLocationCoordinate2D)c title:(NSString *)t
{
    self = [super init];
    if (self)
    {
        _title = t;
        _coordinate = c;
    }
    return self;
}

@end
