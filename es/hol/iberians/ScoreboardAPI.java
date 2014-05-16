package es.hol.iberians;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardAPI {
	private static HashMap<String, Scoreboard> scoreboards = new HashMap<>();
	private static ScoreboardManager manager = Bukkit.getScoreboardManager();
	public static ScoreboardAPI getAPI() {
		return new ScoreboardAPI();
	}
	public Objective getObjective(String name, Scoreboard board) {
		if(board.getObjective(name) != null) {
			return board.getObjective(name);
		} else {
			Objective o = board.registerNewObjective(name, "dummy");
			o.setDisplayName(name);
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			return o;
		}
	}
	public Scoreboard newBoard(String name) {
		Scoreboard board = manager.getNewScoreboard();
		scoreboards.put(name, board);
		return board;
	}
	public Scoreboard getBoard(String name) {
		if(scoreboards.containsKey(name)) {
			return scoreboards.get(name);
		} else {
			return newBoard(name);
		}
	}
	public void removeScoreboard(String name) {
		if(scoreboards.containsKey(name)) {
			for(OfflinePlayer p: scoreboards.get(name).getPlayers()) {
				if(p.isOnline()) {
					Player p2 = p.getPlayer();
					p2.setScoreboard(manager.getNewScoreboard());
				}
			}
			scoreboards.remove(name);
		}
	}
}
