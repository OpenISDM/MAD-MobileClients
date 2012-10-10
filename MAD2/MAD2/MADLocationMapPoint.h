//
//  MADLocationMapPoint.h
//  MAD2
//
//  Created by Derrick Cheng on 7/27/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>
@class MADLocation;

@interface MADLocationMapPoint : NSObject <MKAnnotation>
-(id) initWithCoordinate:(CLLocationCoordinate2D) c title:(NSString *) t;

@property (nonatomic,readonly) CLLocationCoordinate2D coordinate;
@property (nonatomic, copy) NSString *title;
@property (nonatomic,strong) MADLocation * mLocation;

@end
