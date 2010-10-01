/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package razie.draw;

import java.io.IOException;
import java.io.OutputStream;


import com.razie.pub.comms.*;
/**
 * a drawing stream that is plain text, but you can specify the mime type sent to the client
 * 
 * @author razvanc99
 * @version $Id$
 * 
 */
public class MimeDrawStream extends razie.draw.DrawStream.DrawStreamWrapper {

    public MimeDrawStream(OutputStream stream, String mime) throws IOException {
        super (new SimpleDrawStream(Technology.TEXT, stream));
        ((SimpleDrawStream)proxied).writeBytes(HttpHelper.httpHeader(HttpHelper.OK, mime).getBytes());
    }

    /** switch technology of an underlying stream - NOTE I'm assuming you know what you're doing...do you???
     * 
     * @param stream the underlying stream to switch technology
     * @param mime the new mime type
     * @throws IOException
     */
    public MimeDrawStream(DrawStream stream, String mime, Technology t) throws IOException {
       // get the underlying proxied stream and change its technology
       super (stream instanceof DrawStreamWrapper ? ((DrawStreamWrapper)stream).proxied : stream);
       proxied.switchTechnology(t);
       ((SimpleDrawStream)proxied).writeBytes(HttpHelper.httpHeader(HttpHelper.OK, mime).getBytes());
   }

    @Override
    public void close() {
    }
}
