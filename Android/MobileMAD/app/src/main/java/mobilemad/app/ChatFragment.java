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
 *   ChatFragment.java
 *
 * Abstract:
 *   ChatFragment.java is the tab Chat in Mobile Clients for MAD project.
 *   User can join existing channel to chat and send files with AllJoyn Services.
 *
 * Authors:
 *   Andre Lukito, routhsauniere@gmail.com
 *
 * License:
 *  GPL 3.0 This file is subject to the terms and conditions defined
 *  in file 'COPYING.txt', which is part of this source code package.
 *
 * Major Revision History:
 *   2014/07/22: complete version 1.0
 */

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ChatFragment extends Fragment implements Observer {
  public static final int DIALOG_JOIN_ID = 0;
  public static final int DIALOG_LEAVE_ID = 1;
  public static final int DIALOG_ALLJOYN_ERROR_ID = 2;
  private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
  private static final int HANDLE_HISTORY_CHANGED_EVENT = 1;
  private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 2;
  private static final int HANDLE_ALLJOYN_ERROR_EVENT = 3;
  private static final int HANDLE_TOAST_FILES = 4;
  private static final String TAG = "app.ChatFragment";
  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case HANDLE_APPLICATION_QUIT_EVENT: {
          Log.i(TAG, "mHandler.handleMessage(): HANDLE_APPLICATION_QUIT_EVENT");
          getActivity().finish();
        }
        break;
        case HANDLE_HISTORY_CHANGED_EVENT: {
          Log.i(TAG, "mHandler.handleMessage(): HANDLE_HISTORY_CHANGED_EVENT");
          updateHistory();
          break;
        }
        case HANDLE_CHANNEL_STATE_CHANGED_EVENT: {
          Log.i(TAG, "mHandler.handleMessage(): HANDLE_CHANNEL_STATE_CHANGED_EVENT");
          updateChannelState();
          break;
        }
        case HANDLE_ALLJOYN_ERROR_EVENT: {
          Log.i(TAG, "mHandler.handleMessage(): HANDLE_ALLJOYN_ERROR_EVENT");
          alljoynError();
          break;
        }
        case HANDLE_TOAST_FILES:
          Toast toast = Toast.makeText(getActivity().getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT);
          toast.setGravity(Gravity.CENTER, 0, 0);
          toast.show();
          break;
        default:
          break;
      }
    }
  };

  private View rootView;
  private ChatApplication mChatApplication = null;
  private ArrayAdapter<String> aaHistoryList;
  private Button btnJoin, btnLeave, btnSendFiles;
  private TextView lblChannelName, lblChannelStatus;
  private EditText txtMessage;
  private ListView lvHistoryList;

  @Override
  public synchronized void update(Observable o, Object arg) {
    Log.i(TAG, "update(" + arg + ")");
    String qualifier = (String) arg;

    if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
      Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
      mHandler.sendMessage(message);
    }

    if (qualifier.equals(ChatApplication.HISTORY_CHANGED_EVENT)) {
      Message message = mHandler.obtainMessage(HANDLE_HISTORY_CHANGED_EVENT);
      mHandler.sendMessage(message);
    }

    if (qualifier.equals(ChatApplication.USE_CHANNEL_STATE_CHANGED_EVENT)) {
      Message message = mHandler.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
      mHandler.sendMessage(message);
    }

    if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
      Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
      mHandler.sendMessage(message);
    }

    if (qualifier.equals(ChatApplication.TOAST_RECEIVE_FILES)) {
      Message message = mHandler.obtainMessage(HANDLE_TOAST_FILES, "Receive Complete: " + AllJoynService.fileName);
      mHandler.sendMessage(message);
    }

    if (qualifier.equals(ChatApplication.TOAST_SEND_FILES)) {
      Message message = mHandler.obtainMessage(HANDLE_TOAST_FILES, "Transfer Complete: " + AllJoynService.fileName);
      mHandler.sendMessage(message);
    }
  }

  @Override
  public void setMenuVisibility(final boolean visible) {
    super.setMenuVisibility(visible);
    Log.i(TAG, "visible");
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
      updateHistory();

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
    Boolean showsDialog = true;

    switch (id) {
      case DIALOG_JOIN_ID: {
        if (mChatApplication.getNickName().equals("")) {
          showsDialog = false;
          Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                       "Please set your NickName first!", Toast.LENGTH_SHORT);
          toast.setGravity(Gravity.CENTER, 0, 0);
          toast.show();
        } else {
          showsDialog = true;
          DialogBuilder builder = new DialogBuilder();
          result = builder.createUseJoinDialog(getActivity(), mChatApplication);
        }
      }
      break;
      case DIALOG_LEAVE_ID: {
        showsDialog = true;
        DialogBuilder builder = new DialogBuilder();
        result = builder.createUseLeaveDialog(getActivity(), mChatApplication);
      }
      break;
      case DIALOG_ALLJOYN_ERROR_ID: {
        showsDialog = true;
        DialogBuilder builder = new DialogBuilder();
        result = builder.createAllJoynErrorDialog(getActivity(), mChatApplication);
      }
      break;
    }

    if (showsDialog) {
      result.show();
    }
  }

  /**
   * Procedure Name:
   * updateHistory
   * <p/>
   * Procedure Description:
   * Update chat history when user send chat message to another user in the same channel.
   * <p/>
   * Parameters:
   * None.
   * <p/>
   * Possible Error Code or Exception:
   * None.
   */
  private void updateHistory() {
    Log.i(TAG, "updateHistory()");
    aaHistoryList.clear();
    List<String> messages = mChatApplication.getHistory();

    for (String message : messages) {
      aaHistoryList.add(message);
    }

    aaHistoryList.notifyDataSetChanged();
  }

  /**
   * Procedure Name:
   * updateChannelState
   * <p/>
   * Procedure Description:
   * To determine status of AllJoyn client channel. It can be idle and joined.
   * Whenever client channel is idle, then user can join any channel that started by other user.
   * When client channel is joined, then user can send message or files each other.
   * <p/>
   * Parameters:
   * None.
   * <p/>
   * Possible Error Code or Exception:
   * None.
   */
  private void updateChannelState() {
    Log.i(TAG, "updateHistory()");
    AllJoynService.UseChannelState channelState = mChatApplication.useGetChannelState();
    String name = mChatApplication.useGetChannelName();

    if (name == null) {
      name = "Not set";
    }

    lblChannelName.setText(name);

    switch (channelState) {
      case IDLE:
        lblChannelStatus.setText("Idle");
        btnJoin.setEnabled(true);
        btnLeave.setEnabled(false);
        btnSendFiles.setEnabled(false);
        break;
      case JOINED:
        lblChannelStatus.setText("Joined");
        btnJoin.setEnabled(false);
        btnLeave.setEnabled(true);
        btnSendFiles.setEnabled(true);
        break;
    }
  }

  /**
   * An AllJoyn error has happened. Since this activity pops up first, general error and application
   * error will be handled.
   */
  private void alljoynError() {
    if (mChatApplication.getErrorModule() == ChatApplication.Module.GENERAL ||
      mChatApplication.getErrorModule() == ChatApplication.Module.USE) {
      showDialog(DIALOG_ALLJOYN_ERROR_ID);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.i(TAG, "created");
    rootView = inflater.inflate(R.layout.chat_fragment, container, false);

    aaHistoryList = new ArrayAdapter<String>(getActivity(), android.R.layout.test_list_item);
    lvHistoryList = (ListView) rootView.findViewById(R.id.lvHistoryList);
    lvHistoryList.setAdapter(aaHistoryList);

    txtMessage = (EditText) rootView.findViewById(R.id.txtMessage);
    txtMessage.setRawInputType(InputType.TYPE_CLASS_TEXT);
    txtMessage.setImeOptions(EditorInfo.IME_ACTION_SEND);
    txtMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
          return false;
        } else if (actionId == EditorInfo.IME_ACTION_SEND || event == null ||
                   event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
          String message = view.getText().toString();
          Log.i(TAG, "txtMessage.onEditorAction(): got message " + message + ")");
          mChatApplication.newLocalUserMessage(message);
          view.setText("");
        }

        return true;
      }
    });

    btnSendFiles = (Button) rootView.findViewById(R.id.btnSendFiles);
    btnSendFiles.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String[] fileName = {"dataFiles.json", "dataFiles.rdf", "imgMaps.png"};
        String[] savedFileName = {"dataFiles.json", "dataFiles.rdf", "imgMaps.png"};
        mChatApplication.newLocalUserFiles(fileName, savedFileName);
      }
    });

    btnJoin = (Button) rootView.findViewById(R.id.btnJoin);
    btnJoin.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(DIALOG_JOIN_ID);
      }
    });

    btnLeave = (Button) rootView.findViewById(R.id.btnLeave);
    btnLeave.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(DIALOG_LEAVE_ID);
      }
    });

    lblChannelName = (TextView) rootView.findViewById(R.id.lblChannelName);
    lblChannelStatus = (TextView) rootView.findViewById(R.id.lblChannelStatus);

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
    updateHistory();

    /**
     * Now that we're all ready to go, we are ready to accept notifications
     * from other components.
     */
    mChatApplication.addObserver(this);

    return rootView;
  }
}
