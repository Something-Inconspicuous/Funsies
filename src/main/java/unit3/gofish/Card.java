package unit3.gofish;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.IntSupplier;

/**
 * A French suited English playing card of the standerd 52-card deck. 
 */
public final class Card implements Comparable<Card> {
    public enum Rank implements IntSupplier {
        ACE_LOW("Ace", 1),
        TWO("Two", 2),
        THREE("Three", 3),
        FOUR("Four", 4),
        FIVE("Five", 5),
        SIX("Six", 6),
        SEVEN("Seven", 7),
        EIGHT("Eight", 8),
        NINE("Nine", 9),
        TEN("Ten", 10),
        JACK("Jack", 11),
        QUEEN("Queen", 12),
        KING("King", 13),
        ACE_HIGH("Ace", 14),
        JOKER("Joker", Integer.MAX_VALUE), // Trump value
        ;
        private final String commonName;
        private final int faceValue;

        private Rank(final String name) {
            this.commonName = name;
            faceValue = 0;
        }

        private Rank(final String name, final int value) {
            this.commonName = name;
            this.faceValue = value;
        }

        @Override
        public String toString() {
            return commonName;
        }

        @Override
        public int getAsInt() {
            return faceValue;
        }
    }

    public enum Suit {
        CLUBS("Clubs"),
        DIAMONDS("Diamonds"),
        HEARTS("Hearts"),
        SPADES("Spades"),
        ;
        private final String commonName;
        private Suit(final String name) {
            this.commonName = name;
        }

        @Override
        public String toString() {
            return commonName;
        }
    }

    /**
     * Container class for some comparisons of cards.
     */
    public enum Compare implements Comparator<Card> {
        /**
         * Natural ordering, equivalent to {@code Comparators.naturalOrder()}
         */
        NATURAL,

        /**
         * Compares ranks, completely ignores suits.
         */
        BY_RANK((c1, c2) -> c1.rank.compareTo(c2.rank)),

        /**
         * Compares suits, completely ignores ranks.
         */
        BY_SUIT((c1, c2) -> c1.suit.compareTo(c2.suit)),

        /**
         * Compares suits, than breaks ties with ranks.
         */
        DECK_ORDER((c1, c2) -> {
            // Compare suit, rank breaks ties.
            final int scmp = c1.suit.compareTo(c2.suit);
            if(scmp == 0)
                return c1.rank.compareTo(c2.rank);
            else
                return scmp;
        }),
        ;
        private final Comparator<Card> comp;

        private Compare() {
            comp = Comparator.naturalOrder();
        }

        private Compare(final Comparator<Card> comp) {
            this.comp = comp;
        }

        @Override
        public int compare(final Card o1, final Card o2) {
            return comp.compare(o1, o2);
        }

    }

    /**
     * The rank of the card.
     */
    public final Rank rank;

    /**
     * The suit of the card.
     */
    public final Suit suit;

    /**
     * Constructs a card with a given {@link Rank} and {@link Suit}.
     * 
     * @param rank The rank.
     * @param suit The suit
     */
    public Card(final Rank rank, final Suit suit) {
        this.rank = Objects.requireNonNull(rank);
        this.suit = Objects.requireNonNull(suit);
    }

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
    @Override
    public int compareTo(final Card o) {
        Objects.requireNonNull(o);

        // Compare rank, suit breaks ties.
        final int rcmp = rank.compareTo(o.rank);
        if(rcmp == 0)
            return suit.compareTo(o.suit);
        else
            return rcmp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rank == null) ? 0 : rank.hashCode());
        result = prime * result + ((suit == null) ? 0 : suit.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Card other = (Card) obj;
        if (rank != other.rank)
            return false;
        if (suit != other.suit)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
