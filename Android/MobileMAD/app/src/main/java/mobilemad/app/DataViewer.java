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
 *   DataViewer.java
 *
 * Abstract:
 *   DataViewer.java is the class files in Mobile Clients for MAD project.
 *   DataViewer will be used for Data Viewer in Mobile Clients for MAD app. It can:
 *   1. Show bitmap
 *   2. Parse standard format of XML and JSON
 *   3. Parse MAD data format in JSON and RDF/XML
 *   4. Show raw text content
 *   5. Show message in Toast
 *   6. Get the resource icon for image.
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class DataViewer {

  private StringBuffer sb2;
  private static final String ns = null;

  /**
   * Function Name:
   *   imgViewer
   *
   * Function Description:
   *   Open image file in specific path and put into bitmap variable
   *
   * Parameters:
   *   String fileName - filename of image file.
   *
   * Returned Value:
   *   If the function returned normally, the returned is Bitmap;
   *   otherwise, the returned value is null.
   *
   * Possible Error Code or Exception:
   *   File not found.
   */
  protected Bitmap imgViewer(String fileName) throws IOException {
    Bitmap results = null;
    FileInputStream fIn = null;
    InputStreamReader isr = null;
    InputStream is = null;

    try {
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;
      fIn = new FileInputStream(files);
      isr = new InputStreamReader(fIn);
      is = fIn;

      results = BitmapFactory.decodeStream(is);
    } catch (Exception e){
      Log.i("imgViewer Error: ", e.getMessage());
    } finally {
      if (is != null) {
        is.close();
      }
      if (isr != null) {
        isr.close();
      }
      if (fIn != null) {
        fIn.close();
      }
    }

    return results;
  }

  /**
   * Function Name:
   *   XMLParser
   *
   * Function Description:
   *   Parse every XML file format with standard format
   *
   * Parameters:
   *   String fileName - filename of XML file.
   *
   * Returned Value:
   *   If the function returned normally, the returned is String;
   *   otherwise, the returned value is empty String.
   *
   * Possible Error Code or Exception:
   *   File not found.
   */
  protected String XMLParser(String fileName) throws IOException {
    String results = "";
    FileInputStream fIn = null;
    InputStreamReader isr = null;

    try {
      XmlPullParser parser = Xml.newPullParser();
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;
      fIn = new FileInputStream(files);
      isr = new InputStreamReader(fIn);

      // auto-detect the encoding from the stream
      parser.setInput(isr);
      int eventType = parser.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT){
        switch (eventType){
          case XmlPullParser.START_DOCUMENT:
            results = "Start Document\n";
            break;
          case XmlPullParser.START_TAG:
            results += parser.getName() + ": ";
            break;
          case XmlPullParser.END_TAG:
            results += " " + parser.getName();
            break;
          case XmlPullParser.TEXT:
            results += parser.getText();
            break;
        }
        eventType = parser.next();
      }
      results += "\nEnd Document";
    } catch (Exception e){
      Log.i("XMLParser Error: ", e.getMessage());
    } finally {
      if (isr != null) {
        isr.close();
      }
      if (fIn != null) {
        fIn.close();
      }
    }

    return results;
  }

  /**
   * Function Name:
   *   JSONParser
   *
   * Function Description:
   *   Parse every JSON file format with standard format
   *
   * Parameters:
   *   String fileName - filename of JSON file.
   *
   * Returned Value:
   *   If the function returned normally, the returned is String;
   *   otherwise, the returned value is empty String.
   *
   * Possible Error Code or Exception:
   *   File not found.
   */
  protected String JSONParser(String fileName) throws IOException {
    String result, results = "";
    JsonReader jReader = null;
    FileInputStream fIn = null;
    InputStreamReader isr = null;

    try {
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;

      result = textContent(fileName);

      fIn = new FileInputStream(files);
      isr = new InputStreamReader(fIn);
      jReader = new JsonReader(isr);
      jReader.setLenient(true);
      sb2 = new StringBuffer("");

      /**
       * Check file format, whether it is start JSON with Array or Object,
       * otherwise it is not JSON file.
       */
      if(result.charAt(0) == '['){
        JSONArray(jReader);
        results = sb2.toString();
      }else if(result.charAt(0) == '{'){
        JSONObject(jReader);
        results = sb2.toString();
      }else{
        results = "Invalid JSON file";
      }
    } catch (Exception e){
      Log.i("JSONParser Error: ", e.getMessage());
    } finally {
      if (isr != null) {
        isr.close();
      }
      if (fIn != null) {
        fIn.close();
      }
      if (jReader != null) {
        jReader.close();
      }
    }

    return results;
  }

  /**
   * Procedure Name:
   *   JSONArray
   *
   * Procedure Description:
   *   This procedure called when next JSON format are in array format.
   *   Check with "peek()" method to see what is the type for next value and then get the next value
   *   based on type from "peek()" method.
   *   Every value will be put into global private variable sb2 as StringBuffer Object.
   *
   * Parameters:
   *   JsonReader jReader - Next content from JSON (whether it is an Array or Object).
   *
   * Possible Error Code or Exception:
   *   None.
   */
  private void JSONArray(JsonReader jReader) throws IOException {
    JsonToken jToken;
    jReader.beginArray();
    while (jReader.hasNext()) {
      jToken = jReader.peek();
      if (jToken.toString().equalsIgnoreCase("name")) {
        sb2.append(jReader.nextName() + ": \n");
      } else if (jToken.toString().equalsIgnoreCase("boolean")) {
        sb2.append(jReader.nextBoolean() + "\n");
      } else if (jToken.toString().equalsIgnoreCase("number")) {
        sb2.append(jReader.nextDouble() + "\n");
      } else if (jToken.toString().equalsIgnoreCase("string")) {
        sb2.append(jReader.nextString() + "\n");
      } else if (jToken.toString().equalsIgnoreCase("null")) {
        jReader.nextNull();
        sb2.append("Null\n");
      } else if (jToken.toString().equalsIgnoreCase("begin_array")) {
        JSONArray(jReader);
      } else if (jToken.toString().equalsIgnoreCase("begin_object")) {
        JSONObject(jReader);
      } else {
        jReader.skipValue();
      }
    }
    jReader.endArray();
  }

  /**
   * Procedure Name:
   *   JSONObject
   *
   * Procedure Description:
   *   This procedure called when next JSON format are in object format.
   *   Check with "peek()" method to see what is the type for next value and then get the next value
   *   based on type from "peek()" method.
   *   Every value will be put into global private variable sb2 as StringBuffer Object.
   *
   * Parameters:
   *   JsonReader jReader - Next content from JSON (whether it is an Array or Object).
   *
   * Possible Error Code or Exception:
   *   None.
   */
  private void JSONObject(JsonReader jReader) throws IOException {
    JsonToken jToken;
    jReader.beginObject();
    while (jReader.hasNext()) {
      jToken = jReader.peek();
      if (jToken.toString().equalsIgnoreCase("name")) {
        sb2.append(jReader.nextName() + ": ");
      } else if (jToken.toString().equalsIgnoreCase("boolean")) {
        sb2.append(jReader.nextBoolean() + "\n");
      } else if (jToken.toString().equalsIgnoreCase("number")) {
        sb2.append(jReader.nextDouble() + "\n");
      } else if (jToken.toString().equalsIgnoreCase("string")) {
        sb2.append(jReader.nextString() + "\n");
      } else if (jToken.toString().equalsIgnoreCase("null")) {
        jReader.nextNull();
        sb2.append("Null\n");
      } else if (jToken.toString().equalsIgnoreCase("begin_array")) {
        JSONArray(jReader);
      } else if (jToken.toString().equalsIgnoreCase("begin_object")) {
        JSONObject(jReader);
      } else {
        jReader.skipValue();
      }
    }
    jReader.endObject();
  }

  protected HashMap<Integer, HashMap<String, Object>> JSONFacilities(String fileName) throws IOException {
    HashMap<Integer, HashMap<String, Object>> results = new HashMap<Integer, HashMap<String, Object>>();
    HashMap<String, Object> result = new HashMap<String, Object>();
    JsonReader jReader = null;
    JsonToken jToken;
    FileInputStream fIn = null;
    InputStreamReader isr = null;
    int ctr = 0;

    try {
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;

      fIn = new FileInputStream(files);
      isr = new InputStreamReader(fIn);
      jReader = new JsonReader(isr);
      jReader.setLenient(true);

      jReader.beginArray();
      while (jReader.hasNext()) {
        jToken = jReader.peek();
        if (jToken.toString().equalsIgnoreCase("begin_object")) {
          result = JSONDataObject(jReader);
        } else {
          jReader.skipValue();
        }
        results.put(ctr, result);
        ctr++;
      }
      jReader.endArray();

    } catch (Exception e){
      Log.i("JSONFacilities Error: ", e.getMessage());
    } finally {
      if (isr != null) {
        isr.close();
      }
      if (fIn != null) {
        fIn.close();
      }
      if (jReader != null) {
        jReader.close();
      }
    }

    return results;
  }

  private HashMap<String, Object> JSONDataObject(JsonReader jReader) throws IOException {
    String name = "";
    JsonToken jToken;
    HashMap<String, Object> result = new HashMap<String, Object>();

    jReader.beginObject();
    while (jReader.hasNext()) {
      jToken = jReader.peek();
      if (jToken.toString().equalsIgnoreCase("name")) {
        name = jReader.nextName();
        result.put(name, "");
      } else if (jToken.toString().equalsIgnoreCase("boolean")) {
        result.put(name, jReader.nextBoolean());
      } else if (jToken.toString().equalsIgnoreCase("number")) {
        result.put(name, jReader.nextDouble());
      } else if (jToken.toString().equalsIgnoreCase("string")) {
        result.put(name, jReader.nextString());
      } else if (jToken.toString().equalsIgnoreCase("null")) {
        result.put(name, "Null");
        jReader.nextNull();
      } else {
        jReader.skipValue();
      }
    }
    jReader.endObject();

    return result;
  }

  protected HashMap<Integer, HashMap<String, Object>> RDFFacilities(String fileName) throws IOException {
    HashMap<Integer, HashMap<String, Object>> results = new HashMap<Integer, HashMap<String, Object>>();
    FileInputStream fIn = null;
    InputStreamReader isr = null;

    try {
      XmlPullParser parser = Xml.newPullParser();
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;
      fIn = new FileInputStream(files);
      isr = new InputStreamReader(fIn);

      // auto-detect the encoding from the stream
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(isr);
      parser.nextTag();
      results = readRDF(parser);
    } catch (Exception e){
      Log.i("RDFFacilities Error: ", e.getMessage());
    } finally {
      if (isr != null) {
        isr.close();
      }
      if (fIn != null) {
        fIn.close();
      }
    }

    return results;
  }

  private HashMap<Integer, HashMap<String, Object>> readRDF(XmlPullParser parser) throws IOException {
    HashMap<Integer, HashMap<String, Object>> results = new HashMap<Integer, HashMap<String, Object>>();
    int ctr = 0;

    try {
      parser.require(XmlPullParser.START_TAG, ns, "rdf:RDF");

      while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
          continue;
        }
        String name = parser.getName();

        if (name.equals("rdf:Description")) {
          results.put(ctr, readDescription(parser));
          ctr++;
        } else {
          skipParser(parser);
        }
      }
    } catch (XmlPullParserException e) {
      Log.i("readRDF Error: ", e.getMessage());
    }
    return results;
  }

  private HashMap<String, Object> readDescription(XmlPullParser parser) throws IOException {
    HashMap<String, Object> result = new HashMap<String, Object>();

    try {
      parser.require(XmlPullParser.START_TAG, ns, "rdf:Description");
      while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
          continue;
        }
        String name = parser.getName();
        if (name.equals("rdf:name")) {
          result.put("Name", readContent(parser, name));
        } else if (name.equals("rdf:type")) {
          result.put("Type", readContent(parser, name));
        } else if (name.equals("rdf:category")) {
          result.put("Category", readContent(parser, name));
        } else if (name.equals("rdf:district")) {
          result.put("District", readContent(parser, name));
        } else if (name.equals("rdf:address")) {
          result.put("Address", readContent(parser, name));
        } else if (name.equals("rdf:telephone")) {
          result.put("Telephone", readContent(parser, name));
        } else if (name.equals("rdf:latitude")) {
          result.put("Latitude", readContent(parser, name));
        } else if (name.equals("rdf:longitude")) {
          result.put("Longitude", readContent(parser, name));
        } else if (name.equals("rdf:moreinfo")) {
          result.put("Moreinfo", readContent(parser, name));
        } else {
          skipParser(parser);
        }
      }
    } catch (XmlPullParserException e) {
      Log.i("readDescription Error: ", e.getMessage());
    }

    return result;
  }

  private String readContent(XmlPullParser parser, String tag) throws IOException {
    String content = "";
    try {
      parser.require(XmlPullParser.START_TAG, ns, tag);
      content = readText(parser);
      parser.require(XmlPullParser.END_TAG, ns, tag);
    } catch (XmlPullParserException e) {
      Log.i("readContent Error: ", e.getMessage());
    }
    return content;
  }

  private String readText(XmlPullParser parser) throws IOException {
    String result = "";

    try {
      if (parser.next() == XmlPullParser.TEXT) {
        result = parser.getText();
        parser.nextTag();
      }
    } catch (XmlPullParserException e) {
      Log.i("readText Error: ", e.getMessage());
    }

    return result;
  }

  private void skipParser(XmlPullParser parser) throws IOException {
    try {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        throw new IllegalStateException();
      }
      int depth = 1;
      while (depth != 0) {
        switch (parser.next()) {
          case XmlPullParser.END_TAG:
            depth--;
            break;
          case XmlPullParser.START_TAG:
            depth++;
            break;
        }
      }
    } catch (XmlPullParserException e) {
      Log.i("skipParser Error: ", e.getMessage());
    }
  }

  protected String textContent(String fileName) throws IOException {
    String results = "";
    FileInputStream fIn = null;
    InputStreamReader isr = null;

    try {
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;
      fIn = new FileInputStream(files);
      isr = new InputStreamReader(fIn);

      BufferedReader reader = new BufferedReader(isr);
      StringBuilder sb = new StringBuilder();
      String line = null;

      while((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }

      reader.close();

      results = sb.toString();
    } catch (Exception e){
      Log.i("textContent Error: ", e.getMessage());
    } finally {
      if (isr != null) {
        isr.close();
      }
      if (fIn != null) {
        fIn.close();
      }
    }

    return results;
  }

  protected String showData(String fileName) throws IOException {
    String results = "";
    String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

    if (ext.equalsIgnoreCase("xml") || ext.equalsIgnoreCase("rdf")) {
      results = XMLParser(fileName);
    } else if (ext.equalsIgnoreCase("json")) {
      results = JSONParser(fileName);
    } else {
      results = textContent(fileName);
    }

    return results;
  }

  protected void showMessage(Context context, String msg) {
    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(context, msg, duration);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }

  protected int resourceIcon(String category) {
    int result = R.drawable.icon;

    if (category.equals("Shelter(Indoor)")) {
      result = R.drawable.shelter_in;
    } else if (category.equals("Shelter(Outdoor)")) {
      result = R.drawable.shelter_out;
    } else if (category.equals("Medical")) {
      result = R.drawable.medical;
    } else if (category.equals("Rescue")) {
      result = R.drawable.rescue;
    } else if (category.equals("Livelihood")) {
      result = R.drawable.livelihood;
    } else if (category.equals("Communication")) {
      result = R.drawable.communication;
    } else if (category.equals("Volunteer association")) {
      result = R.drawable.volunteer_association;
    } else if (category.equals("Transportation")) {
      result = R.drawable.transportation;
    }

    return result;
  }
}
