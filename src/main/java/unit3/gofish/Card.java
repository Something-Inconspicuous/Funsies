package unit3.gofish;

import unit3.gofish.PlayingCard.Rank;
import unit3.gofish.PlayingCard.Suit;

public interface Card extends Comparable<Card> {

    Rank rank();

    Suit suit();

    /**
     * Compares this card to another, first by rank, then by suit.
     * 
     * @param o The other card.
     * 
     * @return A value less than 0 if the given card is either a 
     * higher rank or the same rank but a "better" suit, a value greater
     * than 0 if the given card is either a lower rank or the same rank
     * but a "worse" suit, or 0 exactly if the two cards are equal.
     * 
     * @throws NullPointerException If the given card is {@code null}.
     */
    int compareTo(Card o);

}