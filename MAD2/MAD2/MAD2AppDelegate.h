//
//  MAD2AppDelegate.h
//  MAD2
//
//  Created by Derrick Cheng on 7/23/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MADLocation.h"
#import "MADLocationDataModel.h"
#import <CoreLocation/CoreLocation.h>

@interface MAD2AppDelegate : UIResponder <UIApplicationDelegate,CLLocationManagerDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator * persistentStoreCoordinator;
@property (nonatomic, strong) MADLocationDataModel * locationDataModel;
- (void)saveContext;
- (NSURL *)applicationDocumentsDirectory;

@property (strong, nonatomic) CLLocation * userLocation;

@end
