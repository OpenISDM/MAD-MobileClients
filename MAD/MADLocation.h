//
//  MADLocation.h
//  MAD2
//
//  Created by Derrick Cheng on 7/23/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface MADLocation : NSManagedObject

@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSNumber * lon;
@property (nonatomic, retain) NSNumber * lat;
@property (nonatomic, retain) NSString * detail;
@property (nonatomic, retain) NSString * type;
@property (nonatomic, retain) NSDate * dateAdded;

@end
