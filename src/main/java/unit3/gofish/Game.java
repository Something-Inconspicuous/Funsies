package unit3.gofish;

import java.util.List;

public class Game {
    private List<? extends Player> players;
    private Deck deck;
    private int turn;

    public Game(List<? extends Player> players, Deck deck) {
        super();
        this.players = players;
        this.deck = deck;
        turn = 0;
    }

    public void play() {
        deck.reshuffle();
        turn = 0;

        for (Player player : players) {
            player.reset();
        }

        int cardsGiven = players.size() <= 2 ? 7 : 5;

        // deal one at a time, just for fun
        while(cardsGiven --> 0) {
            for(Player p : players) {
                p.giveCards(deck.deal());
            }
        }

        while(!deck.isEmpty()) {
            Player current = players.get(turn);
            Request ask = current.getRequest(players);

            List<PlayingCard> take = ask != null ? ask.player().takeCards(ask.rank()) : null;

            if(take == null || take.size() == 0) {
                current.giveCards(deck.deal()); // go fish
            } else {
                current.giveCards(take.toArray(new PlayingCard[take.size()]));
            }

            if(++turn >= players.size()) {
                turn = 0;
            }
        }
    }
}
