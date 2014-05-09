package mobilemad.app;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
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
        new DownloadData().execute("imgMaps.png", txtImageLocation.getText().toString(), "image");
        new DownloadData().execute("dataFiles.json", txtDataLocation.getText().toString(), "text");
      }
    });

    btnDeleteFiles.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String path = Config.path + File.separator + "data";
        File dirFiles = new File(path);

        for (String strFile : dirFiles.list()) {
          File file = new File(dirFiles, strFile);
          if ((strFile.equals("imgMaps.png")) || (strFile.equals("dataFiles.json"))) {
            file.delete();
          }
        }

        alertDlgFragment = AlertDialogFragment.newInstance("Delete Internal Storage Files", "Success", 0);
        alertDlgFragment.setCancelable(false);
        alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
      }
    });

    return rootView;
  }
}
