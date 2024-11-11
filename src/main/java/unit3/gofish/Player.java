package unit3.gofish;

import java.util.List;

public interface Player {
    Request getRequest(List<? extends Player> others);

    void giveCards(Card... cards);

    List<Card> takeCards(Card.Rank ofRank);

    void reset();

    List<Card> books();
}
