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
 *   AlertDialogFragment.java
 *
 * Abstract:
 *   AlertDialogFragment.java is the class files in Mobile Clients for MAD project.
 *   AlertDialogFragment will be used as custom dialog message in fragment to user when needed.
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public class AlertDialogFragment extends DialogFragment {

  protected static String name, type, category, telephone, district, address;
  protected static double latitude, longitude;
  private AlertDialog.Builder alertDialog;
  private String title, msg;
  private int button;

  /**
   * Function Name:
   * newInstance
   * <p/>
   * Function Description:
   * Create new instance for AlertDialogFragment class and put the variable in argument (Bundle).
   * <p/>
   * Parameters:
   * String title - title of dialog.
   * String msg - message of dialog.
   * int button - mode of button that needed to show in dialog.
   * <p/>
   * Returned Value:
   * Returns AlertDialogFragment Object with set of arguments.
   * <p/>
   * Possible Error Code or Exception:
   * none.
   */
  protected static AlertDialogFragment newInstance(String title, String msg, int button) {
    AlertDialogFragment frag = new AlertDialogFragment();
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

    /**
     * button mode selection for specific task that needed by caller.
     */
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

              /**
               * Passing the data into class MapsFragment.
               * Clear the current data value.
               * Set tab position into MapsFragment.
               */
              MapsFragment.name = name;
              MapsFragment.type = type;
              MapsFragment.category = category;
              MapsFragment.district = district;
              MapsFragment.address = address;
              MapsFragment.telephone = telephone;
              MapsFragment.latitude = latitude;
              MapsFragment.longitude = longitude;
              name = "";
              type = "";
              category = "";
              district = "";
              address = "";
              telephone = "";
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

              /**
               * Start new Activity with the Location Settings.
               */
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
      case 3:
        alertDialog.setPositiveButton("Settings",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

              /**
               * Start new Activity with the Settings.
               */
              Intent intent = new Intent(Settings.ACTION_SETTINGS);
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
