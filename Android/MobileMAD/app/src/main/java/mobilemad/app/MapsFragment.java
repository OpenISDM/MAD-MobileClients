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
 *   MapsFragment.java
 *
 * Abstract:
 *   MapsFragment.java is the tab Maps in Mobile Clients for MAD project.
 *   It will be associate with Google Maps to show current location and navigation from selected
 *   item on tab List.
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment implements LocationListener {
  private View rootView;
  private SupportMapFragment supportMapFragment;
  private GoogleMap googleMap;
  private Marker marker;
  private LocationManager locationManager;
  private Location hLocation;
  private DialogFragment alertDlgFragment;
  private TextView txtName, txtType, txtTelephone, txtDistrict, txtAddress;
  private ImageView ivIcon;

  private DownloaderHTTP downloaderHTTP;
  private DataViewer dataViewer;
  protected static String name, type, category, telephone, district, address;
  protected static double latitude, longitude, deviceLatitude, deviceLongitude;
  private String provider;
  final int RQS_GooglePlayServices = 10;

  /**
   * Returns direction URL that created to match with Google APIs for Maps.
   */
  private String getDirectionsUrl(LatLng origin, LatLng dest) {
    String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
    String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
    String sensor = "sensor=false";

    /*String waypoints = "";
    for(int i = 2; i < markerPoints.size(); i++) {
      LatLng point  = (LatLng) markerPoints.get(i);
      if(i==2)
        waypoints = "waypoints=";
      waypoints += point.latitude + "," + point.longitude + "|";
    }*/

    String parameters = str_origin + "&" + str_dest + "&" + sensor/* + "&" + waypoints*/;
    String output = "json";
    String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

    return url;
  }

  private class DownloadDirection extends AsyncTask<String, Void, String> {

    /**
     * Fetching the data from web service.
     */
    @Override
    protected String doInBackground(String... urls) {
      String data = "";
      InputStream downloadStream = null;

      try {
        downloadStream = downloaderHTTP.downloadUrl(urls[0]);

        if (downloadStream != null) {
          BufferedReader br = new BufferedReader(new InputStreamReader(downloadStream));

          StringBuffer sb  = new StringBuffer();

          String line = "";
          while ((line = br.readLine()) != null){
            sb.append(line);
          }

          data = sb.toString();

          br.close();
        }
      } catch(Exception ex) {
        Log.d("DownloadTask", ex.getMessage());
      } finally {
        try {
          if (downloadStream != null) {
            downloadStream.close();
          }
        } catch (IOException e) {}
      }

      return data;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      ParserTask parserTask = new ParserTask();

      /**
       * Invokes the thread for parsing the JSON data
       */
      parserTask.execute(result);
    }
  }

  private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    /**
     * Parse the Google Places in JSON format
     */
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
      JSONObject jObject;
      List<List<HashMap<String, String>>> routes = null;

      try {
        jObject = new JSONObject(jsonData[0]);
        DirectionsJSONParser parser = new DirectionsJSONParser();

        routes = parser.parse(jObject);
      } catch(Exception e) {
        e.printStackTrace();
      }

      return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {

      ArrayList<LatLng> points = null;
      PolylineOptions lineOptions = null;

      /**
       * Traversing through all the routes
       */
      for (int i = 0; i < result.size(); i++) {
        points = new ArrayList<LatLng>();
        lineOptions = new PolylineOptions();

        /**
         * Fetching i-th route
         */
        List<HashMap<String, String>> path = result.get(i);

        /**
         * Fetching all the points in i-th route
         */
        for (int j = 0; j < path.size(); j++) {
          HashMap<String,String> point = path.get(j);

          double lat = Double.parseDouble(point.get("lat"));
          double lng = Double.parseDouble(point.get("lng"));
          LatLng position = new LatLng(lat, lng);

          points.add(position);
        }

        /**
         * Adding all the points in the route to LineOptions
         */
        lineOptions.addAll(points);
        lineOptions.width(2);
        lineOptions.color(Color.RED);
      }

      /**
       * Drawing polyline in the Google Map for the i-th route
       */
      googleMap.addPolyline(lineOptions);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) { // 1
    super.onCreate(savedInstanceState);

    downloaderHTTP = new DownloaderHTTP();
    dataViewer = new DataViewer();

    /**
     * Get the location manager
     */
    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    /**
     * Define the criteria how to select the location provider -> use default
     */
    Criteria criteria = new Criteria();
    provider = locationManager.getBestProvider(criteria, false);

    if ((provider != null) && (!provider.equals(""))) {

      /**
       * Get the location from the given provider
       */
      hLocation = locationManager.getLastKnownLocation(provider);
      locationManager.requestLocationUpdates(provider, 20000, 1, this);

      if (hLocation != null) {
        Log.i("location", "Provider " + provider + " has been selected.");

        /**
         * Initialize the location fields
         */
        onLocationChanged(hLocation);
      } else {
        Log.i("location", "Location not available");
        dataViewer.showMessage(getActivity().getApplicationContext(), "Location can't be retrieved");
      }

    } else {
      alertDlgFragment = AlertDialogFragment.newInstance("Location Services Not Active",
          "Please enable Location Services and GPS", 2);
      alertDlgFragment.setCancelable(false);
      alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.maps_fragment, container, false);

    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    /**
     * Get child fragment inside MapsFragment to show the maps in the child fragment.
     */
    FragmentManager fm = getChildFragmentManager();
    supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.mapView);

    if (supportMapFragment == null) {
      supportMapFragment = SupportMapFragment.newInstance();
      fm.beginTransaction().replace(R.id.mapView, supportMapFragment).commit();
    }
  }

  @Override
  public void onPause() {
    super.onPause();

    locationManager.removeUpdates(this);
  }

  @Override
  public void onResume() {
    super.onResume();

    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

    /**
     * Check availability of Google Play services.
     */
    if (resultCode == ConnectionResult.SUCCESS) {
      if (googleMap == null) {
        double lat, lng;

        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);

        googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
          @Override
          public View getInfoWindow(Marker arg0) {
            return null;
          }

          @Override
          public View getInfoContents(Marker arg0) {
            View v = getLayoutInflater(getArguments()).inflate(R.layout.map_view_data, null);

            ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
            txtName = (TextView) v.findViewById(R.id.txtName);
            txtType = (TextView) v.findViewById(R.id.txtType);
            txtTelephone = (TextView) v.findViewById(R.id.txtTelephone);
            txtDistrict = (TextView) v.findViewById(R.id.txtDistrict);
            txtAddress = (TextView) v.findViewById(R.id.txtAddress);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                dataViewer.resourceIcon(category));

            ivIcon.setImageBitmap(bitmap);
            txtName.setText(name);
            txtType.setText("Type:" + type);
            txtDistrict.setText("District:" + district);
            txtAddress.setText("Address:" + address);
            txtTelephone.setText("Telephone:" + telephone);

            return v;
          }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
          @Override
          public void onInfoWindowClick(Marker marker) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

            /**
             * Check availability of Connectivity services (Wi-Fi or mobile) before
             * downloading the data.
             */
            if (downloaderHTTP.isNetworkAvailable(connectivityManager)) {
              LatLng origin = new LatLng(deviceLatitude, deviceLongitude);
              LatLng dest = new LatLng(latitude, longitude);

              /**
               * Getting URL to the Google Directions API
               */
              String url = getDirectionsUrl(origin, dest);

              DownloadDirection downloadDirection = new DownloadDirection();

              /**
               * Start downloading json data from Google Directions API
               */
              downloadDirection.execute(url);
            } else {
              alertDlgFragment = AlertDialogFragment.newInstance("No Network Available",
                  "Please enable the Mobile Data or Wi-Fi", 3);
              alertDlgFragment.setCancelable(false);
              alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
            }
          }
        });

        /**
         * Request to update the location based on selected provider every minimum time with the
         * Location listener.
         */
        locationManager.requestLocationUpdates(provider, 20000, 1, this);

        lat = deviceLatitude;
        lng = deviceLongitude;

        if ((latitude > 0) && (longitude > 0)) {

          /**
           * Put marker on Google Maps based on latitude and longitude from selected item on tab List.
           */
          marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
              .icon(BitmapDescriptorFactory.fromResource(dataViewer.resourceIcon(category))));

          /**
           * Show detailed info on marker in Google Maps.
           */
          marker.showInfoWindow();

          lat = latitude;
          lng = longitude;
        }

        /**
         * Move Camera into new location (latitude and longitude).
         */
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));
      }
    } else {

      /**
       * Google Play services was not available for some reason.
       */
      Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
          RQS_GooglePlayServices);
      dialog.setCancelable(false);
      dialog.show();
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    deviceLatitude = location.getLatitude();
    deviceLongitude = location.getLongitude();
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
  }

  @Override
  public void onProviderEnabled(String provider) {
  }

  @Override
  public void onProviderDisabled(String provider) {
  }
}
