package unit3.gofish;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import unit3.gofish.Card.Rank;

public class CPUPlayer implements Player {
    private Collection<Card> hand;
    private Collection<Card> books;
    private Random rng;

    public CPUPlayer(Collection<Card> handCollection, Collection<Card> booksCollection) {
        super();
        hand = handCollection;
        hand.clear();
        books = booksCollection;
        books.clear();
        rng = new Random();
    }

    @Override
    public Request getRequest(List<? extends Player> others) {
        if(hand.isEmpty()) return null;
        int i = rng.nextInt(hand.size());
        int j = rng.nextInt(others.size());
        // Prevent asking from oneself.
        // The list probably shouldn't contain the player being asked,
        // but whatever.
        while(others.get(j).equals(this)) {
            j = rng.nextInt(others.size());
        }
        return new Request(others.get(j), hand.stream().skip(i).findFirst().get().rank);
    }

    @Override
    public void giveCards(Card... cards) {
        Collections.addAll(hand, cards);

        for(Rank rank : Rank.values()) {
            List<Card> found = hand.stream().filter(card -> card.rank == rank).toList();
            if(found.size() >= 4) {
                hand.removeAll(found);
                books.addAll(found);
            }
        }
    }

    @Override
    public List<Card> takeCards(Rank ofRank) {
        List<Card> cards = hand.stream().filter(card -> card.rank == ofRank).toList();
        hand.removeAll(cards);
        return cards;
    }
    
    @Override
    public void reset() {
        hand.clear();
        books.clear();
    }

    @Override
    public List<Card> books() {
        return books.stream().toList();
    }
}
