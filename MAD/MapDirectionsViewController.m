//
//  MapDirectionsView.m
//  
//
//  Created by Kamil Pyć on 11-12-02.
//  Copyright (c) 2011 Kamil Pyć. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MapDirectionsViewController.h"
#import "SVProgressHUD.h"

@interface MapDirectionsViewController ()
- (NSMutableArray *)decodePolyLine:(NSMutableString *)encoded;
@end


@implementation MapDirectionsViewController

@synthesize startPoint = _startPoint;
@synthesize endPoint = _endPoint;
@synthesize fillColor = _fillColor;
@synthesize strokeColor = _strokeColor;
@synthesize lineWidth = _lineWidth;
@synthesize walkingMode = _walkingMode;
@synthesize routeLine = _routeLine;
@synthesize mapView = _mapView;


#pragma mark -
#pragma Initial View setup

-(id) init
{
    self = [super init];
    [self setHidesBottomBarWhenPushed:YES];
    
    return self;
}

-(void)viewDidLoad{
    
    [super viewDidLoad];
    
    [self setHidesBottomBarWhenPushed:YES];
    
    self.lineWidth = 3;
    self.fillColor = [UIColor colorWithRed:0.0f green:0.0f blue:1.0f alpha:0.7f];
    self.strokeColor = [UIColor colorWithRed:0.0f green:0.0f blue:1.0f alpha:0.7f];
    self.walkingMode = FALSE;
    
    NSArray *arrayItems = [NSArray arrayWithObjects:@"Drive",@"Walk", nil];
    UISegmentedControl *topButtons = [[UISegmentedControl alloc]initWithItems:arrayItems];
    topButtons.selectedSegmentIndex = 0;
    [self.navigationItem setTitleView:topButtons];
    [topButtons addTarget:self action:@selector(directionsModeSegmentControlTapped:) forControlEvents:UIControlEventValueChanged];
    [topButtons setSegmentedControlStyle:UISegmentedControlStyleBar];
    //[topButtons release];
    
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc]initWithTitle:@"Tips" style:UIBarButtonItemStyleBordered target:self action:@selector(toggleDirectionsTip)];
    
    self.navigationItem.rightBarButtonItem = rightButton;
    
    //UIBarButtonItem *leftButton = [[UIBarButtonItem alloc]initWithTitle:@"Route" style:UIBarButtonItemStyleBordered target:self action:@selector(getRoute)];
    
    //self.navigationItem.leftBarButtonItem = leftButton;
    
    _mapView = [[MKMapView alloc]initWithFrame:self.view.bounds];
    self.mapView.delegate = self;
    self.mapView.autoresizingMask = UIViewAutoresizingFlexibleRightMargin|UIViewAutoresizingFlexibleHeight|UIViewAutoresizingFlexibleWidth;
    self.mapView.showsUserLocation = YES;
    [self.view addSubview:self.mapView];
    
    directionsView = [[DirectionsView alloc]initWithFrame:CGRectMake(0, -70, self.view.bounds.size.width, 70)];
    directionsView.clipsToBounds = YES;
    [self.view addSubview:directionsView];
    directionsView.delegate = self;
    //[directionsView release];
    
    
}

-(void) viewWillAppear:(BOOL)animated
{
    //NSLog(@"Here comes Directions view!!");
    //NSLog(@"Start point: %f, %f", _startPoint.coordinate.latitude, _startPoint.coordinate.longitude);
    //NSLog(@"End point: %f, %f", _endPoint.coordinate.latitude, _endPoint.coordinate.longitude);
    [self.mapView setCenterCoordinate:self.startPoint.coordinate animated:YES];
    [self.mapView addAnnotation:self.startPoint];
    [self.mapView addAnnotation:self.endPoint];
    [self calculateDirections];
}

-(void)toggleDirectionsTip{
    
    [UIView animateWithDuration:0.5f animations:^{
        directionsView.frame = CGRectMake(0,directionsView.frame.origin.y? 0:-70, self.view.bounds.size.width,directionsView.frame.size.height);
    
    }];
}


-(void)calculateDirections{
    NSLog(@"start calculating dir...");
    [SVProgressHUD showInView:self.view status:@"Calculating directions"];

    if (self.startPoint == nil) {
        [SVProgressHUD dismissWithError:@"No start point selected!" afterDelay:1];
    } else if (self.endPoint == nil){
        [SVProgressHUD dismissWithError:@"No end point selected!" afterDelay:1];
    } else {
        NSString *mode = nil;
        if (self.walkingMode) {
            mode= @"walking";
        } else
        {
            mode = @"driving";
        }
        
        NSString* urlString = [NSString stringWithFormat:@"http://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&sensor=true&language=%@&mode=%@", self.startPoint.coordinate.latitude, self.startPoint.coordinate.longitude,self.endPoint.coordinate.latitude , self.endPoint.coordinate.longitude,[[NSLocale currentLocale] localeIdentifier], mode];
        
        NSURL *url = [NSURL URLWithString:urlString];
        NSURLRequest *theRequest = [NSURLRequest requestWithURL:url];
        NSURLConnection *theConnection = [[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
        
        if(theConnection)
            webData = [NSMutableData data];
        else
            NSLog(@"theConnection is NULL");
        /*
        ASIHTTPRequest *asiRequest = [[ASIHTTPRequest alloc]initWithURL:[NSURL URLWithString:urlString]];
        asiRequest.delegate = self;
        [asiRequest startAsynchronous];
         */
    }
        
}

-(void)directionsModeSegmentControlTapped:(UISegmentedControl *)sender
{
    switch (sender.selectedSegmentIndex)
    {
        case 1: //Walk
            self.walkingMode = YES;    
            break;
        default://Drive 
            self.walkingMode = NO;
            break;
    }
    [self calculateDirections];
}

#pragma mark -
#pragma mark Map Helpers


// Decode a polyline.
// See: http://code.google.com/apis/maps/documentation/utilities/polylinealgorithm.html
- (NSMutableArray *)decodePolyLine:(NSMutableString *)encoded {
	[encoded replaceOccurrencesOfString:@"\\\\" withString:@"\\"
								options:NSLiteralSearch
								  range:NSMakeRange(0, [encoded length])];
	NSInteger len = [encoded length];
	NSInteger index = 0;
	NSMutableArray *array = [[NSMutableArray alloc] init];
	NSInteger lat=0;
	NSInteger lng=0;
	while (index < len) {
		NSInteger b;
		NSInteger shift = 0;
		NSInteger result = 0;
		do {
			b = [encoded characterAtIndex:index++] - 63;
			result |= (b & 0x1f) << shift;
			shift += 5;
		} while (b >= 0x20);
		NSInteger dlat = ((result & 1) ? ~(result >> 1) : (result >> 1));
		lat += dlat;
		shift = 0;
		result = 0;
		do {
			b = [encoded characterAtIndex:index++] - 63;
			result |= (b & 0x1f) << shift;
			shift += 5;
		} while (b >= 0x20);
		NSInteger dlng = ((result & 1) ? ~(result >> 1) : (result >> 1));
		lng += dlng;
		NSNumber *latitude = [[NSNumber alloc] initWithFloat:lat * 1e-5];
		NSNumber *longitude = [[NSNumber alloc] initWithFloat:lng * 1e-5];
		CLLocation *loc = [[CLLocation alloc] initWithLatitude:[latitude floatValue] longitude:[longitude floatValue]];
		[array addObject:loc];
	}
	
	return array;
}


-(MKCoordinateRegion)getRegionForBounds:(NSDictionary*)bounds{
    CLLocationCoordinate2D annotationCenter;
    CGFloat southwestLat,southwestLng,northeastLat,northeastLng; 
    northeastLat = [[[bounds objectForKey:@"northeast"] objectForKey:@"lat"] floatValue];
    northeastLng = [[[bounds objectForKey:@"northeast"] objectForKey:@"lng"] floatValue];
    
    southwestLat = [[[bounds objectForKey:@"southwest"] objectForKey:@"lat"] floatValue];
    southwestLng = [[[bounds objectForKey:@"southwest"] objectForKey:@"lng"] floatValue];
    
    
    MKCoordinateSpan span;
    span.longitudeDelta =  fabs(northeastLng - southwestLng) ;
	span.latitudeDelta =  fabs(northeastLat - southwestLat);
    annotationCenter.longitude = (northeastLng + southwestLng)/2;
    annotationCenter.latitude = (northeastLat + southwestLat)/2;
    span.longitudeDelta = span.longitudeDelta*1.1;
    span.latitudeDelta = span.latitudeDelta*1.3;
    MKCoordinateRegion region;
	region.center = annotationCenter;
	region.span = span;
    
    return region;
}

#pragma mark -
#pragma mark Map Delegate

- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id)overlay {
    
    if ([overlay isKindOfClass:[MKPolyline class]]) {
        MKPolylineView* routeLineView = [[MKPolylineView alloc] initWithPolyline:self.routeLine];
        routeLineView.fillColor = self.fillColor;
        routeLineView.strokeColor = self.strokeColor;
        routeLineView.lineWidth = self.lineWidth;
        return routeLineView;
    } else if ([overlay isKindOfClass:[MKCircle class]]){
        MKCircleView* circleView = [[MKCircleView alloc]initWithOverlay:overlay];
        circleView.fillColor = self.fillColor;
        return circleView;
    }
	return nil;
}

#pragma mark - 
#pragma mark Connection Delegate Methods

-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
	[webData setLength: 0];
    NSLog(@"start receiving data...");
}
-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
	[webData appendData:data];
}
-(void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
	[SVProgressHUD dismissWithError:@"Internet connection error!" afterDelay:1];
}
-(void)connectionDidFinishLoading:(NSURLConnection *)connection
{
	//NSLog(@"DONE. Received Bytes: %d", [webData length]);
	
	//NSString *result = [[NSString alloc] initWithBytes: [webData mutableBytes] length:[webData length] encoding:NSUTF8StringEncoding];
    NSData *res = [[NSData alloc] initWithBytes:[webData mutableBytes] length:[webData length]];
    
	NSError * error;
    NSDictionary *respondDictionary = [NSJSONSerialization JSONObjectWithData:res options:kNilOptions error:&error];
    
    NSArray *array = [respondDictionary objectForKey:@"routes"];
    NSDictionary *route = [array lastObject];
    NSDictionary *bounds = [route objectForKey:@"bounds"];
    
    if([[respondDictionary objectForKey:@"status"] isEqualToString:@"ZERO_RESULTS"]) {
        [SVProgressHUD dismissWithError:@"Cannot calculate route between points!" afterDelay:1];
        
        return;
    }  else if([[respondDictionary objectForKey:@"status"] isEqualToString:@"OK"]){
        
        NSDictionary *polDict = [route objectForKey:@"overview_polyline"];
        NSMutableArray *polyLine = [self decodePolyLine:[NSMutableString stringWithString:[polDict objectForKey:@"points"]]];
        
        
        CLLocationCoordinate2D* pointArr = malloc(sizeof(CLLocationCoordinate2D) * polyLine.count);
        for(int idx = 0; idx < polyLine.count; idx++)
        {
            // break the string down even further to latitude and longitude fields.
            CLLocation *loc = [polyLine objectAtIndex:idx];
            
            // create our coordinate and add it to the correct spot in the array
            CLLocationCoordinate2D coordinate = loc.coordinate;
            
            pointArr[idx] = coordinate;
            
        }
        
        [self.mapView setRegion:[self.mapView regionThatFits:[self getRegionForBounds:bounds]] animated:YES];
        if(self.routeLine){
            [self.mapView removeOverlay:self.routeLine];
            self.routeLine = nil;
            
        }
        self.routeLine = [MKPolyline polylineWithCoordinates:pointArr
                                                       count:[polyLine count]];
        
        [self.mapView addOverlay:self.routeLine];
        //[polyLine release];
        
        free(pointArr);
        directionsView.steps = [[[route objectForKey:@"legs"] lastObject] objectForKey:@"steps"];
        
        
        [SVProgressHUD dismissWithSuccess:[NSString stringWithFormat:@"%@ \n %@ ",[[[[route objectForKey:@"legs"] lastObject] objectForKey:@"distance"]objectForKey:@"text"],[[[[route objectForKey:@"legs"] lastObject] objectForKey:@"duration"]objectForKey:@"text"]] afterDelay:2];
        
        
    } else {
        [SVProgressHUD dismissWithError:@"Unknow error!" afterDelay:1];
    }
}

#pragma mark -
#pragma mark DirectionView Delegate

-(void)moveToPoint:(CLLocationCoordinate2D)location{
    
    [self.mapView setCenterCoordinate:location animated:YES];
    [self.mapView removeOverlay:circle];
    /* Need to find way to animate that overlay */
    circle = [MKCircle circleWithCenterCoordinate:location radius:50.f];
    [self.mapView addOverlay:circle];
   
}

#pragma mark -

-(void)dealloc{
    self.mapView.delegate = nil;
    self.routeLine = nil;
    self.startPoint = nil;
    self.endPoint = nil;
    self.strokeColor = nil;
    self.fillColor = nil;
    self.mapView = nil;
    //[super dealloc];
}

@end
