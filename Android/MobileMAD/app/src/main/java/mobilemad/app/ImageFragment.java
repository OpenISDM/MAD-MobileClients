package mobilemad.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends Fragment {

  private ImageView ivMaps;
  private Bitmap bitmap;
  private View rootView;

  private DataViewer dataViewer;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    rootView = inflater.inflate(R.layout.maps_fragment, container, false);

    ivMaps = (ImageView) rootView.findViewById(R.id.ivMaps);
    ivMaps.setImageBitmap(null);

    dataViewer = new DataViewer();

    bitmap = dataViewer.imgViewer("imgMaps.png");
    if (bitmap != null) {
      ivMaps.setImageBitmap(bitmap);
    }

    return rootView;
  }
}
