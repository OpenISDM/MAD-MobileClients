//
//  MADLocationDetailViewController.m
//  MAD2
//
//  Created by Derrick Cheng on 7/25/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import "MADLocationDetailViewController.h"
#include "MADLocation.h"
#include <CoreLocation/CoreLocation.h>
#import <MapKit/MapKit.h>
#import "MADAppDelegate.h"
#import "MapDirectionsViewController.h"

@interface MADLocationDetailViewController ()
- (void)configureView;
@end

@implementation MADLocationDetailViewController

@synthesize location = _location, nameLabel=_nameLabel, typeLabel = _typeLabel,addressLabel=_addressLabel ,directionsButton = _directionsButton;


- (void)setLocation:(MADLocation *)location:newLocation
{
    if (_location != newLocation) {
        _location = newLocation;
        
        // Update the view.
        [self configureView];
    }
}

- (void)viewDidUnload
{
    self.location = nil;
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)configureView
{
    // Update the user interface for the detail item.
    MADLocation *theLocation= self.location;
    if (theLocation) {
        self.nameLabel.text = theLocation.name;
        //self.lonLabel.text = theLocation.lon.description;
        //self.latLabel.text = theLocation.lat.description;
        self.addressLabel.text = theLocation.detail;
        self.typeLabel.text = theLocation.type;
//        CLLocation * cLocation = [[CLLocation alloc] initWithLatitude:[theLocation.lat doubleValue] longitude:[theLocation.lon doubleValue]];
    }
    
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"DisplayDirections"]) {
        MapDirectionsViewController *directionsViewController = [segue destinationViewController];
        MADAppDelegate* appDelegate = (MADAppDelegate*)[[UIApplication sharedApplication] delegate];
        CLLocation * cloc = [appDelegate userLocation];
        /*
        NSString* address = self.location.detail;
        NSString* url = [NSString stringWithFormat: @"http://maps.googleapis.com/maps/api/directions/xml?origin=%f,%f&destination=%@&sensor=false", [cloc coordinate].latitude,[cloc coordinate].longitude,[address stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
         */
        MKPointAnnotation *startPoint = [[MKPointAnnotation alloc] init];
        startPoint.title = @"Start Point";
        startPoint.coordinate = CLLocationCoordinate2DMake(cloc.coordinate.latitude, cloc.coordinate.longitude);
        directionsViewController.startPoint = startPoint;
        
        MKPointAnnotation *endPoint = [[MKPointAnnotation alloc] init];
        endPoint.title = @"End Point";
        endPoint.coordinate = CLLocationCoordinate2DMake([self.location.lat doubleValue], [self.location.lon doubleValue]);
        directionsViewController.endPoint = endPoint;
        
        //NSLog(@"Start point: %f, %f", startPoint.coordinate.latitude, startPoint.coordinate.longitude);
        //NSLog(@"End point: %f, %f", endPoint.coordinate.latitude, endPoint.coordinate.longitude);
    }
    
}

/*
-(IBAction)getDirectionsTo:(id)sender
{
    MADAppDelegate* appDelegate = (MADAppDelegate*)[[UIApplication sharedApplication] delegate];
    CLLocation * cloc = [appDelegate userLocation];
    NSString* address = self.location.detail;
    NSString* url = [NSString stringWithFormat: @"http://maps.google.com/maps?saddr=%f,%f&daddr=%@",[cloc coordinate].latitude,[cloc coordinate].longitude,[address stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    [[UIApplication sharedApplication] openURL: [NSURL URLWithString: url]];
}
*/
#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    [self configureView];
}


@end
