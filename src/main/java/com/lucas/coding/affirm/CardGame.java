package com.lucas.coding.affirm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设计一个简单的纸牌游戏系统，支持如下操作：
 *
 * 玩家加入游戏；
 *
 * 发牌（每个玩家获得一定数量的牌）；
 *
 * 打出一张牌；
 *
 * 查询某个玩家当前的手牌；
 *
 * 可能还会要求判断游戏是否结束（所有人都打完牌）。
 */
public class CardGame {

	static class Card {
		String face;
		Card(String face) {
			this.face = face;
		}

		public String toString() {
			return face;
		}
	}

	static class Player {
		int id;
		List<Card> hand;
		Player(int id) {
			this.id = id;
			this.hand = new ArrayList<>();
		}
	}
	// {player_id: Player}
	Map<Integer, Player> players;

	public CardGame() {
		this.players = new HashMap<>();
	}

	/**
	 * 检查这个玩家是否已经在游戏中；
	 * 如果不在，则创建这个玩家
	 */
	public void join(int playerId) {
		if (!players.containsKey(playerId)) {
			Player player = new Player(playerId);
			players.put(playerId, player);
		}
		System.out.println("Player " + playerId + " joined");
	}

	/**
	 * 检查玩家是否已经加入；
	 * 将传入的牌（cards）添加到玩家当前的手牌中；
	 * 允许多次发牌（可能追加在已有手牌后）。
	 */
	public void deal(int playerId, List<Card> cards) {
		Player player = players.get(playerId);
		if (player == null) {
			System.out.println("Player " + playerId + " not found");
			return;
		}
		player.hand.addAll(cards);
		System.out.println("Player " + playerId + " dealed " + cards.size() + " cards");
	}

	/**
	 * 玩家打出一张牌。
	 * 1. 检查该玩家是否存在；
	 * 2. 检查该玩家的手牌中是否有这张牌；
	 * 2.1 如果有，就从手牌中移除一张；
	 * 3. 成功返回 True，否则返回 False。
	 */
	public boolean play(int playerId, Card card) {
		Player player = players.get(playerId);
		if (player == null) {
			System.out.println("Player " + playerId + " not found");
			return false;
		}
		List<Card> hand = player.hand;
		if (!hand.contains(card)) {
			System.out.println("Player " + playerId + " not contained in hand with:" + card);
		}

		hand.remove(card);
		System.out.println("Player " + playerId + " played " + card);
		return true;
	}

	/**
	 * 1. 检查玩家是否加入；
	 * 2. 返回该玩家当前的手牌（通常返回副本以防外部修改原始数据）。
	 */
	public List<Card> getHand(int playerId) {
		Player player = players.get(playerId);
		if (player == null) {
			System.out.println("Player " + playerId + " not found");
			return null;
		}
		// 不可变拷贝：外部拿到也改不了内部
		return List.copyOf(player.hand);
	}

	/**
	 *
	 * 游戏结束条件：所有已加入的玩家都打完所有牌；
	 * 遍历所有玩家；
	 * 如果有任意玩家手中仍有牌，返回 False；
	 * 如果所有人手牌为空，返回 True。
	 */
	public boolean isFinished() {
		if (players.isEmpty()) return false; // 没人加入，不算结束
		for (Player p : players.values()) {
			if (!p.hand.isEmpty()) return false;
		}
		return true;
	}

	// --------------------------
	public static void main(String[] args) {
		CardGame g = new CardGame();

		Card c1 = new Card("1");
		Card c2 = new Card("2");
		Card c3 = new Card("3");
		Card c4 = new Card("4");

		g.join(1);
		g.join(2);

		g.deal(1, Arrays.asList(c1, c2));
		g.deal(2, Arrays.asList(c3, c4));

		System.out.println("P1 hand: " + g.getHand(1)); // [1, 2]
		System.out.println("P2 hand: " + g.getHand(2)); // [3, 4]

		g.play(1, new Card("1"));
		g.play(2, new Card("3"));

		System.out.println("After playing:");
		System.out.println("P1 hand: " + g.getHand(1)); // [2]
		System.out.println("P2 hand: " + g.getHand(2)); // [4]
		System.out.println("Finished? " + g.isFinished()); // false

		g.play(1, new Card("2"));
		g.play(2, new Card("4"));
		System.out.println("Finished? " + g.isFinished()); // true
	}

}
