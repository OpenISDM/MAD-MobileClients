package com.example.maplisttest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.maplisttest.DataStructure.Facilities;


public class GetJsonFromDevice 
{
	public static void Firestation(String body, Facilities facilities[]) throws Exception
	{
    	
    	
    	JSONArray jArray = new JSONArray(body);
    	
       	for(int i=0; i<jArray.length(); i++)
    	{
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
	public static String getContent(String url) throws Exception
	{
		StringBuilder sb = new StringBuilder();

		HttpClient client = new DefaultHttpClient();
		
		HttpParams httpParams = client.getParams();
		//設置網路超時參數
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		
		HttpResponse response = client.execute(new HttpGet(url));
		
		HttpEntity entity = response.getEntity();
		
		if (entity != null) 
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8192);

			String line = null;
			
			while ((line = reader.readLine())!= null)
			{
				sb.append(line + "\n");
			}
			
			reader.close();
		}
		
		return sb.toString();
	}
}
