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
#import "MAD2AppDelegate.h"
#import "MADDirectionsViewController.h"

@interface MADLocationDetailViewController ()
- (void)configureView;
@end

@implementation MADLocationDetailViewController

@synthesize location = _location, nameLabel=_nameLabel, lonLabel = _lonLabel, latLabel = _latLabel, detailLabel = _detailLabel, typeLabel = _typeLabel,addressLabel=_addressLabel ,directionsButton = _directionsButton;


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
        self.lonLabel.text = theLocation.lon.description;
        self.latLabel.text = theLocation.lat.description;
        self.detailLabel.text = theLocation.detail;
        self.typeLabel.text = theLocation.type;
//        CLLocation * cLocation = [[CLLocation alloc] initWithLatitude:[theLocation.lat doubleValue] longitude:[theLocation.lon doubleValue]];
    }
    
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"ShowDirections"]) {
        MADDirectionsViewController *directionsViewController = [segue destinationViewController];
        MAD2AppDelegate* appDelegate = (MAD2AppDelegate*)[[UIApplication sharedApplication] delegate];
        CLLocation * cloc = [appDelegate userLocation];
        NSString* address = self.location.detail;
        NSString* url = [NSString stringWithFormat: @"http://maps.google.com/maps?saddr=%f,%f&daddr=%@",[cloc coordinate].latitude,[cloc coordinate].longitude,[address stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
        [directionsViewController setMapURL:url];
    }
    
}

-(IBAction)getDirectionsTo:(id)sender
{
    MAD2AppDelegate* appDelegate = (MAD2AppDelegate*)[[UIApplication sharedApplication] delegate];
    CLLocation * cloc = [appDelegate userLocation];
    NSString* address = self.location.detail;
    NSString* url = [NSString stringWithFormat: @"http://maps.google.com/maps?saddr=%f,%f&daddr=%@",[cloc coordinate].latitude,[cloc coordinate].longitude,[address stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    [[UIApplication sharedApplication] openURL: [NSURL URLWithString: url]];
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    [self configureView];
}


@end
