/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.lightsoa;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mark the SoaMethod that it can stream. These methods will always be void - they will stream any
 * responses directly into the object stream.
 * 
 * This is extremely useful when you deal with large volume of items (videos) and you don't want to
 * have the user wait until a search is over...it of course depends on how smart the client is, but
 * this allows you to be smart...
 * 
 * <pre>
 *        class ClassA {
 *            ...
 *            &#064;SoaMethod (descr=&quot;the name of the component&quot;, args = {&quot;name&quot;})
 *            public SoaResponse setName(String name) {
 *            }
 *            ...
 *            &#064;SoaMethod (descr=&quot;the name of the component&quot;, args = {&quot;name&quot;})
 *            &#064;SoaStreamable
 *            public void setName(DrawStream out, String name) {
 *            }
 *            ...
 *        }
 * </pre>
 * 
 * A note on the stream's technoogy and mime=type. These are normally set as requested by the client
 * (through various means, such as a protocol prefix, a format parameter etc). IF you overwrite the
 * streamMimeType, the stream will have no technology and use your mimetype, regardless of what the
 * client asked for. Basically, your method can only return your mime type and that's that.
 * 
 * DO NOT abuse overwriting the stream's mime type. You're supposed to only have a handful of
 * protocols and formats and support them all, with the help of the Drawable3 framework. This should
 * only be used in extreme cases where the data you pass back is in a format not supported by the
 * drawable framework (i.e. XML is currently not supported).
 * 
 * NOTE also that known mime types are converted to the appropriate technology streams. text/html to HTML
 * and application/json to JSON.
 * 
 * @author razvanc99
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
@Inherited
public @interface SoaStreamable {
    /**
     * you can define the mime type for the stream. If you leave the default, it will be set
     * according to other criteria (protocol id etc) and will generally be text/html
     * 
     * NOTE that when you set the mime type, the stream will use TEXT technology, i.e. "toString"
     * and will not do any special formatting anymore, since you seem to know what you're doing :)
     */
    String mime() default "";
}
