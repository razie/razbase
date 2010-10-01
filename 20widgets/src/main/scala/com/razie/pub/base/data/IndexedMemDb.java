/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.base.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * simple in-memory database: each K has a list of V
 * 
 * @author razvanc99
 * 
 * @param <K>
 * @param <V>
 */
public class IndexedMemDb<K1, K2, V> {
    Map<K1, Map<K2, V>> db = new HashMap<K1, Map<K2, V>>();

    public void put(K1 k1, K2 k2, V v) {
        Map<K2, V> l = db.get(k1);
        if (l == null) {
            l = new HashMap<K2, V>();
            db.put(k1, l);
        }
        l.put(k2, v);
    }

    /** @return the list for K or an empty list - never null */
    public Map<K2, V> get(K1 k1) {
        Map<K2, V> l = db.get(k1);
        if (l == null) {
            l = Collections.EMPTY_MAP;
        }
        return l;
    }
    
    /** @return the list for K or an empty list - never null */
    public V get(K1 k1, K2 k2) {
        Map<K2, V> l = db.get(k1);
        if (l == null) {
            return null;
        }
        return l.get(k2);
    }
}
