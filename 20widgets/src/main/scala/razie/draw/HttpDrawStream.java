/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package razie.draw;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import razie.comms.CommsEndPoint;

import com.razie.pub.base.data.HtmlRenderUtils;
import com.razie.pub.comms.HttpHelper;

/**
 * a drawing stream to an http client (plain old web). Will render objects in the html and wrap in
 * http header/footer
 * 
 * @author razvanc99
 * @version $Id$
 */
public class HttpDrawStream extends SimpleDrawStream {
    private boolean            wroteHeader    = false;
    private List<String>       metas          = null;
    private List<String>       httptags          = null;
    protected int countBytes = 0;
    public static final String MIME_TEXT_HTML = "text/html";
    private boolean shouldClose=true;

    public HttpDrawStream(CommsEndPoint ep, OutputStream os) throws IOException {
        super(Technology.HTML, os);
        this.setEndPoint(ep);
    }

    public HttpDrawStream(CommsEndPoint ep, OutputStream os, boolean shouldClose) throws IOException {
      this (ep, os);
      this.shouldClose=shouldClose;
    }
    
    public HttpDrawStream(OutputStream socket) throws IOException {
        super(Technology.HTML, socket);
        writeBytes(HttpHelper.httpHeader(HttpHelper.OK).getBytes());
        writeBytes(HtmlRenderUtils.htmlHeader().getBytes());
    }

    /** add a completed object to the stream */
    @Override
    public void write(Object d) {
//        header();
        super.write(d);
    }

    /** add an object to the stream */
    @Override
    public void open(Object d) {
//        header();
        super.open(d);
    }

    private void header() {
        // this is to allow clients to switch tech to json before the first write
        if (!wroteHeader) {
            wroteHeader = true;
            if (this.technology.equals(Technology.JSON)) {
                writeBytes(HttpHelper.httpHeader(HttpHelper.OK,
                        "application/json").getBytes());
            } else if (this.technology.equals(Technology.HTML)) {
                writeBytes((HttpHelper.httpHeader(HttpHelper.OK)).getBytes());

                String temp = "";
                
                if (this.metas == null)
                    temp = (HtmlRenderUtils.htmlHeader());
                else
                    temp = (HtmlRenderUtils.htmlHeader(
                            this.metas.toArray(new String[0])));

                if (this.httptags != null)
                  for (String tag : this.httptags)
                    temp = temp.replaceFirst("<html>", "<html>\n"+tag); // DAMN, I'm lazy!!!

                writeBytes(temp.getBytes());
            }
        }
    }

    @Override
    public void close() {
//        header();
        // TODO not correct, since BG threads may still produce stuff...
        // ((SimpleDrawStream) proxied).writeBytes("<p> END OF STREAM </p>".getBytes());
        if (this.technology.equals(Technology.HTML)) {
            writeBytes(HtmlRenderUtils.htmlFooter().getBytes());
        }

        if (shouldClose) super.close();
//        else proxied.flush();
    }

    @Override
    protected void writeBytes(byte[] b) {
       header();
       super.writeBytes(b);
   }

    /** add a meta attribute - should be done before streaming starts 
     * 
     * for instance: addMeta("<meta http-equiv=\"refresh\" content=\"10\">") */
    public void addMeta(String string) {
       if (this.wroteHeader)
          throw new IllegalStateException ("Can't add metas after I wrote the header to the socket...review code!");
       
        if (this.metas == null)
            this.metas = new ArrayList<String>();
        this.metas.add(string);
    }
    
    /** add an http tag - should be done before streaming starts */
    public void addHttpTag(String string) {
       if (this.wroteHeader)
          throw new IllegalStateException ("Can't add metas after I wrote the header to the socket...review code!");
       
        if (this.httptags == null)
            this.httptags = new ArrayList<String>();
        this.httptags.add(string);
    }
}
