//
//  MADLocationViewController.m
//  MAD
//
//  Created by Derrick Cheng on 7/24/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import "MADLocationViewController.h"
#import "MADAppDelegate.h"
#import "MADLocationDetailViewController.h"
#import <MapKit/MapKit.h>
#import <CoreLocation/CoreLocation.h>
#import "MADLocationMapPoint.h"
#import "MADLocation.h"
#import "MADLocationTableViewCell.h"
#import "MADLocationViewControllerHelper.h"

//TODO: create refresh table view method

@implementation MADLocationViewController
{
    NSMutableArray * locationsArray;
    NSArray * locationsToShow;
    BOOL isButtonForList;
    IBOutlet MKMapView * mapView;
    UITableView * listView;
    UIView * listMapOuterView;
    CLLocation * curLocation;
    BOOL mapNeedToReload;
    MADLocationMapPoint * selectedAnnotation;
    NSArray * resultsToShow;
}

@synthesize locationDataModel = _locationDataModel;


//SearchBar Delegate stuff
- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText
{
    resultsToShow=[MADLocationViewControllerHelper getSearchResults:locationsToShow ForSearchTerm:searchText];
    [self.searchDisplayController.searchResultsTableView reloadData];
//    [self.searchDisplayController.searchBar resignFirstResponder];
//    [self.searchDisplayController.searchBar resignFirstResponder];
    NSLog(@"text did change");
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar
{
    NSLog(@"Testing cancel was clicked");
}

- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar
{  
    //[self.searchDisplayController.searchBar setShowsScopeBar:YES];
    NSString *text = self.searchDisplayController.searchBar.text;
    NSLog(@"We entered text: %@",text);
    //Reset resultsToShow
    resultsToShow = locationsToShow;
}

//- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar {
//[searchBar becomeFirstResponder];
//}


- (void)viewWillAppear:(BOOL)animated
{
    //TODO: for now reload all the time
    [listView reloadData];
    
    locationsToShow = [MADLocationViewControllerHelper sortLocations:locationsToShow];
    NSLog(@"VIEW WILL APPEAR CALLED");
}

/*
- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar
{
    [self.searchDisplayController.searchBar resignFirstResponder];
    [self.searchDisplayController.searchBar setShowsScopeBar:YES];
}
 */




-(void) viewDidLoad
{
    isButtonForList=FALSE;
    mapNeedToReload=NO;
    [super viewDidLoad];
    listView = (UITableView * ) [self.view viewWithTag:101];
    listMapOuterView = (UIView * ) [self.view viewWithTag:99];
    MADAppDelegate* appDelegate = (MADAppDelegate*)[[UIApplication sharedApplication] delegate];
    _locationDataModel = [appDelegate locationDataModel];
    locationsArray =[_locationDataModel allLocations];
    locationsToShow = [MADLocationViewControllerHelper sortLocations:[MADLocationViewControllerHelper getLocationsGiven:locationsArray And:@"shelter"]];
    curLocation = appDelegate.userLocation;
    resultsToShow = locationsToShow;
    [listView reloadData];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView==self.searchDisplayController.searchResultsTableView)
    {
//        [self performSegueWithIdentifier:@"ShowLocationDetails" sender:self];
        MADLocation* mLocation = [resultsToShow objectAtIndex:[indexPath row]];
        [self.searchDisplayController setActive:NO animated:YES];
        
        [MADLocationViewControllerHelper zoomIntoLocation: mLocation ForMap:mapView];
    }
}

-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (tableView==self.searchDisplayController.searchResultsTableView)
    {
        return [resultsToShow count];
    }
    return [locationsToShow count];
}
-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    MADAppDelegate* appDelegate = (MADAppDelegate*)[[UIApplication sharedApplication] delegate];
    MADLocationTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"locationTableViewCell"];
    NSArray * arrayToDisplay = locationsToShow;
    if (tableView == listView) 
    {
//        arrayToDisplay = locationsToShow;
    } 
    else if (tableView == self.searchDisplayController.searchResultsTableView)
    {
        arrayToDisplay = resultsToShow;
    }
    if (cell == nil)
    {
//        UIViewController *temporaryController = [[UIViewController alloc] initWithNibName:@"locationTableViewCell" bundle:nil];
//        cell = (MADLocationTableViewCell *) temporaryController.view;
        cell = [[MADLocationTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"locationTableViewCell"];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    } 

    MADLocation* location = [arrayToDisplay objectAtIndex:[indexPath row]];
    CLLocation * cl = [[CLLocation alloc] initWithLatitude:[location.lat doubleValue] longitude:[location.lon doubleValue]];
    CLLocationDistance dist = [cl distanceFromLocation:curLocation];
    
    cell.textLabel.text  = [location name];
    cell.detailTextLabel.text = [NSString stringWithFormat:@"%.2f m",dist];
  
    //set image of cell properly
    [MADLocationViewControllerHelper setImageForCell:cell WithLocation:location];
    return cell;
}


- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"ShowLocationDetails"]) {
        MADLocationDetailViewController *detailViewController = [segue destinationViewController];
        MADLocation * location = [locationsToShow objectAtIndex:[listView indexPathForSelectedRow].row];
        [detailViewController setLocation: location];
    }
    if ([[segue identifier] isEqualToString:@"ShowLocationPinDetails"]) {
        MADLocationDetailViewController *detailViewController = [segue destinationViewController];
        MADLocation * location =selectedAnnotation.mLocation;
        [detailViewController setLocation: location];
    }

}

-(IBAction)toggleLocationView:(id)sender
{
 
    if (mapNeedToReload)
    {
        [self addAnnotationsToShowToMap];
    }
    
    [UIView transitionFromView:(isButtonForList ? [self mapView] : listView)
                        toView:(isButtonForList ? listView : [self mapView])
                      duration:0.5
                       options:(isButtonForList ? UIViewAnimationOptionTransitionFlipFromRight :
                                UIViewAnimationOptionTransitionFlipFromLeft)
                    completion:^(BOOL finished) {
                        if (finished) {
                            isButtonForList = !isButtonForList;
                        }
                    }];
    }

-(void) addAnnotationsToShowToMap
{
    [mapView removeAnnotations: [mapView annotations]];
    MADLocationMapPoint* mapPoint;
    for (MADLocation* location in locationsToShow)
    {
        mapPoint = [[MADLocationMapPoint alloc] initWithCoordinate:CLLocationCoordinate2DMake([[location lat] doubleValue], [[location lon] doubleValue]) title:location.name];
        [mapPoint setMLocation:location];
        [[self mapView] addAnnotation:mapPoint];
    }
    [self setZoomToFitAnnotations];
    NSLog(@"Number of Annotations: %u",[[mapView annotations] count]);
    mapNeedToReload=NO;
}

//Lazy load the map view 
-(MKMapView *) mapView
{
    if (mapView ==nil){
        mapView = [[MKMapView alloc] initWithFrame:[listView bounds]];
        mapView.center = listView.center;
        mapView.showsUserLocation = YES;
        [mapView setDelegate:self];
        [self addAnnotationsToShowToMap];
    }

    
    return mapView;
}

- (void)mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation
{
   
//    CLLocationCoordinate2D loc = [userLocation coordinate];
    
//    MKCoordinateRegion region = MKCoordinateRegionMakeWithDistance(loc, 250, 250);
//    [[self mapView] setRegion:region animated:YES];
//    currentLocation = userLocation;
    curLocation = userLocation.location;
    
    NSLog(@"DIDUPDATEUSERLOCATIONS IS CALLED");
}

//MAPVIEW DELEGATE STUFF

- (void)mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view
{
    selectedAnnotation = view.annotation;
}

-(IBAction)showLocationDetails:(id)sender
{
    [self performSegueWithIdentifier:@"ShowLocationPinDetails" sender:sender];
//    [self presentModalViewController: animated:<#(BOOL)#>
    NSLog(@"ShowLocationDetails for Pin Called");
}
- (MKAnnotationView *)mapView:(MKMapView *)mapView
            viewForAnnotation:(id <MKAnnotation>)annotation
{
    // If it's the user location, just return nil.
    if ([annotation isKindOfClass:[MKUserLocation class]])
        return nil;

    // Handle any custom annotations.
    if ([annotation isKindOfClass:[MADLocationMapPoint class]])
    {
        // Try to dequeue an existing pin view first.
        MKPinAnnotationView*    pinView = (MKPinAnnotationView*)[[self mapView]
                                                                 dequeueReusableAnnotationViewWithIdentifier:@"MapPointView"];
        MADLocationMapPoint * mAnnotation = (MADLocationMapPoint*) annotation;
        
        if (!pinView)
        {
            // If an existing pin view was not available, create one.
            pinView = [[MKPinAnnotationView alloc ] initWithAnnotation:mAnnotation reuseIdentifier:@"MapPointView"];

            // Add a detail disclosure button to the callout.
            UIButton* rightButton = [UIButton buttonWithType:
                                     UIButtonTypeDetailDisclosure];
            [rightButton addTarget:self action:@selector(showLocationDetails:) forControlEvents:UIControlEventTouchUpInside];
            pinView.rightCalloutAccessoryView = rightButton;
            pinView.canShowCallout = YES;
            pinView.animatesDrop = YES;
        }
        else
        {
            pinView.annotation = annotation;
        }
        if ([mAnnotation mLocation]!=nil)
        {
            NSString * type = [mAnnotation mLocation].type;
            if ( [type isEqualToString:@"fire"])
            {
                pinView.pinColor = MKPinAnnotationColorPurple; 
            }
            else if ( [type isEqualToString:@"hospital"])
            {
                pinView.pinColor = MKPinAnnotationColorRed; 
            }
            else if ( [type isEqualToString:@"shelter"])
            {
                pinView.pinColor = MKPinAnnotationColorGreen; 
            }
        }
        
        return pinView;
    }
    
    return nil;

}

//HELPERS
- (void) setZoomToFitAnnotations
{
    [MADLocationViewControllerHelper fitAnnotationsForLocations:locationsToShow ForMap:[self mapView]];
}


//Search Bar Delegate
- (void)searchBar:(UISearchBar *)searchBar selectedScopeButtonIndexDidChange:(NSInteger)selectedScope
{
//    [listMapOuterView setNeedsDisplay];
//    NSArray * getLocationsGiven:  (NSArray *) locations And: (NSString *) type


    NSString * type = @"shelter";
    switch(selectedScope)
    {
        case 0:
            type=@"shelter";
        break;
            
        case 1:
            type=@"fire";
        break;
            
        case 2:
            type=@"hospital";
        break;
        
        case 3:
            type=@"all";
        break;

    }
    locationsToShow = [MADLocationViewControllerHelper sortLocations:[MADLocationViewControllerHelper getLocationsGiven:locationsArray And:type]];
    [listView reloadData];
    if (isButtonForList)
    {
    MADLocationMapPoint* mapPoint;
    [mapView removeAnnotations: [mapView annotations]];
    for (MADLocation* location in locationsToShow)
    {
        mapPoint = [[MADLocationMapPoint alloc] initWithCoordinate:CLLocationCoordinate2DMake([[location lat] doubleValue], [[location lon] doubleValue]) title:location.name];
        [mapPoint setMLocation:location];
        [[self mapView] addAnnotation:mapPoint];
    }
    [self setZoomToFitAnnotations];
    [mapView setNeedsDisplay];
    }
    else 
    {
        mapNeedToReload = YES;
    }
}

@end
