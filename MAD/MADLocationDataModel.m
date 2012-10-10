//
//  MADLocationDataModel.m
//  MAD
//
//  Created by Derrick Cheng on 7/24/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import "MADLocationDataModel.h"
#import "MADAppDelegate.h"
#import "LocationsUpdater.h"

@implementation MADLocationDataModel

@synthesize managedObjectContext = _managedObjectContext;
@synthesize managedObjectModel= _managedObjectModel;
@synthesize allLocations=_allLocations;
@synthesize locationsUpdater = _locationsUpdater;

-(id) init
{
    self = [super init];
    if (self != nil) {
        _allLocations = (NSMutableArray *) [self getAllLocations];
        return self;
    }
    return self;
}

- (NSManagedObjectContext*) managedObjectContext
{
    if (_managedObjectContext==nil){
        MADAppDelegate* appDelegate = (MADAppDelegate*)[[UIApplication sharedApplication] delegate];
        _managedObjectContext= appDelegate.managedObjectContext;
    }
    return _managedObjectContext;
}

- (NSManagedObjectModel*) managedObjectModel {
    if (_managedObjectModel==nil){
        MADAppDelegate* appDelegate = (MADAppDelegate*)[[UIApplication sharedApplication] delegate];
        _managedObjectModel = appDelegate.managedObjectModel;
    }
    return _managedObjectModel;
}

-(LocationsUpdater *) locationsUpdater
{
    if (_locationsUpdater==nil)
    {
        _locationsUpdater = [[LocationsUpdater alloc] initWithDataModel:self];
    }
    return _locationsUpdater;
}

-(MADLocation *) addLocationWithName:(NSString *)name
                           longitude:(NSNumber *)lon
                            latitude:(NSNumber *)lat
                              detail:(NSString *)detail
                                type:(NSString *)type
{
    NSLog(@"ADDLOCATION");
    MADLocation * newLocation = [NSEntityDescription insertNewObjectForEntityForName:@"MADLocation"
                                                              inManagedObjectContext:self.managedObjectContext];
    newLocation.name = name;
    newLocation.lon = lon;
    newLocation.lat = lat;
    newLocation.detail = detail;
    newLocation.type = type;
    
    [[self managedObjectContext] save:nil];
    [[self allLocations] addObject:newLocation];
    
    return newLocation;
}

-(NSMutableArray * ) getAllLocations
{
    NSFetchRequest * request = [[NSFetchRequest alloc] init];
    NSEntityDescription * e = [[[self managedObjectModel] entitiesByName] objectForKey:@"MADLocation"];
    [request setEntity: e];
    NSError * error;
    NSArray * result = [[self managedObjectContext] executeFetchRequest:request error:&error];
    if (!result){
        NSLog(@"getAllLocationsError:%@",[error localizedDescription]);
        return nil;
    }
    return [result mutableCopy];
}

-(void) updateLocations
{
    
}



@end
