package com.example.maplisttest;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import com.example.maplisttest.DataStructure.Facilities;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;




public class AllListView extends Fragment {
	
	JSONArray jArray;
	
	String data;

	Facilities [] facilities = new Facilities[300];
	private Builder builder,builderR;
	 /** Called when the activity is first created. */
	 ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	// private SimpleAdapter adapter;
 	
	public AllListView ()
	{
		for (int i=0; i < 300; ++i)
		{
			facilities[i] = new Facilities();
		}
		
	}
	//private ListView listView;
	private View v;
	Location location;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	        // TODO Auto-generated method stub
	        super.onCreate(savedInstanceState);
	        
	    
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) 
	{
	
		File dir = Config.context.getExternalFilesDir(null);
		
		v = inflater.inflate(R.layout.fragment1_layout, container, false);
		
		//fixed NetworkInMainThread Problem
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
	  //fixed NetworkInMainThread Problem
	    
		LocationManager locationManager; 
		String context = Context.LOCATION_SERVICE; 
		locationManager = (LocationManager)Config.context.getSystemService(context); 
		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_FINE); 
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		criteria.setPowerRequirement(Criteria.POWER_LOW); 
		String provider = locationManager.getBestProvider(criteria, true); 

		location = locationManager.getLastKnownLocation(provider); 
		
		//updateWithNewLocation(location);

		locationManager.requestLocationUpdates(provider, 10000, 10,locationListener);
		
		
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
  
	    int length = 1 ;

	    while(facilities[length].name != null)
	    {
	    	length++;
	    }
	    
	    length--;
	    
	  //把資料加入ArrayList中
		 for(int j=0; j<=length; j++)
		 {
			 HashMap<String,Object> item = new HashMap<String,Object>();
			 
			 if(facilities[j].fac_type.equals("FireStation"))
			 {
				 item.put("pic", R.drawable.fire);
			 }
			 else if(facilities[j].fac_type.equals("Hospital"))
			 {
				 item.put("pic", R.drawable.h);
			 }
			 else if(facilities[j].fac_type.equals("Police"))
			 {
				 item.put("pic", R.drawable.police);
			 }
			 else if(facilities[j].fac_type.equals("School") || facilities[j].fac_type.equals("Sport"))
			 {
				 item.put("pic", R.drawable.shelter);
			 }
			 item.put( "Fac_Name", facilities[j].name);
			
			 item.put( "Distance", "Distance:" + Integer.toString(facilities[j].dist) + "m");
			 
			 list.add( item );
		 }
		 ListView mListView = (ListView)v.findViewById(R.id.listView);
		 if( mListView != null ) {
		         mListView.setAdapter( 
		        		 new SimpleAdapter( 
		        				 getActivity(), 
		        				 list,
		        				 R.layout.mylayout1,
		        				 new String[] { "pic","Fac_Name","Distance"},
		        				 new int[] { R.id.imageView1, R.id.textView1, R.id.textView2 } ));
		 }
		 mListView.setOnItemClickListener(new OnItemClickListener(){

		        @Override
		        public void onItemClick(AdapterView<?> parent, View view, final int position,
		            long id) {
		            // TODO Auto-generated method stub
		            
		            builder = new AlertDialog.Builder(getActivity());
		            
		            builder.setTitle("Detail Information");
		            
		            builder.setMessage("Name:" + facilities[position].name + "\n" + "\n" + 
		            					"Telephone:" + facilities[position].tel + "\n" + "\n" +
		            					"Address:" + facilities[position].addr + "\n" + "\n" +
		            					"Distance:" + facilities[position].dist + "m" );
		            
		            
		            builder.setCancelable(false);
		            
		            
		            builder.setPositiveButton("Navigate", new DialogInterface.OnClickListener(){
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                    //按確定要作的事情
		            	RoadManager roadManager = new MapQuestRoadManager();
		        		roadManager.addRequestOption("routeType=pedestrian");
		        		ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
		        		
		        		waypoints.add(new GeoPoint(location.getLatitude() , location.getLongitude()));
		        		waypoints.add(new GeoPoint(facilities[position].lat,facilities[position].lon));
		        		
		        		Road road = roadManager.getRoad(waypoints);
		     
		            	
		                builderR = new AlertDialog.Builder(getActivity());
			            
			            builderR.setTitle("Navigate to " + facilities[position].name);
			            
			            builderR.setMessage(StreamTool.combineRoute(road));
			            
			            builderR.setCancelable(false);
			            
			            
			
			            builderR.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			              @Override
			               public void onClick(DialogInterface dialog, int which) {
			                    //按取消要作的事情
			              }
			            });
			            builderR.create();
		
			            builderR.show();
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
		   
		    });
		 return v;
    }

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

		public void onProviderEnabled(String provider){ } 
		public void onStatusChanged(String provider, int status,Bundle extras){ } 
	};

}
