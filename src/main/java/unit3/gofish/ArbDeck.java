package unit3.gofish;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.Spliterator;
import unit3.gofish.PlayingCard.Rank;
import unit3.gofish.PlayingCard.Suit;

public final class ArbDeck extends AbstractQueue<PlayingCard> implements Deck {
    private PlayingCard[] cards;
    private int top = 0;
    private int capacity;
    private int numRanks;

    private int modCount = 0;

    public ArbDeck(int numRanks) {
        super();
        this.numRanks = numRanks;
        capacity = numRanks * Suit.values().length;
        cards = new PlayingCard[capacity];
        refill();
    }

    @Override
    public boolean offerFirst(PlayingCard e) {
        if(top >= capacity) return false;

        Objects.requireNonNull(e);

        PlayingCard[] copy = new PlayingCard[capacity];
        System.arraycopy(cards, 0, copy, 1, top++);
        cards = copy;
        cards[0] = e;

        modCount++;

        return true;
    }

    @Override
    public boolean offerLast(PlayingCard e) {
        if(top >= capacity) return false;

        Objects.requireNonNull(e);

        cards[top++] = e;
        modCount++;
        return true;
    }

    @Override
    public PlayingCard pollFirst() {
        if(top <= 0) return null;

        PlayingCard[] copy = new PlayingCard[capacity];
        System.arraycopy(cards, 1, copy, 0, --top);
        PlayingCard ret = cards[0];
        cards = copy;
        modCount++;

        return ret;
    }

    @Override
    public PlayingCard pollLast() {
        if(top <= 0) return null;
        modCount++;
        return cards[--top];
    }

    @Override
    public PlayingCard peekFirst() {
        if(top > capacity) return null;
        modCount++;
        return cards[top - 1]; 
    }

    @Override
    public PlayingCard peekLast() {
        if(top <= 0) return null;
        modCount++;
        return cards[top - 1];
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        for(int i = 0; i < top; i++) {
            if(Objects.equals(cards[i], o)) {
                PlayingCard[] copy = new PlayingCard[capacity];
                // Remove via copy
                System.arraycopy(cards, 0, copy, 0, i);
                System.arraycopy(cards, i, copy, i + 1, top - i);
                cards = copy;
                modCount++;

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        for(int i = top; i >= 0; i--) {
            if(Objects.equals(cards[i], o)) {
                PlayingCard[] copy = new PlayingCard[capacity];
                // Remove via copy
                System.arraycopy(cards, 0, copy, 0, i);
                System.arraycopy(cards, i + 1, copy, i, top - i);
                cards = copy;

                modCount++;

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean offer(PlayingCard e) {
        return offerLast(e); // modCounts
    }

    @Override
    public PlayingCard poll() {
        return pollLast(); // modCounts
    }

    @Override
    public PlayingCard peek() {
        return peekLast(); // modCounts
    }

    @Override
    public void push(PlayingCard e) {
        addFirst(e); // 
    }

    @Override
    public PlayingCard pop() {
        return removeFirst();
    }

    @Override
    public int size() {
        return top;
    }

    @Override
    public Spliterator<PlayingCard> spliterator() {
        // return Spliterators.spliterator(cards, 0, top, Spliterator.NONNULL);
        return Arrays.spliterator(cards, 0, top);
    }

    @Override
    public Iterator<PlayingCard> iterator() {
        return new Iterator<PlayingCard>() {
            int i = 0;
            final int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return i < top;
            }

            @Override
            public PlayingCard next() {
                if(modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return cards[i++];
            }
        };
    }

    @Override
    public Iterator<PlayingCard> descendingIterator() {
        return new Iterator<PlayingCard>() {
            int i = top;
            final int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public PlayingCard next() {
                if(modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return cards[--i];
            }
        };
    }

    @Override
    public void refill() {
        top = 0;
        Rank[] ranks = Rank.values();
        for(Suit suit : Suit.values()) {
            // Skip low ace
            for(int rankOrd = 1; rankOrd <= numRanks; rankOrd++) {
                Rank rank = ranks[rankOrd];
                cards[top++] = new PlayingCard(rank, suit);
            }
        }
        modCount++;
    }

    @Override
    public void shuffle() {
        // https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
        // Durstenfeld shuffle
        Random rng = new Random();
        for(int i = 0; i < top - 2; i++) {
            int j = rng.nextInt(top - i) + i;
            PlayingCard temp = cards[i];
            cards[i] = cards[j];
            cards[j] = temp;
        }
        modCount++;
    }

    @Override
    public void addFirst(PlayingCard e) {
        if(!offerFirst(e)) throw new IllegalStateException("Cannot exceed " + capacity + " cards.");
    }

    @Override
    public void addLast(PlayingCard e) {
        if(!offerLast(e)) throw new IllegalStateException("Cannot exceed " + capacity + " cards.");
    }

    @Override
    public PlayingCard removeFirst() {
        PlayingCard ret = pollFirst();
        if(ret == null) throw new NoSuchElementException("No cards present.");
        return ret;
    }

    @Override
    public PlayingCard removeLast() {
        PlayingCard ret = pollLast();
        if(ret == null) throw new NoSuchElementException("No cards present.");
        return ret;
    }

    @Override
    public PlayingCard getFirst() {
        PlayingCard ret = peekFirst();
        if(ret == null) throw new NoSuchElementException("No cards present.");
        return ret;
    }

    @Override
    public PlayingCard getLast() {
        PlayingCard ret = peekLast();
        if(ret == null) throw new NoSuchElementException("No cards present.");
        return ret;
    }
}
