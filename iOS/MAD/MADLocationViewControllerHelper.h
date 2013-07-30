//
//  MADSearchControllerHelper.h
//  MAD2
//
//  Created by Derrick Cheng on 8/5/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>

@class MADLocation;

@interface MADLocationViewControllerHelper : NSObject
+ (NSArray *) sortLocations:(NSArray*)toSort;
+ (NSArray *) getLocationsGiven:(NSArray *)locations And:(NSString *)type;
+ (void) setImageForCell:(UITableViewCell *)cell WithLocation:(MADLocation *)location;
+ (void) fitAnnotationsForLocations:(NSArray*)locationsToShow ForMap:(MKMapView *)mapView;
+ (NSArray*) getSearchResults:(NSArray*)locationsToShow ForSearchTerm:(NSString *)term;
+ (void) zoomIntoLocation:(MADLocation*)mLocation ForMap:(MKMapView *)mapView;
@end
