package mobilemad.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;

import mobilemad.app.MainActivity;
import mobilemad.app.MapsFragment;
import mobilemad.app.ImageFragment;
import mobilemad.app.ListFragment;
import mobilemad.app.ChatFragment;
import mobilemad.app.SettingsFragment;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
  private MainActivity mainActivity;
  private ViewPager mViewPager;
  private MainActivity.SectionsPagerAdapter mSectionsPagerAdapter;
  private Fragment mapsFragment, imageFragment, listFragment, chatFragment, settingsFragment;
  private Dialog result;
  private Dialog dlg;
  private AlertDialog alertDialog;

  private ListView lvData;

  private Button btnJoin, btnLeave, btnSendFiles;
  private TextView lblChannelName, lblChannelStatus;
  private EditText txtMessage;
  private ListView lvHistoryList;

  private ListView lvJoinChannel;
  private Button btnJoinCancel, btnLeaveOk, btnLeaveCancel;

  private EditText txtImageLocation, txtDataLocation;
  private Button btnDownloadFiles, btnDeleteFiles;
  private TextView lblNickName, lblHostChannelName, lblHostChannelStatus;
  private Button btnSetNickName, btnHostSetName, btnHostStart, btnHostStop, btnHostQuit;

  private EditText txtNickName;
  private Button btnNickNameOk, btnNickNameCancel;

  private EditText txtHostNameChannel;
  private Button btnHostNameOk, btnHostNameCancel;

  private Button btnHostStartOk, btnHostStartCancel, btnHostStopOk, btnHostStopCancel;

  private final String nick = "aaa";
  private final String channel = "test";

  public MainActivityTest() {
    super(MainActivity.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    mainActivity = getActivity();
    mViewPager = mainActivity.mViewPager;
    mSectionsPagerAdapter = mainActivity.mSectionsPagerAdapter;
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void test1MainActivity() throws Exception {
    setWiFi(true);

    assertNotNull("Main Activity not allowed to be Null", mainActivity);
  }

  public void test2Pager() throws Exception {
    assertNotNull("mViewPager not allowed to be Null", mViewPager);
    assertNotNull("mSectionsPagerAdapter not allowed to be Null", mSectionsPagerAdapter);
  }

  public void test3NavigationBar() throws Exception {
    final ActionBar actionBar = mainActivity.getSupportActionBar();
    assertEquals(ActionBar.NAVIGATION_MODE_TABS, actionBar.getNavigationMode());
    assertEquals(5, mSectionsPagerAdapter.getCount());

    ActionBar.Tab tab0 = actionBar.getTabAt(0);
    assertNotNull(tab0);
    assertNotNull(tab0.getText());
    assertEquals(mainActivity.getBaseContext().getString(R.string.ui_tabname_maps).toUpperCase(),
      tab0.getText());

    ActionBar.Tab tab1 = actionBar.getTabAt(1);
    assertNotNull(tab1);
    assertNotNull(tab1.getText());
    assertEquals(mainActivity.getBaseContext().getString(R.string.ui_tabname_image).toUpperCase(),
      tab1.getText());

    ActionBar.Tab tab2 = actionBar.getTabAt(2);
    assertNotNull(tab2);
    assertNotNull(tab2.getText());
    assertEquals(mainActivity.getBaseContext().getString(R.string.ui_tabname_list).toUpperCase(),
      tab2.getText());

    ActionBar.Tab tab3 = actionBar.getTabAt(3);
    assertNotNull(tab3);
    assertNotNull(tab3.getText());
    assertEquals(mainActivity.getBaseContext().getString(R.string.ui_tabname_chat).toUpperCase(),
      tab3.getText());

    ActionBar.Tab tab4 = actionBar.getTabAt(4);
    assertNotNull(tab4);
    assertNotNull(tab4.getText());
    assertEquals(mainActivity.getBaseContext().getString(R.string.ui_tabname_settings).toUpperCase(),
      tab4.getText());
  }

  public void test4MapsFragment() {
    mapsFragment = setViewPager(0);

    GoogleMap googleMap;
    FragmentManager fm = mapsFragment.getChildFragmentManager();
    SupportMapFragment supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.mapView);

    if (supportMapFragment == null) {
      supportMapFragment = SupportMapFragment.newInstance();
      googleMap = supportMapFragment.getMap();

      googleMap.setMyLocationEnabled(true);

      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MapsFragment.deviceLatitude, MapsFragment.deviceLongitude), 14.0f));

      assertEquals(new LatLng(MapsFragment.deviceLatitude, MapsFragment.deviceLongitude), googleMap.getCameraPosition().target);
    }
  }

  public void test5ImageFragment() {
    String path = Config.path + File.separator + "data";

    File file = new File(path + File.separator + "imgMaps.png");
    assertTrue(file.exists());

    imageFragment = setViewPager(1);
  }

  public void test6ListFragment() {
    String path = Config.path + File.separator + "data";

    File file = new File(path + File.separator + "dataFiles.rdf");
    assertTrue(file.exists());

    listFragment = setViewPager(2);

    lvData = (ListView) listFragment.getView().findViewById(R.id.lvData);
    assertNotNull(lvData);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        lvData.performItemClick(lvData.getAdapter().getView(0, null, null), 0, lvData.getAdapter().getItemId(0));
      }
    });
    getInstrumentation().waitForIdleSync();

    android.app.Fragment dialog = mainActivity.getFragmentManager().findFragmentByTag("dialog");
    assertTrue(dialog instanceof DialogFragment);
    assertTrue(((DialogFragment) dialog).getShowsDialog());

    String title = dialog.getArguments().getString("title");
    assertEquals("Detail Information", title);

    assertNull(MapsFragment.name);
    assertNull(MapsFragment.type);
    assertNull(MapsFragment.category);
    assertNull(MapsFragment.district);
    assertNull(MapsFragment.address);
    assertNull(MapsFragment.telephone);
    assertEquals(0.0, MapsFragment.latitude);
    assertEquals(0.0, MapsFragment.longitude);

    alertDialog = (AlertDialog) ((DialogFragment) dialog).getDialog();

    final Button btnShow = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

    assertNotNull(btnShow);
    assertEquals("Show", btnShow.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnShow.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertNotNull(MapsFragment.name);
    assertNotNull(MapsFragment.type);
    assertNotNull(MapsFragment.category);
    assertNotNull(MapsFragment.district);
    assertNotNull(MapsFragment.address);
    assertNotNull(MapsFragment.telephone);
    assertNotNull(MapsFragment.latitude);
    assertNotNull(MapsFragment.longitude);

    final Button btnCancel = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

    assertNotNull(btnCancel);
    assertEquals("Cancel", btnCancel.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnCancel.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertFalse(dialog.isVisible());
  }

  public void test7ChatFragment() {
    chatFragment = setViewPager(3);
  }

  public void test8SettingFragment() {
    settingsFragment = setViewPager(4);

    downloadFiles();
    deleteFiles();
    downloadFiles();

    setNickName();
    setHostChannelName();
    startHostChannel();

    joinChannel();
    sendMessage();
    sendFiles();
    leaveChannel();

    stopHostChannel();
    quitApplication();
  }

  public void setWiFi(boolean status) {
    WifiManager wifiManager = (WifiManager) mainActivity.getSystemService(Context.WIFI_SERVICE);

    wifiManager.setWifiEnabled(status);

    if (status) {
      ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
        .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

      while (networkInfo == null) {
        waitFinish(100);
        networkInfo = connectivityManager.getActiveNetworkInfo();
      }

      while (!networkInfo.isConnected()) {
        waitFinish(100);
      }
    }
  }

  public void waitFinish(int counter) {
    try {
      Thread.sleep(counter);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public Fragment setViewPager(final int position) {
    Fragment result;

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        mViewPager.setCurrentItem(position);
      }
    });
    getInstrumentation().waitForIdleSync();

    assertEquals(position, mViewPager.getCurrentItem());

    result = (Fragment) mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());

    assertNotNull(result);
    switch (position) {
      case 0:
        assertTrue(result instanceof MapsFragment);
        break;
      case 1:
        assertTrue(result instanceof ImageFragment);
        break;
      case 2:
        assertTrue(result instanceof ListFragment);
        break;
      case 3:
        assertTrue(result instanceof ChatFragment);
        break;
      case 4:
        assertTrue(result instanceof SettingsFragment);
        break;
    }
    assertNotNull(result.getView());

    return result;
  }

  public void downloadFiles() {
    String path = Config.path + File.separator + "data";

    txtDataLocation = (EditText) settingsFragment.getView().findViewById(R.id.txtDataLocation);

    assertNotNull(txtDataLocation);

    txtImageLocation = (EditText) settingsFragment.getView().findViewById(R.id.txtImageLocation);

    assertNotNull(txtImageLocation);

    btnDownloadFiles = (Button) settingsFragment.getView().findViewById(R.id.btnDownloadFiles);

    assertNotNull(btnDownloadFiles);
    assertEquals("Download Files", btnDownloadFiles.getText());

    setWiFi(false);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        txtImageLocation.setText(Config.IMG_LOCATION2);
        txtDataLocation.setText(Config.FILE_LOCATION2);
        btnDownloadFiles.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    waitFinish(1500);

    android.app.Fragment dialog = mainActivity.getFragmentManager().findFragmentByTag("dialog");
    assertTrue(dialog instanceof DialogFragment);
    assertTrue(((DialogFragment) dialog).getShowsDialog());

    String title = dialog.getArguments().getString("title");
    assertEquals("No Network Available", title);

    alertDialog = (AlertDialog) ((DialogFragment) dialog).getDialog();

    final Button btnSettings = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

    assertNotNull(btnSettings);
    assertEquals("Settings", btnSettings.getText());

    final Button btnCancel = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

    assertNotNull(btnCancel);
    assertEquals("Cancel", btnCancel.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnCancel.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertFalse(dialog.isVisible());

    setWiFi(true);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        txtImageLocation.setText(Config.IMG_LOCATION2);
        txtDataLocation.setText(Config.FILE_LOCATION2);
        btnDownloadFiles.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    waitFinish(2000);

    File file = new File(path + File.separator + "dataFiles.rdf");
    assertTrue(file.exists());
    file = new File(path + File.separator + "imgMaps.png");
    assertTrue(file.exists());
  }

  public void deleteFiles() {
    String path = Config.path + File.separator + "data";

    File file = new File(path + File.separator + "dataFiles.rdf");
    assertTrue(file.exists());
    file = new File(path + File.separator + "imgMaps.png");
    assertTrue(file.exists());

    btnDeleteFiles = (Button) settingsFragment.getView().findViewById(R.id.btnDeleteFiles);

    assertNotNull(btnDeleteFiles);
    assertEquals("Delete Files", btnDeleteFiles.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnDeleteFiles.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    file = new File(path + File.separator + "dataFiles.rdf");
    assertFalse(file.exists());
    file = new File(path + File.separator + "imgMaps.png");
    assertFalse(file.exists());

    getInstrumentation().waitForIdleSync();
    android.app.Fragment dialog = mainActivity.getFragmentManager().findFragmentByTag("dialog");
    assertTrue(dialog instanceof DialogFragment);
    assertTrue(((DialogFragment) dialog).getShowsDialog());

    String title = dialog.getArguments().getString("title");
    assertEquals("Delete Internal Storage Files", title);

    alertDialog = (AlertDialog) ((DialogFragment) dialog).getDialog();

    final Button btnOk = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

    assertNotNull(btnOk);
    assertEquals("OK", btnOk.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnOk.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertFalse(dialog.isVisible());
  }

  public void setNickName() {
    lblNickName = (TextView) settingsFragment.getView().findViewById(R.id.lblNickName);

    assertNotNull(lblNickName);
    assertEquals("Not set", lblNickName.getText());

    btnSetNickName = (Button) settingsFragment.getView().findViewById(R.id.btnSetNickName);

    assertNotNull(btnSetNickName);
    assertEquals("Set NickName", btnSetNickName.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnSetNickName.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    dlg = ((SettingsFragment) settingsFragment).aDialog;
    assertTrue(dlg.isShowing());

    txtNickName = (EditText) dlg.findViewById(R.id.txtNickName);
    assertNotNull(txtNickName);

    btnNickNameOk = (Button) dlg.findViewById(R.id.btnNickNameOk);
    assertNotNull(btnNickNameOk);

    btnNickNameCancel = (Button) dlg.findViewById(R.id.btnNickNameCancel);
    assertNotNull(btnNickNameCancel);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        txtNickName.setText(nick);
        btnNickNameOk.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertEquals(nick, lblNickName.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnNickNameCancel.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertFalse(dlg.isShowing());
  }

  public void setHostChannelName() {
    lblHostChannelName = (TextView) settingsFragment.getView().findViewById(R.id.lblHostChannelName);

    assertNotNull(lblHostChannelName);
    assertEquals("Not set", lblHostChannelName.getText());

    btnHostSetName = (Button) settingsFragment.getView().findViewById(R.id.btnHostSetName);

    assertNotNull(btnHostSetName);
    assertEquals("Set Channel Name", btnHostSetName.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnHostSetName.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    dlg = ((SettingsFragment) settingsFragment).aDialog;
    assertTrue(dlg.isShowing());

    txtHostNameChannel = (EditText) dlg.findViewById(R.id.txtHostNameChannel);
    assertNotNull(txtHostNameChannel);

    btnHostNameOk = (Button) dlg.findViewById(R.id.btnHostNameOk);
    assertNotNull(btnHostNameOk);

    btnHostNameCancel = (Button) dlg.findViewById(R.id.btnHostNameCancel);
    assertNotNull(btnHostNameCancel);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        txtHostNameChannel.setText(channel);
        btnHostNameOk.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertEquals(channel, lblHostChannelName.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnHostNameCancel.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertFalse(dlg.isShowing());
  }

  public void startHostChannel() {
    lblHostChannelStatus = (TextView) settingsFragment.getView().findViewById(R.id.lblHostChannelStatus);

    assertNotNull(lblHostChannelStatus);
    assertEquals("Idle", lblHostChannelStatus.getText());

    btnHostStart = (Button) settingsFragment.getView().findViewById(R.id.btnHostStart);

    assertNotNull(btnHostStart);
    assertEquals("Start Channel", btnHostStart.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnHostStart.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    dlg = ((SettingsFragment) settingsFragment).aDialog;
    assertTrue(dlg.isShowing());

    btnHostStartOk = (Button) dlg.findViewById(R.id.btnHostStartOk);
    assertNotNull(btnHostStartOk);

    btnHostStartCancel = (Button) dlg.findViewById(R.id.btnHostStartCancel);
    assertNotNull(btnHostStartCancel);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnHostStartOk.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    waitFinish(500);

    assertEquals("Advertised", lblHostChannelStatus.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnHostStartCancel.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertFalse(dlg.isShowing());
  }

  public void stopHostChannel() {
    settingsFragment = setViewPager(4);

    btnHostStop = (Button) settingsFragment.getView().findViewById(R.id.btnHostStop);

    assertNotNull(btnHostStop);
    assertEquals("Stop Channel", btnHostStop.getText());
    assertTrue(btnHostStop.isEnabled());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnHostStop.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    dlg = ((SettingsFragment) settingsFragment).aDialog;
    assertTrue(dlg.isShowing());

    btnHostStopOk = (Button) dlg.findViewById(R.id.btnHostStopOk);
    assertNotNull(btnHostStopOk);

    btnHostStopCancel = (Button) dlg.findViewById(R.id.btnHostStopCancel);
    assertNotNull(btnHostStopCancel);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnHostStopOk.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    waitFinish(500);

    assertEquals("Idle", lblHostChannelStatus.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnHostStopCancel.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertFalse(dlg.isShowing());
  }

  public void quitApplication() {
    btnHostQuit = (Button) settingsFragment.getView().findViewById(R.id.btnHostQuit);

    assertNotNull(btnHostQuit);
    assertEquals("Quit Application", btnHostQuit.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnHostQuit.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();
  }

  public void joinChannel() {
    chatFragment = setViewPager(3);

    lblChannelName = (TextView) chatFragment.getView().findViewById(R.id.lblChannelName);

    assertNotNull(lblChannelName);
    assertEquals("Not set", lblChannelName.getText());

    lblChannelStatus = (TextView) chatFragment.getView().findViewById(R.id.lblChannelStatus);

    assertNotNull(lblChannelStatus);
    assertEquals("Idle", lblChannelStatus.getText());

    btnJoin = (Button) chatFragment.getView().findViewById(R.id.btnJoin);

    assertNotNull(btnJoin);
    assertEquals("Join Channel", btnJoin.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnJoin.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    dlg = ((ChatFragment) chatFragment).aDialog;
    assertTrue(dlg.isShowing());

    lvJoinChannel = (ListView) dlg.findViewById(R.id.lvJoinChannel);
    assertNotNull(lvJoinChannel);

    btnJoinCancel = (Button) dlg.findViewById(R.id.btnJoinCancel);
    assertNotNull(btnJoinCancel);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        lvJoinChannel.performItemClick(lvJoinChannel.getAdapter().getView(0, null, null), 0, lvJoinChannel.getAdapter().getItemId(0));
      }
    });
    getInstrumentation().waitForIdleSync();

    assertEquals(channel, lblChannelName.getText());
    assertEquals("Joined", lblChannelStatus.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnJoinCancel.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertFalse(dlg.isShowing());
  }

  public void sendFiles() {
    btnSendFiles = (Button) chatFragment.getView().findViewById(R.id.btnSendFiles);

    assertNotNull(btnSendFiles);
    assertEquals("Send Files", btnSendFiles.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnSendFiles.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();
  }

  public void sendMessage() {
    lvHistoryList = (ListView) chatFragment.getView().findViewById(R.id.lvHistoryList);

    assertNotNull(lvHistoryList);
    assertEquals(0, lvHistoryList.getAdapter().getCount());

    txtMessage = (EditText) chatFragment.getView().findViewById(R.id.txtMessage);

    assertNotNull(txtMessage);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        txtMessage.setText("send testing message.");
        txtMessage.onEditorAction(0);
      }
    });
    getInstrumentation().waitForIdleSync();

    assertEquals(1, lvHistoryList.getAdapter().getCount());
    String text = lvHistoryList.getAdapter().getItem(0).toString();
    assertEquals("(" + nick + ") send testing message.", text.substring(text.lastIndexOf("]") + 2));
  }

  public void leaveChannel() {
    btnLeave = (Button) chatFragment.getView().findViewById(R.id.btnLeave);

    assertNotNull(btnLeave);
    assertEquals("Leave Channel", btnLeave.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnLeave.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    dlg = ((ChatFragment) chatFragment).aDialog;
    assertTrue(dlg.isShowing());

    btnLeaveOk = (Button) dlg.findViewById(R.id.btnLeaveOk);
    assertNotNull(btnLeaveOk);

    btnLeaveCancel = (Button) dlg.findViewById(R.id.btnLeaveCancel);
    assertNotNull(btnLeaveCancel);

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnLeaveOk.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertEquals("Not set", lblChannelName.getText());
    assertEquals("Idle", lblChannelStatus.getText());

    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        btnLeaveCancel.performClick();
      }
    });
    getInstrumentation().waitForIdleSync();

    assertFalse(dlg.isShowing());
  }
}
