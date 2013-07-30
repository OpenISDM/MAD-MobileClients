package com.example.jsondemo;


import org.json.JSONArray;

import com.example.jsondemo.DataStructure.*;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	
	TextView textView1;
	
	JSONArray jArray;
	
	String data;

	Facilities [] facilities = new Facilities[300];
    
  
 	
	public MainActivity ()
	{
		for (int i=0; i < 300; ++i)
		{
			facilities[i] = new Facilities();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
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
		locationManager = (LocationManager)getSystemService(context); 
		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_FINE); 
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		criteria.setPowerRequirement(Criteria.POWER_LOW); 
		String provider = locationManager.getBestProvider(criteria, true); 

		Location location = locationManager.getLastKnownLocation(provider); 

	//	initialmap(location);
		
		updateWithNewLocation(location);

		locationManager.requestLocationUpdates(provider, 10000, 10,locationListener);

    //Location location = locationServiceInitial();
	    try 
	    {
			GetJsonFromIS.Firestation("http://mad-is.iis.sinica.edu.tw/facilities/FireStation.json",facilities);
		} 
	    catch (Exception e) 
		{
			e.printStackTrace();
		}
	    
	    SortByMinDistance.firestation(facilities,location.getLatitude() , location.getLongitude());
	   
	    
	    int i = 1 ;
	    
	    String data = "Name:" + facilities[0].name +" dist: "+ facilities[0].dist + "m" + "\n";
		data += "Addr: "+ facilities[0].addr + "\n";
    	data += "Tel: "+ facilities[0].tel + "\n";
    	data += "Lat: "+ facilities[0].lat + "\n";
    	data += "Lon: "+ facilities[0].lon + "\n";
    	data += "------------------------------------------" + "\n";
	    
	    while(facilities[i].name != null)
	    {
	    	data += "Name:" + facilities[i].name +" dist: "+ facilities[i].dist + "m" + "\n";
	    	data += "Addr: "+ facilities[i].addr + "\n";
	    	data += "Tel: "+ facilities[i].tel + "\n";
	    	data += "Lat: "+ facilities[i].lat + "\n";
	    	data += "Lon: "+ facilities[i].lon + "\n";
	    	data += "------------------------------------------" + "\n";
	    	i++;
	    }
	    data +=  "\n\n\n\n\n";
	    TextView textView = (TextView)findViewById(R.id.textView1);
	    textView.setText(data);

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private final LocationListener locationListener = new LocationListener() 
	{ 
		public void onLocationChanged(Location location) 
		{ 
			updateWithNewLocation(location); 
		} 

		public void onProviderDisabled(String provider)
		{ 
			updateWithNewLocation(null); 
		} 

		public void onProviderEnabled(String provider){ } 
		public void onStatusChanged(String provider, int status,Bundle extras){ } 
	};
	
	public void updateWithNewLocation(Location location)
	{

		Context contextApp = getApplicationContext();

		String text ;
		int duration = 2000;

		if(location == null)
		{
			text ="Null Location";

		}
		else
		{
			text = "Lat : " + location.getLatitude() + " Lon: " + location.getLongitude();
		}

		Toast toast = Toast.makeText(contextApp, text, duration);
		toast.show();
	}

}
