package com.example.maplisttest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;

import com.example.maplisttest.DataStructure.Facilities;

public class StreamTool {
	
	public static byte[] readInputStream(InputStream is) throws IOException 
	{  
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024]; // 用数据装  
        int len = -1;  
        while ((len = is.read(buffer)) != -1) 
        {  
            outstream.write(buffer, 0, len);  
        }  
        outstream.close();  
        // 关闭流一定要记得。  
        return outstream.toByteArray();  
    } 
	
	
	public static String readFromFile(File fin) 
	{
	     StringBuilder data = new StringBuilder();
	     BufferedReader reader = null;
	     try 
	     {
	         reader = new BufferedReader(new InputStreamReader(new FileInputStream(fin), "utf-8"));
	         String line;
	         while ((line = reader.readLine()) != null) 
	         {
	             data.append(line);
	         }
	     } 
	     catch (Exception e) 
	     {
	         ;
	     } 
	     finally 
	     {
	         try 
	         {
	             reader.close();
	         } 
	         catch (Exception e) 
	         {
	             ;
	         }
	     }
	     
	     return data.toString();
	}
	 
		
	public static void writeToFile(File fout, String data) 
	{
		 FileOutputStream osw = null;
		 try 
		 {
			 osw = new FileOutputStream(fout);
		     osw.write(data.getBytes());
		     osw.flush();
		 } 
		 catch (Exception e) 
		 {
		        ;
		 } 
		 finally 
		 {
			 try 
			 {
				 osw.close();  
			 } 
			 catch (Exception e) 
			 {
		            ;
		     }
		 }
	}
	 
	public static String JSONEncode(Facilities[] facilities) throws JSONException 
	{ //將資料轉成JSON格式
		 int length = 1 ;

		 while(facilities[length].name != null)
		 {
		    length++;
		 }
		    
		 length--;
		    
	     JSONArray jsonArray = new JSONArray();
	     
	     for(int i = 0 ; i <= length ; i++)
	     {
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
	 
	public static StringBuilder combineRoute(Road road)
	{
		int min = 0,sec;
		
		StringBuilder route = new StringBuilder("Step ").append("0 : \n");
		
		RoadNode node = road.mNodes.get(0);
		
		sec = (int)(node.mDuration);
		
		if(sec >= 60)
		{
			min = sec/60;
			
			sec = sec - min*60;
			
		}
		
		route = route.append(node.mInstructions).append("\n");
		if(min >0)
		{
			route = route.append((int)((node.mLength)*1000)).append("m , ").append(min).append("min\n");
		}
		else
		{
			route = route.append((int)((node.mLength)*1000)).append("m , ").append(sec).append("sec\n");
		}
		
		route = route.append("---\n");
		
		for (int i=1; i<road.mNodes.size(); i++)
		{	
	 		node = road.mNodes.get(i);
	 		
	 		sec = (int)(node.mDuration);
			
			if(sec >= 60)
			{
				min = sec/60;
				
				sec = sec - min*60;
				
			}
			
	 		route = route.append("Step ").append(i).append(" : \n");
	 		route = route.append(node.mInstructions).append("\n");
	 		if(min >0)
			{
				route = route.append((int)((node.mLength)*1000)).append("m , ").append(min).append("min\n");
			}
			else
			{
				route = route.append((int)((node.mLength)*1000)).append("m , ").append(sec).append("sec\n");
			}
	 		route = route.append("---\n");
		}
			
		return route;
	}
}
