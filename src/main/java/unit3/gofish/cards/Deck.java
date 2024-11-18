package unit3.gofish.cards;

import java.util.Deque;

public interface Deck extends Deque<PlayingCard> {
    default PlayingCard deal() {
        return pollLast();
    }

    default Card slip() {
        return pollFirst();
    }

    void refill();

    void shuffle();

    default void reshuffle() {
        refill();
        shuffle();
    }
}
