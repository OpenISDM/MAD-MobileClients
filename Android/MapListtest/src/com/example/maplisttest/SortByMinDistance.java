package com.example.maplisttest;



import com.example.maplisttest.DataStructure.Facilities;


import android.location.Location;

public class SortByMinDistance
{
	public static void firestation(Facilities facilities[],double userlat,double userlon)
	{
		int length = 0;
		
		int count;
		
		Facilities temp = new Facilities();
		
		while(facilities[length].name != null)
		{
			facilities[length].dist = (int) getDistance(facilities[length].lat , facilities[length].lon , userlat , userlon );
			length++;
		}
		
		count = length;
		
		for(int j = 0 ; j < length ; j++)//bubble sort
		{
			for(int i = 0 ; i < count ; i++)
			{
				if(getDistance(facilities[i].lat , facilities[i].lon , userlat , userlon ) > (getDistance(facilities[i+1].lat , facilities[i+1].lon , userlat , userlon )))
				{
					temp = facilities[i];
					
					facilities[i] = facilities[i+1];
					
					facilities[i+1] = temp;			
				}
				
			}
			count--;
		}
		
	}
	
	
	
	private static double getDistance(double lat1, double lon1, double lat2, double lon2) 
	{  
		float[] results=new float[1];  
		
		Location.distanceBetween(lat1, lon1, lat2, lon2, results); 
		
		return results[0];  
	}  


}
