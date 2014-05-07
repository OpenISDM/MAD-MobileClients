package mobilemad.app;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
MapsFragment class as user view for maps.
 */
public class MapsFragment extends Fragment implements LocationListener {

  private View rootView;
  private SupportMapFragment supportMapFragment;
  private GoogleMap googleMap;
  private LocationManager locationManager;
  private Location hLocation;
  private DialogFragment alertDlgFragment;

  private DataViewer dataViewer;
  protected static String name, type, telephone, district, address;
  protected static double latitude, longitude, deviceLatitude, deviceLongitude;
  private String msg, provider;
  final int RQS_GooglePlayServices = 10;

  /*
  Refresh the view when user back to this page. Override method from Fragment.
  Show message in Log that passed by ListFragment.
   */
  @Override
  public void setMenuVisibility(final boolean visible) {
    super.setMenuVisibility(visible);
    if (visible) {
      /*if (name == null) name = "";
      if (type == null) type = "";
      if (telephone == null) telephone = "";
      if (district == null) district = "";
      if (address == null) address = "";

      Log.i("Name", name);
      Log.i("Type", type);
      Log.i("Telephone", telephone);
      Log.i("District", district);
      Log.i("Address", address);
      Log.i("Latitude", String.valueOf(latitude));
      Log.i("Longitude", String.valueOf(longitude));*/

      /*msg = "Name: " + name + "\n";*/
      /*msg = "Type: " + type + "; ";
      msg += "Telephone: " + telephone + "; ";
      msg += "District: " + district + "; ";
      msg += "Address: " + address + "";*/

      /*if (map != null) {
        map = fragment.getMap();
        map.setMyLocationEnabled(true);

        UiSettings settings = map.getUiSettings();
        settings.setAllGesturesEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
      }*/
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

        locationManager.requestLocationUpdates(provider, 400, 1, this);

        lat = deviceLatitude;
        lng = deviceLongitude;

        if ((latitude > 0) && (longitude > 0)) {
          msg = "Type: " + type + "; ";
          msg += "Telephone: " + telephone + "; ";
          msg += "District: " + district + "; ";
          msg += "Address: " + address + "";

          googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name).snippet(msg));
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
