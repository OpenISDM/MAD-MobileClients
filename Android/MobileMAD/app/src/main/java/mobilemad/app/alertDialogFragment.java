package mobilemad.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Andre on 05/01/2014.
 */
public class alertDialogFragment extends DialogFragment {

  private AlertDialog.Builder alertDialog;

  private String title, msg;
  private int button;
  protected static String name, type, telephone, district, address;
  protected static double latitude, longitude;

  protected static alertDialogFragment newInstance(String title, String msg, int button) {
    alertDialogFragment frag = new alertDialogFragment();
    Bundle args = new Bundle();
    args.putString("title", title);
    args.putString("msg", msg);
    args.putInt("button", button);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    title = getArguments().getString("title");
    msg = getArguments().getString("msg");
    button = getArguments().getInt("button");
    alertDialog = new AlertDialog.Builder(getActivity());
    alertDialog.setTitle(title);
    alertDialog.setMessage(msg);

    switch (button) {
      case 0:
        alertDialog.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
              }
            }
          );
        break;
      case 1:
        alertDialog.setPositiveButton("Show",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                MapsFragment.name = name;
                MapsFragment.type = type;
                MapsFragment.telephone = telephone;
                MapsFragment.district = district;
                MapsFragment.address = address;
                MapsFragment.latitude = latitude;
                MapsFragment.longitude = longitude;
                name = "";
                type = "";
                telephone = "";
                district = "";
                address = "";
                latitude = 0;
                longitude = 0;
                MainActivity.mViewPager.setCurrentItem(0);
              }
            }
          );
        alertDialog.setNegativeButton("Cancel",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              dialog.dismiss();
            }
          }
        );
        break;
      case 2:
        alertDialog.setPositiveButton("Settings",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
              startActivity(intent);
            }
          }
        );
        alertDialog.setNegativeButton("Cancel",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              dialog.dismiss();
            }
          }
        );
        break;
    }

    return alertDialog.create();
  }
}
