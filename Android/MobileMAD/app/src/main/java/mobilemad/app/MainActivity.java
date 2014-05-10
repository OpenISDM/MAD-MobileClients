package mobilemad.app;

import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

  private DialogFragment alertDlgFragment;

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  protected static SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  protected static ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Config.path = getExternalFilesDir(null).getAbsolutePath();

    // Set up the action bar.
    final ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    // When swiping between different sections, select the corresponding
    // tab. We can also use ActionBar.Tab#select() to do this if we have
    // a reference to the Tab.
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
      }
    });

    // For each of the sections in the app, add a tab to the action bar.
    for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
      // Create a tab with text corresponding to the page title defined by
      // the adapter. Also specify this Activity object, which implements
      // the TabListener interface, as the callback (listener) for when
      // this tab is selected.
      actionBar.addTab(
        actionBar.newTab()
          .setText(mSectionsPagerAdapter.getPageTitle(i))
          .setTabListener(this)
      );
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuitem_about:
        Calendar calendar = Calendar.getInstance();
        alertDlgFragment = AlertDialogFragment.newInstance("Academia Sinica - OPENISDM", "Mobile MAD App ©" + String.valueOf(calendar.get(Calendar.YEAR)), 0);
        alertDlgFragment.setCancelable(false);
        alertDlgFragment.show(getFragmentManager(), "dialog");
        return true;
      case R.id.menuitem_quit:
        finish(); // close the activity
        return true;
    }
    return false;
  }

  // When the given tab is selected, switch to the corresponding page in
  // the ViewPager.
  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    mViewPager.setCurrentItem(tab.getPosition());
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    private Fragment fragmentMaps, fragmentImage, fragmentData, fragmentList, fragmentSettings;

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 1:
          fragmentImage = new ImageFragment();
          return fragmentImage;
        case 2:
          fragmentList = new ListFragment();
          return fragmentList;
        case 3:
          fragmentSettings = new SettingsFragment();
          return fragmentSettings;
        case 4:
          fragmentData = new DataFragment();
          return fragmentData;
        default:
          fragmentMaps = new MapsFragment();
          return fragmentMaps;
      }
    }

    @Override
    public int getCount() {
      return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      Locale l = Locale.getDefault();
      switch (position) {
        case 0:
          return getString(R.string.ui_tabname_maps).toUpperCase(l);
        case 1:
          return getString(R.string.ui_tabname_image).toUpperCase(l);
        case 2:
          return getString(R.string.ui_tabname_list).toUpperCase(l);
        case 3:
          return getString(R.string.ui_tabname_settings).toUpperCase(l);
        case 4:
          return getString(R.string.ui_tabname_data).toUpperCase(l);
      }
      return null;
    }
  }
}