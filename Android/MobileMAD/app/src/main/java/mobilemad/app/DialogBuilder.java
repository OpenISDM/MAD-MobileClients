/**
 * Copyright (c) 2011, AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTUOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package mobilemad.app;

/**
 * Project Name:
 *   Mobile Clients for MAD
 *
 * Version:
 *   1.0
 *
 * File Name:
 *   DialogBuilder.java
 *
 * Authors:
 *   AllJoyn
 *
 * Editors:
 *   Andre Lukito, routhsauniere@gmail.com
 *
 * License:
 *  AllJoyn Copyright.
 *
 * Major Revision History:
 *   2014/07/22: complete version 1.0
 */

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class DialogBuilder {
  private static final String TAG = "app.Dialogs";

  public Dialog createUseJoinDialog(final Activity activity, final ChatApplication application) {
    Log.i(TAG, "createUseJoinDialog()");
    final Dialog dialog = new Dialog(activity);
    dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.usejoindialog);
    dialog.setCancelable(false);

    ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(activity, android.R.layout.test_list_item);
    final ListView lvJoinChannel = (ListView) dialog.findViewById(R.id.lvJoinChannel);
    lvJoinChannel.setAdapter(channelListAdapter);

    List<String> channels = application.getFoundChannels();
    for (String channel : channels) {
      int lastDot = channel.lastIndexOf('.');
      if (lastDot < 0) {
        continue;
      }
      String channelName = channel.substring(lastDot + 1);
      if (!channelName.equals("filetransfer")) {
        channelListAdapter.add(channelName);
      }
    }
    channelListAdapter.notifyDataSetChanged();

    lvJoinChannel.setOnItemClickListener(new ListView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = lvJoinChannel.getItemAtPosition(position).toString();
        application.useSetChannelName(name);
        application.useJoinChannel();

        /**
         * Android likes to reuse dialogs for performance reasons. If
				 * we reuse this one, the list of channels will eventually be
				 * wrong since it can change. We have to tell the Android
				 * application framework to forget about this dialog completely.
				 */
        activity.removeDialog(ChatFragment.DIALOG_JOIN_ID);
        dialog.dismiss();
      }
    });

    Button btnJoinCancel = (Button) dialog.findViewById(R.id.btnJoinCancel);
    btnJoinCancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {

        /**
         * Android likes to reuse dialogs for performance reasons. If
				 * we reuse this one, the list of channels will eventually be
				 * wrong since it can change. We have to tell the Android
				 * application framework to forget about this dialog completely.
				 */
        activity.removeDialog(ChatFragment.DIALOG_JOIN_ID);
        dialog.dismiss();
      }
    });

    return dialog;
  }

  public Dialog createUseLeaveDialog(Activity activity, final ChatApplication application) {
    Log.i(TAG, "createUseLeaveDialog()");
    final Dialog dialog = new Dialog(activity);
    dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.useleavedialog);
    dialog.setCancelable(false);

    Button btnLeaveOk = (Button) dialog.findViewById(R.id.btnLeaveOk);
    btnLeaveOk.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        application.useLeaveChannel();
        application.useSetChannelName("Not set");
        dialog.cancel();
      }
    });

    Button btnLeaveCancel = (Button) dialog.findViewById(R.id.btnLeaveCancel);
    btnLeaveCancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        dialog.cancel();
      }
    });

    return dialog;
  }

  public Dialog createNickNameDialog(Activity activity, final ChatApplication application) {
    Log.i(TAG, "createNickNameDialog()");
    final Dialog dialog = new Dialog(activity);
    dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.nicknamedialog);
    dialog.setCancelable(false);

    final EditText txtNickName = (EditText) dialog.findViewById(R.id.txtNickName);
    txtNickName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
          String name = view.getText().toString();
          if (name.trim().length() > 0) {
            application.setNickName(name);
            dialog.cancel();
          }
        }
        return true;
      }
    });

    Button btnNickNameOk = (Button) dialog.findViewById(R.id.btnNickNameOk);
    btnNickNameOk.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        String name = txtNickName.getText().toString();
        if (name.trim().length() > 0) {
          application.setNickName(name);
          dialog.cancel();
        }
      }
    });

    Button btnNickNameCancel = (Button) dialog.findViewById(R.id.btnNickNameCancel);
    btnNickNameCancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        dialog.cancel();
      }
    });

    return dialog;
  }


  public Dialog createHostNameDialog(Activity activity, final ChatApplication application) {
    Log.i(TAG, "createHostNameDialog()");
    final Dialog dialog = new Dialog(activity);
    dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.hostnamedialog);
    dialog.setCancelable(false);

    final EditText txtHostNameChannel = (EditText) dialog.findViewById(R.id.txtHostNameChannel);
    txtHostNameChannel.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
          String name = view.getText().toString();
          if (name.trim().length() > 0) {
            application.hostSetChannelName(name);
            application.hostInitChannel();
            dialog.cancel();
          }
        }
        return true;
      }
    });

    Button btnHostNameOk = (Button) dialog.findViewById(R.id.btnHostNameOk);
    btnHostNameOk.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        String name = txtHostNameChannel.getText().toString();
        if (name.trim().length() > 0) {
          application.hostSetChannelName(name);
          application.hostInitChannel();
          dialog.cancel();
        }
      }
    });

    Button btnHostNameCancel = (Button) dialog.findViewById(R.id.btnHostNameCancel);
    btnHostNameCancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        dialog.cancel();
      }
    });

    return dialog;
  }

  public Dialog createHostStartDialog(Activity activity, final ChatApplication application) {
    Log.i(TAG, "createHostStartDialog()");
    final Dialog dialog = new Dialog(activity);
    dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.hoststartdialog);
    dialog.setCancelable(false);

    Button btnHostStartOk = (Button) dialog.findViewById(R.id.btnHostStartOk);
    btnHostStartOk.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        application.hostStartChannel();
        dialog.cancel();
      }
    });

    Button btnHostStartCancel = (Button) dialog.findViewById(R.id.btnHostStartCancel);
    btnHostStartCancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        dialog.cancel();
      }
    });

    return dialog;
  }

  public Dialog createHostStopDialog(Activity activity, final ChatApplication application) {
    Log.i(TAG, "createHostStopDialog()");
    final Dialog dialog = new Dialog(activity);
    dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.hoststopdialog);
    dialog.setCancelable(false);

    Button btnHostStopOk = (Button) dialog.findViewById(R.id.btnHostStopOk);
    btnHostStopOk.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        application.hostStopChannel();
        dialog.cancel();
      }
    });

    Button btnHostStopCancel = (Button) dialog.findViewById(R.id.btnHostStopCancel);
    btnHostStopCancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        dialog.cancel();
      }
    });

    return dialog;
  }

  public Dialog createAllJoynErrorDialog(Activity activity, final ChatApplication application) {
    Log.i(TAG, "createAllJoynErrorDialog()");
    final Dialog dialog = new Dialog(activity);
    dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.alljoynerrordialog);
    dialog.setCancelable(false);

    TextView lblErrorDescription = (TextView) dialog.findViewById(R.id.lblErrorDescription);
    lblErrorDescription.setText(application.getErrorString());

    Button btnErrorOk = (Button) dialog.findViewById(R.id.btnErrorOk);
    btnErrorOk.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        dialog.cancel();
      }
    });

    return dialog;
  }
}
