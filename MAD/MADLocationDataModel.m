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

@synthesize managedObjectContext = __managedObjectContext;
@synthesize managedObjectModel= _managedObjectModel;
@synthesize allLocations=_allLocations;
@synthesize locationsUpdater = _locationsUpdater;

-(id) init
{
    self = [super init];
    if (self!=nil){
        _allLocations=(NSMutableArray *) [self getAllLocations];
        return self;
    }
    return self;
}

- (NSManagedObjectContext*) managedObjectContext {
    if (__managedObjectContext==nil){
        MADAppDelegate* appDelegate = (MADAppDelegate*)[[UIApplication sharedApplication] delegate];
        __managedObjectContext= appDelegate.managedObjectContext;
    }
    return __managedObjectContext;
}

- (NSManagedObjectModel*) managedObjectModel {
    if (_managedObjectModel==nil){
        MADAppDelegate* appDelegate = (MADAppDelegate*)[[UIApplication sharedApplication] delegate];
        _managedObjectModel = appDelegate.managedObjectModel;
    }
    return _managedObjectModel;
}


-(MADLocation *) addLocationWithName:(NSString *)name Longitude:(NSNumber *)lon Latitude:(NSNumber *)lat Detail: (NSString *) detail Type: (NSString *)type
{
    NSLog(@"ADDLOCATION");
    MADLocation * newLocation = [NSEntityDescription insertNewObjectForEntityForName:@"MADLocation"inManagedObjectContext:self.managedObjectContext];
    newLocation.name = name;
    newLocation.lon = lon;
    newLocation.lat = lat;
    newLocation.detail = detail;
    newLocation.type = type;
    
    [[self managedObjectContext] save:nil];
    
    [[self allLocations] addObject:newLocation];
    return newLocation;
}

-(MADLocation *) addLocation
{
    return [self addLocationWithName:@"STUB" Longitude:[[NSNumber alloc] initWithDouble:1.1] Latitude:[[NSNumber alloc] initWithDouble:2.2] Detail:@"Detail" Type:@"Type"];
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

-(NSMutableArray * ) getAllShelters
{
//    NSFetchRequest * request = [[NSFetchRequest alloc] init];
//    NSEntityDescription * e = [[[self managedObjectModel] entitiesByName] objectForKey:@"MADLocation"];
//    [request setEntity: e];
//    NSError * error;
//    NSArray * result = [[self managedObjectContext] executeFetchRequest:request error:&error];
//    if (!result){
//        NSLog(@"getAllLocationsError:%@",[error localizedDescription]);
//        return nil;
//    }
//    return [result mutableCopy];
    return nil;
}


-(void) updateLocations
{
    
}

-(LocationsUpdater *) locationsUpdater
{
    if (_locationsUpdater==nil)
    {
        _locationsUpdater = [[LocationsUpdater alloc] initWithDataModel:self];
    }
    return _locationsUpdater;
}

@end
