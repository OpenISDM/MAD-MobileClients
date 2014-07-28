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
 *   In the future, this download manual functionality will be hide or delete, and download data
 *   will be done automatically).
 *   Other function are for configure chatting application using AllJoyn Services. User can
 *   configure nickname for chatting, create new channel for chat and send files.
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

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SettingsFragment extends Fragment implements Observer {
  public static final int DIALOG_ALLJOYN_ERROR_ID = 4;
  static final int DIALOG_SET_NICK_NAME_ID = 0;
  static final int DIALOG_SET_NAME_ID = 1;
  static final int DIALOG_START_ID = 2;
  static final int DIALOG_STOP_ID = 3;
  private static final String TAG = "app.SettingsFragment";
  private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
  private static final int HANDLE_NICK_NAME_CHANGED_EVENT = 1;
  private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 2;
  private static final int HANDLE_ALLJOYN_ERROR_EVENT = 3;
  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case HANDLE_APPLICATION_QUIT_EVENT: {
          Log.i(TAG, "mHandler.handleMessage(): HANDLE_APPLICATION_QUIT_EVENT");
          getActivity().finish();
        }
        break;
        case HANDLE_NICK_NAME_CHANGED_EVENT: {
          Log.i(TAG, "mHandler.handleMessage(): HANDLE_NICK_NAME_CHANGED_EVENT");
          updateNickName();
        }
        break;
        case HANDLE_CHANNEL_STATE_CHANGED_EVENT: {
          Log.i(TAG, "mHandler.handleMessage(): HANDLE_CHANNEL_STATE_CHANGED_EVENT");
          updateChannelState();
        }
        break;
        case HANDLE_ALLJOYN_ERROR_EVENT: {
          Log.i(TAG, "mHandler.handleMessage(): HANDLE_ALLJOYN_ERROR_EVENT");
          alljoynError();
        }
        break;
        default:
          break;
      }
    }
  };

  private ChatApplication mChatApplication = null;
  private DialogFragment alertDlgFragment;
  private DownloaderHTTP downloaderHTTP;
  private DataViewer dataViewer;
  private Context context;
  private View rootView;
  private TextView lblNickName, lblHostChannelName, lblHostChannelStatus;
  private Button btnSetNickName, btnHostSetName, btnHostStart, btnHostStop, btnHostQuit;
  private EditText txtImageLocation, txtDataLocation;
  private Button btnDownloadFiles, btnDeleteFiles;

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
    txtImageLocation.setHint(Config.IMG_LOCATION1);
    txtImageLocation.setText(Config.IMG_LOCATION1);

    txtDataLocation = (EditText) rootView.findViewById(R.id.txtDataLocation);
    txtDataLocation.setHint(Config.FILE_LOCATION1);
    txtDataLocation.setText(Config.FILE_LOCATION1);

    btnDownloadFiles = (Button) rootView.findViewById(R.id.btnDownloadFiles);
    btnDeleteFiles = (Button) rootView.findViewById(R.id.btnDeleteFiles);

    lblNickName = (TextView) rootView.findViewById(R.id.lblNickName);
    lblNickName.setText("");

    lblHostChannelName = (TextView) rootView.findViewById(R.id.lblHostChannelName);
    lblHostChannelName.setText("");

    lblHostChannelStatus = (TextView) rootView.findViewById(R.id.lblHostChannelStatus);
    lblHostChannelStatus.setText("Idle");

    btnSetNickName = (Button) rootView.findViewById(R.id.btnSetNickName);
    btnSetNickName.setEnabled(true);

    btnHostSetName = (Button) rootView.findViewById(R.id.btnHostSetName);
    btnHostSetName.setEnabled(true);

    btnHostStart = (Button) rootView.findViewById(R.id.btnHostStart);
    btnHostStart.setEnabled(false);

    btnHostStop = (Button) rootView.findViewById(R.id.btnHostStop);
    btnHostStop.setEnabled(false);

    btnHostQuit = (Button) rootView.findViewById(R.id.btnHostQuit);
    btnHostQuit.setEnabled(true);

    btnDownloadFiles.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        /**
         * Check availability of Connectivity services (Mobile or Wi-Fi) before
         * downloading the data.
         */
        ConnectivityManager connectivityManager =
          (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (downloaderHTTP.isNetworkAvailable(connectivityManager)) {

          /**
           * Invokes the thread for download the data from the server and saved file locally.
           */
          new DownloadData().execute("imgMaps.png", txtImageLocation.getText().toString(), "image");
          new DownloadData().execute("dataFiles.rdf", txtDataLocation.getText().toString(), "text");
          //new DownloadData().execute("dataFiles.json", Config.FILE1_LOCATION, "text");
        } else {
          alertDlgFragment = AlertDialogFragment.newInstance("No Network Available",
            "Please enable the Mobile Data or Wi-Fi", 3);
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

        alertDlgFragment = AlertDialogFragment.newInstance("Delete Internal Storage Files",
          "Success", 0);
        alertDlgFragment.setCancelable(false);
        alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
      }
    });

    btnSetNickName.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(DIALOG_SET_NICK_NAME_ID);
      }
    });

    btnHostSetName.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(DIALOG_SET_NAME_ID);
      }
    });

    btnHostStart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialog(DIALOG_START_ID);
      }
    });

    btnHostStop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialog(DIALOG_STOP_ID);
      }
    });

    btnHostQuit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mChatApplication.quit();
      }
    });

    /**
     * Keep a pointer to the Android Application class around. We use this
     * as the Model for our MVC-based application. Whenever we are started
     * we need to "check in" with the application so it can ensure that our
     * required services are running.
     */
    mChatApplication = (ChatApplication) getActivity().getApplication();
    mChatApplication.checkin();

    /**
     * Call down into the model to get its current state. Since the model
     * outlives its Activities, this may actually be a lot of state and not
     * just empty.
     */
    updateChannelState();
    updateNickName();

    /**
     * Now that we're all ready to go, we are ready to accept notifications
     * from other components.
     */
    mChatApplication.addObserver(this);

    return rootView;
  }

  @Override
  public synchronized void update(Observable o, Object arg) {
    Log.i(TAG, "update(" + arg + ")");
    String qualifier = (String) arg;

    if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
      Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
      mHandler.sendMessage(message);
    }

    if (qualifier.equals(ChatApplication.HOST_NICK_NAME_CHANGED_EVENT)) {
      Message message = mHandler.obtainMessage(HANDLE_NICK_NAME_CHANGED_EVENT);
      mHandler.sendMessage(message);
    }

    if (qualifier.equals(ChatApplication.HOST_CHANNEL_STATE_CHANGED_EVENT)) {
      Message message = mHandler.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
      mHandler.sendMessage(message);
    }

    if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
      Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
      mHandler.sendMessage(message);
    }
  }

  @Override
  public void setMenuVisibility(boolean menuVisible) {
    super.setMenuVisibility(menuVisible);
    if (rootView != null) {

      /**
       * Keep a pointer to the Android Application class around. We use this
       * as the Model for our MVC-based application. Whenever we are started
       * we need to "check in" with the application so it can ensure that our
       * required services are running.
       */
      mChatApplication = (ChatApplication) getActivity().getApplication();
      mChatApplication.checkin();

      /**
       * Call down into the model to get its current state. Since the model
       * outlives its Activities, this may actually be a lot of state and not
       * just empty.
       */
      updateChannelState();
      updateNickName();

      /**
       * Now that we're all ready to go, we are ready to accept notifications
       * from other components.
       */
      mChatApplication.addObserver(this);
    }
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "onDestroy()");
    mChatApplication = (ChatApplication) getActivity().getApplication();
    mChatApplication.deleteObserver(this);
    super.onDestroy();
  }

  /**
   * Procedure Name:
   * showDialog
   * <p/>
   * Procedure Description:
   * Create dialog by calling DialogBuilder class depends on type of dialog that called by developer.
   * <p/>
   * Parameters:
   * int id - used to identify the type of dialog to be shown to the user.
   * <p/>
   * Possible Error Code or Exception:
   * None.
   */
  protected void showDialog(int id) {
    Log.i(TAG, "showDialog()");
    Dialog result = null;

    switch (id) {
      case DIALOG_SET_NICK_NAME_ID: {
        DialogBuilder builder = new DialogBuilder();
        result = builder.createNickNameDialog(getActivity(), mChatApplication);
      }
      break;
      case DIALOG_SET_NAME_ID: {
        DialogBuilder builder = new DialogBuilder();
        result = builder.createHostNameDialog(getActivity(), mChatApplication);
      }
      break;
      case DIALOG_START_ID: {
        DialogBuilder builder = new DialogBuilder();
        result = builder.createHostStartDialog(getActivity(), mChatApplication);
      }
      break;
      case DIALOG_STOP_ID: {
        DialogBuilder builder = new DialogBuilder();
        result = builder.createHostStopDialog(getActivity(), mChatApplication);
      }
      break;
      case DIALOG_ALLJOYN_ERROR_ID: {
        DialogBuilder builder = new DialogBuilder();
        result = builder.createAllJoynErrorDialog(getActivity(), mChatApplication);
      }
      break;
    }

    result.show();
  }

  /**
   * Procedure Name:
   * updateChannelState
   * <p/>
   * Procedure Description:
   * To determine status of AllJoyn host channel and host channel name. It can be idle, named,
   * bound, advertised, connected, and unknown.
   * Whenever host channel is idle, then user can set host channel name and start the channel.
   * When user start the host channel, AllJoyn will checking the host channel name and bound the
   * host channel.
   * When host channel is started, it will advertised itself and it can be discovered by neighbour
   * whenever they connected into same network and open the application.
   * Host channel will state connected if there is another user join the host by this application.
   * Host channel status will be unknown if there is problem with AllJoyn services when user try to
   * start the host channel.
   * After host channel started, then user can stop the host channel too and the state will
   * become idle and it can't discovered by neighbour even though they still connected into same
   * network and open the application.
   * <p/>
   * Parameters:
   * None.
   * <p/>
   * Possible Error Code or Exception:
   * None.
   */
  private void updateChannelState() {
    AllJoynService.HostChannelState channelState = mChatApplication.hostGetChannelState();
    String name = mChatApplication.hostGetChannelName();
    boolean haveName = true;

    if (name == null) {
      haveName = false;
      name = "Not set";
    }

    lblHostChannelName.setText(name);

    switch (channelState) {
      case IDLE:
        lblHostChannelStatus.setText("Idle");
        break;
      case NAMED:
        lblHostChannelStatus.setText("Named");
        break;
      case BOUND:
        lblHostChannelStatus.setText("Bound");
        break;
      case ADVERTISED:
        lblHostChannelStatus.setText("Advertised");
        break;
      case CONNECTED:
        lblHostChannelStatus.setText("Connected");
        break;
      default:
        lblHostChannelStatus.setText("Unknown");
        break;
    }

    if (channelState == AllJoynService.HostChannelState.IDLE) {
      btnHostSetName.setEnabled(true);
      if (haveName) {
        btnHostStart.setEnabled(true);
      } else {
        btnHostStart.setEnabled(false);
      }
      btnHostStop.setEnabled(false);
    } else {
      btnHostSetName.setEnabled(false);
      btnHostStart.setEnabled(false);
      btnHostStop.setEnabled(true);
    }
  }

  /**
   * Procedure Name:
   * updateNickName
   * <p/>
   * Procedure Description:
   * To update nickname will be used by user when they use the chat feature.
   * <p/>
   * Parameters:
   * None.
   * <p/>
   * Possible Error Code or Exception:
   * None.
   */
  private void updateNickName() {
    String nickName = mChatApplication.getNickName();

    if (nickName.trim().length() <= 0) {
      nickName = "Not set";
    }

    lblNickName.setText(nickName);
  }

  /**
   * An AllJoyn error has happened. Since this activity pops up first, general error and application
   * error will be handled.
   */
  private void alljoynError() {
    if (mChatApplication.getErrorModule() == ChatApplication.Module.GENERAL ||
      mChatApplication.getErrorModule() == ChatApplication.Module.USE) {
      getActivity().showDialog(DIALOG_ALLJOYN_ERROR_ID);
    }
  }

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
        } else {
          error = "Server is not available.";
        }
      } catch (Exception ex) {
        error = ex.getMessage();
      } finally {
        try {
          if (downloadStream != null) {
            downloadStream.close();
          }
        } catch (IOException e) {
        }
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
}
