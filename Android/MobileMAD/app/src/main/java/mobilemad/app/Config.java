package mobilemad.app;

/**
 * Copyright (c) 2014  OpenISDM
 * <p/>
 * Project Name:
 * Mobile Clients for MAD
 * <p/>
 * Version:
 * 1.0
 * <p/>
 * File Name:
 * Config.java
 * <p/>
 * Abstract:
 * Config.java is the class files in Mobile Clients for MAD project.
 * Config will be used as global protected variable that can be use in another class.
 * Some of variable can be edited and other variable can't be edited or act as CONSTANT value.
 * CONSTANT variable name will be created with All Uppercase style.
 * <p/>
 * Authors:
 * Andre Lukito, routhsauniere@gmail.com
 * <p/>
 * License:
 * GPL 3.0 This file is subject to the terms and conditions defined
 * in file 'COPYING.txt', which is part of this source code package.
 * <p/>
 * Major Revision History:
 * 2014/5/01: complete version 1.0
 */

public class Config {
  protected final static String IMG_LOCATION = "http://192.168.1.10/cache/Cache.png";
  protected final static String FILE_LOCATION = "http://192.168.1.10/cache/Cache.rdf";
  protected final static String FILE1_LOCATION = "http://192.168.1.10/cache/Cache.json";
  protected final static String IMG_LOCATION1 = "http://140.109.22.142/cache/Cache.png";
  protected final static String FILE_LOCATION1 = "http://140.109.22.142/cache/Cache.rdf";
  protected final static String FILE1_LOCATION1 = "http://140.109.22.142/cache/Cache.json";
  protected final static String IMG_LOCATION2 = "http://140.109.22.153/topic/img";
  protected final static String FILE_LOCATION2 = "http://140.109.22.153/topic/rdf";
  protected final static String FILE1_LOCATION2 = "http://140.109.22.153/topic/json";


  protected final static String SHELTER_INDOOR = "Shelter(Indoor)";
  protected final static String SHELTER_OUTDOOR = "Shelter(Outdoor)";
  protected final static String MEDICAL = "Medical";
  protected final static String RESCUE = "Rescue";
  protected final static String LIVELIHOOD = "Livelihood";
  protected final static String COMMUNICATION = "Communication";
  protected final static String VOLUNTEER_ASSOCIATION = "Volunteer association";
  protected final static String TRANSPORTATION = "Transportation";
  protected static String path = "";
}