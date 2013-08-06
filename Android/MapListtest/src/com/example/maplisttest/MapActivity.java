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
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;




public class MapActivity extends Activity 
{
    private org.osmdroid.views.MapView mapView;
    ItemizedOverlay<OverlayItem> mMyLocationOverlay;
    private MapController mapController;
    private ResourceProxy mResourceProxy;
    private Builder builder;
    private LocationManager lms;
	private String bestProvider = LocationManager.GPS_PROVIDER;
	private boolean getGPSService;
	private Location location;
	private int type = 0 ;//type for redraw map, 0:route map , 1:configure overlay
	private boolean shelterSwitch = true;
	private boolean hospitalSwitch = true;
	private boolean firestationSwitch = true;
	private boolean policeSwitch = true;
	
	
	
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
	
	int length = 1 ;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		
		File dir = Config.context.getExternalFilesDir(null);
		
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		    .detectDiskReads()
		    .detectDiskWrites()
		    .detectNetwork()   // or .detectAll() for all detectable problems
		    .penaltyLog()
		    .build());
		    
		    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		    .detectLeakedSqlLiteObjects()
		    .penaltyLog()
		    .penaltyDeath()
		    .build());
		
		
		LocationManager locationManager; 
		String context = Context.LOCATION_SERVICE; 
		locationManager = (LocationManager)getSystemService(context); 
		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_FINE); 
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		criteria.setPowerRequirement(Criteria.POWER_LOW); 
		String provider = locationManager.getBestProvider(criteria, true); 
		
		location = locationManager.getLastKnownLocation(provider); 
	
		File inFile = new File(dir, "All.txt");
		
		if(inFile.exists())
		{
			String is = StreamTool.readFromFile(inFile);
			 try {
				 	GetJsonFromDevice.Firestation(is,facilities);
				 } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
		}
		else
		{
			String os = null;

		    try 
		    {
				GetJsonFromIS.Firestation("http://mad-is.iis.sinica.edu.tw/facilities.json",facilities);
				SortByMinDistance.firestation(facilities,location.getLatitude() , location.getLongitude());
				os = StreamTool.JSONEncode(facilities);
			} 
		    catch (Exception e) 
			{
				e.printStackTrace();
			}

		    File outFile = new File(dir, "All.txt");
		    StreamTool.writeToFile(outFile, os);
		}
  
	    

	    while(facilities[length].name != null)
	    {
	    	length++;
	    }
	    
	    length--;
	    
		

		initialmap(location);

		locationManager.requestLocationUpdates(provider, 100000, 10,locationListener); 
    	
		
    	
   	 	final ProgressDialog dialog = ProgressDialog.show(MapActivity.this,"Loading now...", "Please wait",true);
        new Thread(new Runnable()
        {
            @Override
            public void run() 
            {
                try
                {
                	routemap(new GeoPoint(location.getLatitude(),location.getLongitude()), new GeoPoint(facilities[0].lat,facilities[0].lon),0);
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
        
        Button button = (Button)findViewById(R.id.button02); 
        button.setOnClickListener(new Button.OnClickListener()
        {
        	@Override
	        public void onClick(View v) 
        	{
		        // TODO Auto-generated method stub 
		        Intent intent = new Intent();
		        intent.setClass(MapActivity.this, MainActivity.class);
		        startActivity(intent); 
		        MapActivity.this.finish(); 
	        }
        });
        
        
        ImageButton Sbutton = (ImageButton)findViewById(R.id.ShelterButton); 
        Sbutton.setOnClickListener(new ImageButton.OnClickListener(){
        @Override
        public void onClick(View v) {
        // TODO Auto-generated method stub 
        	int i = 0 ;
        	
        	if(shelterSwitch)
        	{
        		shelterSwitch = false;
        	}
        	else
        		shelterSwitch = true;
        	
        	while(!(facilities[i].fac_type.equals("School")))
        	{
        		i++;
        	}
        	
        	routemap(new GeoPoint(location.getLatitude(),location.getLongitude()), new GeoPoint(facilities[i].lat,facilities[i].lon),1);
        
        }
        });

        ImageButton Hbutton = (ImageButton)findViewById(R.id.HospitalButton); 
        Hbutton.setOnClickListener(new ImageButton.OnClickListener(){
        @Override
        public void onClick(View v) {
        // TODO Auto-generated method stub 
     
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
        	routemap(new GeoPoint(location.getLatitude(),location.getLongitude()), new GeoPoint(facilities[i].lat,facilities[i].lon),1);
        
        	
        }
        });
        
        ImageButton Fbutton = (ImageButton)findViewById(R.id.FireStationButton); 
        Fbutton.setOnClickListener(new ImageButton.OnClickListener(){
        @Override
        public void onClick(View v) {
        // TODO Auto-generated method stub 
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
        	routemap(new GeoPoint(location.getLatitude(),location.getLongitude()), new GeoPoint(facilities[i].lat,facilities[i].lon),1);
        
        }
        });
        
        ImageButton Pbutton = (ImageButton)findViewById(R.id.PoliceButton); 
        Pbutton.setOnClickListener(new ImageButton.OnClickListener(){
        @Override
        public void onClick(View v) {
        // TODO Auto-generated method stub 
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
        	routemap(new GeoPoint(location.getLatitude(),location.getLongitude()), new GeoPoint(facilities[i].lat,facilities[i].lon),1);
        
        }
        });
        
        
        //Toast.makeText(this, facilities[0].name, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	
	    } 
	}
 
	private final LocationListener locationListener = new LocationListener() 
	{ 
		public void onLocationChanged(Location location) 
		{ 
			
		} 

		public void onProviderDisabled(String provider)
		{ 
			 
		} 

		public void onProviderEnabled(String provider){ } 
		public void onStatusChanged(String provider, int status,Bundle extras){ } 
	};

  

	public Location locationServiceInitial() 
	{
		/* 取得系統定位服務 */
		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		   
		/* 確認是否開啟GPS服務or網路定位服務 */
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) 
		{
			getGPSService=true;
			lms = (LocationManager) getSystemService(LOCATION_SERVICE); // 取得系統定位服務
			Criteria criteria = new Criteria(); // 資訊提供者選取標準
			bestProvider = lms.getBestProvider(criteria, true);
			Location location = lms.getLastKnownLocation(bestProvider); // 使用GPS定位座標
		    
			if(location != null)
			{
				return location;
			}
			else
			{
				Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
				return null;
			}
		} 
		else 
		{
			Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
			// 開啟設定畫面
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			return null;
		} 
	}
	 
	 
	public void routemap(GeoPoint startPoint, GeoPoint endPoint, int type)
	{
		
		mapView.getOverlays().clear();
		
		final ArrayList<ExtendedOverlayItem> roadItems = new ArrayList<ExtendedOverlayItem>();
		
		ItemizedOverlayWithBubble<ExtendedOverlayItem> roadNodes = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(this, roadItems, mapView);
		
		mapView.getOverlays().add(roadNodes);
		
		
		final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		Drawable drawable1 = this.getResources().getDrawable(R.drawable.ic_launcher);
		//圖層的坐標必須在離線地圖訪問內
		OverlayItem oitem1 = new OverlayItem("User", "User Position", new GeoPoint(location.getLatitude(),location.getLongitude()));
		oitem1.setMarker(drawable1);//設置overlayitem圖片
		items.add(oitem1); 
		
		int count = 0;
		
		for(int i = 0 ; i <= length ; i++)
		{
			OverlayItem oitem = new OverlayItem(facilities[i].fac_type, facilities[i].name, new GeoPoint(facilities[i].lat,facilities[i].lon));
			oitem.setMarker(setmarker(i));//設置overlayitem圖片
			if((facilities[i].fac_type.equals("School") && shelterSwitch) || 
				(facilities[i].fac_type.equals("Hospital") && hospitalSwitch) ||
				(facilities[i].fac_type.equals("FireStation") && firestationSwitch) ||
				(facilities[i].fac_type.equals("Police") && policeSwitch) )
			{
				items.add(oitem);
	
				temp[count] = facilities[i];
				count++;
				
			}
			
		}
		//自定義圖層的使用
    	this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
    			//重載點擊overlayitem的反應
    			new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>()
    			{
    				@Override
    				public boolean onItemSingleTapUp(final int index, final OverlayItem item)
    				{
    					if(index != 0)
    					{
	    					builder = new AlertDialog.Builder(MapActivity.this);
	    		            
	    		            builder.setTitle("Detail Information");
	    		            
	    		            builder.setMessage("Name:" + temp[index-1].name + "\n" + "\n" + 
	    		            					"Telephone:" + temp[index-1].tel + "\n" + "\n" +
	    		            					"Address:" + temp[index-1].addr + "\n" + "\n" +
	    		            					"Distance:" + temp[index-1].dist + "m" );
	    		            
	    		            
	    		            builder.setCancelable(false);
	    		            
	    		            
	    		            builder.setPositiveButton("Navigate", new DialogInterface.OnClickListener(){
	    		            @Override
	    		            public void onClick(DialogInterface dialog, int which) {
	    		                    //按確定要作的事情
	    		            	 final ProgressDialog Pdialog = ProgressDialog.show(MapActivity.this,
	    		                         "Routing now...", "Please wait",true);
	    		                 new Thread(new Runnable(){
	    		                     @Override
	    		                     public void run() {
	    		                         try{
	    		                        	 routemap(new GeoPoint(location.getLatitude(),location.getLongitude()), new GeoPoint(temp[index-1].lat,temp[index-1].lon),0);
	    		                         }
	    		                         catch(Exception e){
	    		                             e.printStackTrace();
	    		                         }
	    		                         finally{
	    		                             Pdialog.dismiss();
	    		                         }
	    		                     } 
	    		                }).start();	
	    		              }
	    		            });
	    		            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
	    		              @Override
	    		               public void onClick(DialogInterface dialog, int which) {
	    		                    //按取消要作的事情
	    		              }
	    		            });
	    		            builder.create();
	    		            builder.show();
    					}
    					return true;
    				}
     
    				@Override
                    public boolean onItemLongPress(final int index,final OverlayItem item) 
    				{
    					return true;
                    }
    			}, mResourceProxy);
    	//添加相關的圖層
    	mapView.getOverlays().add(this.mMyLocationOverlay);
    	
    	if(type == 0)//navigate overlay setting
    	{
		
			RoadManager roadManager = new MapQuestRoadManager();
			roadManager.addRequestOption("routeType=pedestrian");
			ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
			
			waypoints.add(startPoint);
			waypoints.add(endPoint);
			
			Road road = roadManager.getRoad(waypoints);
			
			PathOverlay roadOverlay = RoadManager.buildRoadOverlay(road, mapView.getContext());
			
			mapView.getOverlays().add(roadOverlay);
			
			Drawable marker = getResources().getDrawable(R.drawable.marker_node);
		    for (int i=0; i<road.mNodes.size(); i++)
		    {
		    	RoadNode node = road.mNodes.get(i);
		    	ExtendedOverlayItem nodeMarker = new ExtendedOverlayItem("Step "+i, "", node.mLocation, this);
		        nodeMarker.setDescription(node.mInstructions);
		        nodeMarker.setSubDescription(road.getLengthDurationText(node.mLength, node.mDuration));
		        Drawable icon;
		        
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
		        	icon = getResources().getDrawable(R.drawable.ic_roundabout);
		        }
		        
		        nodeMarker.setImage(icon);
		        nodeMarker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
		        nodeMarker.setMarker(marker);
		        roadNodes.addItem(nodeMarker);
		    }
    	}
		mapView.invalidate();
    	
	}
	
	public void initialmap(final Location location)
	{
		mapView = (org.osmdroid.views.MapView) findViewById(R.id.map);
		mResourceProxy=new DefaultResourceProxyImpl(this);
    
		mapView.setTileSource(new GoogleOffLineXYTileSource(
             "Google Maps", //download folder name 
             ResourceProxy.string.unknown, 
             12, //min zoomlevel
             18, //max zoomlevel
             256,
             ".png",//圖片的格式
			 "http://mt3.google.com/vt/v=w2.97")); 
		
		mapView.setUseDataConnection(true);//connect to Internet or not
		mapController= mapView.getController();
		mapController.setCenter(new GeoPoint(location.getLatitude(),location.getLongitude())); //set default point
		mapController.setZoom(15);
		mapView.setBuiltInZoomControls(true);  
		mapView.setMultiTouchControls(true); 
		mapView.setClickable(false);//fixed cannot move the map
	
    
	}

	private Drawable setmarker(int i)
	{
		Drawable d = null;
		
		Drawable drawable2 = this.getResources().getDrawable(R.drawable.shelter);
		
		Drawable drawable3 = this.getResources().getDrawable(R.drawable.h);
		
		Drawable drawable4 = this.getResources().getDrawable(R.drawable.fire);
		
		Drawable drawable5 = this.getResources().getDrawable(R.drawable.police);
		
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
		 else if(facilities[i].fac_type.equals("School") || facilities[i].fac_type.equals("Sport"))
		 {
			 d = drawable2;
		 }
		
		return d;
	}
	
}
