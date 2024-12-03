package unit3.gofish;

import unit3.gofish.cards.PlayingCard;

public record Request(Player player, PlayingCard.Rank rank) {}
