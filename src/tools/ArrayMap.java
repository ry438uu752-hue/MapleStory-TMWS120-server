/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ArrayMap<K, V>
extends AbstractMap<K, V>
implements Serializable {
    public static final long serialVersionUID = 9179541993413738569L;
    private transient Set<? extends Map.Entry<K, V>> entries = null;
    private ArrayList<Entry<K, V>> list;

    public ArrayMap() {
        this.list = new ArrayList();
    }

    public ArrayMap(Map<K, V> map) {
        this.list = new ArrayList();
        this.putAll(map);
    }

    public ArrayMap(int initialCapacity) {
        this.list = new ArrayList(initialCapacity);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entries == null) {
            this.entries = new AbstractSet<Entry<K, V>>(){

                @Override
                public void clear() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return ArrayMap.this.list.iterator();
                }

                @Override
                public int size() {
                    return ArrayMap.this.list.size();
                }
            };
        }
        return (Set<Map.Entry<K, V>>) this.entries;
    }

    @Override
    public V put(K key, V value) {
        int i;
        int size = this.list.size();
        Entry<K, V> entry = null;
        if (key == null) {
            for (i = 0; i < size && (entry = this.list.get(i)).getKey() != null; ++i) {
            }
        } else {
            for (i = 0; i < size && !key.equals((entry = this.list.get(i)).getKey()); ++i) {
            }
        }
        V oldValue = null;
        if (i < size) {
            oldValue = entry.getValue();
            entry.setValue(value);
        } else {
            this.list.add(new Entry<K, V>(key, value));
        }
        return oldValue;
    }

    public static class Entry<K, V>
    implements Map.Entry<K, V>,
    Serializable {
        public static final long serialVersionUID = 9179541993413738569L;
        protected K key;
        protected V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return (this.key == null ? e.getKey() == null : this.key.equals(e.getKey())) && (this.value == null ? e.getValue() == null : this.value.equals(e.getValue()));
        }

        @Override
        public int hashCode() {
            int keyHash = this.key == null ? 0 : this.key.hashCode();
            int valueHash = this.value == null ? 0 : this.value.hashCode();
            return keyHash ^ valueHash;
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}

