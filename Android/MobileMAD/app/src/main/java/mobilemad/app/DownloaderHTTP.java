package mobilemad.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andre on 05/09/2014.
 */
public class DownloaderHTTP {
  // Given a string representation of a URL, sets up a connection and gets
  // an input stream.
  protected InputStream downloadUrl(String urlString) {
    HttpURLConnection conn = null;

    try {
      URL url = new URL(urlString);

      conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);
      // Starts the query
      conn.connect();

      return conn.getInputStream();
    } catch (Exception e) {
      Log.i("Exception", e.getMessage());
      return null;
    }
  }

  protected void saveImage(InputStream inputStream, String path, String files) {
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
      try {
        outputStream.close();
      } catch(Exception ex) {}
    }
  }

  protected void saveText(InputStream inputStream, String path, String files) {
    OutputStream outputStream = null;
    byte[] buffer = new byte[1024];
    int bufferLength = 0;

    try {
      File file = new File(path);
      file.mkdirs();
      file = new File(files);
      outputStream = new FileOutputStream(file);

      //now, read through the input buffer and write the contents to the file
      while ((bufferLength = inputStream.read(buffer)) > 0 ) {
        //add the data in the buffer to the file in the file output stream (the file on the sd card
        outputStream.write(buffer, 0, bufferLength);
      }
    } catch (Exception e) {
      Log.i("saveText", e.getMessage());
    } finally {
      try {
        outputStream.close();
      } catch(Exception ex) {}
    }
  }
}
