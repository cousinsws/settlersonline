package me.cousinss.settlers.server.game;

import me.cousinss.settlers.server.game.card.ResourceSet;
import me.cousinss.settlers.server.game.player.Player;

public record TradeOffer(Player from, Player to, ResourceSet offer, ResourceSet request) {}
