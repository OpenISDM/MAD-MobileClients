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

/******************************************************************************
 *  Class name: FireStationListView
 *  Inheritance: ListViewTab
 *  Methods: onCreateView , LocationListener
 *  Functionality: Show the Firestation information from open data.  
******************************************************************************/
public class FireStationListView extends Fragment {
	private Builder builder,builderR;
	
	ArrayList<HashMap<String,Object>> list = 
			 new ArrayList<HashMap<String,Object>>();
	
	private View v;
		
	public Location location;
	
	public JSONArray jArray;

	public Facilities [] facilities = new Facilities[300];
	
	public FireStationListView (){
		for (int i=0; i < 300; ++i){
			facilities[i] = new Facilities();
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	        // TODO Auto-generated method stub
	        super.onCreate(savedInstanceState);	    
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		File dir = Config.context.getExternalFilesDir(null);	
		//Get External Files Directory
		
		LocationManager locationManager; 
		
		File inFile;	//Input file
		
		File outFile;	//Output file
		
		String is;	//String from input file
		
		String os;	//String to output file
		
		int length = 1 ;	//Length of facilities data 
		
		v = inflater.inflate(R.layout.fragment1_layout, container, false);
		
		
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
		String context = Context.LOCATION_SERVICE; 
		
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
		
		
		inFile = new File(dir, "FireStation.txt");
		
		if(inFile.exists()){
			/* Check if file exist */
			is = StreamTools.ReadFromFile(inFile);
			try {
				StreamTools.GetJSONFromDevice(is,facilities);
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		else{
			/* If not, get data from IS */
			os = null;

		    try {
		    	StreamTools.GetJSONFromIS(Config.FirestationURL,facilities);
		    	
				StreamTools.SortByMinDistance(facilities,
											  location.getLatitude(),
											  location.getLongitude());
				
				os = StreamTools.JSONEncode(facilities);
			} 
		    catch (Exception e) {
				e.printStackTrace();
			}

		    outFile = new File(dir, "FireStation.txt");
		    
		    StreamTools.WriteToFile(outFile, os);
		}
  
	    while(facilities[length].name != null){
	    	/* Calculate the length of facilities data*/
	    	length++;
	    }
	    
	    length--;
	    
	  
		 for(int j=0; j<=length; j++){
			 /* Add data into arraylist */
			 HashMap<String,Object> item = new HashMap<String,Object>();
			 	
			 item.put("pic", R.drawable.fire);
			 
			 item.put( "Fac_Name", facilities[j].name);
			
			 item.put("Distance", 
					  "Distance:"+Integer.toString(facilities[j].dist)+"m");
			 
			 list.add( item );
		 }
		 
		 ListView mListView = (ListView)v.findViewById(R.id.listView);
		 
		 if( mListView != null ) {
			 /* Set ListView content */
			 mListView.setAdapter(new SimpleAdapter(
					 			  getActivity(),list,
		        				  R.layout.mylayout1,
		        				  new String[] {"pic","Fac_Name","Distance"},
				 				  new int[] {R.id.imageView1,R.id.textView1,
				 							 R.id.textView2 } ));
		 }
		 
		 mListView.setOnItemClickListener(new OnItemClickListener(){   
			 @Override
		     public void onItemClick(AdapterView<?> parent, View view,
		        		             final int position, long id){
				 builder = new AlertDialog.Builder(getActivity());
		            
		         builder.setTitle("Detail Information");
		            
		         builder.setMessage(
		        		 "Name:" + facilities[position].name + "\n\n" + 
		        		 "Telephone:" + facilities[position].tel+"\n\n"+
		            	 "Address:" + facilities[position].addr +"\n\n"+
		            	 "Distance:" + facilities[position].dist +"m" );
		            
		            
		         builder.setCancelable(false);
		            
		            
		         builder.setPositiveButton(
		        		 "Navigate",new DialogInterface.OnClickListener(){ 
		             @Override
		             public void onClick(DialogInterface dialog, int which){
		            	 /* To do when press "Navigate" */
			             RoadManager roadManager = 
			            		 new MapQuestRoadManager();
			            	
			        	 roadManager.addRequestOption(
			        			 "routeType=pedestrian");
			        		
			        	 ArrayList<GeoPoint> waypoints =
			        			 new ArrayList<GeoPoint>();
			        		
			        	 waypoints.add(new GeoPoint(location.getLatitude(),
			        								location.getLongitude()));
			        		
			        	 waypoints.add(new GeoPoint(facilities[position].lat,
			        			 					facilities[position].lon));
			        		
			        	 Road road = roadManager.getRoad(waypoints);
			     
			             builderR = new AlertDialog.Builder(getActivity());
				            
				         builderR.setTitle("Navigate to "+
				        		 		   facilities[position].name);
				            
				         builderR.setMessage(StreamTools.CombineRoute(road));
				            
				         builderR.setCancelable(false);

				         builderR.setNegativeButton(
				        		 "Cancel",new DialogInterface.OnClickListener(){
				        			 @Override
				            		 public void onClick(
				            				 DialogInterface dialog,int which){
				            				/*To do when press Cancel*/
				            		 }
				            	 });
				            
			            builderR.create();
		
			            builderR.show();
		              }
		            });
     
		         builder.setNegativeButton(
		        		 "Cancel",new DialogInterface.OnClickListener(){
		        			 @Override
		        			 public void onClick(DialogInterface dialog,
		        					 			 int which){
		        				 /*To do when press Cancel*/
		        			 }
		        		 });
		            
		         builder.create();
		            
		         builder.show();
		     }
		   
		 });
		 
		 return v;
    }

	/*******************************************************
	 *  Method name: LocationListener
	 *  Functionality: Listening location status changed
	 *  @param: N/A
	 *  @return: N/A
	*******************************************************/
	private final LocationListener locationListener = new LocationListener(){ 
		public void onLocationChanged(Location location) { 
			//updateWithNewLocation(location); 
		} 

		public void onProviderDisabled(String provider){ 
			//updateWithNewLocation(null); 
		} 

		public void onProviderEnabled(String provider){ 
			
		} 
		public void onStatusChanged(String provider,int status,Bundle extras){ 
			
		} 
	};

}
