package mobilemad.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.util.Xml;

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

  protected Bitmap imgViewer(String fileName) {
    Bitmap results = null;
    FileInputStream fIn = null;
    InputStreamReader isr = null;
    InputStream is = null;

    try {
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;
      /*fIn = getActivity().openFileInput(fileName);*/
      fIn = new FileInputStream(files);
      isr = new InputStreamReader(fIn);
      is = fIn;

      results = BitmapFactory.decodeStream(is);
    } catch (Exception e){
      Log.i("imgViewer Error: ", e.getMessage());
    } finally {
      try {
        if (is != null) {
          is.close();
        }
        if (isr != null) {
          isr.close();
        }
        if (fIn != null) {
          fIn.close();
        }
      } catch (IOException e) {}
    }

    return results;
  }

  protected String XMLParser(String fileName) {
    String results = "";
    FileInputStream fIn = null;
    InputStreamReader isr = null;

    try {
      XmlPullParser parser = Xml.newPullParser();
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;
      /*fIn = getActivity().openFileInput(fileName);*/
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
      try {
        if (isr != null) {
          isr.close();
        }
        if (fIn != null) {
          fIn.close();
        }
      } catch (IOException e) {}
    }

    return results;
  }

  protected String JSONParser(String fileName) {
    String result, results = "";
    JsonReader jReader = null;
    FileInputStream fIn = null;
    InputStreamReader isr = null;

    try {
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;

      result = textContent(fileName);

      /*fIn = getActivity().openFileInput(fileName);*/
      fIn = new FileInputStream(files);
      isr = new InputStreamReader(fIn);
      jReader = new JsonReader(isr);
      jReader.setLenient(true);
      sb2 = new StringBuffer("");

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
      try {
        if (isr != null) {
          isr.close();
        }
        if (fIn != null) {
          fIn.close();
        }
        if (jReader != null) {
          jReader.close();
        }
      } catch(Exception ex) {}
    }

    return results;
  }

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

  protected HashMap<Integer, HashMap<String, Object>> JSONFacilities(String fileName) {
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

      /*fIn = getActivity().openFileInput(fileName);*/
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
      try {
        if (isr != null) {
          isr.close();
        }
        if (fIn != null) {
          fIn.close();
        }
        if (jReader != null) {
          jReader.close();
        }
      } catch(Exception ex) {}
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

  protected HashMap<Integer, HashMap<String, Object>> RDFFacilities(String fileName) {
    HashMap<Integer, HashMap<String, Object>> results = new HashMap<Integer, HashMap<String, Object>>();
    FileInputStream fIn = null;
    InputStreamReader isr = null;
    String name = "";
    int ctr = 0;

    try {
      XmlPullParser parser = Xml.newPullParser();
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;
      /*fIn = getActivity().openFileInput(fileName);*/
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
      try {
        if (isr != null) {
          isr.close();
        }
        if (fIn != null) {
          fIn.close();
        }
      } catch (IOException e) {}
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
        } else if (name.equals("rdf:address")) {
          result.put("Address", readContent(parser, name));
        } else if (name.equals("rdf:district")) {
          result.put("District", readContent(parser, name));
        } else if (name.equals("rdf:telphone")) {
          result.put("Telphone", readContent(parser, name));
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

  protected String textContent(String fileName) {
    String results = "";
    BufferedReader reader = null;
    FileInputStream fIn = null;
    InputStreamReader isr = null;

    try {
      String path = Config.path + File.separator + "data";
      String files = path + File.separator + fileName;
      /*fIn = getActivity().openFileInput(fileName);*/
      fIn = new FileInputStream(files);
      isr = new InputStreamReader(fIn);

      reader = new BufferedReader(isr);
      StringBuilder sb = new StringBuilder();
      String line = null;

      while((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }

      results = sb.toString();
    } catch (Exception e){
      Log.i("textContent Error: ", e.getMessage());
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
        if (isr != null) {
          isr.close();
        }
        if (fIn != null) {
          fIn.close();
        }
      } catch(Exception ex) {}
    }

    return results;
  }

  protected String showData(String fileName) {
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

}
