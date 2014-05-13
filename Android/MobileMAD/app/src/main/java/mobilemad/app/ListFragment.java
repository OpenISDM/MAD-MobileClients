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
 *   ListFragment.java
 *
 * Abstract:
 *   ListFragment.java is the tab List in Mobile Clients for MAD project.
 *   List will show all facilities, detail for each facility, and show the location on
 *   Google Maps when user select the facility.
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

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListFragment extends Fragment {
  private DialogFragment alertDlgFragment;
  private View rootView;
  private ListView lvData;
  private SimpleAdapter sAdapter;

  private DataViewer dataViewer;
  private HashMap<Integer, HashMap<String, Object>> result;
  private HashMap<String, Object> data;
  private ArrayList<HashMap<String, Object>> listData;

  /**
   * Procedure Name:
   *   contentData
   *
   * Procedure Description:
   *   Make list from parsed JSON or RDF data to put into list view.
   *
   * Parameters:
   *   String[] fileName - Array of String for filename.
   *   If the parsed data is empty, list view will empty.
   *
   * Possible Error Code or Exception:
   *   none
   */
  private void contentData(String... fileName) throws IOException {
    result = dataViewer.JSONFacilities(fileName[0]);
    if (result.isEmpty()) {
      result = dataViewer.RDFFacilities(fileName[1]);
    }

    listData.clear();

    for(Map.Entry<Integer, HashMap<String, Object>> entry : result.entrySet()) {
      data = new HashMap<String, Object>();
      int key = entry.getKey();

      HashMap<String, Object> value = entry.getValue();
      for(Map.Entry<String, Object> entry1 : value.entrySet()) {
        String key1 = entry1.getKey();
        Object value1 = entry1.getValue();

        if (key1.equalsIgnoreCase("Name")) {
          data.put("Name", value1);
        } else if (key1.equalsIgnoreCase("Type")) {
          data.put("Type", value1);
        } else if (key1.equalsIgnoreCase("Category")) {
          data.put("Category", dataViewer.resourceIcon(value1.toString()));
        }
      }

      listData.add(data);
    }
  }

  private class contentViewer extends AsyncTask<String, Void, Void> {
    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(String... fileName) {
      try {
        contentData(fileName);
      } catch (IOException e) {
        Log.i("ListFragment - contentViewer - IOException", e.getMessage());
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
      if (!result.isEmpty()) {

        /**
         * Create Simple Adapter to put the value from list (after contentData called).
         * Show it on list view.
         */
        sAdapter = new SimpleAdapter(getActivity(), listData, R.layout.list_view_data, new String[] {"Name", "Type", "Category"}, new int[] {R.id.txtName, R.id.txtType, R.id.ivIcon});
        lvData.setAdapter(sAdapter);
      }
    }
  }

  @Override
  public void setMenuVisibility(final boolean visible) {
    super.setMenuVisibility(visible);
    if (visible) {

      /**
       * Check if default view not null.
       * Invokes the thread for show the data on list view when user return to tab List.
       */
      if (rootView != null) {
        new contentViewer().execute("dataFiles.json", "dataFiles.rdf");
      }
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    dataViewer = new DataViewer();
    result = new HashMap<Integer, HashMap<String, Object>>();
    data = new HashMap<String, Object>();
    listData = new ArrayList<HashMap<String,Object>>();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.list_fragment, container, false);

    lvData = (ListView) rootView.findViewById(R.id.lvData);

    lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
        if ((lvData != null)) {
          String name, type, category, telephone, district, address, msg;

          /**
           * Get value from list based on position when list view item clicked.
           */
          name = result.get(position).get("Name").toString();
          type = result.get(position).get("Type").toString();
          category = result.get(position).get("Category").toString();
          district = result.get(position).get("District").toString();
          address = result.get(position).get("Address").toString();
          telephone = result.get(position).get("Telephone").toString();

          msg = "Name: " + name + "\n";
          msg += "Type: " + type + "\n";
          msg += "District: " + district + "\n";
          msg += "Address: " + address + "\n";
          msg += "Telephone: " + telephone + "\n";

          /**
           * Passing the data into class AlertDialogFragment
           */
          AlertDialogFragment.name = name;
          AlertDialogFragment.type = type;
          AlertDialogFragment.category = category;
          AlertDialogFragment.district = district;
          AlertDialogFragment.address = address;
          AlertDialogFragment.telephone = telephone;
          AlertDialogFragment.latitude = Double.valueOf(result.get(position).get("Latitude").toString());
          AlertDialogFragment.longitude = Double.valueOf(result.get(position).get("Longitude").toString());

          alertDlgFragment = AlertDialogFragment.newInstance("Detail Information", msg, 1);
          alertDlgFragment.setCancelable(false);
          alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
        }
      }
    });

    /**
     * Invokes the thread for show the data on list view for the first time.
     */
    new contentViewer().execute("dataFiles.json", "dataFiles.rdf");

    return rootView;
  }
}
