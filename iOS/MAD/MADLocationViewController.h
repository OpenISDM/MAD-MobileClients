//
//  MADLocationViewController.h
//  MAD2
//
//  Created by Derrick Cheng on 7/24/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MADLocationDataModel.h"
#import <MapKit/MapKit.h>

@interface MADLocationViewController : UIViewController <UITableViewDataSource, MKMapViewDelegate, UISearchDisplayDelegate, UISearchBarDelegate>

@property (nonatomic, weak) MADLocationDataModel* locationDataModel;

-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section;
-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath;

-(IBAction)toggleLocationView:(id)sender;
-(IBAction)showLocationDetails:(id)sender;

-(MKMapView *) mapView;

-(void) mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation;

-(void) setZoomToFitAnnotations;

-(void) addAnnotationsToShowToMap;

@end
