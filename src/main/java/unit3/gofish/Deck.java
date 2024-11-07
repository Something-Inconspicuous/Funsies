package unit3.gofish;

import java.util.Deque;

public interface Deck extends Deque<Card> {
    default Card deal() {
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
