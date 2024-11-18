package unit3;

import java.lang.foreign.ValueLayout;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
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

        @Override
        public String toString() {
            return "{" + key + " => " + value + "}";
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
    private transient int numBuckets;

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

        if(bucket == null){
            data[i] = new Bucket<>();
            bucket = data[i];
            numBuckets++;
        }
        else if(bucket.size() >= BUCKET_CAPACITY){
            growAndRehash();
            i = hash % data.length;
            bucket = data[i];
        }

        Entry<Key, Value> entry = new Entry<Key, Value>(key, value);
        if(bucket.add(entry)) {
            size++;
            return true;
        } else return false;
    }

    public void set(Key key, Value value) {
        int hash = Objects.hashCode(key);
        int i = hash % data.length;
        Bucket<Key, Value> bucket = data[i];

        if(bucket.removeIf(e -> Objects.equals(e.key, key))) {
            bucket.add(new Entry<Key, Value>(key, value));
        } else {
            put(key, value);
        }
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
                    numBuckets++;
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
        numBuckets = 0;
    }

    @Override
    public Iterator<Entry<Key, Value>> iterator() {
        return new Iterator<Entry<Key,Value>>() {
            int i = 0;
            int numBucketsTraversed = 0;
            Iterator<Entry<Key, Value>> bucketIterator = initIterator();

            private Iterator<Entry<Key, Value>> initIterator() {
                Iterator<Entry<Key, Value>> it = data[i = findNextBucket()].iterator();
                i = findNextBucket(i + 1);
                return it;
            }

            @Override
            public boolean hasNext() {
                return bucketIterator != null && (bucketIterator.hasNext() || i != -1);
            }

            @Override
            public Entry<Key, Value> next() {
                if(bucketIterator.hasNext()) return bucketIterator.next();
                numBucketsTraversed++;
                Bucket<Key, Value> bucket = data[i];
                i = findNextBucket(i + 1);
                if(bucket == null) {
                    throw new NoSuchElementException("No more elements in HashTable.");
                }
                bucketIterator = bucket.iterator();
                return bucketIterator.next();
            }
        };
    }

    private int findNextBucket(int from, int till) {
        for (int i = from; i < till; i++) {
            // System.out.println("bucket " + i + " = " + data[i]);
            if(data[i] != null) return i;
        }
        return -1;
    }
    private int findNextBucket() {
        return findNextBucket(0, data.length);
    }

    private int findNextBucket(int from) {
        return findNextBucket(from, data.length);
    }

    @Override
    public String toString() {
        if(isEmpty()) return "[]";

        Iterator<Entry<Key, Value>> it = iterator();
        StringBuilder sb = new StringBuilder("[");
        while (true) {
            sb.append(it.next());
                
            if(it.hasNext()) {
                sb.append(", ");
            } else {
                sb.append("]");
                break;
            }
        }

        return sb.toString();
    }
    
    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}
