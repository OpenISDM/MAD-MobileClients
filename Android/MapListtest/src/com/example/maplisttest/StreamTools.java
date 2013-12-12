package com.example.maplisttest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;

import android.location.Location;

import com.example.maplisttest.DataStructure.Facilities;


/******************************************************************************
 *  Class name: StreamTools
 *  Inheritance: N/A
 *  Methods: ReadFromFile, WriteToFile, JSONEncode, CombineRoute, 
 *  		 SortByMinDistance, getDistance, GetJSONFromIS,
 *           GetJSONFromDevice, getContent
 *  Functionality: Show all the information on map.  
******************************************************************************/

/**
 * @author      Academia Sinica
 * @version     $Revision: 1.0 $, $Date:  2013/11/05  $
 * @since       
 * @classname	StreamTools
 */
public class StreamTools {
    /**************************************************************************
	 *  Method name: ReadFromFile
	 *  Functionality: Read data from file
	 *  @param: File fin: input file
	 *  @return: String
	**************************************************************************/
	public static String ReadFromFile(File fin) {
	     StringBuilder data = new StringBuilder();
	     
	     BufferedReader reader = null;
	     
	     String line;
	     
	     try {
	         reader = 
	        		 new BufferedReader(new InputStreamReader(new FileInputStream(fin),"utf-8"));
	         
	         while ((line = reader.readLine()) != null) {
	             data.append(line);
	         }
	     } 
	     catch (Exception e) {
	         ;
	     } 
	     finally {
	         try {
	             reader.close();
	         } 
	         catch (Exception e) {
	             ;
	         }
	     }
	     
	     return data.toString();
	}
	 
    /**************************************************************************
	 *  Method name: WriteToFile
	 *  Functionality: Write data to file
	 *  @param: File fout : output file
	 *          String data : output data
	 *  @return: N/A
	**************************************************************************/	
	public static void WriteToFile(File fout, String data) {
		 FileOutputStream osw = null;
		 
		 try {
			 osw = new FileOutputStream(fout);
			 
		     osw.write(data.getBytes());
		     
		     osw.flush();
		 } 
		 catch (Exception e) {
		        ;
		 } 
		 finally {
			 try {
				 osw.close();  
			 } 
			 catch (Exception e) {
		            ;
		     }
		 }
	}
	 
    /**************************************************************************
	 *  Method name: JSONEncode
	 *  Functionality: Encode facilities structure to JSONArray format string
	 *  @param: Facilities[] facilities : facilities information
	 *  @return: String
	**************************************************************************/	
	public static String JSONEncode(Facilities[] facilities) throws JSONException { 
		 int length = 1 ;

		 while(facilities[length].name != null){
		    length++;
		 }
		    
		 length--;
		    
	     JSONArray jsonArray = new JSONArray();
	     
	     for(int i = 0 ; i <= length ; i++){
	            JSONObject jsonObject = new JSONObject();
	            
	            jsonObject.put("name", facilities[i].name);
	            
	            jsonObject.put("fac_type", facilities[i].fac_type); 
	            
	            jsonObject.put("addr", facilities[i].addr);
	            
	            jsonObject.put("tel", facilities[i].tel); 
	            
	            jsonObject.put("lat", facilities[i].lat); 
	            
	            jsonObject.put("lon", facilities[i].lon); 
	            
	            jsonObject.put("dist", facilities[i].dist); 
	            
	            jsonArray.put(jsonObject);
	     }
     
	     return jsonArray.toString();
	}
	 
    /**************************************************************************
	 *  Method name: CombineRoute
	 *  Functionality: Combine route information into StringBuilder
	 *  @param: Road road : Routed road
	 *  @return: StringBuilder
	**************************************************************************/	
	public static StringBuilder CombineRoute(Road road){
		int min = 0,sec;
		
		StringBuilder route = new StringBuilder("Step ").append("0 : \n");
		
		RoadNode node = road.mNodes.get(0);
		
		sec = (int)(node.mDuration);
		
		if(sec >= 60){
			min = sec/60;
			
			sec = sec - min*60;
			
		}
		
		route = route.append(node.mInstructions).append("\n");
		
		if(min >0){
			/* spend over 1 minute */
			route = route.append("Distance : ")
						 .append((int)((node.mLength)*1000))
						 .append("m , Duration : ")
						 .append(min)
						 .append("min\n");
		}
		else{
			route = route.append("Distance : ")
						 .append((int)((node.mLength)*1000))
						 .append("m , Duration : ")
						 .append(sec)
						 .append("sec\n");
		}
		
		route = route.append("---\n");
		
		for (int i=1; i<road.mNodes.size(); i++){	
	 		node = road.mNodes.get(i);
	 		
	 		sec = (int)(node.mDuration);
			
			if(sec >= 60){
				min = sec/60;
				
				sec = sec - min*60;
				
			}
			
	 		route = route.append("Step ").append(i).append(" : \n");
	 		route = route.append(node.mInstructions).append("\n");
	 		if(min >0){
				route = route.append("Distance : ")
							 .append((int)((node.mLength)*1000))
							 .append("m , Duration : ")
							 .append(min)
							 .append("min\n");
			}
			else{
				route = route.append("Distance : ")
							 .append((int)((node.mLength)*1000))
							 .append("m , Duration : ")
							 .append(sec)
							 .append("sec\n");
			}
	 		route = route.append("---\n");
		}
			
		return route;
	}
	
    /**************************************************************************
	 *  Method name: SortByMinDistance
	 *  Functionality: Sort facilities information by minimum distance
	 *  @param: Facilities facilities[] : facilities information
				double userlat : user position's latitude
				double userlon : user position's longitude
	 *  @return: N/A
	**************************************************************************/
	public static void SortByMinDistance(Facilities facilities[],
										 double userlat,double userlon){
		int length = 0;
			
		int count;
			
		Facilities temp = new Facilities();
			
		while(facilities[length].name != null){
			facilities[length].dist = (int) getDistance(facilities[length].lat,
														facilities[length].lon,
														userlat, userlon);
			
			length++;
		}
			
		count = length;
			
		for(int j = 0 ; j < length ; j++){
			/* Bubble Sort */
			for(int i = 0 ; i < count ; i++){
				if(getDistance(facilities[i].lat, facilities[i].lon,userlat, userlon ) > (getDistance(facilities[i+1].lat, facilities[i+1].lon,userlat, userlon))){
					temp = facilities[i];
						
					facilities[i] = facilities[i+1];
						
					facilities[i+1] = temp;			
				}
					
			}
			count--;
		}
			
	}
	
    /**************************************************************************
	 *  Method name: getDistance
	 *  Functionality: get distance between object1 & 2
	 *  @param: double lat1 : object1's latitude 
	    		double lon1 : object1's longitude
			    double lat2 : object2's latitude
			    double lon2 : object2's longitude
	 *  @return: double
	**************************************************************************/
	private static double getDistance(double lat1, double lon1,
			                          double lat2, double lon2) {  
		float[] results=new float[1];  
			
		Location.distanceBetween(lat1, lon1, lat2, lon2, results); 
			
		return results[0];  
	}  
	
    /**************************************************************************
	 *  Method name: GetJSONFromIS
	 *  Functionality: get JSON data from IS
	 *  @param: String url : IS's address
	   			Facilities facilities[] : facilities information
	 *  @return: N/A
	**************************************************************************/
	public static void GetJSONFromIS (String url, Facilities facilities[]) 
			throws Exception{
    	String body = getContent(url);
    	
    	JSONArray jArray = new JSONArray(body);
    	
       	for(int i=0; i<jArray.length(); i++){
    		JSONObject obj = jArray.getJSONObject(i);
    		
    		facilities[i].name = obj.getString("name");
    		
    		facilities[i].fac_type = obj.getString("fac_type");
    		
    		facilities[i].addr = obj.getString("addr");
    		
    		facilities[i].tel = obj.getString("tel");
    		
    		facilities[i].lat = obj.getDouble("lat");
    		
    		facilities[i].lon = obj.getDouble("lon");
    	}
	}
	
    /**************************************************************************
	 *  Method name: GetJSONFromDevice
	 *  Functionality: get JSON data from local device
	 *  @param: String data : local device file string
	   			Facilities facilities[] : facilities information
	 *  @return: N/A
	**************************************************************************/
	public static void GetJSONFromDevice(String data, Facilities facilities[])throws Exception{
    	JSONArray jArray = new JSONArray(data);
    	
       	for(int i=0; i<jArray.length(); i++){
    		JSONObject obj = jArray.getJSONObject(i);
    		
    		facilities[i].name = obj.getString("name");
    		
    		facilities[i].fac_type = obj.getString("fac_type");
    		
    		facilities[i].addr = obj.getString("addr");
    		
    		facilities[i].tel = obj.getString("tel");
    		
    		facilities[i].lat = obj.getDouble("lat");
    		
    		facilities[i].lon = obj.getDouble("lon");
    		
    		facilities[i].dist = obj.getInt("dist");
    	}
	}
	
    /**************************************************************************
	 *  Method name: getContent
	 *  Functionality: get string from url
	 *  @param: String url 
	 *  @return: string
	**************************************************************************/
	private static String getContent(String url) throws Exception{
		StringBuilder sb = new StringBuilder();

		HttpClient client = new DefaultHttpClient();
		
		HttpParams httpParams = client.getParams();
		
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		
		HttpResponse response = client.execute(new HttpGet(url));
		
		HttpEntity entity = response.getEntity();
		
		if (entity != null) {
			BufferedReader reader = 
					new BufferedReader(new InputStreamReader(
										entity.getContent(), "UTF-8"), 8192);

			String line = null;
			
			while ((line = reader.readLine())!= null){
				sb.append(line + "\n");
			}
			
			reader.close();
		}
		
		return sb.toString();
	}
}
