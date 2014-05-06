package mobilemad.app;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.Object;

public class SettingsFragment extends Fragment {

  private DialogFragment alertDlgFragment;
  private Context context;
  private Toast toast;
  private View rootView;
  private EditText txtImageLocation, txtDataLocation;
  private Button btnDownloadFiles, btnDeleteFiles;

  private int duration;

  private void showMessage(String msg) {
    context = getActivity().getApplicationContext();
    duration = Toast.LENGTH_SHORT;
    toast = Toast.makeText(context, msg, duration);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }

  private class DownloadImage extends AsyncTask<String, Void, Void> {
    private String error = null;
    private ProgressDialog pgDialog = new ProgressDialog(getActivity());

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) {
      try {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
      } catch (Exception e) {
        return null;
      }
    }

    protected void onPreExecute() {
      pgDialog.setMessage("Please wait...");
      pgDialog.show();
    }

    protected Void doInBackground(String... urls) {
      FileOutputStream outputStream = null;
      File file = null;

      try {
        String path = getActivity().getExternalFilesDir(null).getAbsolutePath() + File.separator + "data";
        String fileName = "imgMaps.png";
        String files = path + File.separator + fileName;

        InputStream downloadStream;
        Bitmap bmp;

        downloadStream = downloadUrl(urls[0]);

        if (downloadStream != null) {
          bmp = BitmapFactory.decodeStream(downloadStream);
          file = new File(path);
          file.mkdirs();
          file = new File(files);
          /*outputStream = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);*/
          outputStream = new FileOutputStream(file);
          bmp.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
        }
      } catch(Exception ex) {
        error = ex.getMessage();
      } finally {
        try {
          outputStream.close();
        } catch(Exception ex) {
          error = ex.getMessage();
        }
      }

      return null;
    }

    protected void onPostExecute(Void unused) {
      pgDialog.dismiss();
      if (error != null) {
        showMessage("Problem with Download Image\n" + error);
      } else {
        showMessage("Download Image Files\nSuccess");
      }
    }
  }

  private class DownloadData extends AsyncTask<String, Void, Void> {
    private String error = null;
    private ProgressDialog pgDialog = new ProgressDialog(getActivity());

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) {
      try {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
      } catch (Exception e) {
        return null;
      }
    }

    protected void onPreExecute() {
      pgDialog.setMessage("Please wait...");
      pgDialog.show();
    }

    protected Void doInBackground(String... urls) {
      FileOutputStream outputStream = null;
      File file = null;

      try {
        String path = getActivity().getExternalFilesDir(null).getAbsolutePath() + File.separator + "data";
        String fileName = "dataFiles.json";
        String files = path + File.separator + fileName;

        InputStream downloadStream;

        byte[] buffer = new byte[1024];
        int bufferLength = 0;

        downloadStream = downloadUrl(urls[0]);

        if (downloadStream != null) {
          file = new File(path);
          file.mkdirs();
          file = new File(files);
          /*outputStream = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);*/
          outputStream = new FileOutputStream(file);

          //now, read through the input buffer and write the contents to the file
          while ((bufferLength = downloadStream.read(buffer)) > 0 ) {
            //add the data in the buffer to the file in the file output stream (the file on the sd card
            outputStream.write(buffer, 0, bufferLength);
          }
        }
      } catch(Exception ex) {
        error = ex.getMessage();
      } finally {
        try {
          outputStream.close();
        } catch(Exception ex) {
          error = ex.getMessage();
        }
      }

      return null;
    }

    protected void onPostExecute(Void unused) {
      pgDialog.dismiss();
      if (error != null) {
        showMessage("Problem with Download Data\n" + error);
      } else {
        showMessage("Download Data Files\nSuccess");
      }
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
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
        new DownloadImage().execute(txtImageLocation.getText().toString());
        new DownloadData().execute(txtDataLocation.getText().toString());
      }
    });

    btnDeleteFiles.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String path = getActivity().getExternalFilesDir(null).getAbsolutePath() + File.separator + "data";
        File dirFiles = new File(path);
        /*File dirFiles = getActivity().getFilesDir();*/
        for (String strFile : dirFiles.list()) {
          File file = new File(dirFiles, strFile);
          file.delete();
        }
        alertDlgFragment = alertDialogFragment.newInstance("Delete Internal Storage Files", "Success", 0);
        alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
      }
    });

    return rootView;
  }
}
