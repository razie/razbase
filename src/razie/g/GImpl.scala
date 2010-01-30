package razie.g

import razie.{G}

object GUid {
   /** to allocate next UID...this should be done better */
   private var seqNum : Int = 1;

   /**
    * just a simple UID implementation, to fake IDs for objects that don't have them.
    */
   def apply () =  "Uid-" + {seqNum+=1; seqNum} + "-" + String.valueOf(System.currentTimeMillis());
}

/** very simple location indication: it's either an url or a directory...or both */
class GLoc (val url:String, val dir:String) {}

/**
 * Each entity/asset has a unique key, which identifies the asset's type, id and location. Borrowed
 * from OSS/J's ManagedEntityKey (type, key, location), this is lighter and designed to pass through
 * URLs and be easily managed as a string form.
 * 
 * <p>
 * asset-URI has this format: <code>"razie.uri:entityType:entityKey@location"</code>
 * <p>
 * asset-context URI has this format: <code>"razie.puri:entityType:entityKey@location&context"</code>
 *
 * When the asset support framework is used, this key can have this other form:
 * <p>
 * REST asset-URI has this format: <code>"http://SERVER:PORT/asset/entityType/entityKey"</code>
 * <p>
 * REST asset-URI has this format: <code>"http://SERVER:PORT/asset/KEY-ENCODED"</code>
 * <p>
 * OR: REST asset-URI has this format: <code>"http://SERVER:PORT/asset/entityType/entityKey?context"</code>
 * 
 * Actions can be invoked like so
 * <p>
 * REST action asset-URI has this format: <code>"http://SERVER:PORT/asset/entityType/entityKey/action?args"</code>
 * <p>
 * REST asset-URI has this format: <code>"http://SERVER:PORT/asset/KEY-ENCODED/action&args"</code>
 * <p>
 * OR: REST asset-URI has this format: <code>"http://SERVER:PORT/asset/entityType/entityKey?context"</code>
 * 
 * <ul>
 * <li>type is the type of entity, should be unique among all other types. HINT: do not keep
 * defining "Movie" etc - always assume someone else did...use "AlBundy.Movie" for instance :D
 * <li>key is the unique key of the given entity, unique in this location and for this type. The key
 * could be anything that doesn't have an '@' un-escaped. it could contain ':' itself like an
 * XCAP/XPATH etc, which is rather cool...?
 * <li>location identifies the location of the entity: either URL or folder or a combination. 
 * A folder implies it's on the localhost. An URL implies it's in that specific server.
 * </ul>
 * 
 * <p>
 * Keys must have at least type. By convention, if the key is missing, the key refers to all
 * entities of the given type.
 * 
 * @author razvanc99
 */
class GRef (val meta:String, val id:String, val loc:GLoc) {
   
   def this (m:String, k:String) = this (m, k, G.LOCAL)
   def this (m:String) = this (m, GUid(), G.LOCAL)

    override def equals(o:Any):Boolean = o match {
       case r : GRef =>  meta.equals(r.meta) && id.equals(r.id)
       case _ => false
    }

    override def hashCode() : Int = meta.hashCode() + (if(id != null ) id.hashCode() else 0)

    /** short descriptive string */
    override def toString = 
       G.PREFIX+":" + meta + ":" + (if(id == null ) "" else java.net.URLEncoder.encode(id, "UTF-8")) + (if (loc == null || G.LOCAL.equals(loc)) "" else ("@" + loc.toString()))

    /**
     * Use this method to get a string that is safe to use in a URL. Note that whenever the string
     * is encoded when you want to use it it must be decoded with fromUrlEncodedString(String).
     */
    def toUrlEncodedString : String = 
            java.net.URLEncoder.encode(toString, "UTF-8")
}

trait GReferenceable {
   def key : GRef
}

trait GResolver [T] {
   def resolve (key : GRef) : T
}

trait GAssocResolver [From, To] {
   def resolve (from:From, as:String) : To
}
