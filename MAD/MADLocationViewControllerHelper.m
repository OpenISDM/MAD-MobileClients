//
//  MADSearchControllerHelper.m
//  MAD
//
//  Created by Derrick Cheng on 8/5/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import "MADLocationViewControllerHelper.h"
#import <CoreLocation/CoreLocation.h>
#import "MADLocation.h"
#import "MADAppDelegate.h"
#import "MADLocationMapPoint.h"

@implementation MADLocationViewControllerHelper

+(NSArray *) sortLocations: (NSArray*) toSort
{
    NSArray * sortedArray;
    sortedArray = [toSort sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        MADAppDelegate* appDelegate = (MADAppDelegate*)[[UIApplication sharedApplication] delegate];
        CLLocation * curLocation = appDelegate.userLocation;
        
        MADLocation * loc1 = (MADLocation *) obj1;
        CLLocation * cl1 = [[CLLocation alloc] initWithLatitude:[loc1.lat doubleValue] longitude:[loc1.lon doubleValue]];
        CLLocationDistance dist1 = [cl1 distanceFromLocation:curLocation];
        MADLocation * loc2 = (MADLocation *) obj2;
        CLLocation * cl2 = [[CLLocation alloc] initWithLatitude:[loc2.lat doubleValue] longitude:[loc2.lon doubleValue]];
        CLLocationDistance dist2 = [cl2 distanceFromLocation:curLocation];
        return dist1>dist2;
    }];
    return sortedArray;
}

+ (NSArray *) getLocationsGiven:  (NSArray *) locations And: (NSString *) type
{
    if ([@"all" isEqualToString:type])
    {
        return locations;
    }
    NSMutableArray * result = [[NSMutableArray alloc] init];
    for (MADLocation * location in locations)
    {
        if ([location.type isEqualToString:type])
        {
            [result addObject:location];
        }
    }
    return result;
}

+ (void) setImageForCell:(UITableViewCell *) cell WithLocation:(MADLocation *) location
{
    if ([[location type] isEqualToString:@"police"])
    {
        cell.imageView.image = [UIImage imageNamed:@"light_shield.png"];
    }
    else if ([[location type] isEqualToString:@"shelter"])
    {
        cell.imageView.image = [UIImage imageNamed:@"light_home.png"]; 
    }
    else if ([[location type] isEqualToString:@"hospital"])
    {
        cell.imageView.image = [UIImage imageNamed:@"light_add.png"]; 
    }
    else if ([[location type] isEqualToString:@"fire"])
    {
        cell.imageView.image = [UIImage imageNamed:@"light_flame.png"]; 
    }
}

+(void) fitAnnotationsForLocations: (NSArray*) locationsToShow ForMap: (MKMapView *) mapView
{
    double up,down,left,right;
    //double padding = 1.0;
    if ([locationsToShow count] > 0){
        MADLocation* loc =[ locationsToShow objectAtIndex:0];
        up = [[loc lon] doubleValue ];
        down = [[loc lon] doubleValue];
        right = [[loc lat] doubleValue];
        left = [[loc lat] doubleValue];
        
        for (MADLocation * tloc in locationsToShow)
        {
            if ([[tloc lon] doubleValue]>up){
                up = [[tloc lon] doubleValue];
            }
            if ([[tloc lon] doubleValue]<down){
                down = [[tloc lon] doubleValue];
            }
            if ([[tloc lat] doubleValue]>right){
                right = [[tloc lat] doubleValue];
            }
            if ([[tloc lat] doubleValue]<left){
                left = [[tloc lat] doubleValue];
            }
        }
        MKMapPoint upperleft,upperright,lowerleft,lowerright;
        upperleft.x = left; upperleft.y = up;
        upperright.x = right; upperright.y = up;
        lowerleft.x = left; lowerleft.y = down;
        lowerright.x = right; lowerright.y = down;
        //        CLLocationDistance latMeters = MKMetersBetweenMapPoints(upperleft,upperright)+2*padding;
        //        CLLocationDistance lonMeters = MKMetersBetweenMapPoints(upperleft,lowerleft)+2*padding;
        double xcenter = (right+left)/2.0;
        double ycenter = (up+down)/2.0;
        CLLocationCoordinate2D center;
        center.latitude=xcenter;
        center.longitude=ycenter;
        MKCoordinateSpan span = MKCoordinateSpanMake(right-left,up-down);
        MKCoordinateRegion region = MKCoordinateRegionMake(center,span);
//        MKCoordinateRegion region= mapView.region;
//        region.center=center;
//        region.span.longitudeDelta/=15*(up-down);
//        region.span.latitudeDelta /= 15*(right-left);
        [mapView setRegion:region animated:YES];
    }
}

+ (NSMutableArray*) getSearchResults: (NSArray*) locationsToShow ForSearchTerm: (NSString *) term;
{
    NSString *pattern = [[NSString alloc] initWithFormat:@".*%@.*",term];
    NSError * error = NULL;
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:pattern options:NSRegularExpressionCaseInsensitive  error:&error];
    NSMutableArray * results = [[NSMutableArray alloc] init];
    for (MADLocation * location in locationsToShow)
    {
        NSUInteger numberOfMatches = [regex numberOfMatchesInString:location.name
                                                            options:0
                                                              range:NSMakeRange(0, [location.name length])];
        if ([@" " isEqualToString:term]|| numberOfMatches > 0) {
            [results addObject:location];
        }
        
//        if ([location.name hasPrefix:term])
//        {
//            [results addObject:location];
//        }
    }
    return results;
}

+ (void) zoomIntoLocation:(MADLocation*) mLocation ForMap:(MKMapView *)mapView 
{
    CLLocationCoordinate2D center;
    center.latitude=[mLocation.lat doubleValue];
    center.longitude=[mLocation.lon doubleValue];
    [mapView setCenterCoordinate:center animated:YES];
    MKCoordinateSpan span = MKCoordinateSpanMake(0.01, 0.01);
    MKCoordinateRegion region = MKCoordinateRegionMake(center, span);
    [mapView setRegion:region animated:YES];
    for (MADLocationMapPoint * mPoint in mapView.annotations)
    {
        if (mPoint.mLocation== mLocation)
        {
            NSLog(@"FUCK YEA FOUND THE ANNOTATIONNNNNNNNNNNNN!!!!!!");
            [mapView selectAnnotation:mPoint animated:YES];
            return;
        }
    }
    
//    [mapView selectAnnotation:av.annotation animated:NO];

}

@end
