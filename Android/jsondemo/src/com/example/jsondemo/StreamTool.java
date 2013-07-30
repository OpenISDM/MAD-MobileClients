package com.example.jsondemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamTool {
	public static byte[] readInputStream(InputStream is) throws IOException {  
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024]; // 用数据装  
        int len = -1;  
        while ((len = is.read(buffer)) != -1) {  
            outstream.write(buffer, 0, len);  
        }  
        outstream.close();  
        // 关闭流一定要记得。  
        return outstream.toByteArray();  
    } 

}
