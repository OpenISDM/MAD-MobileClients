package mobilemad.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

/*
DataFragment class that used for development only. Show result of parse data.
Experiment with the Override method from Fragment.
 */
public class DataFragment extends Fragment {

  private View rootView;
  private EditText txtData;

  private DataViewer dataViewer;
  private HashMap<Integer, HashMap<String, Object>> result;
  private String content;
  private Boolean resume;
  private int ctr;

  private void getContent() {
    txtData.setText("");
    content = dataViewer.showData("dataFiles.rdf");
    if (content.trim().length() > 0) {
      txtData.setText(content);
    }
  }

  private void contentJSON() {
    String results = "";

    txtData.setText("");

    result = dataViewer.JSONFacilities("dataFiles.json");

    for(Map.Entry<Integer, HashMap<String, Object>> entry : result.entrySet()) {
      int key = entry.getKey();
      results += String.valueOf(key) + ":\n";
      HashMap<String, Object> value = entry.getValue();
      for(Map.Entry<String, Object> entry1 : value.entrySet()) {
        String key1 = entry1.getKey();
        Object value1 = entry1.getValue();
        results += key1 + ": ";
        results += String.valueOf(value1) + "\n";
      }
      results += "\n";
    }

    txtData.setText(results);
  }

  private void contentRDF() {
    String results = "";

    txtData.setText("");

    result = dataViewer.RDFFacilities("dataFiles.rdf");

    for(Map.Entry<Integer, HashMap<String, Object>> entry : result.entrySet()) {
      int key = entry.getKey();
      results += String.valueOf(key) + ":\n";
      HashMap<String, Object> value = entry.getValue();
      for(Map.Entry<String, Object> entry1 : value.entrySet()) {
        String key1 = entry1.getKey();
        Object value1 = entry1.getValue();
        results += key1 + ": ";
        results += String.valueOf(value1) + "\n";
      }
      results += "\n";
    }

    txtData.setText(results);
  }

  @Override
  public void setMenuVisibility(final boolean visible) { // 0
    super.setMenuVisibility(visible);
    if (visible) {
      if (rootView != null) {
        /*getContent();*/
        /*contentJSON();*/
        contentRDF();
      }
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) { // 1
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { // 2
    // Inflate the layout for this fragment
    rootView = inflater.inflate(R.layout.data_fragment, container, false);

    txtData = (EditText) rootView.findViewById(R.id.txtData);

    dataViewer = new DataViewer();
    result = new HashMap<Integer, HashMap<String, Object>>();

    /*getContent();*/
    /*contentJSON();*/
    contentRDF();

    return rootView;
  }

  @Override
  public void onStart() { // 3
    super.onStart();
  }

  @Override
  public void onResume() { // 4
    super.onResume();
  }
}
