/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package com.razie.pubstage.comms;

import java.net.URL;


/**
 * will filter out html header/footers, if present, and get you the contens only.
 * 
 * @author razvanc99
 */
public class HtmlContents extends StrCommStream {

    /** creates a comm channel with the remote URL */
    public HtmlContents(URL url, IStrFilter... f) {
        super(url, f);
    }

    /** creates a comm channel with the remote URL */
    public HtmlContents(String url, IStrFilter... f) {
        super(url, f);
    }

    /** strip header/footer */
    @Override
    protected String readStreamImpl() {
        String otherList = super.readStreamImpl();
        otherList = justBody(otherList);
        return otherList;
    }

    /** strip header/footer */
    @Override
    protected String readStreamLineImpl() {
        String otherList = super.readStreamLineImpl();
        otherList = justBody(otherList);
        return otherList;
    }

    /**
     * try to get just the body of an html document - useful for simple results wrapped in html,
     * like default lightsoa responses
     * 
     * @param document the html document
     * @return cut out all known headers and tags, try to get just the body contents
     */
    public static String justBody(String document) {
       // TODO 3-2 optimize this...one single sed cmd
        String s = document.replaceAll("<html>", "").replaceAll("</html>", "").replaceAll("<body[^>]*>", "")
                .replaceAll("</body>", "");
        s = s.replaceFirst("<head>.*</head>", "");
        return s;
    }
}
