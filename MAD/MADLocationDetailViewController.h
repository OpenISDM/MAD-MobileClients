//
//  MADLocationDetailViewController.h
//  MAD2
//
//  Created by Derrick Cheng on 7/25/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import <Foundation/Foundation.h>
@class MADLocation;
@class MKUserLocation;

@interface MADLocationDetailViewController : UITableViewController
@property (nonatomic, strong) MADLocation * location;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *lonLabel;
@property (weak, nonatomic) IBOutlet UILabel *latLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UILabel *typeLabel;
@property (weak, nonatomic) IBOutlet UIButton *directionsButton;
//-(IBAction)getDirectionsTo:(id)sender;

@end
