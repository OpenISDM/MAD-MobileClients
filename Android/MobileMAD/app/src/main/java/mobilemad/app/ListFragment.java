package mobilemad.app;

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

  private void contentData(String... fileName) {
    result = dataViewer.RDFFacilities(fileName[0]);
    if (result.isEmpty()) {
      result = dataViewer.JSONFacilities(fileName[1]);
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
      contentData(fileName);

      return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
      if (!result.isEmpty()) {
        sAdapter = new SimpleAdapter(getActivity(), listData, R.layout.list_view_data, new String[] {"Name", "Type"}, new int[] {R.id.txtName, R.id.txtType});
        lvData.setAdapter(sAdapter);
      }
    }
  }

  @Override
  public void setMenuVisibility(final boolean visible) {
    super.setMenuVisibility(visible);
    if (visible) {
      if (rootView != null) {
        new contentViewer().execute("dataFiles.rdf", "dataFiles.json");
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
    // Inflate the layout for this fragment
    rootView = inflater.inflate(R.layout.list_fragment, container, false);

    lvData = (ListView) rootView.findViewById(R.id.lvData);

    lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
        if ((lvData != null)) {
          String name, type, telephone, district, address, msg;

          name = result.get(position).get("Name").toString();
          type = result.get(position).get("Type").toString();
          telephone = result.get(position).get("Telphone").toString();
          district = result.get(position).get("District").toString();
          address = result.get(position).get("Address").toString();

          msg = "Name: " + name + "\n";
          msg += "Type: " + type + "\n";
          msg += "Telephone: " + telephone + "\n";
          msg += "District: " + district + "\n";
          msg += "Address: " + address + "\n";

          alertDlgFragment = AlertDialogFragment.newInstance("Detail Information", msg, 1);
          alertDlgFragment.setCancelable(false);
          AlertDialogFragment.name = name;
          AlertDialogFragment.type = type;
          AlertDialogFragment.telephone = telephone;
          AlertDialogFragment.district = district;
          AlertDialogFragment.address = address;
          AlertDialogFragment.latitude = Double.valueOf(result.get(position).get("Latitude").toString());
          AlertDialogFragment.longitude = Double.valueOf(result.get(position).get("Longitude").toString());
          alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
        }
      }
    });

    new contentViewer().execute("dataFiles.rdf", "dataFiles.json");

    return rootView;
  }
}
