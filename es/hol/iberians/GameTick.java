package es.hol.iberians;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class GameTick {
	private CPVP pl;
	public GameTick(CPVP pl) {
		this.pl = pl;
	}
	public void start() { //Fix bug in a future
		/*Bukkit.getScheduler().scheduleSyncDelayedTask(this.pl, new Runnable() {
			@Override
			public void run() {
				for(final Arena a: ArenaManager.getManager().getArenas()) { 
                                    if(a.getPlayers().size() < 2 && a.getPlayers().size() > 0){
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable(){
                                            @Override
                                            public void run(){
                                                Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + CPVP.cfg.getString("add-start-arena").replace("%na%", String.valueOf(a.getId())));
                                            }
                                        }, 500L);
                                    }
				}

			}
		},20L);*/
	}
}
