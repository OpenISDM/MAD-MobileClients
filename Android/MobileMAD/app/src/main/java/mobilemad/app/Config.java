package mobilemad.app;

/**
 * Copyright (c) 2014  OpenISDM
 *
 * Project Name:
 *   Mobile Clients for MAD
 *
 * Version:
 *   1.0
 *
 * File Name:
 *   Config.java
 *
 * Abstract:
 *   Config.java is the class files in Mobile Clients for MAD project.
 *   Config will be used as global protected variable that can be use in another class.
 *   Some of variable can be edited and other variable can't be edited or act as CONSTANT value.
 *   CONSTANT variable name will be created with All Uppercase style.
 *
 * Authors:
 *   Andre Lukito, routhsauniere@gmail.com
 *
 * License:
 *  GPL 3.0 This file is subject to the terms and conditions defined
 *  in file 'COPYING.txt', which is part of this source code package.
 *
 * Major Revision History:
 *   2014/5/01: complete version 1.0
 */

public class Config {
  protected static String path = "";
  protected final static String IMG_LOCATION =
      "http://media.nj.com/ledgerupdates_impact/photo/2010/08/nb-mappng-f3544c4d29006cfa_large.png";
  protected final static String FILE_LOCATION =
      "http://blog.paultondeur.com/files/2010/UnityExternalJSONXML/books.json";
  protected final static String IMG_LOCATION1 = "http://140.109.22.181/image";
  protected final static String FILE_LOCATION1 = "http://140.109.22.181/topic";
  protected final static String IMG_LOCATION2 = "http://140.109.17.48/image";
  protected final static String FILE_LOCATION2 = "http://140.109.17.48/topic";
  protected final static String IMG_LOCATION3 =
      "http://140.109.22.197/static/Topic/TWN-112-583552/TWN-112-583552.png";
  protected final static String FILE_LOCATION3 =
      "http://140.109.22.197/static/Topic/TWN-112-583552/TWN-112-583552.json";

  protected final static String SHELTER_INDOOR = "Shelter(Indoor)";
  protected final static String SHELTER_OUTDOOR = "Shelter(Outdoor)";
  protected final static String MEDICAL = "Medical";
  protected final static String RESCUE = "Rescue";
  protected final static String LIVELIHOOD = "Livelihood";
  protected final static String COMMUNICATION = "Communication";
  protected final static String VOLUNTEER_ASSOCIATION = "Volunteer association";
  protected final static String TRANSPORTATION = "Transportation";
}