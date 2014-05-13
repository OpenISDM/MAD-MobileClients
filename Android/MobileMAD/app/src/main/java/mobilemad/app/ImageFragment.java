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
 *   ImageFragment.java
 *
 * Abstract:
 *   ImageFragment.java is the tab Image in Mobile Clients for MAD project.
 *   Image will show Maps in image for user when disaster occur and there is no network connection.
 *
 * Authors:
 *   Andre Lukito, routhsauniere@gmail.com
 *
 * License:
 *  GPL 3.0 This file is subject to the terms and conditions defined
 *  in file 'COPYING.txt', which is part of this source code package.
 *
 * Major Revision History:
 *   2014/5/02: complete version 1.0
 */

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;

public class ImageFragment extends Fragment {
  private ImageView ivMaps;
  private Bitmap bitmap;
  private View rootView;

  private DataViewer dataViewer;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    bitmap = null;
    dataViewer = new DataViewer();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.image_fragment, container, false);

    ivMaps = (ImageView) rootView.findViewById(R.id.ivMaps);

    try {
      bitmap = dataViewer.imgViewer("imgMaps.png");
    } catch (IOException e) {
      Log.i("ImageFragment - onCreateView - IOException", e.getMessage());
    }

    ivMaps.setImageBitmap(bitmap);

    return rootView;
  }
}
