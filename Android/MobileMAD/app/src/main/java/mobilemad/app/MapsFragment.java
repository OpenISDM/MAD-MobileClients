package mobilemad.app;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
MapsFragment class as user view for maps.
 */
public class MapsFragment extends Fragment implements LocationListener {

  private View rootView;
  private SupportMapFragment supportMapFragment;
  private GoogleMap googleMap;
  Polyline polyline;
  private Marker marker;
  private LocationManager locationManager;
  private Location hLocation;
  private DialogFragment alertDlgFragment;
  private TextView txtName, txtType, txtTelephone, txtDistrict, txtAddress;
  private ImageView ivIcon;

  private DataViewer dataViewer;
  protected static String name, type, telephone, district, address;
  protected static double latitude, longitude, deviceLatitude, deviceLongitude;
  private String provider;
  final int RQS_GooglePlayServices = 10;

  /*
  Refresh the view when user back to this page. Override method from Fragment.
  Show message in Log that passed by ListFragment.
   */

  private String getDirectionsUrl(LatLng origin,LatLng dest){

    // Origin of route
    String str_origin = "origin="+origin.latitude+","+origin.longitude;

    // Destination of route
    String str_dest = "destination="+dest.latitude+","+dest.longitude;

    // Sensor enabled
    String sensor = "sensor=false";

    // Waypoints
    /*String waypoints = "";
    for(int i=2;i<markerPoints.size();i++){
      LatLng point  = (LatLng) markerPoints.get(i);
      if(i==2)
        waypoints = "waypoints=";
      waypoints += point.latitude + "," + point.longitude + "|";
    }*/

    // Building the parameters to the web service
    String parameters = str_origin+"&"+str_dest+"&"+sensor/*+"&"+waypoints*/;

    // Output format
    String output = "json";

    // Building the url to the web service
    String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

    return url;
  }

  private String downloadUrl(String strUrl) throws IOException {
    String data = "";
    InputStream iStream = null;
    HttpURLConnection urlConnection = null;
    try{
      URL url = new URL(strUrl);

      // Creating an http connection to communicate with url
      urlConnection = (HttpURLConnection) url.openConnection();

      // Connecting to url
      urlConnection.connect();

      // Reading data from url
      iStream = urlConnection.getInputStream();

      BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

      StringBuffer sb  = new StringBuffer();

      String line = "";
      while( ( line = br.readLine())  != null){
        sb.append(line);
      }

      data = sb.toString();

      br.close();

    }catch(Exception e){
      Log.d("Exception while downloading url", e.toString());
    }finally{
      iStream.close();
      urlConnection.disconnect();
    }
    return data;
  }

  // Fetches data from url passed
  private class DownloadTask extends AsyncTask<String, Void, String> {

    // Downloading data in non-ui thread
    @Override
    protected String doInBackground(String... url) {

      // For storing data from web service

      String data = "";

      try{
        // Fetching the data from web service
        data = downloadUrl(url[0]);
      }catch(Exception e){
        Log.d("Background Task",e.toString());
      }
      return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      ParserTask parserTask = new ParserTask();

      // Invokes the thread for parsing the JSON data
      parserTask.execute(result);
    }
  }

  /** A class to parse the Google Places in JSON format */
  private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

      JSONObject jObject;
      List<List<HashMap<String, String>>> routes = null;

      try{
        jObject = new JSONObject(jsonData[0]);
        DirectionsJSONParser parser = new DirectionsJSONParser();

        // Starts parsing data
        routes = parser.parse(jObject);
      }catch(Exception e){
        e.printStackTrace();
      }
      return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {

      ArrayList<LatLng> points = null;
      PolylineOptions lineOptions = null;

      // Traversing through all the routes
      for(int i=0;i<result.size();i++){
        points = new ArrayList<LatLng>();
        lineOptions = new PolylineOptions();

        // Fetching i-th route
        List<HashMap<String, String>> path = result.get(i);

        // Fetching all the points in i-th route
        for(int j=0;j<path.size();j++){
          HashMap<String,String> point = path.get(j);

          double lat = Double.parseDouble(point.get("lat"));
          double lng = Double.parseDouble(point.get("lng"));
          LatLng position = new LatLng(lat, lng);

          points.add(position);
        }

        // Adding all the points in the route to LineOptions
        lineOptions.addAll(points);
        lineOptions.width(2);
        lineOptions.color(Color.RED);
      }

      // Drawing polyline in the Google Map for the i-th route
      googleMap.addPolyline(lineOptions);
    }
  }

  @Override
  public void setMenuVisibility(final boolean visible) {
    super.setMenuVisibility(visible);
    if (visible) {
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) { // 1
    super.onCreate(savedInstanceState);

    dataViewer = new DataViewer();

    // Get the location manager
    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    // Define the criteria how to select the location provider -> use default
    Criteria criteria = new Criteria();
    provider = locationManager.getBestProvider(criteria, false);
    hLocation = locationManager.getLastKnownLocation(provider);

    // Initialize the location fields
    if (hLocation != null) {
      Log.i("location", "Provider " + provider + " has been selected.");
      onLocationChanged(hLocation);
    } else {
      Log.i("location", "Location not available");
      alertDlgFragment = alertDialogFragment.newInstance("Location Services Not Active", "Please enable Location Services and GPS", 2);
      alertDlgFragment.setCancelable(false);
      alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
    }
  }

  // Inflate the layout for this fragment
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.maps_fragment, container, false);

    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
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
    // If Google Play services is available
    if (resultCode == ConnectionResult.SUCCESS) {
      if (googleMap == null) {
        double lat, lng;

        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);

        googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
          // Use default InfoWindow frame
          @Override
          public View getInfoWindow(Marker arg0) {
            return null;
          }

          // Defines the contents of the InfoWindow
          @Override
          public View getInfoContents(Marker arg0) {

            View v = getLayoutInflater(getArguments()).inflate(R.layout.map_view_data, null);

            ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
            txtName = (TextView) v.findViewById(R.id.txtName);
            txtType = (TextView) v.findViewById(R.id.txtType);
            txtTelephone = (TextView) v.findViewById(R.id.txtTelephone);
            txtDistrict = (TextView) v.findViewById(R.id.txtDistrict);
            txtAddress = (TextView) v.findViewById(R.id.txtAddress);

            txtName.setText(name);
            txtType.setText("Type:" + type);
            txtTelephone.setText("Telephone:" + telephone);
            txtDistrict.setText("District:" + district);
            txtAddress.setText("Address:" + address);

            // Returning the view containing InfoWindow contents
            return v;

          }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
          @Override
          public void onInfoWindowClick(Marker marker) {
            /*polyline = googleMap.addPolyline(new PolylineOptions().add(new LatLng(deviceLatitude, deviceLongitude), new LatLng(latitude, longitude)).geodesic(true));*/
            LatLng origin = new LatLng(deviceLatitude, deviceLongitude);
            LatLng dest = new LatLng(latitude, longitude);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
          }
        });

        locationManager.requestLocationUpdates(provider, 400, 1, this);

        lat = deviceLatitude;
        lng = deviceLongitude;

        if ((latitude > 0) && (longitude > 0)) {

          marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
          marker.showInfoWindow();

          lat = latitude;
          lng = longitude;
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));
      }
    } else {
      // Google Play services was not available for some reason
      Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), RQS_GooglePlayServices);
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
