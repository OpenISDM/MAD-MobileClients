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
 *   1. Open Files with FileInputStream.
 *   2. Remove Byte Order Mark (BOM) in file with UTF encoding.
 *   3. Show bitmap.
 *   4. Parse standard structure of XML and JSON.
 *   5. Parse MAD data structure in JSON and RDF/XML.
 *   6. Show raw text content.
 *   7. Show message in Toast.
 *   8. Get the resource icon for image.
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

public class DataViewer {

  private static final String ns = null;
  private StringBuffer sb2;

  /**
   * Function Name:
   * openFiles
   * <p/>
   * Function Description:
   * Open file in specific path.
   * <p/>
   * Parameters:
   * String fileName - name of file.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is FileInputStream;
   * otherwise, the returned value is null.
   * <p/>
   * Possible Error Code or Exception:
   * File not found.
   */
  private FileInputStream openFiles(String fileName) {
    FileInputStream result = null;

    try {
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;
      result = new FileInputStream(files);
    } catch (Exception e) {
      Log.i("openFiles Error: ", e.getMessage());
    }

    return result;
  }

  /**
   * Function Name:
   * removeByteOrderMark
   * <p/>
   * Function Description:
   * Remove Byte Order Mark (BOM) from file when they occur, it happens when the file have
   * encoding mark like UTF-8 and UTF-16.
   * BOM occur at the beginning of file with several bytes as mark. To identify it, it depends
   * on the encoding of the files like the following encoding:
   * 1. 0xEF & 0xBB & 0xBF -> UTF-8
   * 2. 0xFF & 0xFE -> UTF-16LE
   * 3. 0xFF & 0xFF -> UTF-16BE
   * <p/>
   * Parameters:
   * BufferedInputStream bufferedInputStream - BufferedInputStream object that get stream value
   * after file opened with FileInputStream.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is InputStreamReader;
   * otherwise, the returned value is null.
   * <p/>
   * Possible Error Code or Exception:
   * None.
   */
  public InputStreamReader removeByteOrderMark(BufferedInputStream bufferedInputStream)
    throws IOException {
    bufferedInputStream.mark(3);
    int byte1 = bufferedInputStream.read();
    int byte2 = bufferedInputStream.read();
    if (byte1 == 0xFF && byte2 == 0xFE) {
      return new InputStreamReader(bufferedInputStream, "UTF-16LE");
    } else if (byte1 == 0xFF && byte2 == 0xFF) {
      return new InputStreamReader(bufferedInputStream, "UTF-16BE");
    } else {
      int byte3 = bufferedInputStream.read();
      if (byte1 == 0xEF && byte2 == 0xBB && byte3 == 0xBF) {
        return new InputStreamReader(bufferedInputStream, "UTF-8");
      } else {
        bufferedInputStream.reset();
        return new InputStreamReader(bufferedInputStream);
      }
    }
  }

  /**
   * Function Name:
   * imgViewer
   * <p/>
   * Function Description:
   * Open image file in specific path and put into bitmap variable.
   * <p/>
   * Parameters:
   * String fileName - name of image file.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is Bitmap;
   * otherwise, the returned value is null.
   * <p/>
   * Possible Error Code or Exception:
   * File not found.
   */
  protected Bitmap imgViewer(String fileName) throws IOException {
    Bitmap results = null;
    InputStream inputStream = null;

    try {
      inputStream = openFiles(fileName);

      if (inputStream != null) {
        results = BitmapFactory.decodeStream(inputStream);
      }
    } catch (Exception e) {
      Log.i("imgViewer Error: ", e.getMessage());
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }

    return results;
  }

  /**
   * Function Name:
   * XMLParser
   * <p/>
   * Function Description:
   * Parse every XML file format with standard structure.
   * <p/>
   * Parameters:
   * String fileName - filename of XML file.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is String;
   * otherwise, the returned value is empty String.
   * <p/>
   * Possible Error Code or Exception:
   * File not found.
   */
  protected String XMLParser(String fileName) throws IOException {
    String results = "";
    FileInputStream fileInputStream = null;
    InputStreamReader inputStreamReader = null;

    try {
      XmlPullParser parser = Xml.newPullParser();

      fileInputStream = openFiles(fileName);

      if (fileInputStream != null) {
        inputStreamReader = new InputStreamReader(fileInputStream);

        parser.setInput(inputStreamReader);
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
          switch (eventType) {
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
      }
    } catch (Exception e) {
      Log.i("XMLParser Error: ", e.getMessage());
    } finally {
      if (inputStreamReader != null) {
        inputStreamReader.close();
      }
      if (fileInputStream != null) {
        fileInputStream.close();
      }
    }

    return results;
  }

  /**
   * Function Name:
   * JSONParser
   * <p/>
   * Function Description:
   * Parse every JSON file format with standard structure.
   * <p/>
   * Parameters:
   * String fileName - name of JSON file.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is String;
   * otherwise, the returned value is empty String.
   * <p/>
   * Possible Error Code or Exception:
   * File not found.
   */
  protected String JSONParser(String fileName) throws IOException {
    String result, results = "";
    JsonReader jReader = null;
    FileInputStream fileInputStream = null;
    InputStreamReader inputStreamReader = null;

    try {
      fileInputStream = openFiles(fileName);

      if (fileInputStream != null) {
        result = textContent(fileName);
        inputStreamReader = new InputStreamReader(fileInputStream);

        jReader = new JsonReader(inputStreamReader);
        jReader.setLenient(true);

        sb2 = new StringBuffer("");

        /**
         * Check file format, whether it is start JSON with Array or Object,
         * otherwise it is not JSON file.
         */
        if (result.charAt(0) == '[') {
          JSONArray(jReader);
          results = sb2.toString();
        } else if (result.charAt(0) == '{') {
          JSONObject(jReader);
          results = sb2.toString();
        } else {
          results = "Invalid JSON file";
        }
      }
    } catch (Exception e) {
      Log.i("JSONParser Error: ", e.getMessage());
    } finally {
      if (jReader != null) {
        jReader.close();
      }
      if (inputStreamReader != null) {
        inputStreamReader.close();
      }
      if (fileInputStream != null) {
        fileInputStream.close();
      }
    }

    return results;
  }

  /**
   * Procedure Name:
   * JSONArray
   * <p/>
   * Procedure Description:
   * This procedure called when next JSON format are in array structure.
   * Check with "peek()" method to see what is the type for next value and then get the next value
   * based on type from "peek()" method.
   * Every value will be put into global private variable sb2 as StringBuffer Object.
   * <p/>
   * Parameters:
   * JsonReader jReader - Next start content from JsonReader object as an Array.
   * <p/>
   * Possible Error Code or Exception:
   * None.
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
   * JSONObject
   * <p/>
   * Procedure Description:
   * This procedure called when next JSON format are in object structure.
   * Check with "peek()" method to see what is the type for next value and then get the next value
   * based on type from "peek()" method.
   * Every value will be put into global private variable sb2 as StringBuffer Object.
   * <p/>
   * Parameters:
   * JsonReader jReader - Next start content from JsonReader object as an Object.
   * <p/>
   * Possible Error Code or Exception:
   * None.
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

  /**
   * Function Name:
   * JSONFacilities
   * <p/>
   * Function Description:
   * Parse JSON Files with specific structure (see the data in DataFormat folder).
   * <p/>
   * Parameters:
   * String fileName - name of JSON file.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is
   * LinkedHashMap<Integer, LinkedHashMap<String, Object>>;
   * otherwise, the returned value is null.
   * <p/>
   * Possible Error Code or Exception:
   * File not found.
   * Different structure.
   */
  protected LinkedHashMap<Integer, LinkedHashMap<String, Object>> JSONFacilities(String fileName)
    throws IOException {
    LinkedHashMap<Integer, LinkedHashMap<String, Object>> results =
      new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
    LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
    JsonReader jReader = null;
    JsonToken jToken;
    FileInputStream fileInputStream = null;
    BufferedInputStream bufferedInputStream = null;
    InputStreamReader inputStreamReader = null;
    int ctr = 0;

    try {
      fileInputStream = openFiles(fileName);

      if (fileInputStream != null) {
        bufferedInputStream = new BufferedInputStream(fileInputStream);
        inputStreamReader = removeByteOrderMark(bufferedInputStream);

        jReader = new JsonReader(inputStreamReader);
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
      }
    } catch (Exception e) {
      Log.i("JSONFacilities Error: ", e.getMessage());
    } finally {
      if (jReader != null) {
        jReader.close();
      }
      if (inputStreamReader != null) {
        inputStreamReader.close();
      }
      if (bufferedInputStream != null) {
        bufferedInputStream.close();
      }
      if (fileInputStream != null) {
        fileInputStream.close();
      }
    }

    return results;
  }

  /**
   * Function Name:
   * JSONDataObject
   * <p/>
   * Function Description:
   * This function called by JSONFacilities as the data structure begin with JSON object.
   * Check with "peek()" method to see what is the type for next value and then get the next value
   * based on type from "peek()" method.
   * Every value will be put into variable result with type LinkedHashMap<String, Object>.
   * <p/>
   * Parameters:
   * JsonReader jReader - Next start content from JsonReader object as an Object.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is LinkedHashMap<String, Object>;
   * otherwise, the returned value is null.
   * <p/>
   * Possible Error Code or Exception:
   * None.
   */
  private LinkedHashMap<String, Object> JSONDataObject(JsonReader jReader) throws IOException {
    String name = "";
    JsonToken jToken;
    LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();

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

  /**
   * Function Name:
   * RDFFacilities
   * <p/>
   * Function Description:
   * Parse RDF/XML Files with specific structure (see the data in DataFormat folder).
   * <p/>
   * Parameters:
   * String fileName - name of RDF/XML file.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is
   * LinkedHashMap<Integer, LinkedHashMap<String, Object>>;
   * otherwise, the returned value is null.
   * <p/>
   * Possible Error Code or Exception:
   * File not found.
   * Different structure.
   */
  protected LinkedHashMap<Integer, LinkedHashMap<String, Object>> RDFFacilities(String fileName)
    throws IOException {
    LinkedHashMap<Integer, LinkedHashMap<String, Object>> results =
      new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
    FileInputStream fileInputStream = null;
    BufferedInputStream bufferedInputStream = null;
    InputStreamReader inputStreamReader = null;

    try {
      fileInputStream = openFiles(fileName);

      if (fileInputStream != null) {
        bufferedInputStream = new BufferedInputStream(fileInputStream);
        inputStreamReader = removeByteOrderMark(bufferedInputStream);

        XmlPullParser parser = Xml.newPullParser();

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStreamReader);
        parser.nextTag();
        results = readRDF(parser);
      }
    } catch (Exception e) {
      Log.i("RDFFacilities Error: ", e.getMessage());
    } finally {
      if (inputStreamReader != null) {
        inputStreamReader.close();
      }
      if (bufferedInputStream != null) {
        bufferedInputStream.close();
      }
      if (fileInputStream != null) {
        fileInputStream.close();
      }
    }

    return results;
  }

  /**
   * Function Name:
   * readRDF
   * <p/>
   * Function Description:
   * This function called by RDFFacilities as the data structure begin with "rdf:RDF" as
   * the beginning of tag.
   * Check each Tag with specific name tag to continue the process, otherwise skip the tag.
   * <p/>
   * Parameters:
   * XmlPullParser parser - next start content from XmlPullParser object after the
   * beginning of tag.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is
   * LinkedHashMap<Integer, LinkedHashMap<String, Object>>;
   * otherwise, the returned value is null.
   * <p/>
   * Possible Error Code or Exception:
   * start tag not found.
   */
  private LinkedHashMap<Integer, LinkedHashMap<String, Object>> readRDF(XmlPullParser parser)
    throws IOException {
    LinkedHashMap<Integer, LinkedHashMap<String, Object>> result =
      new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
    int ctr = 0;

    try {
      parser.require(XmlPullParser.START_TAG, ns, "rdf:RDF");

      while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
          continue;
        }
        String name = parser.getName();

        if (name.equals("rdf:Description")) {
          result.put(ctr, readDescription(parser));
          ctr++;
        } else {
          skipParser(parser);
        }
      }
    } catch (XmlPullParserException e) {
      Log.i("readRDF Error: ", e.getMessage());
    }
    return result;
  }

  /**
   * Function Name:
   * readDescription
   * <p/>
   * Function Description:
   * This function called by readRDF as the data structure begin with "rdf:Description" as
   * the beginning of tag.
   * Check each Tag with specific name tag to get the value, otherwise skip the tag.
   * Every value will be put into variable result with type LinkedHashMap<String, Object>.
   * <p/>
   * Parameters:
   * XmlPullParser parser - next start content from XmlPullParser object after the
   * beginning of tag.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is LinkedHashMap<String, Object>;
   * otherwise, the returned value is null.
   * <p/>
   * Possible Error Code or Exception:
   * Start tag not found.
   */
  private LinkedHashMap<String, Object> readDescription(XmlPullParser parser) throws IOException {
    LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();

    try {
      parser.require(XmlPullParser.START_TAG, ns, "rdf:Description");
      while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
          continue;
        }
        String name = parser.getName();
        if (name.equals("ns1:hasName")) {
          result.put("Name", readContent(parser, name));
        } else if (name.equals("ns1:hasType")) {
          result.put("Type", readContent(parser, name));
        } else if (name.equals("ns1:hasCategory")) {
          result.put("Category", readContent(parser, name));
        } else if (name.equals("ns1:hasDistrict")) {
          result.put("District", readContent(parser, name));
        } else if (name.equals("ns1:hasAddress")) {
          result.put("Address", readContent(parser, name));
        } else if (name.equals("ns1:hasTelephone")) {
          result.put("Telephone", readContent(parser, name));
        } else if (name.equals("ns1:latitude")) {
          result.put("Latitude", readContent(parser, name));
        } else if (name.equals("ns1:longitude")) {
          result.put("Longitude", readContent(parser, name));
        } else if (name.equals("ns1:moreInfo")) {
          result.put("MoreInfo", readContent(parser, name));
        } else {
          skipParser(parser);
        }
      }
    } catch (XmlPullParserException e) {
      Log.i("readDescription Error: ", e.getMessage());
    }

    return result;
  }

  /**
   * Function Name:
   * readContent
   * <p/>
   * Function Description:
   * This function called by readDescription to get the tag value with the specific tag.
   * The value will be put into variable content with type String.
   * <p/>
   * Parameters:
   * XmlPullParser parser - next start content from XmlPullParser object after the
   * beginning of tag.
   * String tag - name of tag that needed to get the value of specific tag.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is String;
   * otherwise, the returned value is empty String.
   * <p/>
   * Possible Error Code or Exception:
   * tag not found.
   */
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

  /**
   * Function Name:
   * readText
   * <p/>
   * Function Description:
   * This function called by readRDF to get the tag value with the specific tag.
   * The value will be put into variable result with type String.
   * <p/>
   * Parameters:
   * XmlPullParser parser - value of tag from XmlPullParser object with specific tag.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is String;
   * otherwise, the returned value is empty String.
   * <p/>
   * Possible Error Code or Exception:
   * tag not found.
   */
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

  /**
   * Procedure Name:
   * skipParser
   * <p/>
   * Procedure Description:
   * This procedure called by readContent and readDescription to skip the tag that not needed.
   * Skip tag will check from the start tag until end tag. So, every tag inside this current tag
   * will skipped.
   * <p/>
   * Parameters:
   * XmlPullParser parser - next start content from XmlPullParser object after the
   * beginning of tag
   * <p/>
   * Possible Error Code or Exception:
   * tag is not start tag.
   */
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

  /**
   * Function Name:
   * textContent
   * <p/>
   * Function Description:
   * Parse every file format as text file.
   * <p/>
   * Parameters:
   * String fileName - name of text file.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is String;
   * otherwise, the returned value is empty String.
   * <p/>
   * Possible Error Code or Exception:
   * File not found.
   */
  protected String textContent(String fileName) throws IOException {
    String results = "";
    FileInputStream fileInputStream = null;
    InputStreamReader inputStreamReader = null;

    try {
      fileInputStream = openFiles(fileName);

      if (fileInputStream != null) {
        inputStreamReader = new InputStreamReader(fileInputStream);

        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
          sb.append(line + "\n");
        }

        reader.close();

        results = sb.toString();
      }
    } catch (Exception e) {
      Log.i("textContent Error: ", e.getMessage());
    } finally {
      if (inputStreamReader != null) {
        inputStreamReader.close();
      }
      if (fileInputStream != null) {
        fileInputStream.close();
      }
    }

    return results;
  }

  /**
   * Function Name:
   * showData
   * <p/>
   * Function Description:
   * Check extension of files and call specific procedure or function to parse the file content
   * as the standard structure.
   * <p/>
   * Parameters:
   * String fileName - name of file.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is String;
   * otherwise, the returned value is empty String.
   * <p/>
   * Possible Error Code or Exception:
   * none.
   */
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

  /**
   * Procedure Name:
   * showMessage
   * <p/>
   * Procedure Description:
   * Show message using Android package (Toast), can be called by another class that need to
   * show short message as notification to user.
   * <p/>
   * Parameters:
   * Context context - it will use Android code to get activity from the one who call this
   * feature [getApplicationContext()].
   * String msg - String message that need to be shown to the user..
   * <p/>
   * Possible Error Code or Exception:
   * none.
   */
  protected void showMessage(Context context, String msg) {
    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(context, msg, duration);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }

  /**
   * Function Name:
   * resourceIcon
   * <p/>
   * Function Description:
   * Check category name to get the specific icon with specific name.
   * <p/>
   * Parameters:
   * String category - category name to define the icon of data.
   * <p/>
   * Returned Value:
   * If the function returned normally, the returned is drawable as int;
   * otherwise, the returned value is default drawable as int.
   * <p/>
   * Possible Error Code or Exception:
   * none.
   */
  protected int resourceIcon(String category) {
    int result = R.drawable.icon;

    if (category.equals(Config.SHELTER_INDOOR)) {
      result = R.drawable.shelter_in;
    } else if (category.equals(Config.SHELTER_OUTDOOR)) {
      result = R.drawable.shelter_out;
    } else if (category.equals(Config.MEDICAL)) {
      result = R.drawable.medical;
    } else if (category.equals(Config.RESCUE)) {
      result = R.drawable.rescue;
    } else if (category.equals(Config.LIVELIHOOD)) {
      result = R.drawable.livelihood;
    } else if (category.equals(Config.COMMUNICATION)) {
      result = R.drawable.communication;
    } else if (category.equals(Config.VOLUNTEER_ASSOCIATION)) {
      result = R.drawable.volunteer_association;
    } else if (category.equals(Config.TRANSPORTATION)) {
      result = R.drawable.transportation;
    }

    return result;
  }
}
