/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.comms;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;

import com.razie.pub.base.data.HtmlRenderUtils;

/** basic http utilities
 * 
 * @author razvanc
 */
public class HttpHelper {
    public static final String OK       = "200 OK";
    public static final String EXC      = "500 Exception";
    public static final String NOTFOUND = "404 Not Found";

    /** wrap reply text into http
     * 
     * @param code the code, i.e. constant OK above
     * @param s the string reply
     * @param len the length of the reply or 0 
     * @return
     */
    public static String httpWrap(String code, String s, long len) {
        String temp = (s == null ? "" : s);
        temp = HtmlRenderUtils.htmlWrap(temp);
        @SuppressWarnings("unused")
        long finallen = len == 0 ? temp.getBytes().length : len;
        String r = httpHeader(code) + temp;
        return r;
    }

    /** wrap reply text into http
     * 
     * @param code
     * @return
     */
    public static String httpHeader(String code){
        String ctype = "text/html";
        String r = "HTTP/1.1 " + code + "\r\nContent-Type: " + ctype + "\r\n\r\n";
        return r;
    }

    public static String httpHeader(String code, String contentType, String...tags) {
        String r = "HTTP/1.1 " + code + "\r\nContent-Type: " + contentType;
        for (String s : tags)
        r += "\r\n" + s;
        return r + "\r\n\r\n";
    }

    public static String httpWrapPic(String fname, long len) {
        String type = "image/gif";
        String ext = fname.toUpperCase();
        if (ext.endsWith(".GIF")) {
            type = "image/gif";
        } else if (ext.endsWith(".ICO")) {
            type = "image/x-icon";
        } else if (ext.endsWith(".JPG")) {
            type = "image/jpeg";
        }
        return httpWrapMimeType (type, len, "Expires: Thu, 15 Apr 2010 20:00:00 GMT");
    }

    public static String httpWrapOtherFile(String fname, long len) {
        String type = "text/html";
        String ext = fname.toUpperCase();
        if (ext.endsWith(".JS")) {
            type = "text/javascript";
        } else if (ext.endsWith(".XML")) {
            type = "application/xml";
        } else if (ext.endsWith(".CSS")) {
            type = "text/css";
        } else if (ext.endsWith(".WMV")) {
            type = "video/x-ms-wmv";
        } else {
        }
        return httpWrapMimeType (type, len);
    }

    public static String httpWrapMimeType(String type, long len, String...fields) {
//        return "HTTP/1.1 200 OK\r\nContent-Type: " + type + "\r\n\r\n";
        return httpHeader(OK, type, fields);
    }

    public static boolean isOtherFile(String fname) {
        String ext = fname.toUpperCase();
        return ext.endsWith(".JS") || ext.endsWith(".JS") || ext.endsWith(".XML") || ext.endsWith(".CSS")|| ext.endsWith(".WMV");
    }

    public static boolean isImage(String fname) {
        String ext = fname.toUpperCase();
        return ext.endsWith(".GIF") || ext.endsWith(".JPG")
                || ext.endsWith(".PNG") || ext.endsWith(".ICO");
    }

    /**
     * send a GET request
     * 
     * @param hostname - remote server to write to
     * @param port - remote port to write to
     * @param cmd the POST line i.e. "POST /url HTTP/1.1"
     * @param httpArgs http args
     * @param content the content posted over
     * @throws IOException
     * 
     * TODO 2 return UrlConnection or something like that...with error codes and stuff
     */
    public static Socket sendGET(Socket remote, String cmd, AttrAccess httpArgs) throws IOException {
        PrintStream out = new PrintStream(remote.getOutputStream());
        // this is how an http request is sent via the socket
        out.println(cmd);
        
        if (httpArgs == null)
            httpArgs = new AttrAccessImpl();
//        httpArgs.set("Content-Length", content.length()); 
        
        for (String n : httpArgs.getPopulatedAttr())
            out.println(n + ": " + httpArgs.a(n));
        
        out.println("");
//        out.print(content);
        
        return remote;
    }
    
    /**
     * send a POST request
     * 
     * @param hostname - remote server to write to
     * @param port - remote port to write to
     * @param cmd the POST line i.e. "POST /url HTTP/1.1"
     * @param httpArgs http args
     * @param content the content posted over
     * @throws IOException
     * 
     * TODO 2 return UrlConnection or something like that...with error codes and stuff
     */
    public static Socket sendPOST(Socket remote, String cmd, AttrAccess httpArgs, String content) throws IOException {
        PrintStream out = new PrintStream(remote.getOutputStream());
        // this is how an http request is sent via the socket
        out.println(cmd);
        
        if (httpArgs == null)
            httpArgs = new AttrAccessImpl();
        httpArgs.set("Content-Length", content.length()); 
        
        for (String n : httpArgs.getPopulatedAttr())
            out.println(n + ": " + httpArgs.a(n));
        
        out.println("");
        out.print(content);
        
        return remote;
    }
    
    /**
     * send a POST request
     * 
     * @param hostname - remote server to write to
     * @param port - remote port to write to
     * @param cmd the POST line i.e. "POST /url HTTP/1.1"
     * @param httpArgs http args
     * @param content the content posted over
     * @throws IOException
     * 
     * TODO 2 return UrlConnection or something like that...with error codes and stuff
     */
    public static Socket sendPOST(String hostname, Integer port, String cmd, AttrAccess httpArgs, String content) throws IOException {
        Socket remote = new Socket(hostname, port);
        return sendPOST (remote, cmd, httpArgs, content) ;
    }

    /**
     * send a POST request with binary content
     * 
     * @param hostname - remote server to write to
     * @param port - remote port to write to
     * @param cmd the POST line i.e. "POST /url HTTP/1.1"
     * @param httpArgs http args
     * @param content the content posted over
     * @throws IOException
     * 
     * TODO 2 return UrlConnection or something like that...
     */
    public static Socket sendBinaryPOST(String hostname, Integer port, String cmd, AttrAccess httpArgs, Object content) throws IOException {
        Socket remote = new Socket(hostname, port);
        PrintStream out = new PrintStream(remote.getOutputStream());
        // this is how an http request is sent via the socket
        out.println(cmd);
        
        if (httpArgs == null)
            httpArgs = new AttrAccessImpl();
        httpArgs.set("Content-Type", "application/octet-stream");
//TODO 2-1       httpArgs.set("Content-Length", content.length());
        
        for (String n : httpArgs.getPopulatedAttr())
            out.println(n + ": " + httpArgs.a(n));
        
        out.println("");
        
        ObjectOutputStream oos = new ObjectOutputStream(remote.getOutputStream());
        oos.writeObject(content);
        oos.flush();
 
        return remote;
    }
}