package com.example.maplisttest;

import java.io.File;
import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.bonuspack.overlays.ItemizedOverlayWithBubble;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import com.example.maplisttest.DataStructure.Facilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;



/******************************************************************************
 *  Class name: MapActivity
 *  Inheritance: N/A
 *  Methods: onCreateView, LocationListener, onConfigurationChanged, routemap,
 *           InitialMap
 *  Functionality: Show all the information on map.  
******************************************************************************/
public class MapActivity extends Activity 
{
    private org.osmdroid.views.MapView mapView;
    
    ItemizedOverlay<OverlayItem> mMyLocationOverlay;
    
    private MapController mapController;
    
    private ResourceProxy mResourceProxy;
    
    private Builder builder;

	private Location location;

	private boolean shelterSwitch = true;
	
	private boolean hospitalSwitch = true;
	
	private boolean firestationSwitch = true;
	
	private boolean policeSwitch = true;
	
	int length = 1 ;//Length of facilities data 
	
	Facilities [] facilities = new Facilities[300];
	
	Facilities [] temp = new Facilities[300];
	
	public MapActivity ()
	{
		for (int i=0; i < 300; ++i)
		{
			facilities[i] = new Facilities();
		}
		for (int i=0; i < 300; ++i)
		{
			temp[i] = new Facilities();
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		File dir;
		
		LocationManager locationManager; 
		
		String context;

		File inFile;	//Input file
		
		File outFile;	//Output file
		
		String is;	//String from input file
		
		String os;	//String to output file
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mapview);
		
		dir = Config.context.getExternalFilesDir(null);
		
		Button button;
		
		ImageButton shelterButton;
		
		ImageButton hospitalButton;
		
		ImageButton firestationButton;
		
		ImageButton policeButton;

		
		
		/* fixed NetworkInMainThread Problem */
	    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	    						   .detectDiskReads()
	    						   .detectDiskWrites()
	    						   .detectNetwork()   
	    	/* or .detectAll() for all detectable problems */
	    						   .penaltyLog()
	    						   .build());
	    
	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	    					   .detectLeakedSqlLiteObjects()
	    					   .penaltyLog()
	    					   .penaltyDeath()
	    					   .build());
	    
	    
	    /* Set LocationManager */
		context = Context.LOCATION_SERVICE; 
		
		locationManager = 
				(LocationManager)Config.context.getSystemService(context); 
		
		Criteria criteria = new Criteria(); 
		
		criteria.setAccuracy(Criteria.ACCURACY_FINE); 
		
		criteria.setAltitudeRequired(false); 
		
		criteria.setBearingRequired(false); 
		
		criteria.setCostAllowed(true); 
		
		criteria.setPowerRequirement(Criteria.POWER_LOW); 
		
		String provider = locationManager.getBestProvider(criteria, true); 

		location = locationManager.getLastKnownLocation(provider); 

		locationManager.requestLocationUpdates(provider, 10000, 10,
											   locationListener);
	
		inFile = new File(dir, "All.txt");
		
		if(inFile.exists())
		{
			/* Check if file exist */
			is = StreamTools.ReadFromFile(inFile);
			
			 try
			 {
				 StreamTools.GetJSONFromDevice(is,facilities);
				 
			 } 
			 catch (Exception e)
			 {
				e.printStackTrace();
			 }		
			 
		}
		else
		{
			/* If not, get data from IS */
			os = null;

		    try 
		    {
		    	StreamTools.GetJSONFromIS(Config.FacilitiesURL,facilities);
		    	
				StreamTools.SortByMinDistance(facilities,
						                      location.getLatitude(), 
						                      location.getLongitude());
				
				os = StreamTools.JSONEncode(facilities);
			} 
		    catch (Exception e) 
			{
				e.printStackTrace();
			}

		    outFile = new File(dir, "All.txt");
		    
		    StreamTools.WriteToFile(outFile, os);
		    
		}
  
	    while(facilities[length].name != null)
	    {
	    	/* Calculate the length of facilities data*/
	    	length++;
	    }
	    
	    length--;
	    
	    InitialMap(location);

		locationManager.requestLocationUpdates(provider, 100000, 10,
											   locationListener); 
    	
		
    	/* Build Progress Dialog to process heavy load function */
   	 	final ProgressDialog dialog = ProgressDialog.show(MapActivity.this,
   	 													  "Loading now...",
   	 													  "Please wait",true);
        new Thread(new Runnable()
        {
            @Override
            public void run() 
            {
                try
                {
                	/* heavy load function */
                	RouteMap(new GeoPoint(location.getLatitude(),
                						  location.getLongitude()),
                						  new GeoPoint(facilities[0].lat,
                								       facilities[0].lon),0);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    dialog.dismiss();
                }
            } 
        }).start();	
        
        button = (Button)findViewById(R.id.button02); 
        
        button.setOnClickListener(new Button.OnClickListener()
        {
        	@Override
	        public void onClick(View v) 
        	{
		        Intent intent = new Intent();
		        
		        intent.setClass(MapActivity.this, MainActivity.class);
		        
		        startActivity(intent); 
		        
		        MapActivity.this.finish(); 
	        }
        });
        
        
        shelterButton = (ImageButton)findViewById(R.id.ShelterButton);
        
        shelterButton.setOnClickListener(new ImageButton.OnClickListener()
        {
        	@Override
        	public void onClick(View v) 
        	{
	        	int i = 0 ;
	        	
	        	if(shelterSwitch)
	        	{
	        		shelterSwitch = false;
	        	}
	        	else
	        	{	        		
	        		shelterSwitch = true;
	        	}
	        	
	        	while(!(facilities[i].fac_type.equals("School")))
	        	{
	        		i++;
	        	}
	        	
	        	RouteMap(new GeoPoint(location.getLatitude(),
	        						  location.getLongitude()),
	        			 new GeoPoint(facilities[i].lat,facilities[i].lon),1);
	        
        	}
        });

        hospitalButton = (ImageButton)findViewById(R.id.HospitalButton); 
        
        hospitalButton.setOnClickListener(new ImageButton.OnClickListener()
        {
        	@Override
        	public void onClick(View v) 
        	{
	        	int i = 0 ;
	        	
	        	if(hospitalSwitch)
	        	{
	        		hospitalSwitch = false;
	        	}
	        	else
	        		hospitalSwitch = true;
	        	
	        	while(!(facilities[i].fac_type.equals("Hospital")))
	        	{
	        		i++;
	        	}
	        	RouteMap(new GeoPoint(location.getLatitude(),
	        			              location.getLongitude()), 
	        			 new GeoPoint(facilities[i].lat,facilities[i].lon),1);
	        
        	}
        });
        
        firestationButton = (ImageButton)findViewById(R.id.FireStationButton); 
        
        firestationButton.setOnClickListener(new ImageButton.OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
	        	int i = 0 ;
	        	
	        	if(firestationSwitch)
	        	{
	        		firestationSwitch = false;
	        	}
	        	else
	        		firestationSwitch = true;
	        	
	        	while(!(facilities[i].fac_type.equals("FireStation")))
	        	{
	        		i++;
	        	}
	        	RouteMap(new GeoPoint(location.getLatitude(),
	        			              location.getLongitude()),
	        			 new GeoPoint(facilities[i].lat,facilities[i].lon),1);

        	}
        });
        
        policeButton = (ImageButton)findViewById(R.id.PoliceButton); 
        
        policeButton.setOnClickListener(new ImageButton.OnClickListener()
        {
        	@Override
        	public void onClick(View v) 
        	{
	        	int i = 0 ;
	        	
	        	if(policeSwitch)
	        	{
	        		policeSwitch = false;
	        	}
	        	else
	        		policeSwitch = true;
	        	
	        	while(!(facilities[i].fac_type.equals("Police")))
	        	{
	        		i++;
	        	}
	        	RouteMap(new GeoPoint(location.getLatitude(),
	        			              location.getLongitude()), 
	        			 new GeoPoint(facilities[i].lat,facilities[i].lon),1);    
	        }
        });
 
	}
	
	
	/**************************************************************************
	 *  Method name: onConfigurationChanged
	 *  Functionality: Avoid execute onCreate when orientation change 
	 *  @param: Configuration
	 *  @return: N/A
	**************************************************************************/
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if(newConfig.orientation ==Configuration.ORIENTATION_LANDSCAPE)
        {
            
        }
        else if(newConfig.orientation ==Configuration.ORIENTATION_PORTRAIT)
        {
            
        }
    }
 
    /**************************************************************************
	 *  Method name: LocationListener
	 *  Functionality: Listening location status changed
	 *  @param: N/A
	 *  @return: N/A
	**************************************************************************/
	private final LocationListener locationListener = new LocationListener()
	{ 
		public void onLocationChanged(Location location) 
		{ 
			//updateWithNewLocation(location); 
		} 

		public void onProviderDisabled(String provider)
		{ 
			//updateWithNewLocation(null); 
		} 

		public void onProviderEnabled(String provider)
		{ 
			
		} 
		public void onStatusChanged(String provider,int status,Bundle extras)
		{ 
			
		} 
	};
	 
    /**************************************************************************
	 *  Method name: RouteMap
	 *  Functionality: Route to place which show on map
	 *  @param: GeoPoint startPoint : The route's start point  
	 			GeoPoint endPoint	: The route's end point
	   			int type			: which type to do this function
	                                  value 0 : navigate ,1 : initial 
	 *  @return: N/A
	**************************************************************************/
	public void RouteMap(GeoPoint startPoint, GeoPoint endPoint, int type)
	{
		Drawable icon;
		
		Drawable marker;
		
		RoadManager roadManager;
		
		RoadNode node;
		
		Road road;
		
		PathOverlay roadOverlay;
		
		mapView.getOverlays().clear();
		
		final ArrayList<ExtendedOverlayItem> roadItems = 
				new ArrayList<ExtendedOverlayItem>();
		
		ItemizedOverlayWithBubble<ExtendedOverlayItem> roadNodes = 
				new ItemizedOverlayWithBubble<ExtendedOverlayItem>(this,
																   roadItems,
																   mapView);
		
		mapView.getOverlays().add(roadNodes);
		
		final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		Drawable drawable1 = this.getResources()
								 .getDrawable(R.drawable.ic_launcher);
		
		OverlayItem oitem1 = new OverlayItem("User", "User Position",
											 new GeoPoint(
													 location.getLatitude(),
													 location.getLongitude()));
		
		oitem1.setMarker(drawable1);//set the marker icon
		
		items.add(oitem1); 
		
		int count = 0;
		
		for(int i = 0 ; i <= length ; i++)
		{
			OverlayItem oitem = new OverlayItem(facilities[i].fac_type,
												facilities[i].name, 
												new GeoPoint(
														facilities[i].lat,
														facilities[i].lon));
			
			oitem.setMarker(setmarker(i));
			
			if((facilities[i].fac_type.equals("School") 
					&& shelterSwitch) 
				|| (facilities[i].fac_type.equals("Hospital") 
					&& hospitalSwitch)
				|| (facilities[i].fac_type.equals("FireStation") 
					&& firestationSwitch) 
				|| (facilities[i].fac_type.equals("Police") 
					&& policeSwitch) )
			{
				items.add(oitem);
	
				temp[count] = facilities[i];
				
				count++;
				
			}
			
		}
		//Create overlay by self
    	this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
    			
    			new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>()
    			{
    				@Override
    				public boolean onItemSingleTapUp(final int index, 
    						                         final OverlayItem item)
    				{
    					if(index != 0)
    					{
    						/* Show the message when click map icon except user's position*/
	    					builder = new AlertDialog.Builder(MapActivity.this);
	    		            
	    		            builder.setTitle("Detail Information");
	    		            
	    		            builder.setMessage("Name:" + 
	    		                                temp[index-1].name + "\n\n" + 
	    		            					"Telephone:" + 
	    		                                temp[index-1].tel + "\n\n" +
	    		            					"Address:" + 
	    		                                temp[index-1].addr + "\n\n" +
	    		            					"Distance:" + 
	    		                                temp[index-1].dist + "m" );
	    		            
	    		            builder.setCancelable(false);
	    		            
	    		            
	    		            builder.setPositiveButton("Navigate", 
	    		            						  new DialogInterface
	    		            						  .OnClickListener()
	    		            {
	    		            	@Override
	    		            	public void onClick(DialogInterface dialog,
	    		            			            int which) 
	    		            	{
	    		            		/* To do when press Positive Button*/
	   	        
		    		            	 final ProgressDialog Pdialog = 
		    		            			 ProgressDialog.show(
		    		            					 MapActivity.this,
		    		            					 "Routing now...",
		    		            					 "Please wait",true);
		    		                 new Thread(new Runnable()
		    		                 {
		       /* Build Progress Dialog to process heavy load function */
		    		                     @Override
		    		                     public void run() 
		    		                     {
		    		                         try
		    		                         {
		    		                        	 RouteMap(
		    		                        		new GeoPoint(
		    		                        				location
		    		                        				.getLatitude(),
		    		                        				location
		    		                        				.getLongitude()),
		    		                        		new GeoPoint(
		    		                        				temp[index-1].lat,
		    		                        				temp[index-1].lon),
		    		                        		0);
		    		                         }
		    		                         catch(Exception e)
		    		                         {
		    		                             e.printStackTrace();
		    		                         }
		    		                         finally
		    		                         {
		    		                             Pdialog.dismiss();
		    		                         }
		    		                     } 
		    		                }).start();	
	    		            	}
	    		            });
	    		            builder.setNegativeButton("Cancel",
	    		            						  new DialogInterface
	    		            						  .OnClickListener()
	    		            {
	    		            	@Override
	    		            	public void onClick(DialogInterface dialog,
	    		            			            int which) 
	    		            	{
	    		                    /* To do when press Negative Button*/
	    		            	}
	    		            });
	    		            
	    		            builder.create();
	    		            
	    		            builder.show();
    					}
    					return true;
    				}
     
    				@Override
                    public boolean onItemLongPress(final int index,
                    							   final OverlayItem item) 
    				{
    					return true;
                    }
    			}, mResourceProxy);
    	
    	mapView.getOverlays().add(this.mMyLocationOverlay);
    	
    	if(type == 0)//navigate overlay setting
    	{
			roadManager = new MapQuestRoadManager();
			
			roadManager.addRequestOption("routeType=pedestrian");
			
			ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
			
			waypoints.add(startPoint);
			
			waypoints.add(endPoint);
			
			road = roadManager.getRoad(waypoints);
			
			roadOverlay =
					RoadManager.buildRoadOverlay(road, mapView.getContext());
			
			mapView.getOverlays().add(roadOverlay);
			
			marker = 
					getResources().getDrawable(R.drawable.marker_node);
			
		    for (int i=0; i<road.mNodes.size(); i++)
		    {
		    	node = road.mNodes.get(i);
		    	
		    	ExtendedOverlayItem nodeMarker =
		    			new ExtendedOverlayItem("Step "+i, "",
		    					                node.mLocation, this);
		    	
		        nodeMarker.setDescription(node.mInstructions);
		        
		        nodeMarker.setSubDescription(
		        		road.getLengthDurationText(node.mLength,
		        				                   node.mDuration));
		      
		        if(node.mManeuverType == 1)
		        {
		        	icon = getResources().getDrawable(R.drawable.t1);
		        }
		        else if(node.mManeuverType == 3)
		        {
		        	icon = getResources().getDrawable(R.drawable.t3);
		        }
		        else if(node.mManeuverType == 4)
		        {
		        	icon = getResources().getDrawable(R.drawable.t4);
		        }
		        else if(node.mManeuverType == 5)
		        {
		        	icon = getResources().getDrawable(R.drawable.t5);
		        }
		        else if(node.mManeuverType == 6)
		        {
		        	icon = getResources().getDrawable(R.drawable.t6);
		        }
		        else if(node.mManeuverType == 7)
		        {
		        	icon = getResources().getDrawable(R.drawable.t7);
		        }
		        else if(node.mManeuverType == 8)
		        {
		        	icon = getResources().getDrawable(R.drawable.t8);
		        }
		        else if(node.mManeuverType == 24)
		        {
		        	icon = getResources().getDrawable(R.drawable.t24);
		        }
		        else
		        {
		        	icon= getResources().getDrawable(R.drawable.ic_roundabout);
		        }
		        
		        nodeMarker.setImage(icon);
		        
		        nodeMarker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
		        
		        nodeMarker.setMarker(marker);
		        
		        roadNodes.addItem(nodeMarker);
		    }
    	}
		mapView.invalidate();
    	
	}
    /**************************************************************************
	 *  Method name: InitialMap
	 *  Functionality: initial the map view content
	 *  @param: Location location
	 *  @return: N/A
	**************************************************************************/
	public void InitialMap(final Location location)
	{
		mapView = (org.osmdroid.views.MapView) findViewById(R.id.map);
		
		mResourceProxy=new DefaultResourceProxyImpl(this);
    
		mapView.setTileSource(new GoogleOffLineXYTileSource(
             "Google Maps", //download folder name 
             ResourceProxy.string.unknown, 
             12, //min zoomlevel
             18, //max zoomlevel
             256,
             ".png",
			 "http://mt3.google.com/vt/v=w2.97")); 
		
		mapView.setUseDataConnection(true);//connect to Internet or not
		
		mapController= mapView.getController();
		
		mapController.setCenter(new GeoPoint(location.getLatitude(),
				                             location.getLongitude())); 
		
		mapController.setZoom(15);
		
		mapView.setBuiltInZoomControls(true); 
		
		mapView.setMultiTouchControls(true); 
		
		mapView.setClickable(false);//fixed cannot move the map
	
	}

	private Drawable setmarker(int i)
	{
		/* Set markers which show on map */
		Drawable d = null;
		
		Drawable drawable2 = 
				this.getResources().getDrawable(R.drawable.shelter);
		
		Drawable drawable3 = 
				this.getResources().getDrawable(R.drawable.h);
		
		Drawable drawable4 = 
				this.getResources().getDrawable(R.drawable.fire);
		
		Drawable drawable5 = 
				this.getResources().getDrawable(R.drawable.police);
		
		 if(facilities[i].fac_type.equals("FireStation"))
		 {
			 d = drawable4;
		 }
		 else if(facilities[i].fac_type.equals("Hospital"))
		 {
			 d = drawable3;
		 }
		 else if(facilities[i].fac_type.equals("Police"))
		 {
			 d =drawable5;
		 }
		 else if(facilities[i].fac_type.equals("School") 
				 || facilities[i].fac_type.equals("Sport"))
		 {
			 d = drawable2;
		 }
		
		return d;
	}
	
}
