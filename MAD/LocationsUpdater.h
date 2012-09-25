//
//  LocationsUpdater.h
//  MAD2
//
//  Created by Derrick Cheng on 7/24/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MADLocationDataModel;

@interface LocationsUpdater : NSObject

@property (nonatomic, weak) MADLocationDataModel * locationDataModel;

-(id) initWithDataModel:(MADLocationDataModel *) dataModel;

-(NSArray *) getNewData;
-(NSArray *) getNewShelterData;
-(NSArray *) getNewPoliceData;
-(NSArray *) getNewHospitalData;
-(NSArray *) getNewFireData;

-(void) update;
-(BOOL *) needsUpdate;

@end
