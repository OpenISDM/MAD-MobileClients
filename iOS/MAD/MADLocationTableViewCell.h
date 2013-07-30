//
//  MADLocationTableViewCell.h
//  MAD2
//
//  Created by Derrick Cheng on 8/5/12.
//  Copyright (c) 2012 UC Berkeley EECS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MADLocationTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel * distanceLabel;
@property (weak, nonatomic) IBOutlet UILabel * titleLabel;

@end
