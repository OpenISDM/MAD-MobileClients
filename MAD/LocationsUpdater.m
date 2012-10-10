//
//  LocationsUpdater.m
//  MAD
//
//  Created by Derrick Cheng on 7/24/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import "LocationsUpdater.h"
#import "MADLocationDataModel.h"
#import "MADAppDelegate.h"

@implementation LocationsUpdater

@synthesize locationDataModel = _locationDataModel;

-(id) initWithDataModel:(MADLocationDataModel *) dataModel
{
    self = [super init];
    if (self !=nil){
        [self setLocationDataModel:dataModel];
    }
    return self;
}

-(NSArray *) readFromJson:(NSString *) filename
{
    NSString *filePath = [[NSBundle mainBundle] pathForResource:[filename stringByDeletingPathExtension] ofType:@"json"];
    NSError * error;
    NSData* data = [NSData dataWithContentsOfFile:filePath];
    NSArray * arr = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
    //NSLog(@"DATA %@: ", [[arr objectAtIndex:0] objectForKey:@"name"]);
    
    return arr;
}

-(NSArray *) getNewData
{
    NSArray* interim = [[self getNewShelterData] arrayByAddingObjectsFromArray: [self getNewHospitalData]];
    return interim;
}

-(NSArray *) getNewShelterData
{
    return [self readFromJson:@"shelterConv.json"];
}

-(NSArray *) getNewPoliceData
{
    return [self readFromJson:@"medicalConv"];
}

-(NSArray *) getNewFireData
{
    return [self readFromJson:@"medicalConv"];
}

-(NSArray *) getNewHospitalData
{
    return [self readFromJson:@"medicalConv"];
}

-(void) update
{
    NSNumberFormatter *f = [[NSNumberFormatter alloc] init];
    [f setNumberStyle:NSNumberFormatterDecimalStyle];
    NSArray *array = [self getNewData];
    
    if ([array count]==[[_locationDataModel allLocations] count])
    {
        NSLog(@"No need to update!");
        return;
    }
    for (NSDictionary *json in array)
    {
        NSString *name = [json objectForKey:@"name"];
        NSLog(@"name:%@", name);
        NSNumber *lon =[json objectForKey:@"lon"];
        NSLog(@"lon:%@", lon);
        NSNumber *lat =[json objectForKey:@"lat"];
        NSLog(@"lat:%@", lat);
        NSString *detail = [json objectForKey:@"detail"];
        NSLog(@"detail:%@", detail);
        NSString *type = [json objectForKey:@"type"];
        NSLog(@"type:%@",type);
        if (name ==nil || lon == nil || lat == nil || detail==nil || type ==nil)
        {
            NSLog(@"field is null, skipping it!");
            continue;
        } 
        [self.locationDataModel addLocationWithName:name longitude:lon latitude:lat addr:detail type:type tel:@"0000"];
    }
}

-(BOOL *) needsUpdate
{
    return nil;
}

@end
