package es.hol.iberians;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class LobbyTimer {
	private CPVP pl;
	public LobbyTimer(CPVP pl) {
		this.pl = pl;
	}
	public void start() {
		/*Bukkit.getScheduler().scheduleSyncRepeatingTask(this.pl, new Runnable() {
			@Override
			public void run() {
				for(Arena a: ArenaManager.getManager().getArenas()) {
                                    Scoreboard s = a.getSBApi().getBoard("arena" + a.getId());
                                    Objective o = a.getSBApi().getObjective("scoreInGame", s);
                                    o.setDisplaySlot(DisplaySlot.SIDEBAR);
                                    o.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f[*] &c--- &2&lCPVP &c--- &f[*]"));
                                    
                                    int size = a.getPlayers().size();
                                    if(CPVP.cfg.getBoolean("usedb") == true){
                                        ArenaManager.getManager().setCoins(a);
                                        ArenaManager.getManager().setDeaths(a);
                                        ArenaManager.getManager().setKills(a);
                                        for(String p : a.getPlayers()){
                                            Score line = o.getScore(Bukkit.getOfflinePlayer(""));
                                            line.setScore(6);
                                            Score kills = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Kills: " + ChatColor.DARK_GREEN + ArenaManager.getManager().getKills().get(p)));
                                            kills.setScore(5);
                                            Score deaths = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Deaths: " + ChatColor.DARK_GREEN + ArenaManager.getManager().deathstemp.get(p)));
                                            deaths.setScore(4);
                                            Score coins = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Coins: " + ChatColor.DARK_GREEN + ArenaManager.getManager().coinstemp.get(p)));
                                            coins.setScore(3);
                                            Score line2 = o.getScore(Bukkit.getOfflinePlayer(""));
                                            line2.setScore(2);
                                            Score players = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Players: " + ChatColor.DARK_GREEN + size));
                                            players.setScore(1);
                                            Score arenaID = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "ArenaID: " + ChatColor.DARK_GREEN + a.getId()));
                                            arenaID.setScore(0);
                                        }
                                    }else{
                                        for(String p : a.getPlayers()){
                                            Score line = o.getScore(Bukkit.getOfflinePlayer(""));
                                            line.setScore(6);
                                            if(ArenaManager.getManager().coinstemp.get(p) == null && ArenaManager.getManager().deathstemp.get(p) == null && ArenaManager.getManager().killstemp.get(p) == null){
                                                Score kills = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Kills: " + ChatColor.DARK_GREEN + "0"));
                                                kills.setScore(5);
                                                Score deaths = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Deaths: " + ChatColor.DARK_GREEN + "0"));
                                                deaths.setScore(4);
                                                Score coins = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Coins: " + ChatColor.DARK_GREEN + "0"));
                                                coins.setScore(3);
                                            }else{
                                                Score kills = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Kills: " + ChatColor.DARK_GREEN + ArenaManager.getManager().killstemp.get(p)));
                                                kills.setScore(5);
                                                Score deaths = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Deaths: " + ChatColor.DARK_GREEN + ArenaManager.getManager().deathstemp.get(p)));
                                                deaths.setScore(4);
                                                Score coins = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Coins: " + ChatColor.DARK_GREEN + ArenaManager.getManager().coinstemp.get(p)));
                                                coins.setScore(3);
                                            }
                                            Score line2 = o.getScore(Bukkit.getOfflinePlayer(""));
                                            line2.setScore(2);
                                            Score players = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Players: " + ChatColor.DARK_GREEN + size));
                                            players.setScore(1);
                                            Score arenaID = o.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "ArenaID: " + ChatColor.DARK_GREEN + a.getId()));
                                            arenaID.setScore(0);
                                        }
                                    }
				}
			}
		}, 0L, 20L);*/

	}
}
