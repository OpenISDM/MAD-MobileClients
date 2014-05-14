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
 *   DataFragment.java
 *
 * Abstract:
 *   DataFragment.java is the hidden tab Data in Mobile Clients for MAD project.
 *   Data will be use for development only before used in the real thing.
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataFragment extends Fragment {

  private View rootView;
  private EditText txtData;

  private DataViewer dataViewer;
  private LinkedHashMap<Integer, LinkedHashMap<String, Object>> result;
  private String content;

  private void getContent() throws IOException {
    txtData.setText("");
    content = dataViewer.showData("dataFiles.json");
    if (content.trim().length() > 0) {
      txtData.setText(content);
    }
  }

  private void contentJSON() throws IOException {
    String results = "";

    txtData.setText("");

    result = dataViewer.JSONFacilities("dataFiles.json");

    for(Map.Entry<Integer, LinkedHashMap<String, Object>> entry : result.entrySet()) {
      int key = entry.getKey();
      results += String.valueOf(key) + ":\n";
      LinkedHashMap<String, Object> value = entry.getValue();
      for(Map.Entry<String, Object> entry1 : value.entrySet()) {
        String key1 = entry1.getKey();
        Object value1 = entry1.getValue();
        results += key1 + ": ";
        results += String.valueOf(value1) + "\n";
      }
      results += "\n";
    }

    txtData.setText(results);
  }

  private void contentRDF() throws IOException {
    String results = "";

    txtData.setText("");

    result = dataViewer.RDFFacilities("dataFiles.rdf");

    for(Map.Entry<Integer, LinkedHashMap<String, Object>> entry : result.entrySet()) {
      int key = entry.getKey();
      results += String.valueOf(key) + ":\n";
      HashMap<String, Object> value = entry.getValue();
      for(Map.Entry<String, Object> entry1 : value.entrySet()) {
        String key1 = entry1.getKey();
        Object value1 = entry1.getValue();
        results += key1 + ": ";
        results += String.valueOf(value1) + "\n";
      }
      results += "\n";
    }

    txtData.setText(results);
  }

  @Override
  public void setMenuVisibility(final boolean visible) { // 0
    super.setMenuVisibility(visible);
    if (visible) {
      if (rootView != null) {
        try {
          getContent();
        } catch (IOException e) {
          Log.i("DataFragment - setMenuVisibility - IOException", e.getMessage());
        }
        /*contentJSON();*/
        /*contentRDF();*/
      }
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) { // 1
    super.onCreate(savedInstanceState);

    dataViewer = new DataViewer();
    result = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { // 2
    rootView = inflater.inflate(R.layout.data_fragment, container, false);

    txtData = (EditText) rootView.findViewById(R.id.txtData);

    try {
      getContent();
    } catch (IOException e) {
      Log.i("DataFragment - onCreateView - IOException", e.getMessage());
    }
    /*contentJSON();*/
    /*contentRDF();*/

    return rootView;
  }

  @Override
  public void onStart() { // 3
    super.onStart();
  }

  @Override
  public void onResume() { // 4
    super.onResume();
  }
}
