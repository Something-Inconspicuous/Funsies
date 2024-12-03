package unit3.gofish;

import java.util.List;

import unit3.gofish.cards.PlayingCard;

public interface Player {
    Request getRequest(List<? extends Player> others);

    void giveCards(PlayingCard... cards);

    List<PlayingCard> takeCards(PlayingCard.Rank ofRank);

    void reset();

    List<PlayingCard> books();
}
