package unit3;

import java.lang.foreign.ValueLayout;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;

public class HashTable<Key, Value> implements Iterable<unit3.HashTable.Entry<Key, Value>> {
    public static class Entry<_Key, _Value> {
        private _Key key;
        private _Value value;

        public Entry(_Key k, _Value v) {
            super();
            this.key = k;
            this.value = v;
        }

        public _Key getKey() {
            return key;
        }

        public _Value getValue() {
            return value;
        }

        public int keyHash() {
            return Objects.hashCode(key);
        }
    }
    private static class Bucket<_Key, _Value> extends TreeSet<Entry<_Key, _Value>> {
        public Bucket() {
            super(Comparator.comparingInt(Entry::keyHash));
        }

        public Entry<_Key, _Value> find(_Key element) {
            if(this.size() == 0) return null;
            if(this.size() == 1) return getFirst();

            Entry<_Key, _Value> maybe = this.ceiling(new Entry<>(element, null));
            if(Objects.equals(maybe.getKey(), element)) {
                return maybe;
            }
            return null;
        }
    }

    private Bucket<Key, Value>[] data;

    private transient int size;

    private static final int BUCKET_CAPACITY = 8;

    private HashTable(int capacity) {
        super();
        makeData(capacity);
    }

    public HashTable() {
        this(16);
    }

    public boolean put(Key key, Value value) {
        int hash = Objects.hashCode(key);
        int i = hash % data.length;
        Bucket<Key, Value> bucket = data[i];

        if(bucket == null)
            bucket = new Bucket<>();
        else if(bucket.size() >= BUCKET_CAPACITY){
            growAndRehash();
            i = hash % data.length;
            bucket = data[i];
        }

        return bucket.add(new Entry<Key, Value>(key, value));
    }

    public void set(Key key, Value value) {
        int hash = Objects.hashCode(key);
        int i = hash % data.length;
        Bucket<Key, Value> bucket = data[i];

        bucket.removeIf(e -> Objects.equals(e.key, key));
    }

    public Value get(Key e) {
        int i = Objects.hashCode(e) % data.length;

        Bucket<Key, Value> bucket = data[i];
        if(bucket == null) return null;

        Entry<Key, Value> maybe = bucket.find(e);
        if(maybe == null) return null;

        return maybe.getValue();
    }

    private void growAndRehash() {
        Bucket<Key, Value>[] oldData = data;
        makeData(data.length << 1);
        for(Bucket<Key, Value> bucket : oldData) {
            for (Entry<Key, Value> e : bucket) {
                int i = index(e);
                if(data[i] == null) {
                    data[i] = new Bucket<>();
                }
                data[i].add(e);
            }
        }
    }

    private int index(Entry<Key, Value> e) {
        return e.keyHash() % data.length;
    }

    @SuppressWarnings("unchecked")
    private void makeData(int length) {
        data = new Bucket[length];
    }

    @Override
    public Iterator<Entry<Key, Value>> iterator() {
        // TODO Auto-generated method stub
        return null;
    }
}
