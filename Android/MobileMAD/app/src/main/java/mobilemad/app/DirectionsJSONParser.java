package mobilemad.app;

/**
 * Copyright (c) 2014  OpenISDM
 *
 * Project Name:
 *   Mobile Clients for MAD
 *
 * Version:
 *   1.0
 *
 * File Name:
 *   DirectionsJSONParser.java
 *
 * Abstract:
 *   DirectionsJSONParser.java is the class files in Mobile Clients for MAD project.
 *   DirectionsJSONParser will be used in Mobile Clients for MAD app as:
 *   1. Parse Google APIs for Maps data in JSON format for navigation.
 *   2. Create the poly to draw into Google Maps in MapsFragment.
 *
 * Authors:
 *   Andre Lukito, routhsauniere@gmail.com
 *
 * License:
 *  GPL 3.0 This file is subject to the terms and conditions defined
 *  in file 'COPYING.txt', which is part of this source code package.
 *
 * Major Revision History:
 *   2014/5/13: complete version 1.0
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {

  /**
   * Function Name:
   *   parse
   *
   * Function Description:
   *   Receives a JSONObject and returns a list of lists containing latitude and longitude.
   *
   * Parameters:
   *   JSONObject jObject - content from JSONObject object as an Object.
   *
   * Returned Value:
   *   If the function returned normally, the returned is
   *   List<List<HashMap<String, String>>>;
   *   otherwise, the returned value is null.
   *
   * Possible Error Code or Exception:
   *   Different structure.
   */
  public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
    List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
    JSONArray jRoutes = null;
    JSONArray jLegs = null;
    JSONArray jSteps = null;

    try {
      jRoutes = jObject.getJSONArray("routes");

      /** Traversing all routes */
      for (int i = 0; i < jRoutes.length(); i++) {
        jLegs = ((JSONObject)jRoutes.get(i)).getJSONArray("legs");
        List path = new ArrayList<HashMap<String, String>>();

        /** Traversing all legs */
        for(int j = 0; j < jLegs.length(); j++) {
          jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

          /** Traversing all steps */
          for (int k = 0; k < jSteps.length(); k++) {
            String polyline = "";
            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
            List<LatLng> list = decodePoly(polyline);

            /** Traversing all points */
            for (int l = 0; l < list.size(); l++){
              HashMap<String, String> hm = new HashMap<String, String>();
              hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
              hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
              path.add(hm);
            }
          }
          routes.add(path);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }catch (Exception e){
    }

    return routes;
  }

  /**
   * Function Name:
   *   decodePoly
   *
   * Function Description:
   *   Method to decode polyline points.
   *   Courtesy :
   *      jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
   *
   * Parameters:
   *   String encoded - representation of poly in String.
   *
   * Returned Value:
   *   If the function returned normally, the returned is List<LatLng>;
   *   otherwise, the returned value is null.
   *
   * Possible Error Code or Exception:
   *   Different structure.
   */
  private List<LatLng> decodePoly(String encoded) {
    List<LatLng> poly = new ArrayList<LatLng>();
    int index = 0;
    int len = encoded.length();
    int lat = 0;
    int lng = 0;

    while (index < len) {
      int b;
      int shift = 0;
      int result = 0;

      do {
        b = encoded.charAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);

      int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
      lat += dlat;

      shift = 0;
      result = 0;

      do {
        b = encoded.charAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);

      int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
      lng += dlng;

      LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
      poly.add(p);
    }

    return poly;
  }
}