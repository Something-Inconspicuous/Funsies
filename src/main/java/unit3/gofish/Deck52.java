package unit3.gofish;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.Spliterator;
import unit3.gofish.Card.Rank;
import unit3.gofish.Card.Suit;

public final class Deck52 extends AbstractQueue<Card> implements Deck {
    private Card[] cards;
    private int top = 0;
    private final static int CAPACITY = (Rank.values().length - 1) * Suit.values().length;

    private int modCount = 0;

    public Deck52() {
        super();
        cards = new Card[CAPACITY];
        refill();
    }

    @Override
    public boolean offerFirst(Card e) {
        if(top >= CAPACITY) return false;

        Objects.requireNonNull(e);

        Card[] copy = new Card[CAPACITY];
        System.arraycopy(cards, 0, copy, 1, top++);
        cards = copy;
        cards[0] = e;

        modCount++;

        return true;
    }

    @Override
    public boolean offerLast(Card e) {
        if(top >= CAPACITY) return false;

        Objects.requireNonNull(e);

        cards[top++] = e;
        modCount++;
        return true;
    }

    @Override
    public Card pollFirst() {
        if(top <= 0) return null;

        Card[] copy = new Card[CAPACITY];
        System.arraycopy(cards, 1, copy, 0, --top);
        Card ret = cards[0];
        cards = copy;
        modCount++;

        return ret;
    }

    @Override
    public Card pollLast() {
        if(top <= 0) return null;
        modCount++;
        return cards[--top];
    }

    @Override
    public Card peekFirst() {
        if(top > CAPACITY) return null;
        modCount++;
        return cards[top - 1]; 
    }

    @Override
    public Card peekLast() {
        if(top <= 0) return null;
        modCount++;
        return cards[top - 1];
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        for(int i = 0; i < top; i++) {
            if(Objects.equals(cards[i], o)) {
                Card[] copy = new Card[CAPACITY];
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
                Card[] copy = new Card[CAPACITY];
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
    public boolean offer(Card e) {
        return offerLast(e); // modCounts
    }

    @Override
    public Card poll() {
        return pollLast(); // modCounts
    }

    @Override
    public Card peek() {
        return peekLast(); // modCounts
    }

    @Override
    public void push(Card e) {
        addFirst(e); // 
    }

    @Override
    public Card pop() {
        return removeFirst();
    }

    @Override
    public int size() {
        return top;
    }

    @Override
    public Spliterator<Card> spliterator() {
        // return Spliterators.spliterator(cards, 0, top, Spliterator.NONNULL);
        return Arrays.spliterator(cards, 0, top);
    }

    @Override
    public Iterator<Card> iterator() {
        return new Iterator<Card>() {
            int i = 0;
            final int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return i < top;
            }

            @Override
            public Card next() {
                if(modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return cards[i++];
            }
        };
    }

    @Override
    public Iterator<Card> descendingIterator() {
        return new Iterator<Card>() {
            int i = top;
            final int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public Card next() {
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
            // Skip low ace and joker
            for(int rankOrd = 1; rankOrd < ranks.length - 1; rankOrd++) {
                Rank rank = ranks[rankOrd];
                cards[top++] = new Card(rank, suit);
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
            Card temp = cards[i];
            cards[i] = cards[j];
            cards[j] = temp;
        }
        modCount++;
    }

    @Override
    public void addFirst(Card e) {
        if(!offerFirst(e)) throw new IllegalStateException("Cannot exceed " + CAPACITY + " cards.");
    }

    @Override
    public void addLast(Card e) {
        if(!offerLast(e)) throw new IllegalStateException("Cannot exceed " + CAPACITY + " cards.");
    }

    @Override
    public Card removeFirst() {
        Card ret = pollFirst();
        if(ret == null) throw new NoSuchElementException("No cards present.");
        return ret;
    }

    @Override
    public Card removeLast() {
        Card ret = pollLast();
        if(ret == null) throw new NoSuchElementException("No cards present.");
        return ret;
    }

    @Override
    public Card getFirst() {
        Card ret = peekFirst();
        if(ret == null) throw new NoSuchElementException("No cards present.");
        return ret;
    }

    @Override
    public Card getLast() {
        Card ret = peekLast();
        if(ret == null) throw new NoSuchElementException("No cards present.");
        return ret;
    }
}
