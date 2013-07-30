//
//  MADLocation.h
//  MAD
//
//  Created by Lai Yi An on 10/10/12.
//  Copyright (c) 2012 OpenISDM. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface MADLocation : NSManagedObject

@property (nonatomic, retain) NSDate * dateAdded;
@property (nonatomic, retain) NSString * addr;
@property (nonatomic, retain) NSNumber * lat;
@property (nonatomic, retain) NSNumber * lon;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSString * type;
@property (nonatomic, retain) NSDate * dateUpdated;
@property (nonatomic, retain) NSString * tel;

@end
