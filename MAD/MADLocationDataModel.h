//
//  MADLocationDataModel.h
//  MAD2
//
//  Created by Derrick Cheng on 7/24/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MADLocation.h"


@class LocationsUpdater;

@interface MADLocationDataModel : NSObject
    
@property (nonatomic, weak) NSManagedObjectContext *managedObjectContext;
@property (nonatomic, weak) NSManagedObjectModel *managedObjectModel;
@property (nonatomic, strong) NSMutableArray *allLocations;
@property (nonatomic, strong) LocationsUpdater *locationsUpdater;
@property (nonatomic, strong) NSArray *locationsToShow;

-(MADLocation *) addLocationWithName:(NSString *)name longitude:(NSNumber *)lon latitude:(NSNumber *)lat detail:(NSString *) detail type:(NSString *)type;
-(NSArray *) getAllLocations;
-(void) updateLocations;

@end
