/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * A cached map that uses SoftReferences to store the values.
 * @author Alex Athanasopoulos
 *
 * @param <K>
 * @param <V>
 */
public class SoftCache<K,V> implements Cache<K, V> {
  private CacheLoader<K,V> loader;
  private Map<K,CacheReference<K,V>> map = new HashMap<K,CacheReference<K,V>>();
  private ReferenceQueue<V>  queue;
  
  static class CacheReference<K,V> extends SoftReference<V> {
    K key;
    public CacheReference(K key, V value, ReferenceQueue<? super V> q) {
      super(value, q);
      this.key = key;
    }    
  }
  
  public SoftCache(CacheLoader<K, V> loader) {
    super();
    this.loader = loader;
    queue = new ReferenceQueue<V>();
  }

  synchronized private void cleanup() {
    for(;;) {
      CacheReference<K,V> ref = (CacheReference<K,V>) queue.poll();
      if ( ref == null) {
        break;
      }
      map.remove(ref.key);
    }
  }
  synchronized public V get(K key) throws LoadException {
    SoftReference<V> ref = map.get(key);
    if ( ref != null ) {
      V value = ref.get();
      if ( value != null ) {
        return value;        
      }
    }
    cleanup();
    V v;
    try {
      v = loader.load(key);
    } catch (Exception e) {
      throw new LoadException( e );
    }
    map.put(key,  new CacheReference<K,V>(key, v,queue));
    return v;
  }

}
