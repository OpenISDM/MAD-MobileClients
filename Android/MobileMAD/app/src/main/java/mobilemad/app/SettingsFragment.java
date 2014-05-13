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
 *   SettingsFragment.java
 *
 * Abstract:
 *   SettingsFragment.java is the tab Settings in Mobile Clients for MAD project.
 *   Settings will be use for download the data from POS based on IP that provided by user (For now.
 *   In the future, this tab will be hide or delete, and download data will be done automatically).
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
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SettingsFragment extends Fragment {
  private DialogFragment alertDlgFragment;
  private DownloaderHTTP downloaderHTTP;
  private DataViewer dataViewer;
  private Context context;
  private View rootView;
  private EditText txtImageLocation, txtDataLocation;
  private Button btnDownloadFiles, btnDeleteFiles;

  private class DownloadData extends AsyncTask<String, Void, Void> {
    private String error = null;
    private ProgressDialog pgDialog = new ProgressDialog(getActivity());

    @Override
    protected void onPreExecute() {
      pgDialog.setMessage("Please wait...");
      pgDialog.setCanceledOnTouchOutside(false);
      pgDialog.show();
    }

    /**
     * Fetching the data from server.
     */
    @Override
    protected Void doInBackground(String... urls) {
      InputStream downloadStream = null;

      try {
        String path = Config.path + File.separator + "data";
        String fileName = urls[0];
        String files = path + File.separator + fileName;

        downloadStream = downloaderHTTP.downloadUrl(urls[1]);

        if (downloadStream != null) {
          if (urls[2].equals("image")) {
            downloaderHTTP.saveImage(downloadStream, path, files);
          } else if (urls[2].equals("text")) {
            downloaderHTTP.saveText(downloadStream, path, files);
          }
        }
      } catch(Exception ex) {
        error = ex.getMessage();
      } finally {
        try {
          downloadStream.close();
        } catch (IOException e) {}
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
      pgDialog.dismiss();
      if (error != null) {
        dataViewer.showMessage(context, "Download Data Failed");
        Log.i("downloadData", error);
      } else {
        dataViewer.showMessage(context, "Download Data Completed");
      }
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    context = getActivity().getApplicationContext();
    downloaderHTTP = new DownloaderHTTP();
    dataViewer = new DataViewer();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.settings_fragment, container, false);

    txtImageLocation = (EditText) rootView.findViewById(R.id.txtImageLocation);
    txtDataLocation = (EditText) rootView.findViewById(R.id.txtDataLocation);
    btnDownloadFiles = (Button) rootView.findViewById(R.id.btnDownloadFiles);
    btnDeleteFiles = (Button) rootView.findViewById(R.id.btnDeleteFiles);

    txtImageLocation.setHint(Config.imgLocation2);
    txtImageLocation.setText(Config.imgLocation2);
    txtDataLocation.setHint(Config.fileLocation2);
    txtDataLocation.setText(Config.fileLocation2);

    btnDownloadFiles.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        /**
         * Check availability of Connectivity services (Wi-Fi or mobile) before
         * downloading the data.
         */
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (downloaderHTTP.isNetworkAvailable(connectivityManager)) {

          /**
           * Invokes the thread for download the data from the server and saved file locally.
           */
          new DownloadData().execute("imgMaps.png", txtImageLocation.getText().toString(), "image");
          new DownloadData().execute("dataFiles.json", txtDataLocation.getText().toString(), "text");
        } else {
          alertDlgFragment = AlertDialogFragment.newInstance("No Network Available", "Please enable the Mobile Data or Wi-Fi", 3);
          alertDlgFragment.setCancelable(false);
          alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
        }
      }
    });

    btnDeleteFiles.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        /**
         * delete every files in storage with specific path.
         */
        String path = Config.path + File.separator + "data";
        File dirFiles = new File(path);

        for (String strFile : dirFiles.list()) {
          File file = new File(dirFiles, strFile);
          file.delete();
        }

        alertDlgFragment = AlertDialogFragment.newInstance("Delete Internal Storage Files", "Success", 0);
        alertDlgFragment.setCancelable(false);
        alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
      }
    });

    return rootView;
  }
}
