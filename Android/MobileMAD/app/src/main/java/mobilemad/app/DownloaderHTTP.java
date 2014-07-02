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
 *   DownloaderHTTP.java
 *
 * Abstract:
 *   DownloaderHTTP.java is the class files in Mobile Clients for MAD project.
 *   DownloaderHTTP will be used in Mobile Clients for MAD app as:
 *   1. Check network availability.
 *   2. Download data from URL.
 *   3. Save downloaded data into bitmap or text file.
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloaderHTTP {

  /**
   * Function Name:
   * isNetworkAvailable
   * <p/>
   * Function Description:
   * Returns the network availability, such as Mobile or Wi-Fi.
   * <p/>
   * Parameters:
   * ConnectivityManager connectivityManager - System Service value for connectivity services,
   * such as Mobile or Wi-Fi.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is true;
   * otherwise, the returned value is false.
   * <p/>
   * Possible Error Code or Exception:
   * None.
   */
  protected boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
    boolean result = false;
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

    if ((networkInfo != null) && (networkInfo.isConnected())) {
      result = true;
    }

    return result;
  }

  /**
   * Function Name:
   * downloadUrl
   * <p/>
   * Function Description:
   * Sets up a connection and get the downloaded data from WebServices.
   * <p/>
   * Parameters:
   * String urlString - representation of a URL for download data.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is InputStream;
   * otherwise, the returned value is null.
   * <p/>
   * Possible Error Code or Exception:
   * Connection problem.
   * Wrong URL.
   */
  protected InputStream downloadUrl(String urlString) {
    HttpURLConnection conn = null;

    try {
      URL url = new URL(urlString);

      conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);

      /** Starts the query */
      conn.connect();

      return conn.getInputStream();
    } catch (Exception e) {
      Log.i("Exception", e.getMessage());
      return null;
    }
  }

  /**
   * Procedure Name:
   * saveImage
   * <p/>
   * Procedure Description:
   * Save downloaded data into bitmap file as PNG format.
   * <p/>
   * Parameters:
   * InputStream inputStream - representation of downloaded data.
   * String path - directory location to save the bitmap file.
   * String files - full path with filename to save the bitmap file.
   * <p/>
   * Possible Error Code or Exception:
   * Can't save the file.
   */
  protected void saveImage(InputStream inputStream, String path, String files) throws IOException {
    OutputStream outputStream = null;

    try {
      Bitmap bmp = BitmapFactory.decodeStream(inputStream);
      File file = new File(path);
      file.mkdirs();
      file = new File(files);
      outputStream = new FileOutputStream(file);
      bmp.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
    } catch (Exception e) {
      Log.i("saveImage", e.getMessage());
    } finally {
      outputStream.close();
    }
  }

  /**
   * Procedure Name:
   * saveText
   * <p/>
   * Procedure Description:
   * Save downloaded data into file.
   * <p/>
   * Parameters:
   * InputStream inputStream - representation of downloaded data.
   * String path - directory location to save the file.
   * String files - full path with filename to save the file.
   * <p/>
   * Possible Error Code or Exception:
   * Can't save the file.
   */
  protected void saveText(InputStream inputStream, String path, String files) throws IOException {
    OutputStream outputStream = null;
    byte[] buffer = new byte[1024];
    int bufferLength = 0;

    try {
      File file = new File(path);
      file.mkdirs();
      file = new File(files);
      outputStream = new FileOutputStream(file);

      //now, read through the input buffer and write the contents to the file
      while ((bufferLength = inputStream.read(buffer)) > 0) {
        //add the data in the buffer to the file in the file output stream (the file on the sd card
        outputStream.write(buffer, 0, bufferLength);
      }
    } catch (Exception e) {
      Log.i("saveText", e.getMessage());
    } finally {
      outputStream.close();
    }
  }
}
