package mobilemad.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
MapsFragment class as user view for maps.
 */
public class MapsFragment extends Fragment {
  private View rootView;

  protected static String name, type, telephone, district, address;
  protected static double longitude, latitude;

  /*
  Refresh the view when user back to this page. Override method from Fragment.
  Show message in Log that passed by ListFragment.
   */
  @Override
  public void setMenuVisibility(final boolean visible) {
    super.setMenuVisibility(visible);
    if (visible) {
      if (name == null) name = "";
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
      Log.i("Longitude", String.valueOf(longitude));
    }
  }

  // Inflate the layout for this fragment
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.maps_fragment, container, false);
    return rootView;
  }
}
