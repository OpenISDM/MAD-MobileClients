//
//  LocationsUpdater.m
//  MAD2
//
//  Created by Derrick Cheng on 7/24/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import "LocationsUpdater.h"
#import "MADLocationDataModel.h"
#import "MAD2AppDelegate.h"

@implementation LocationsUpdater
@synthesize locationDataModel=_locationDataModel;

-(id) initWithDataModel:(MADLocationDataModel *) dataModel
{
    self = [super init];
    if (self !=nil){

//        MAD2AppDelegate* appDelegate = (MAD2AppDelegate*)[[UIApplication sharedApplication] delegate];
//        [self setLocationDataModel:[appDelegate locationDataModel]];

        [self setLocationDataModel:dataModel];
    }
    return self;
}

-(NSArray *) getNewData
{
    NSArray* interim = [[self getNewShelterData] arrayByAddingObjectsFromArray: [self getNewFireData]];
//    NSArray* interim = [[NSArray alloc] init ];
    return [interim arrayByAddingObjectsFromArray:[self getNewHospitalData]];
}

-(NSArray *) getNewShelterData
{
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"shelterConv" ofType:@"json"]; 
    NSError * error;
    NSData* data = [NSData dataWithContentsOfFile:filePath];
    NSArray * array = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
    //    NSLog(@"DATA %@: ", [[array objectAtIndex:0] objectForKey:@"name"]);
    return array;
}

-(NSArray *) getNewPoliceData
{
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"shelterConv" ofType:@"json"]; 
    NSError * error;
    NSData* data = [NSData dataWithContentsOfFile:filePath];
    NSArray * array = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
    //    NSLog(@"DATA %@: ", [[array objectAtIndex:0] objectForKey:@"name"]);
    return array;
}

-(NSArray *) getNewFireData
{
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"convFire" ofType:@"json"]; 
    NSError * error;
    NSData* data = [NSData dataWithContentsOfFile:filePath];
    NSArray * array = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
    //    NSLog(@"DATA %@: ", [[array objectAtIndex:0] objectForKey:@"name"]);
    return array;
}

-(NSArray *) getNewHospitalData
{
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"medicalConv" ofType:@"json"]; 
    NSError * error;
    NSData* data = [NSData dataWithContentsOfFile:filePath];
    NSArray * array = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
    //    NSLog(@"DATA %@: ", [[array objectAtIndex:0] objectForKey:@"name"]);
    return array;
}

-(void) update
{
    NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
    [f setNumberStyle:NSNumberFormatterDecimalStyle];
    NSArray * array = [self getNewData];
    if ([array count]==[[_locationDataModel allLocations] count])
    {
        NSLog(@"No need to update!");
        return;
    }
    for (NSDictionary * json in array)
    {
        if ([self locationDataModel] == nil){
            NSLog(@"WTF IS IT NIL!");
        } else {
            NSLog(@"IT IS NOT NIL");
        }
        NSString* name = [json objectForKey:@"name"];
        NSLog(@"name:%@", name);
        NSNumber * lon =[json objectForKey:@"lon"];
        NSLog(@"lon:%@", lon);
        NSNumber * lat =[json objectForKey:@"lat"];
        NSLog(@"lat:%@", lat);
        NSString* detail = [json objectForKey:@"detail"];
        NSLog(@"detail:%@", detail);
        NSString* type = [json objectForKey:@"type"];
        NSLog(@"type:%@",type);
        if (name ==nil || lon == [NSNull null] || lat == [NSNull null] || detail==nil || type ==nil)
        {
            NSLog(@"field is null, skipping it!");
            continue;
        }
        [[self locationDataModel] addLocationWithName:name Longitude: lon Latitude:lat Detail:detail Type:type];
  
    }
}

-(BOOL *) needsUpdate
{
    return nil;
}

@end
