package mobilemad.app;

import android.app.DialogFragment;
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

  private void contentJSON() {
    result = dataViewer.JSONFacilities("dataFiles.json");
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

    sAdapter = new SimpleAdapter(getActivity(), listData, R.layout.list_view_data, new String[] {"Name", "Type"}, new int[] {R.id.txtName, R.id.txtType});
    lvData.setAdapter(sAdapter);
  }

  private void contentRDF() {
    result = dataViewer.RDFFacilities("dataFiles.rdf");
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

    sAdapter = new SimpleAdapter(getActivity(), listData, R.layout.list_view_data, new String[] {"Name", "Type"}, new int[] {R.id.txtName, R.id.txtType});
    lvData.setAdapter(sAdapter);
  }

  @Override
  public void setMenuVisibility(final boolean visible) {
    super.setMenuVisibility(visible);
    if (visible) {
      if (rootView != null) {
        /*contentJSON();*/
        contentRDF();
      }
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    rootView = inflater.inflate(R.layout.list_fragment, container, false);

    lvData = (ListView) rootView.findViewById(R.id.lvData);

    dataViewer = new DataViewer();
    result = new HashMap<Integer, HashMap<String, Object>>();
    data = new HashMap<String, Object>();
    listData = new ArrayList<HashMap<String,Object>>();

    lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
        if ((lvData != null)) {
          String name, type, telephone, district, address, msg;

          /*Log.i("Name", "Item with id [" + id + "] - Position [" + position + "] - Name [" + result.get(position).get("name").toString() + "]");*/

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

          alertDlgFragment = alertDialogFragment.newInstance("Detail Information", msg, 1);
          alertDialogFragment.name = name;
          alertDialogFragment.type = type;
          alertDialogFragment.telephone = telephone;
          alertDialogFragment.district = district;
          alertDialogFragment.address = address;
          alertDialogFragment.latitude = Double.valueOf(result.get(position).get("Latitude").toString());
          alertDialogFragment.longitude = Double.valueOf(result.get(position).get("Longitude").toString());
          alertDlgFragment.show(getActivity().getFragmentManager(), "dialog");
        }
      }
    });

    /*contentJSON();*/
    contentRDF();

    return rootView;
  }
}
